package com.mall.service;

import com.mall.model.SysRole;
import com.mall.model.SysUser;
import com.mall.param.RoleParam;

import java.util.List;

/**
 * Created by 王乾 on 2018/1/27.
 */
public interface IRoleService {

    void save(RoleParam param);

    void update(RoleParam param);

    List<SysRole> getAll();

    List<SysRole> getRoleListByUserId(int userId);

    List<SysRole> getRoleListByAclId(int aclId);

    List<SysUser> getUserListByRoleList(List<SysRole> roleList);
}
