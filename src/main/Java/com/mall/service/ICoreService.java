package com.mall.service;

import com.mall.model.SysAcl;

import java.util.List;

/**
 * Created by 王乾 on 2018/1/30.
 */
public interface ICoreService {

    List<SysAcl> getCurrentUserAclList();


    List<SysAcl> getRoleAclList(int roleId);


     List<SysAcl> getUserAclList(int userId);
}

