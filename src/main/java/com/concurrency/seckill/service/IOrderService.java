package com.concurrency.seckill.service;

import com.concurrency.seckill.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.vo.OrderDetailVO;

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

    /**
     * 业务：返回订单页面详情
     * @param orderId Order's id
     * @return Order detail view object
     */
    OrderDetailVO getOrderDetail(Long orderId);
}
