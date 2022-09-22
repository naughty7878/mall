package com.hd.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author hd
 * @email sunlightcs@gmail.com
 * @date 2022-09-01 13:45:27
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

