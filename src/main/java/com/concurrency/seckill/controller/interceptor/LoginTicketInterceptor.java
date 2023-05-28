package com.concurrency.seckill.controller.interceptor;

import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.service.IUserService;
import com.concurrency.seckill.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(ticket)) {
            response.sendRedirect(request.getContextPath() + "/login/toLogin");
            return false;
        }
        User user = userService.getUserByCookie(ticket);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login/toLogin");
            return false;
        }
        return true;
    }
}
