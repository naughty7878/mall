package com.hd.mall.gateway.filter;

import com.hd.mall.gateway.api.ApiResponse;
import com.hd.mall.gateway.constants.GatewayConstants;
import com.hd.mall.gateway.dto.ApiDto;
import com.hd.mall.gateway.service.ErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 接口动态请求过滤器
 */
@Slf4j
@Order(10000)
@Component
public class DynamicFilter implements GatewayFilter {

    @Autowired
    private ErrorService errorService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 从请求属性取出接口信息
        ApiDto apiDto = (ApiDto) exchange.getAttributes().get(GatewayConstants.API_INFO_ATTRIBUTE);
        if (apiDto == null) {
            log.error("请求中缺少必要的API信息属性: {}", GatewayConstants.API_INFO_ATTRIBUTE);
            return handleMissingApiInfo(exchange);
        }
        String url = apiDto.getUrl();
        if (url == null || url.isBlank()) {
            log.error("API信息中的URL为空，apiCode: {}", apiDto.getCode());
            return handleInvalidUrl(exchange, apiDto.getCode());
        }

        // 2. 重写请求URL
        ServerHttpRequest request = exchange.getRequest().mutate()
                .path(url)
                .build();

        return chain.filter(exchange.mutate().request(request).build())
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

    private Mono<Void> handleMissingApiInfo(ServerWebExchange exchange) {
        ApiResponse<String> errorResponse = ApiResponse.failed("请求缺少API信息");
        return errorService.writeErrorResponse(exchange, errorResponse);
    }

    private Mono<Void> handleInvalidUrl(ServerWebExchange exchange, String apiCode) {
        ApiResponse<String> errorResponse = ApiResponse.failed("API配置异常[%s]: URL为空".formatted(apiCode));
        return errorService.writeErrorResponse(exchange, errorResponse);
    }
}
