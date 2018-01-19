package com.mall.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.internal.metadata.facets.Validatable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by 王乾 on 2018/1/19.
 */
@Getter
@Setter
@ToString
public class AclModuleParam {
    private Integer id;

    @NotBlank(message = "权限模板名称不可以为空")
    @Length(min = 2,max = 20,message = "权限模块的长度要在2-20个字之间")
    private String name;

    private Integer parentId = 0;

    private String level;

    @NotNull(message = "权限模块展示顺序不能为空")
    private Integer seq;

    @NotNull(message = "权限模块状态不能为空")
    @Min(value = 0,message = "权限模块状态不合法")
    @Max(value = 1,message = "权限模块状态不合法")
    private Integer status;

    @Length(max = 200,message = "权限模块的备注要在20个字以内")
    private String remark;


}
