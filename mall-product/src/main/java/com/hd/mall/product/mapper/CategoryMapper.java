package com.hd.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hd.mall.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品三级分类 Mapper 接口
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
