package com.concurrency.seckill.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     * @param message 秒杀信息
     */
    public void sendSeckillMessage(String message) {
        log.info("RabbitMQ发送消息: " + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }
}
