package com.hd.mall.ware.dao;

import com.hd.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:46:28
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
