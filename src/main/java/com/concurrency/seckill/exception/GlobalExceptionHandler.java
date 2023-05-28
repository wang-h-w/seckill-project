package com.concurrency.seckill.exception;

import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.entity.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean handleException(Exception e) {
        if (e instanceof GlobalException ex) {
            return RespBean.error(ex.getRespBeanEnum());
        } else if (e instanceof BindException exception) {
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常: " + exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
