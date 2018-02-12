package com.mall.service;

import java.util.List;

/**
 * Created by 王乾 on 2018/2/11.
 */
public interface IRoleAclService {

    void changeRoleAcls(Integer roleId, List<Integer> aclIdList);

}
