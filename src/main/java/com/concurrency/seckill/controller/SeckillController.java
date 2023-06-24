package com.concurrency.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.concurrency.seckill.entity.Order;
import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.entity.vo.RespBeanEnum;
import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.service.IOrderService;
import com.concurrency.seckill.service.ISeckillOrderService;
import com.concurrency.seckill.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Linux优化前压测：198
     * Linux优化后压测：286
     */
    // @RequestMapping("/doSeckill")
    public String doSeckill(Model model, Long goodsId) {
        User user = hostHolder.getUser();
        GoodsDTO goods = goodsService.findGoodsDTOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() <= 0) {
            model.addAttribute("errMsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFailPage";
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errMsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "seckillFailPage";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        model.addAttribute("user", user);
        return "orderDetail";
    }

    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckillWithStaticCache(Long goodsId) {
        User user = hostHolder.getUser();
        GoodsDTO goods = goodsService.findGoodsDTOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() <= 0) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 判断是否重复抢购
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + goodsId + ":" + user.getId());
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        Order order = orderService.seckill(user, goods);
        return RespBean.success(order);
    }
}
