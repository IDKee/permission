package com.mall.dao;

import com.mall.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    List<SysAclModule> getChildAclModuleListByLevel(@Param("level")String level);
    //批量更新level
    void batchUpdateLevel(@Param("sysAclModuleList")List<SysAclModule> sysDeptList);

    int countByNameAndParentId(@Param("parentId")Integer parentId,@Param("name") String name,@Param("id") Integer id);;
}