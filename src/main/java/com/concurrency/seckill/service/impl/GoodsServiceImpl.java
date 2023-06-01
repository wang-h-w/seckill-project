package com.concurrency.seckill.service.impl;

import com.concurrency.seckill.entity.Goods;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.vo.SeckillCountdownVO;
import com.concurrency.seckill.mapper.GoodsMapper;
import com.concurrency.seckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsDTO> findGoodsDTO() {
        return goodsMapper.findGoodsDTO();
    }

    @Override
    public GoodsDTO findGoodsDTOByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsDTOByGoodsId(goodsId);
    }

    @Override
    public SeckillCountdownVO findCountdownVOByGoodsId(Long goodsId) {
        SeckillCountdownVO countdownVO = new SeckillCountdownVO();
        GoodsDTO goodsDTO = goodsMapper.findGoodsDTOByGoodsId(goodsId);
        Date startDate = goodsDTO.getStartDate(), endDate = goodsDTO.getEndDate();
        Date nowDate = new Date();
        if (nowDate.before(startDate)) {
            countdownVO.setSeckillStatus(0);
            countdownVO.setRemainSeconds((int)((startDate.getTime() - nowDate.getTime()) / 1000));
        } else if (nowDate.after(endDate)) {
            countdownVO.setSeckillStatus(2);
            countdownVO.setRemainSeconds(-1);
        } else {
            countdownVO.setSeckillStatus(1);
            countdownVO.setRemainSeconds(0);
        }
        return countdownVO;
    }
}
