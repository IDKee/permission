package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.mall.common.RequestHolder;
import com.mall.dao.SysDeptMapper;
import com.mall.dao.SysUserMapper;
import com.mall.exception.ParamException;
import com.mall.model.SysDept;
import com.mall.model.SysUser;
import com.mall.param.DeptParam;
import com.mall.service.IDeptService;
import com.mall.service.IUserService;
import com.mall.util.BeanValidator;
import com.mall.util.IpUtil;
import com.mall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by 王乾 on 2018/1/1.
 */
@Service("DeptService")
public class DeptServiceImpl implements IDeptService{

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    public void save(DeptParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw  new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept dept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                        .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
//      dept.setOperator("system");
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
//      dept.setOperateIp("127.0.0.1");
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
    }

    /**
     * 更新部门
     * @param param
     */
    public void update(DeptParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw  new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新部门不存在");
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw  new ParamException("同一层级下存在相同名称的部门");
        }

        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();

        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
//      after.setOperator("system");
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before,after);
    }

    /* 保证同时成功或者同时失败 */
    @Transactional
    private void updateWithChild(SysDept before,SysDept after){

        String newLevelPrefix = after.getLevel();//0.2
        String OldLevelPrefix = before.getLevel();//0.1
        if ( !after.getLevel().equals(before.getLevel())) {
            //部门等级前缀更改了，子部门需要更新
            // 0.1.1   0.1.2  0.1.3
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)){
                for (SysDept dept : deptList){
                    //获取更新前的部门的子部门的level 0.1.1->0.2.1
                    String level = dept.getLevel();
                    if(level.indexOf(OldLevelPrefix) == 0){
                        level = newLevelPrefix + level.substring(OldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                //批量更新子部门
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);

    }

    /**
     * 检查同一层级存不存在相同名称的部门
     * @param parentId
     * @param deptName
     * @param deptId
     * @return
     */
    private boolean checkExist(Integer parentId,String deptName,Integer deptId){
        return  sysDeptMapper.countByNameAndParentId(parentId,deptName,deptId) > 0;
    }

    /**
     * 传入部门id,返回这个部门的级别
     * @param deptId
     * @return
     */
    private String getLevel(Integer deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if ( dept == null){
            return  null;
        }
        return  dept.getLevel();
    }


    /**
     *删除部门
     * @param deptId
     */

    public void delete(int deptId) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept, "待删除的部门不存在，无法删除");
        if (sysDeptMapper.countByParentId(dept.getId()) > 0) {
            throw new ParamException("当前部门下面有子部门，无法删除");
        }
        if(sysUserMapper.countByDeptId(dept.getId()) > 0) {
            throw new ParamException("当前部门下面有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
    }

}
