package com.mall.controller;

import com.google.common.collect.Maps;
import com.mall.beans.Mail;
import com.mall.beans.PageQuery;
import com.mall.common.JsonData;
import com.mall.model.SysRole;
import com.mall.param.AclParam;
import com.mall.service.IAclService;
import com.mall.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by 王乾 on 2018/1/19.
 */
@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Resource
    private IAclService iAclService;
    @Resource
    private IRoleService iRoleService;
    /**
     * 新增权限点
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam param){
        iAclService.save(param);
        return JsonData.success();

    }

    /**
     * 更新权限点
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam param){
        iAclService.update(param);
        return JsonData.success();
    }

    /**
     * 查询权限点列表
     * @param aclModuleId
     * @param page
     * @return
     */
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData list(@RequestParam("aclModuleId")Integer aclModuleId, PageQuery page){
        return JsonData.success(iAclService.getPageByAclModuleId(aclModuleId,page));
    }

    /**
     * 权限已经分配的用户和权限已分配的角色
     * @param aclId
     * @return
     */
    @RequestMapping("acls.json")
    @ResponseBody
    public JsonData acls(@RequestParam("aclId") int aclId){
        Map<String ,Object> map = Maps.newHashMap();
        List<SysRole> roleList = iRoleService.getRoleListByAclId(aclId);
        map.put("roles", roleList);
        map.put("acls", iRoleService.getUserListByRoleList(roleList));
        return JsonData.success(map);
    }
}
