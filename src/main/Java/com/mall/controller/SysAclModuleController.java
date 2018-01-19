package com.mall.controller;

import com.mall.common.JsonData;
import com.mall.param.AclModuleParam;
import com.mall.service.IAclModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by 王乾 on 2018/1/19.
 */
@Controller
@RequestMapping("/sys/aclModule")
public class SysAclModuleController {

    @Resource
    private IAclModuleService iAclModuleService;

    @RequestMapping("/acl.page")
    public ModelAndView page(){
        return new ModelAndView("acl");
    }

    /**
     * 增加权限
     * @param param
     * @return JsonData
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAclModule(AclModuleParam param){
        iAclModuleService.save(param);
        return  JsonData.success();
    }

    /**
     * 更新权限
     * @param param
     * @return JsonData
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAclModule(AclModuleParam param){
        iAclModuleService.update(param);
        return  JsonData.success();
    }

}
