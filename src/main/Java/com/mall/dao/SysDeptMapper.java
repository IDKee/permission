package com.mall.dao;

import com.mall.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> getAllDept();

    List<SysDept> getChildDeptListByLevel(@Param("level")String level);
    //批量更新level
    void batchUpdateLevel(@Param("sysDeptList")List<SysDept> sysDeptList);

    int countByNameAndParentId(@Param("parentId")Integer parentId,@Param("name") String name,@Param("id") Integer id);

    int countByParentId(Integer deptId);


}