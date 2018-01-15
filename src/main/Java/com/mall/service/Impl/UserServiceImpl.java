package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.mall.dao.SysUserMapper;
import com.mall.exception.ParamException;
import com.mall.model.SysUser;
import com.mall.param.UserParam;
import com.mall.service.IUserService;
import com.mall.util.BeanValidator;
import com.mall.util.MD5Util;
import com.mall.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by 王乾 on 2018/1/13.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Resource
    private SysUserMapper sysUserMapper;



    public SysUser findByKeyword(String keyword){
        return sysUserMapper.findByKeyword(keyword);
    }

    /**
     * 增加用户
     * @param param
     */
    public void save(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw  new ParamException("电话已被占用");
        }
        if (checkEmailExist(param.getMail(),param.getId())){
            throw  new ParamException("邮箱已被占用");
        }
        String password = PasswordUtil.randomPassword();
        password = "123456";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                            .mail(param.getMail()).password(encryptedPassword).deptId(param.getDeptId())
                            .status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator("system");
        user.setOperateIp("127.0.0.1");
        user.setOperateTime(new Date());

        // TODO sendEmail

        sysUserMapper.insertSelective(user);
    }

    /**
     * 更新用户
     * @param param
     */
    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw  new ParamException("电话已被占用");
        }
        if (checkEmailExist(param.getMail(),param.getId())){
            throw  new ParamException("邮箱已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).deptId(param.getDeptId())
                .status(param.getStatus()).remark(param.getRemark()).build();
        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId) > 0;
    }

    private boolean checkTelephoneExist(String phone,Integer userId){
        return sysUserMapper.countByTelephone(phone,userId) > 0;
    }

}
