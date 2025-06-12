package com.hd.mall.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器	HIGHEST_PRECEDENCE + 1 (-2147483647)	需要最早处理异常
 * 核心网关过滤器	-1	Gateway 内置过滤器常用值
 * 认证/安全过滤器	0 - 100	早期阶段处理
 * 业务逻辑过滤器	100 - 1000	业务处理阶段
 * 路由相关过滤器	10000+	最后阶段处理
 * 最晚执行的组件	LOWEST_PRECEDENCE (2147483647)	最后防线
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)// 确保优先级高于默认的异常处理器
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 设置响应状态码和Content-Type
        exchange.getResponse().setStatusCode(determineHttpStatus(ex));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 构建错误响应体
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", determineErrorCode(ex));
        errorResponse.put("message", determineErrorMessage(ex));
        errorResponse.put("timestamp", System.currentTimeMillis());

        // 序列化为JSON
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(errorResponse))
                .onErrorResume(JsonProcessingException.class, e -> 
                        Mono.just("{\"code\":500,\"message\":\"JSON序列化失败\"}"))
                .flatMap(json -> exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse()
                                .bufferFactory()
                                .wrap(json.getBytes(StandardCharsets.UTF_8)))));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        // 根据异常类型确定HTTP状态码
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
//        else if (ex instanceof RuntimeException &&
//                  ex.getMessage() != null &&
//                  ex.getMessage().contains("外部API")) {
//            // 处理外部API相关的异常
//            return HttpStatus.BAD_GATEWAY;
//        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private int determineErrorCode(Throwable ex) {
        // 可以根据异常类型定义特定的错误码
//        if (ex.getMessage() != null && ex.getMessage().contains("外部API返回的数据为空")) {
//            return 4001; // 自定义业务错误码
//        } else if (ex.getMessage() != null && ex.getMessage().contains("外部API请求失败")) {
//            return 4002;
//        }
        return 500; // 默认错误码
    }

    private String determineErrorMessage(Throwable ex) {
        // 对异常消息进行安全处理
        if (ex.getMessage() == null) {
            return "系统内部错误";
        }
        return ex.getMessage().replace("\"", "'"); // 简单处理防止JSON注入
    }
}