package com.mall.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mall.common.JsonData;
import com.mall.model.SysUser;
import com.mall.param.RoleParam;
import com.mall.service.*;
import com.mall.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Resource
    private IRoleUserService iRoleUserService;
    @Resource
    private IUserService iUserService;

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

    /**
     * 点击角色后，获取当前角色的 选中和未选中的用户对象
     * @param roleId
     * @return
     */
    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData users(@RequestParam("roleId") int roleId){
        // 得到已选的用户列表，当前选中角色的所用的用户列表
        List<SysUser> selectedUserList = iRoleUserService.getListByRoleId(roleId);
        // 获取所有的用户
        List<SysUser> allUserList = iUserService.getAll();
        // 列表的操作 获取未选中的用户列表
        List<SysUser> unselectUserList = Lists.newArrayList();
        // JDK1.8 对当前选中角色的所用用户对象进行流式遍历，进行map的操作，map的值为取出list中sysUser的id
        Set<Integer> selectedUserIdList = selectedUserList.stream().map(sysUser ->sysUser.getId() ).collect(Collectors.toSet());
        // 这样对List遍历特别慢，需要用到上面的jdk1.8进行加速
//        for (SysUser sysUser : allUserList){
//            //  用户的状态为1，并且没有被选中
//            if (sysUser.getStatus() == 1 && !selectedUserList.contains(sysUser)){
//                unselectUserList.add(sysUser);
//            }
//        }
        //加速后
        for (SysUser sysUser : allUserList){
            //  用户的状态为1，并且没有被选中
            if (sysUser.getStatus() == 1 && !selectedUserIdList.contains(sysUser.getId())){
                unselectUserList.add(sysUser);
            }
        }
        // JDK1.8 对当前选中角色的所用用户对象进行流式遍历，进行filter的操作，把sysUser的状态码不为1的过滤掉
        //selectedUserList = selectedUserList.stream().filter(sysUser -> sysUser.getStatus() !=1 ).collect(Collectors.toList());
        // 把已选的未选的通过一个接口返回出去
        Map<String,List<SysUser>> map = Maps.newHashMap();
        map.put("selected", selectedUserList);
        map.put("unselected",unselectUserList);
        return JsonData.success();
    }







}
