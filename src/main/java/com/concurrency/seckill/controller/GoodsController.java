package com.concurrency.seckill.controller;

import com.concurrency.seckill.entity.vo.SeckillDetailVO;
import com.concurrency.seckill.entity.vo.RespBean;
import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * Linux优化前压测：838
     * Linux优化后压测：914
     */
    // @RequestMapping("/toList")
    public String toList(Model model) {
        model.addAttribute("user", hostHolder.getUser());
        model.addAttribute("goodsList", goodsService.findGoodsDTO());
        return "goodsList";
    }

    @RequestMapping(value = "/toList", produces = "text/html")
    @ResponseBody
    public String toList(Model model, HttpServletRequest request, HttpServletResponse response) {
        String html = (String) redisTemplate.opsForValue().get("view:goodsList");
        if (!StringUtils.isEmpty(html)) return html;
        model.addAttribute("user", hostHolder.getUser());
        model.addAttribute("goodsList", goodsService.findGoodsDTO());
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html)) redisTemplate.opsForValue().set("view:goodsList", html, 60, TimeUnit.SECONDS);
        return html;
    }

    // @RequestMapping("/toDetail/{goodsId}")
    public String toDetailWithoutCache(Model model, @PathVariable("goodsId") long id) {
        model.addAttribute("user", hostHolder.getUser());
        model.addAttribute("goods", goodsService.findGoodsDTOByGoodsId(id));
        model.addAttribute("countdown", goodsService.findCountdownVOByGoodsId(id));
        return "goodsDetail";
    }

    // @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html")
    // @ResponseBody
    public String toDetailWithRedisCache(Model model, @PathVariable("goodsId") long id, HttpServletRequest request, HttpServletResponse response) {
        // 我觉得秒杀页面不能加入Redis缓存，否则刷新秒杀页面后倒计时不准确
        String html = (String) redisTemplate.opsForValue().get("view:goodsDetail:" + id);
        if (!StringUtils.isEmpty(html)) return html;
        model.addAttribute("user", hostHolder.getUser());
        model.addAttribute("goods", goodsService.findGoodsDTOByGoodsId(id));
        model.addAttribute("countdown", goodsService.findCountdownVOByGoodsId(id));
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
        if (!StringUtils.isEmpty(html)) redisTemplate.opsForValue().set("view:goodsDetail:" + id, html, 60, TimeUnit.SECONDS);
        return html;
    }

    @RequestMapping("/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetailWithStaticCache(@PathVariable("goodsId") long id) {
        SeckillDetailVO seckillDetailVO = new SeckillDetailVO();
        seckillDetailVO.setUser(hostHolder.getUser());
        seckillDetailVO.setGoodsDTO(goodsService.findGoodsDTOByGoodsId(id));
        seckillDetailVO.setSeckillCountdownVO(goodsService.findCountdownVOByGoodsId(id));
        return RespBean.success(seckillDetailVO);
    }
}
