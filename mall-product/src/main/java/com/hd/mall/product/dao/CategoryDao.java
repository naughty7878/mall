package com.hd.mall.product.dao;

import com.hd.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author hd
 * @email sunlightcs@gmail.com
 * @date 2022-09-01 13:45:27
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
