package com.hd.mall.member.controller;

import com.hd.mall.common.api.CommonResult;
import com.hd.mall.coupon.api.dto.CouponDto;
import com.hd.mall.coupon.client.CouponFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 会员 前端控制器
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@RestController
public class MemberController {

    @Autowired
    private CouponFeignClient couponFeignClient;

    @PostMapping("/member/member/coupons")
    public CommonResult<List<CouponDto>> memberCoupons() {
        return couponFeignClient.memberCoupons();
    }

}
