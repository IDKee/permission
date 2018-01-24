package com.mall.service;

import com.mall.beans.PageQuery;
import com.mall.beans.PageResult;
import com.mall.model.SysAcl;
import com.mall.param.AclParam;

/**
 * Created by 王乾 on 2018/1/23.
 */
public interface IAclService {
    void save(AclParam param);

    void update(AclParam param);

    PageResult<SysAcl> getPageByAclModuleId(Integer aclModuleId, PageQuery page);
}
