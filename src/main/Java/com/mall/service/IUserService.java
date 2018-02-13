package com.mall.service;

import com.mall.beans.PageQuery;
import com.mall.beans.PageResult;
import com.mall.model.SysUser;
import com.mall.param.UserParam;

import java.util.List;

/**
 * Created by 王乾 on 2018/1/13.
 */
public interface IUserService {

    void update(UserParam param);

    void save(UserParam param);

    SysUser findByKeyword(String keyword);

    PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page);

    List<SysUser> getAll();
}
