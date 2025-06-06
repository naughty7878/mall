
package com.hd.mall.coupon.api.service;

import com.hd.mall.common.api.CommonResult;
import com.hd.mall.coupon.api.dto.CouponDto;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 远程调用接口
 */
public interface CouponRemoteService {

    // 获取员工优惠卷列表
    @PostMapping("/coupon/coupon/member/list")
    CommonResult<List<CouponDto>> memberCoupons();

}