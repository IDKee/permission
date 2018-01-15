package com.mall.service;

import com.mall.param.DeptParam;

/**
 * Created by 王乾 on 2018/1/13.
 */
public interface IDeptService {

     void save(DeptParam param);

     void update(DeptParam param);

     void delete(int deptId);

}
