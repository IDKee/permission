package com.mall.beans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * Created by 王乾 on 2018/1/16.
 */
public class PageQuery {

    @Getter
    @Setter
    @Min(value = 1,message = "当前页码不合法")
    private int pageNo = 1;

    @Getter
    @Setter
    @Min(value = 1,message = "每页暂时的数量不合法")
    private  int pageSize = 10;

    @Setter
    private  int offset;

    public int getOffset(){
        return (pageNo - 1) * pageSize;
    }

}
