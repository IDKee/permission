package com.mall.controller;

import com.mall.common.JsonData;
import com.mall.exception.PermissionException;
import com.mall.param.TestVo;
import com.mall.util.BeanValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by 王乾 on 2017/12/30.
 */

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("hello");
        throw new PermissionException("111");
     //   return JsonData.success("hello, permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate (TestVo vo){
        log.info("validate");
        Map<String ,String> map = BeanValidator.validateObject(vo);
        if(map != null && map.entrySet().size()>0){
            for (Map.Entry<String,String> entry:map.entrySet()){
                log.info("{}->{}",entry.getKey(),entry.getValue());
            }

        }
        return JsonData.success("hello, permission");
    }
}
