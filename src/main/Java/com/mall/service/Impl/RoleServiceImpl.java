package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.mall.common.RequestHolder;
import com.mall.dao.SysRoleMapper;
import com.mall.dto.AclModuleLevelDto;
import com.mall.exception.ParamException;
import com.mall.model.SysRole;
import com.mall.param.RoleParam;
import com.mall.service.IRoleService;
import com.mall.util.BeanValidator;
import com.mall.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by 王乾 on 2018/1/27.
 */
@Service("iRoleService")
public class RoleServiceImpl implements IRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;


    /**
     * 添加角色
     * @param param
     */
    public void save(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole role = SysRole.builder().name(param.getName()).status(param.getStatus())
                .type(param.getType()).remark(param.getRemark()).build();
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRoleMapper.insertSelective(role);
    }

    /**
     * 更新角色
     * @param param
     */
    public void update(RoleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getName(),param.getId())){
            throw new ParamException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的角色不存在");

        SysRole after = SysRole.builder().id(param.getId()).name(param.getName()).status(param.getStatus())
                .type(param.getType()).remark(param.getRemark()).build();
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysRoleMapper.updateByPrimaryKeySelective(after);
    }

    /**
     * 获取所有的角色
     * @return
     */
    public List<SysRole> getAll(){
        return sysRoleMapper.getAll();
    }

    private boolean checkExist(String name,Integer id){
        return sysRoleMapper.countByName(name,id) > 0;
    }
}
