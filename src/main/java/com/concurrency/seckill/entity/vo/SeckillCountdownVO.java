package com.concurrency.seckill.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SeckillCountdownVO {

    @NotNull
    private int seckillStatus; // 0: 未开始; 1: 秒杀中; 2: 已结束

    @NotNull
    private int remainSeconds; // -1: 已结束
}
