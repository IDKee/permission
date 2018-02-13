package com.mall.service;

import com.mall.model.SysUser;

import java.util.List;

/**
 * Created by 王乾 on 2018/2/11.
 */
public interface IRoleUserService {

    List<SysUser> getListByRoleId(int roleId);

}
