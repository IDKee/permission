package com.mall.service.Impl;

import com.mall.dao.SysUserMapper;
import com.mall.exception.ParamException;
import com.mall.model.SysUser;
import com.mall.param.UserParam;
import com.mall.service.IUserService;
import com.mall.util.BeanValidator;
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
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                            .mail(param.getMail()).password(password).deptId(param.getDeptId())
                            .status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator("system");
        user.setOperateIp("127.0.0.1");
        user.setOperateTime(new Date());

        // TODO sendEmail

        sysUserMapper.insertSelective(user);


    }

    public boolean checkEmailExist(String mail,Integer userId){
        return false;
    }

    public boolean checkTelephoneExist(String phone,Integer userId){
        return false;
    }

}
