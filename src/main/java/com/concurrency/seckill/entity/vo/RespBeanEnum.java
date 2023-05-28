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
    LOGIN_PASSWORD_WRONG(500212, "密码错误");


    private final Integer code;
    private final String message;
}
