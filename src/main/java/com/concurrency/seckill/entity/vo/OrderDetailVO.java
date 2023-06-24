package com.concurrency.seckill.entity.vo;

import com.concurrency.seckill.entity.Order;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private GoodsDTO goodsDTO;
    private Order order;
}
