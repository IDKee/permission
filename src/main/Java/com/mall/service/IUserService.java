package com.mall.service;

import com.mall.model.SysUser;
import com.mall.param.UserParam;

/**
 * Created by 王乾 on 2018/1/13.
 */
public interface IUserService {

    void update(UserParam param);

    void save(UserParam param);

    SysUser findByKeyword(String keyword);
}
