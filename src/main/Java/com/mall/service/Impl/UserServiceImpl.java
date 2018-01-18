package com.mall.service.Impl;

import com.google.common.base.Preconditions;
import com.mall.beans.PageQuery;
import com.mall.beans.PageResult;
import com.mall.common.RequestHolder;
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
import java.util.List;

/**
 * Created by 王乾 on 2018/1/13.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Resource
    private SysUserMapper sysUserMapper;


    /**
     * 查询部门下面得人员列表
     * @param deptId
     * @param page
     * @return
     */
    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page){
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);
        //此部门id下有人
        if ( count > 0){
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId,page);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }


    /**
     * 跟据手机或邮箱进行查询
     * @param keyword
     * @return
     */
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
//        user.setOperator("system");
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
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
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp("127.0.0.1");
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId) > 0;
    }

    private boolean checkTelephoneExist(String phone,Integer userId){
        return sysUserMapper.countByTelephone(phone,userId) > 0;
    }

}
