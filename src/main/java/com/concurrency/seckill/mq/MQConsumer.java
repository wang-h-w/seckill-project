package com.concurrency.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.concurrency.seckill.entity.Order;
import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.mq.SeckillMessage;
import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrderService orderService;

    /**
     * 下单
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("RabbitMQ接收消息: " + message);
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        GoodsDTO goods = goodsService.findGoodsDTOByGoodsId(goodsId);
        // 通过Redis那一关，现在要操作数据库了，必须再通过数据库进行验证
        if (goods.getStockCount() <= 0) return;
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + goodsId + ":" + user.getId());
        if (seckillOrder != null) return;
        orderService.seckill(user, goods);
    }
}
