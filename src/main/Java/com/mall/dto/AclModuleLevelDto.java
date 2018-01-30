package com.mall.dto;

import com.google.common.collect.Lists;
import com.mall.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by 王乾 on 2018/1/19.
 * 权限的展示对象
 */
@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule {

    private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();

    private List<AclDto> aclList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule aclModule){
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule,dto); //相比SysAclModule多了List<AclModuleLevelDto>，和List<AclDto>
        return dto;
    }

}
