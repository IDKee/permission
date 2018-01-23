package com.mall.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by 王乾 on 2018/1/23.
 * 
 */
@Getter
@Setter
@ToString
public class AclParam {
    private Integer id;
    @NotBlank(message = "权限点名称不可以为空")
    @Length(min = 2,max = 20,message = "权限点名称长度必须在2-20个字之间")
    private String name;

    @NotNull(message = "必须指定权限模块")
    private Integer aclModuleId;

    @Length(min = 2,max = 100,message = "权限点URL长度必须在6-100个字符之间")
    private String  url;

    @NotNull(message = "必须指定权限点的类型")
    @Min(value = 1,message = "权限点类型不合法")
    @Max(value = 3,message = "权限点类型不合法")
    private Integer type;

    @NotNull(message = "必须指定权限点的状态")
    @Min(value = 0,message = "权限点的状态不合法")
    @Max(value = 1,message = "权限点的状态不合法")
    private Integer status;

    @NotNull(message = "必须指定权限点的展示序列")
    private Integer seq;

    @Length(min = 0,max = 200,message = "备注长度必须在6-200个字符之间")
    private String remark;

}
