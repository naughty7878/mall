package com.hd.mall.coupon.client;

import com.hd.mall.coupon.api.MallCouponInterface;
import com.hd.mall.coupon.api.service.CouponRemoteService;
import com.hd.mall.coupon.client.fallback.CouponFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = MallCouponInterface.SERVICE_NAME, fallback = CouponFeignClientFallback.class)
public interface CouponFeignClient extends CouponRemoteService {

}