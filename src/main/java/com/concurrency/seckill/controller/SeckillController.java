package com.concurrency.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.concurrency.seckill.entity.Order;
import com.concurrency.seckill.entity.SeckillOrder;
import com.concurrency.seckill.entity.User;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.mq.SeckillMessage;
import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.entity.vo.RespBeanEnum;
import com.concurrency.seckill.entity.vo.SeckillStatus;
import com.concurrency.seckill.exception.GlobalException;
import com.concurrency.seckill.mq.MQProducer;
import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.service.IOrderService;
import com.concurrency.seckill.service.ISeckillOrderService;
import com.concurrency.seckill.utils.HostHolder;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

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
    @Autowired
    private MQProducer producer;

    /**
     * Linux优化前压测：198
     * Linux优化后压测：286
     * Linux预减库存后压测：594
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

    //@RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    //@ResponseBody
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

    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckillWithRedisPreSub(@PathVariable String path, Long goodsId) {
        User user = hostHolder.getUser();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 校验秒杀地址是否合法
        boolean checkResult = orderService.checkPath(user, goodsId, path);
        if (!checkResult) return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        // 判断是否重复抢购
        // 存在问题：当后续逻辑还没处理完时，来自同一用户的其他请求还能通过这个校验，导致一个用户可能抢购多次
        // 这个问题带来的更深入的问题：同一用户的抢购在SQL层会被排除，但在这里却能预减库存成功，导致Redis库存为0时SQL可能还有剩余库存
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + goodsId + ":" + user.getId());
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        // 预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId); // 这里必须直接减，不能先查然后判断，否则无法保证原子性
        if (stock != null && stock < 0) {
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // RabbitMQ
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        producer.sendSeckillMessage(JSON.toJSONString(seckillMessage));
        return RespBean.success(SeckillStatus.WAITING.getStatus());
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(Long goodsId) {
        User user = hostHolder.getUser();
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(Long goodsId, String captcha) {
        User user = hostHolder.getUser();
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        String uuid = orderService.createPath(user, goodsId);
        return RespBean.success(uuid);
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void captcha(Long goodsId, HttpServletResponse response) {
        try {
            if (goodsId < 0) throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
            User user = hostHolder.getUser();
            response.setContentType("image/png");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            SpecCaptcha captcha = new SpecCaptcha(130, 32, 4);
            redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败: " + e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 但这样会导致，每次秒杀活动前都必须重启服务
        List<GoodsDTO> list = goodsService.findGoodsDTO();
        if (CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsDTO -> redisTemplate.opsForValue().set("seckillGoods:" + goodsDTO.getId(), goodsDTO.getStockCount()));
    }
}
