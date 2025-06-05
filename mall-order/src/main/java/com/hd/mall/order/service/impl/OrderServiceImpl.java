package com.hd.mall.order.service.impl;

import com.hd.mall.order.entity.Order;
import com.hd.mall.order.mapper.OrderMapper;
import com.hd.mall.order.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
