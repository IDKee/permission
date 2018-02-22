package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mall.common.RequestHolder;
import com.mall.dao.SysRoleAclMapper;
import com.mall.dao.SysRoleMapper;
import com.mall.dao.SysRoleUserMapper;
import com.mall.dao.SysUserMapper;
import com.mall.dto.AclModuleLevelDto;
import com.mall.exception.ParamException;
import com.mall.model.SysRole;
import com.mall.model.SysUser;
import com.mall.param.RoleParam;
import com.mall.service.IRoleService;
import com.mall.util.BeanValidator;
import com.mall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by 王乾 on 2018/1/27.
 */
@Service("iRoleService")
public class RoleServiceImpl implements IRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    /**
     * 添加角色
     * @param param
     */
    public void save(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole role = SysRole.builder().name(param.getName()).status(param.getStatus())
                .type(param.getType()).remark(param.getRemark()).build();
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRoleMapper.insertSelective(role);
    }

    /**
     * 更新角色
     * @param param
     */
    public void update(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的角色不存在");

        SysRole after = SysRole.builder().id(param.getId()).name(param.getName()).status(param.getStatus())
                .type(param.getType()).remark(param.getRemark()).build();
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRoleMapper.updateByPrimaryKeySelective(after);
    }

    /**
     * 获取所有的角色
     * @return
     */
    public List<SysRole> getAll(){
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name,Integer id){
        return sysRoleMapper.countByName(name,id) > 0;
    }

    /**
     * 通过userid 获取角色列表
     * @param userId
     * @return
     */
    public List<SysRole> getRoleListByUserId(int userId){
        List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        // 通过list取出对象
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }

    /**
     * 通过权限id获取角色列表
     * @param aclId
     * @return
     */
    public List<SysRole> getRoleListByAclId(int aclId){
        List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }

    /**
     * 根据角色列表获取用户列表
     * @param roleList
     * @return
     */
    public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
        if(CollectionUtils.isEmpty(roleList)){
            return Lists.newArrayList();
        }
        // role 只是取里面的id 组成一个list
        List<Integer> roleIdList = roleList.stream().map(role -> role.getId() ).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        // 这些角色都没有分配用户
        if (CollectionUtils.isEmpty( userIdList)) {
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }

}
