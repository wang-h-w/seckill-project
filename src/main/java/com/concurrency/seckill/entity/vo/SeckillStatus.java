package com.concurrency.seckill.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum SeckillStatus {

    WAITING(0L), // 秒杀进行中

    FAILED(-1L); // 秒杀失败

    private final Long status;
}
