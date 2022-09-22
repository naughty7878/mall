package com.hd.mall.coupon.dao;

import com.hd.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:16:06
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
