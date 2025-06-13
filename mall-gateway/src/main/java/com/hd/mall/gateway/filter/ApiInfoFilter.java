package com.hd.mall.gateway.filter;

import com.hd.mall.gateway.api.ApiRequest;
import com.hd.mall.gateway.api.ApiResponse;
import com.hd.mall.gateway.constants.GatewayConstants;
import com.hd.mall.gateway.dto.ApiDto;
import com.hd.mall.gateway.dto.ApiReqDto;
import com.hd.mall.gateway.service.ErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * API信息过滤器
 */
@Slf4j
@Order(1)
@Component
public class ApiInfoFilter implements GatewayFilter {

    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    @Autowired
    private WebClient webClient;
    @Autowired
    private ErrorService errorService;

    @Value("${gateway.route.api.url:http://localhost:10001/route/api/get}")
    private String apiUrl;
    // 缓存过期时间，默认 60 分钟
    @Value("${gateway.route.cache.expiry:60}")
    private long cacheExpiry;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 从请求头获取API code
        String apiCode = exchange.getRequest().getHeaders().getFirst("api");
        if (apiCode == null || apiCode.isBlank()) {
            return chain.filter(exchange);
        }

        // 2. 从Redis获取接口信息
        String redisKey = GatewayConstants.REDIS_KEY_GATEWAY_API + apiCode;
        return getFromRedis(redisKey)
                .switchIfEmpty(getFromExternalApi(apiCode, redisKey))
                .flatMap(apiInfo -> {
                    // 3. 将接口信息存入请求属性，供后续过滤器使用
                    exchange.getAttributes().put(GatewayConstants.API_INFO_ATTRIBUTE, apiInfo);
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    ApiResponse<String> errorResponse = ApiResponse.failed("获取接口信息失败: " + e.getMessage());
                    return errorService.writeErrorResponse(exchange, errorResponse);
                });
    }



    private Mono<ApiDto> getFromRedis(String key) {
        return reactiveRedisTemplate.opsForValue().get(key)
                .ofType(ApiDto.class) // 确保类型安全
                .doOnNext(info -> log.debug("从Redis获取接口信息: {}", info))
                .onErrorResume(e -> {
                    log.error("Redis查询失败", e);
                    return Mono.empty();
                });
    }

    private Mono<ApiDto> getFromExternalApi(String apiCode, String redisKey) {
        return buildApiRequest(apiCode)
                .flatMap(this::callExternalApi)
                .flatMap(response -> handleApiResponse(response, redisKey))
                .doOnSuccess(apiInfo ->
                        log.info("成功从外部接口获取并缓存API信息 - code: {}, data: {}", apiCode, apiInfo))
                .onErrorResume(e -> {
                    log.error("获取接口({})信息失败", apiCode, e);
                    // 返回错误响应
                    return Mono.error(new RuntimeException("获取接口信息失败，请稍后重试"));
                });
    }

    // 构建请求对象（响应式封装）
    private Mono<ApiRequest<ApiReqDto>> buildApiRequest(String apiCode) {
        return Mono.fromCallable(() -> {
            ApiRequest<ApiReqDto> apiRequest = new ApiRequest<>();
            ApiReqDto apiReqDto = new ApiReqDto();
            apiReqDto.setCode(apiCode);
            apiRequest.setData(apiReqDto);
            return apiRequest;
        });
    }

    // 调用外部API
    private Mono<ApiResponse<ApiDto>> callExternalApi(ApiRequest<ApiReqDto> request) {
        return webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ApiDto>>() {});
    }

    // 处理API响应并缓存
    private Mono<ApiDto> handleApiResponse(ApiResponse<ApiDto> response, String redisKey) {
        return Mono.defer(() -> {
            // 1. 检查响应本身是否为null
            if (response == null) {
                log.warn("外部API返回的响应为null");
                return Mono.error(new RuntimeException("外部API返回空响应"));
            }

            // 2. 检查业务响应码
            if (response.getCode() != 0) {
                log.warn("外部API请求失败 - code: {}, message: {}",
                        response.getCode(), response.getMessage());
                return Mono.error(new RuntimeException(
                        String.format("外部API请求失败[%s]: %s",
                                response.getCode(), response.getMessage())));
            }

            // 3. 检查data字段是否为null
            if (response.getData() == null) {
                log.warn("外部API返回的数据为null - code: {}, message: {}",
                        response.getCode(), response.getMessage());
                return Mono.error(new RuntimeException("外部API返回的数据为空"));
            }

            // 4. 数据有效，存入Redis并返回
            return reactiveRedisTemplate.opsForValue()
                    .set(redisKey, response.getData(), Duration.ofMinutes(cacheExpiry))
                    .thenReturn(response.getData());
        });
    }


}
