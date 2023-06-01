package com.concurrency.seckill.service;

import com.concurrency.seckill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import com.concurrency.seckill.entity.vo.SeckillCountdownVO;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 业务：查询商品的全部信息
     * @return List of goods
     */
    List<GoodsDTO> findGoodsDTO();

    /**
     * 业务：查询某个商品的详细信息
     * @param goodsId Good's id
     * @return Good's detail
     */
    GoodsDTO findGoodsDTOByGoodsId(Long goodsId);

    /**
     * 业务：查询某个商品的秒杀倒计时信息（状态及秒数）
     * @param goodsId Good's id
     * @return Countdown info
     */
    SeckillCountdownVO findCountdownVOByGoodsId(Long goodsId);
}
