package com.concurrency.seckill.service.impl;

import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.mapper.SeckillOrderMapper;
import com.concurrency.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
