package com.mall.filter;

import com.mall.common.Const;
import com.mall.common.RequestHolder;
import com.mall.model.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 王乾 on 2018/1/18.
 */
@Slf4j
public class LoginFilter implements Filter {
    public void destroy() {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;
        String servletPath = req.getServletPath();

        SysUser sysUser = (SysUser)req.getSession().getAttribute(Const.CURRENT_USER);
        if(sysUser == null){
            //必须加上/,如果不加上/可能不是访问根路径下的signin.jsp
            String path = "/signin.jsp";
            resp.sendRedirect(path);
            return;
        }
        //存到RequestHolder里面，高并发处理
        RequestHolder.add(sysUser);
        RequestHolder.add(req);
        filterChain.doFilter(servletRequest,servletResponse);
        return;
    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
