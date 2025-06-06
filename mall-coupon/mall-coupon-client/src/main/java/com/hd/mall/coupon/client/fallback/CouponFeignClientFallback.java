package com.hd.mall.coupon.client.fallback;

import com.hd.mall.common.api.CommonResult;
import com.hd.mall.coupon.api.dto.CouponDto;
import com.hd.mall.coupon.api.service.CouponRemoteService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponFeignClientFallback implements CouponRemoteService {

    @Override
    public CommonResult<List<CouponDto>> memberCoupons() {
        return CommonResult.failed();
    }
}