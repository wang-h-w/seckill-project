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

    // 通用
    SUCCESS(200, "Success."),
    ERROR(500, "Server wrong."),

    // 登录模块
    BIND_ERROR(500210, "参数校验异常"),
    LOGIN_USER_WRONG(500211, "该手机号码未注册"),
    LOGIN_PASSWORD_WRONG(500212, "密码错误"),

    // 秒杀模块
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "秒杀商品每位用户限购一件"),
    REQUEST_ILLEGAL(500502, "请求非法"),
    ERROR_CAPTCHA(500503, "验证码错误"),

    // 订单模块
    ORDER_NOT_EXIST(500300, "订单不存在");


    private final Integer code;
    private final String message;
}
