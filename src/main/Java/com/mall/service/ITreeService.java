package com.mall.service;

import com.mall.dto.AclModuleLevelDto;
import com.mall.dto.DeptLevelDto;

import java.util.List;

/**
 * Created by 王乾 on 2018/1/14.
 */
public interface ITreeService {

    List<DeptLevelDto> deptTree();

    List<AclModuleLevelDto> aclModuleTree();

}
