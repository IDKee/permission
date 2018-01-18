package com.mall.controller;

import com.mall.common.Const;
import com.mall.model.SysUser;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 王乾 on 2018/1/15.
 */
@Controller
public class UserController {

    @Resource
    private IUserService iUserService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        String path = "signin.jsp";
        response.sendRedirect(path);
    }

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        SysUser sysUser = iUserService.findByKeyword(username);
        String errorMsg = "";
        String ret = request.getParameter("ret");

        if(StringUtils.isBlank(username)){
            errorMsg = "用户名不可为空";
        }else if(StringUtils.isBlank(password)){
            errorMsg = "密码不可为空";
        }else if(sysUser == null){
            errorMsg = "查询不到指定用户";
        }else if(!sysUser.getPassword().equals(MD5Util.encrypt(password))){
            errorMsg = "用户名或者密码错误";
        }else if(sysUser.getStatus() != 1){
            errorMsg = "用户已被冻结，请联系管理员";
        }else{
            request.getSession().setAttribute(Const.CURRENT_USER,sysUser);
            if(StringUtils.isNotBlank(ret)){
                response.sendRedirect(ret);
                return;
            }else {
                response.sendRedirect("/admin/index.page");
                return;
            }
        }

        request.setAttribute(Const.ERROR,errorMsg);
        request.setAttribute(Const.USERNAME,username);
        if(StringUtils.isNotBlank(ret)){
            request.setAttribute(Const.RET,ret);
        }
        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request,response);

    }

}
