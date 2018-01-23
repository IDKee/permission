package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.mall.common.RequestHolder;
import com.mall.dao.SysAclMapper;
import com.mall.exception.ParamException;
import com.mall.model.SysAcl;
import com.mall.param.AclParam;
import com.mall.service.IAclService;
import com.mall.util.BeanValidator;
import com.mall.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 王乾 on 2018/1/23.
 *
 */
@Service("iAclService")
public class AclServiceImpl implements IAclService {
    @Resource
    private SysAclMapper sysAclMapper;

    /**
     * 增加权限点
     * @param param
     */
    public void save(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下存在相同的名称模块点");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId())
                .url(param.getUrl()).type(param.getType()).status(param.getStatus())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        acl.setCode(generateCode());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateTime(new Date());
        sysAclMapper.insertSelective(acl);
    }

    /**
     * 更新权限点
     * @param param
     *
     */
    public void update(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下存在相同的名称模块点");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限点不存在");

        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId())
                .url(param.getUrl()).type(param.getType()).status(param.getStatus())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());

        sysAclMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(int aclModuleId,String name,Integer id){
        return false;
    }

    /**
     * 生成code值
     * @return
     */
    private String generateCode(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()) + "_" + (int)(Math.random()*100);
    }
}
