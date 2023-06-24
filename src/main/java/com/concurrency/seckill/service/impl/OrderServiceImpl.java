package com.concurrency.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.concurrency.seckill.entity.Order;
import com.concurrency.seckill.entity.SeckillGoods;
import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.vo.OrderDetailVO;
import com.concurrency.seckill.entity.vo.RespBeanEnum;
import com.concurrency.seckill.exception.GlobalException;
import com.concurrency.seckill.mapper.OrderMapper;
import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.concurrency.seckill.service.ISeckillGoodsService;
import com.concurrency.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsDTO goods) {
        // 先减秒杀库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        boolean updateResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1")
                .eq("goods_id", seckillGoods.getGoodsId()).gt("stock_count", 0));
        if (!updateResult) return null;
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单（通过用户ID+商品ID的索引进行去重，如果有同一用户的重复订单则无法入数据表，本方法事务回滚）
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + goods.getId() + ":" + user.getId(), seckillOrder); // 方便controller中判断是否重复抢购
        return order;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        if (orderId == null) throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        orderDetailVO.setOrder(order);
        orderDetailVO.setGoodsDTO(goodsService.findGoodsDTOByGoodsId(order.getGoodsId()));
        return orderDetailVO;
    }
}
