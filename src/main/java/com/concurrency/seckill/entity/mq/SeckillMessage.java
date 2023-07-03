package com.concurrency.seckill.entity.mq;

import com.concurrency.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {

    private User user;

    private Long goodsId;

}
