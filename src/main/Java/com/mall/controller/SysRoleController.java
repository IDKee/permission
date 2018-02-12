package com.mall.controller;

import com.mall.common.JsonData;
import com.mall.param.RoleParam;
import com.mall.service.IRoleAclService;
import com.mall.service.IRoleService;
import com.mall.service.ITreeService;
import com.mall.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private IRoleAclService iRoleAclService;

    @RequestMapping("/role.page")
    @ResponseBody
    public ModelAndView page(){
        return new ModelAndView("role");
    }

    /**
     * 保存角色
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveRole(RoleParam param){
        iRoleService.save(param);
        return JsonData.success();
    }

    /**
     * 更新角色
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(RoleParam param){
        iRoleService.update(param);
        return JsonData.success();
    }

    /**
     * 得到 所有的角色
     * @return
     */
    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData list(){
        return JsonData.success(iRoleService.getAll());
    }

    /**
     * 获取当前点击角色下的权限树
     * @param roleId
     * @return
     */
    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(@RequestParam("roleId") int roleId){
        return JsonData.success(iTreeService.roleTree(roleId));
    }

    /**
     * 角色权限，点击保存按钮
     * @param roleId
     * @param aclIds 不是必填的，默认值是空字符串
     * @return
     */
    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam("roleId") int roleId, @RequestParam(value = "aclIds", required = false, defaultValue = "") String aclIds){
        // 利用工具类把字符串转化成list
        List<Integer> aclIdList = StringUtil.splitToListInt(aclIds);
        iRoleAclService.changeRoleAcls(roleId,aclIdList);
        return JsonData.success();
    }





}
