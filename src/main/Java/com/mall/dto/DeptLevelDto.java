package com.mall.dto;

import com.google.common.collect.Lists;
import com.mall.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by 王乾 on 2018/1/1.
 * 部门层级的结果
 */
@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept{

    /**
     * 相比SysDept 多了一个List<DeptLevelDto>
     */
    private List<DeptLevelDto> deptList = Lists.newArrayList();

    /**
     * 传入部门的对象,返回拷贝后的dto对象
     * @param dept
     * @return
     */
    public static  DeptLevelDto adapt(SysDept dept){
        DeptLevelDto dto = new DeptLevelDto();
        BeanUtils.copyProperties(dept,dto);
        return dto;
    }
}
