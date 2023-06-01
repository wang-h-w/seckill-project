package com.concurrency.seckill.mapper;

import com.concurrency.seckill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.concurrency.seckill.entity.dto.GoodsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author Haowen
 * @since 2023-05-28
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsDTO> findGoodsDTO();

    GoodsDTO findGoodsDTOByGoodsId(Long goodsId);
}
