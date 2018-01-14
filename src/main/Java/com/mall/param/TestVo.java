package com.mall.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by 王乾 on 2017/12/31.
 */
@Getter
@Setter
public class TestVo {

    @NotBlank
    private  String msg;

    @NotNull
    private Integer id;
}
