package com.concurrency.seckill.service.impl;

import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.vo.SeckillStatus;
import com.concurrency.seckill.mapper.SeckillOrderMapper;
import com.concurrency.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + goodsId + ":" + user.getId());
        if (seckillOrder != null) return seckillOrder.getOrderId();
        else if (Boolean.TRUE.equals(redisTemplate.hasKey("isStockEmpty:" + goodsId))) return SeckillStatus.FAILED.getStatus();
        else return SeckillStatus.WAITING.getStatus();
    }
}
