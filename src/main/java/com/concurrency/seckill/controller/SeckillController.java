package com.concurrency.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.concurrency.seckill.entity.Order;
import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.vo.RespBeanEnum;
import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.service.IOrderService;
import com.concurrency.seckill.service.ISeckillOrderService;
import com.concurrency.seckill.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, Long goodsId) {
        User user = hostHolder.getUser();
        GoodsDTO goods = goodsService.findGoodsDTOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() <= 0) {
            model.addAttribute("errMsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "/seckillFailPage";
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errMsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "/seckillFailPage";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        model.addAttribute("user", user);
        return "/orderDetail";
    }
}
