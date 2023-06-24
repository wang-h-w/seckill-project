package com.concurrency.seckill.entity.vo;

import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillDetailVO {
    private User user;
    private GoodsDTO goodsDTO;
    private SeckillCountdownVO seckillCountdownVO;
}
