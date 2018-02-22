package com.mall.service.Impl;

import com.google.common.collect.Lists;
import com.mall.common.RequestHolder;
import com.mall.dao.SysAclMapper;
import com.mall.dao.SysRoleAclMapper;
import com.mall.dao.SysRoleUserMapper;
import com.mall.model.SysAcl;
import com.mall.service.ICoreService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 王乾 on 2018/1/30.
 */
@Service("iCoreService")
public class CoreServiceImpl implements ICoreService {

    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    /**
     * 获得当前用户的权限列表
     * @return
     */
    public List<SysAcl> getCurrentUserAclList(){
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    /**
     * 指定角色已经分配的权限点列表
     * @param roleId
     * @return
     */
    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> aclIdList   = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)) {
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }
    /**
     * 查询某个用户的已经分配的权限点（由于用户可能有多种角色身份所有可能是很多权限点的总和）
     * @param userId
     * @return
     */
    public List<SysAcl> getUserAclList(int userId){
        /**
         * 1. 如果是超级用户，返回所用的权限点
         * 2. 不是超级用户进行下面的操作
         * 3. 获取这个用户id下的所有为他分配的角色的id
         * 4. 根据3中获得角色的id列表，来获得角色对应的权限点的id
         * 5. 根据4获得的权限点的id列表查询权限点的对象列表
         */
        if(isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        //当前用户已经分配的角色id,每个用户可能有多种角色
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            //当前用户没有分配任何role
            return Lists.newArrayList();
        }
        //每个用户可能有多种角色，获取某个用户所有角色所对应的权限点id
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if(CollectionUtils.isEmpty(userAclIdList)){
            //角色的权限点取消掉没有了
            return Lists.newArrayList();
        }
        //根据id，查找权限点对象
        return sysAclMapper.getByIdList(userAclIdList);
    }

    /**
     * 是不是超级管理员
     * @return
     */
    public boolean isSuperAdmin(){
        return true;
    }

}
