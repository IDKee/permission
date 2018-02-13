package com.mall.service.Impl;

import com.google.common.collect.Lists;
import com.mall.dao.SysRoleUserMapper;
import com.mall.dao.SysUserMapper;
import com.mall.model.SysUser;
import com.mall.service.IRoleUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 王乾 on 2018/2/11.
 */
@Service("iRoleUserService")
public class RoleUserServiceImpl implements IRoleUserService {

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 选中的角色对应的用户的列表
     * @param roleId
     * @return
     */
    public List<SysUser> getListByRoleId(int roleId){
        // 获取当前点击角色的用户id
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        // 判断查询的用户id是不是空
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }

}
