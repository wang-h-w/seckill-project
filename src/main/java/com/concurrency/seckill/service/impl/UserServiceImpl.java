package com.concurrency.seckill.service.impl;

import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.mapper.UserMapper;
import com.concurrency.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
