package com.hd.mall.coupon.provider.controller;

import com.hd.mall.common.api.CommonResult;
import com.hd.mall.common.util.BeanCopyUtils;
import com.hd.mall.coupon.api.dto.CouponDto;
import com.hd.mall.coupon.api.service.CouponRemoteService;
import com.hd.mall.coupon.provider.entity.Coupon;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@RestController
public class CouponController implements CouponRemoteService {

    @Override
    public CommonResult<List<CouponDto>> memberCoupons() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("满100减10");
        List<CouponDto> dtos = BeanCopyUtils.copyList(List.of(coupon), CouponDto.class);
        return CommonResult.success(dtos);
    }
}
