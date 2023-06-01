package com.concurrency.seckill.controller;

import com.concurrency.seckill.service.IGoodsService;
import com.concurrency.seckill.service.IUserService;
import com.concurrency.seckill.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/toList")
    public String toList(Model model) {
        model.addAttribute("user", hostHolder.getUser());
        model.addAttribute("goodsList", goodsService.findGoodsDTO());
        return "/goodsList";
    }

    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, @PathVariable("goodsId") long id) {
        model.addAttribute("user", hostHolder.getUser());
        model.addAttribute("goods", goodsService.findGoodsDTOByGoodsId(id));
        model.addAttribute("countdown", goodsService.findCountdownVOByGoodsId(id));
        return "/goodsDetail";
    }
}
