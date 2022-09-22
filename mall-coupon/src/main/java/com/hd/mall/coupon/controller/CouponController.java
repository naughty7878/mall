package com.hd.mall.coupon.controller;

import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.common.utils.R;
import com.hd.mall.coupon.entity.CouponEntity;
import com.hd.mall.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 优惠券信息
 *
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:16:06
 */
// 原生注解，支持Nacos动态刷新功能
@RefreshScope
@RestController
@RequestMapping("coupon/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @Value("${coupon.user.name:xx}")
    private String couponUsername;

    /**
     * 用户名
     */
    @RequestMapping("/username")
    // @RequiresPermissions("coupon:coupon:list")
    public R username(){
        return R.ok().put("username", couponUsername);
    }


    /**
     * 列表
     */
    @RequestMapping("/member/list")
    // @RequiresPermissions("coupon:coupon:list")
    public R memberList(){
        CouponEntity coupon = new CouponEntity();
        coupon.setId(1L);
        coupon.setCouponName("优惠卷");
        return R.ok().put("coupons", Arrays.asList(coupon));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:coupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:coupon:info")
    public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:coupon:save")
    public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:coupon:update")
    public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:coupon:delete")
    public R delete(@RequestBody Long[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
