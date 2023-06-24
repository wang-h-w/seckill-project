package com.concurrency.seckill.controller;


import com.concurrency.seckill.entity.vo.OrderDetailVO;
import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean getDetailWithStaticCache(Long orderId) {
        OrderDetailVO orderDetailVO = orderService.getOrderDetail(orderId);
        return RespBean.success(orderDetailVO);
    }
}
