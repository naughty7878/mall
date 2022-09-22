package com.hd.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author hd
 * @email sunlightcs@gmail.com
 * @date 2022-09-01 13:45:27
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();
}

