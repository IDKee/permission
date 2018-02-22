package com.mall.controller;

import com.google.common.collect.Maps;
import com.mall.beans.PageQuery;
import com.mall.beans.PageResult;
import com.mall.common.JsonData;
import com.mall.model.SysUser;
import com.mall.param.UserParam;
import com.mall.service.IRoleService;
import com.mall.service.ITreeService;
import com.mall.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 王乾 on 2018/1/13.
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private IUserService iUserService;
    @Resource
    private ITreeService iTreeService;
    @Resource
    private IRoleService iRoleService;
    /**
     * 增加用户
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveUser(UserParam param){
        iUserService.save(param);
        return  JsonData.success();
    }

    /**
     * 更新用户
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateUser(UserParam param){
        iUserService.update(param);
        return  JsonData.success();
    }

    /**
     * 用户分页
     * @param deptId
     * @param pageQuery
     * @return
     */
    @RequestMapping("page.json")
    @ResponseBody
    public JsonData page(@RequestParam("deptId") int deptId, PageQuery pageQuery){
        PageResult<SysUser> result = iUserService.getPageByDeptId(deptId, pageQuery);
        return  JsonData.success(result);
    }

    /**
     * 此用户的权限，点击小红旗调用的接口
     * @return
     */
    @RequestMapping("acls.json")
    @ResponseBody
    public JsonData acls(@RequestParam("userId") int userId){
        Map<String ,Object> map = Maps.newHashMap();
        map.put("acls",iTreeService.userAclTree(userId));
        map.put("roles",iRoleService.getRoleListByUserId(userId));
        return JsonData.success(map);
    }


}
