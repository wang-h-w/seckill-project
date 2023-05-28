package com.concurrency.seckill.service;

import com.concurrency.seckill.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.concurrency.seckill.entity.vo.LoginVO;
import com.concurrency.seckill.entity.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-25
 */
public interface IUserService extends IService<User> {

    /**
     * 业务：登录
     * @param loginVO LoginVO (mobile + password)
     * @return Response
     */
    RespBean doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response);

    /**
     * 业务：根据Cookie值获取用户
     * @param userTicket User's cookie
     * @return User
     */
    User getUserByCookie(String userTicket);
}
