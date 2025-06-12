package com.hd.mall.gateway.filter;

import com.hd.mall.gateway.constants.GatewayConstants;
import com.hd.mall.gateway.service.TimingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 耗时统计过滤器
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Component
public class TimingFilter implements GatewayFilter {

    @Autowired
    private TimingService timingService;

    private static final Logger log = LoggerFactory.getLogger(TimingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 记录请求开始时间（整体处理开始）
        long startTime = System.nanoTime();
        exchange.getAttributes().put(GatewayConstants.START_TIME_ATTR, startTime);

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // 2. 成功响应后记录耗时
                    timingService.logRequestTiming(exchange, false);
                })
                .doOnError(throwable -> {
                    // 3. 出错时也记录耗时
                    timingService.logRequestTiming(exchange, true);
                });
    }
}