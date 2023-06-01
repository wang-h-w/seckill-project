package com.concurrency.seckill.service;

import com.concurrency.seckill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
public interface IOrderService extends IService<Order> {

    /**
     * 业务：秒杀
     * @param user User
     * @param goods Goods
     * @return Order
     */
    Order seckill(User user, GoodsDTO goods);
}
