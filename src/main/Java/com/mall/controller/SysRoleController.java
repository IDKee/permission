package com.mall.controller;

import com.mall.common.JsonData;
import com.mall.param.RoleParam;
import com.mall.service.IRoleService;
import com.mall.service.ITreeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by 王乾 on 2018/1/27.
 */
@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private IRoleService iRoleService;
    @Resource
    private ITreeService iTreeService;

    @RequestMapping("/role.page")
    @ResponseBody
    public ModelAndView page(){
        return new ModelAndView("role");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveRole(RoleParam param){
        iRoleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(RoleParam param){
        iRoleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData list(){
        return JsonData.success(iRoleService.getAll());
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(@RequestParam("roleId") int roleId){
        return JsonData.success(iTreeService.roleTree(roleId));
    }



}
