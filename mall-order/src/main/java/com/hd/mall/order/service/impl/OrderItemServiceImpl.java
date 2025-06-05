package com.hd.mall.order.service.impl;

import com.hd.mall.order.entity.OrderItem;
import com.hd.mall.order.mapper.OrderItemMapper;
import com.hd.mall.order.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单项信息 服务实现类
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
