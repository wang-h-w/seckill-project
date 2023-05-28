package com.concurrency.seckill.mapper;

import com.concurrency.seckill.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Haowen
 * @since 2023-05-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
