package com.mall.dto;

import com.mall.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * Created by 王乾 on 2018/1/30.
 */
@Setter
@Getter
@ToString
public class AclDto {

    private boolean checked = false; //权限点是否选中

    private boolean hasAcl = false; //有没有权限操作

    private static AclDto adapt(SysAcl acl){
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(acl, dto); //相比acl多了checked和hasAcl两个属性
        return dto;
    }
}
