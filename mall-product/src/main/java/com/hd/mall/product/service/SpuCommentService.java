package com.hd.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author hd
 * @email sunlightcs@gmail.com
 * @date 2022-09-01 13:45:27
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

