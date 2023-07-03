package com.concurrency.seckill.service;

import com.concurrency.seckill.entity.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.concurrency.seckill.entity.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 查询秒杀结果
     * @param user User
     * @param goodsId Good's ID
     * @return orderId-成功; -1-秒杀失败; 0-秒杀进行中
     */
    Long getResult(User user, Long goodsId);
}
