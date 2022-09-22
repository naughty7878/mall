package com.hd.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:16:05
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

