package com.hd.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hd.mall.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单项信息 Mapper 接口
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
