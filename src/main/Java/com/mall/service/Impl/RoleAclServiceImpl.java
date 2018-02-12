package com.mall.service.Impl;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.RequestHolder;
import com.mall.dao.SysRoleAclMapper;
import com.mall.model.SysRoleAcl;
import com.mall.service.IRoleAclService;
import com.mall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by 王乾 on 2018/2/11.
 *
 */
@Service("iRoleAclService")
public class RoleAclServiceImpl implements IRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    /**
     * 更改角色附有的权限
     * @param roleId
     * @param aclIdList
     */
    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList){
        // 获取当且角色的权限
        List<Integer> originAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        // 判断点击保存后是不是和数据库的一样，防止点击提交重复更改
        if( originAclIdList.size() == aclIdList.size() ){
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdList)){
                return;
            }
        }
        updateRoleAcls(roleId,aclIdList);
    }

    /**
     * 删除旧的权限，然后新增权限
     * 要么都成功，要么都不成功
     * @param roleId
     * @param aclIdList
     */
    @Transactional
    private void updateRoleAcls(int roleId, List<Integer> aclIdList){
        // 首先把原本的权限删除掉
        sysRoleAclMapper.deleteByRoleId(roleId);
        // 传入新的权限集合空的
        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }
        // 遍历每一个权限id，构造roleAcl的对象，进行批量插入
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for (Integer aclId : aclIdList){
            SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(aclId)
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        //批量插入
        sysRoleAclMapper.batchInsert(roleAclList);
    }

}
