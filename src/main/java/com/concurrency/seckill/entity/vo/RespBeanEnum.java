package com.concurrency.seckill.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 公共返回对象枚举
 */
@ToString
@AllArgsConstructor
@Getter
public enum RespBeanEnum {
    SUCCESS(200, "Success."),
    ERROR(600, "Server wrong.");

    private final Integer code;
    private final String message;
}
