package com.hd.mall.gateway.filter;

import com.hd.mall.gateway.constants.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 接口动态请求过滤器
 */
@Slf4j
@Order(10000)
@Component
public class DynamicServerFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange)
                .doFirst(() -> {
                    // 1. 记录转发开始时间（在转发操作即将执行前）
                    long forwardStartTime = System.nanoTime();
                    exchange.getAttributes().put(GatewayConstants.FORWARD_START_TIME_ATTR, forwardStartTime);
                    log.debug("转发请求到下游服务...");
                })
                .doOnSuccess(aVoid -> {
                    // 2. 成功响应后记录耗时
                    long forwardEndTime = System.nanoTime();
                    exchange.getAttributes().put(GatewayConstants.FORWARD_END_TIME_ATTR, forwardEndTime);
                    log.debug("下游服务已响应成功...");
                })
                .doOnError(throwable -> {
                    // 3. 出错时也记录耗时
                    long forwardEndTime = System.nanoTime();
                    exchange.getAttributes().put(GatewayConstants.FORWARD_END_TIME_ATTR, forwardEndTime);
                    log.debug("下游服务已响应错误...");
                });
    }
}
