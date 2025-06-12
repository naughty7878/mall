package com.hd.mall.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.mall.gateway.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ErrorService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将错误响应以JSON格式写入HTTP响应
     *
     * @param exchange ServerWebExchange对象
     * @param errorResponse 要返回的错误响应对象
     * @return Mono<Void> 表示操作完成
     */
    public Mono<Void> writeErrorResponse(ServerWebExchange exchange, ApiResponse<String> errorResponse) {
        // 处理错误情况，返回错误响应
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (errorResponse == null) {
            return Mono.error(new IllegalArgumentException("响应错误，不能null"));
        }

        return Mono.fromCallable(() -> objectMapper.writeValueAsString(errorResponse))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(jsonContent -> {
                    byte[] bytes = jsonContent.getBytes(StandardCharsets.UTF_8);
                    return exchange.getResponse().writeWith(
                            Mono.just(exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(bytes))
                    );
                })
                .onErrorResume(JsonProcessingException.class, e -> {
                    // JSON序列化失败时返回简单错误信息
                    byte[] fallbackBytes = "{\"code\":500,\"message\":\"JSON序列化响应失败\"}"
                            .getBytes(StandardCharsets.UTF_8);
                    return exchange.getResponse().writeWith(
                            Mono.just(exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(fallbackBytes))
                    );
                });
    }

}
