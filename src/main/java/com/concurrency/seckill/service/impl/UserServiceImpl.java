package com.concurrency.seckill.service.impl;

import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.vo.LoginVO;
import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.entity.vo.RespBeanEnum;
import com.concurrency.seckill.exception.GlobalException;
import com.concurrency.seckill.mapper.UserMapper;
import com.concurrency.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.concurrency.seckill.utils.CookieUtil;
import com.concurrency.seckill.utils.Md5Util;
import com.concurrency.seckill.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public RespBean doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        User user = userMapper.selectById(mobile);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_USER_WRONG);
        }
        if (!Md5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_PASSWORD_WRONG);
        }

        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket, 24 * 60 * 60);

        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket) {
        if (StringUtils.isEmpty(userTicket)) return null;
        return (User) redisTemplate.opsForValue().get("user:" + userTicket);
    }
}
