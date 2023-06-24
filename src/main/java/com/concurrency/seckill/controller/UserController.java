package com.concurrency.seckill.controller;


import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Haowen
 * @since 2023-05-25
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info() {
        return RespBean.success(hostHolder.getUser());
    }
}
