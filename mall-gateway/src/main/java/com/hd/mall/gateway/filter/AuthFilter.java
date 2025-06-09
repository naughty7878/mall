package com.hd.mall.gateway.filter;

import com.hd.mall.gateway.api.ApiResponse;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class AuthFilter implements GlobalFilter, Ordered {
    
    private final WebClient webClient;
    private final List<String> skipUrls = Arrays.asList("/auth/login", "/auth/logout");
    
    public AuthFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://auth-service").build();
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        
        // 跳过登录和登出接口
        if (skipUrls.contains(path)) {
            return chain.filter(exchange);
        }
        
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        
        if (token == null) {
            return unauthorized(exchange, "缺少认证token");
        }
        
        return webClient.post()
                .uri("/auth/verify")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .flatMap(response -> {
                    if (response.getCode() == 0) {
                        ServerHttpRequest request = exchange.getRequest().mutate()
//                                .header("X-User-Id", response.getUserInfo().getUsername())
//                                .header("X-User-Role", response.getUserInfo().getRole())
                                .build();
                        return chain.filter(exchange.mutate().request(request).build());
                    } else {
                        return unauthorized(exchange, response.getMessage());
                    }
                })
                // 在Gateway的AuthFilter中处理续期响应
//                .flatMap(response -> {
//                    if (response.isSuccess()) {
//                        ServerHttpResponse serverHttpResponse = exchange.getResponse();
//
//                        // 如果有新token，通过header返回给客户端
//                        if (response.getNewToken() != null) {
//                            serverHttpResponse.getHeaders().add("X-New-Token", response.getNewToken());
//                        }
//
//                        ServerHttpRequest request = exchange.getRequest().mutate()
//                                .header("X-User-Id", response.getUserInfo().getUsername())
//                                .build();
//                        return chain.filter(exchange.mutate().request(request).build());
//                    } else {
//                        return unauthorized(exchange, response.getMessage());
//                    }
//                })
                .onErrorResume(e -> unauthorized(exchange, "认证服务不可用"));
    }
    
    // unauthorized方法保持不变...
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = "{\"code\": 401, \"message\": \"" + message + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}