package com.concurrency.seckill.mapper;

import com.concurrency.seckill.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
