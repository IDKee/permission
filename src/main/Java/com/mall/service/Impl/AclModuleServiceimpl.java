package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.mall.common.RequestHolder;
import com.mall.dao.SysAclModuleMapper;
import com.mall.exception.ParamException;
import com.mall.model.SysAclModule;
import com.mall.model.SysDept;
import com.mall.param.AclModuleParam;
import com.mall.service.IAclModuleService;
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
 * Created by 王乾 on 2018/1/19.
 */
@Service("iAclModuleService")
public class AclModuleServiceImpl implements IAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    /**
     * 新增权限模块
     * @param param
     */
    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw  new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
    }

    /**
     * 更新权限模块
     * @param param
     */
    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw  new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");

        SysAclModule after = SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        updateWithChild(before,after);
    }

    /**
     * 更新当前部门还要更新它的子部门
     * @param before
     * @param after
     */
    @Transactional
    private void updateWithChild(SysAclModule before,SysAclModule after){
        String newLevelPrefix = after.getLevel();
        String OldLevelPrefix = before.getLevel();
        if ( !after.getLevel().equals(before.getLevel())) {
            //权限模块等级前缀更改了，子权限模块需要更新
            // 0.1.1   0.1.2  0.1.3
            List<SysAclModule> sysAclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(sysAclModuleList)){
                for (SysAclModule sysAclModule : sysAclModuleList){
                    //获取更新前子权限模块的level 0.1.1->0.2.1
                    String level = sysAclModule.getLevel();
                    if(level.indexOf(OldLevelPrefix) == 0){
                        level = newLevelPrefix + level.substring(OldLevelPrefix.length());
                        sysAclModule.setLevel(level);
                    }
                }
                //批量更新子权限模块
                sysAclModuleMapper.batchUpdateLevel(sysAclModuleList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(Integer parentId,String aclModuleName,Integer aclModuleId){
        return sysAclModuleMapper.countByNameAndParentId(parentId,aclModuleName,aclModuleId) > 0;
    }

    /**
     * 模块的等级获取
     * @param aclModuleId 权限模块的id
     * @return 权限模块的level(0.6)
     */
    private String getLevel(Integer aclModuleId){
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if ( aclModule == null){
            return  null;
        }
        return  aclModule.getLevel();
    }



}
