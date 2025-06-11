package com.hd.mall.gateway.service;

import com.hd.mall.gateway.api.ApiResponse;
import com.hd.mall.gateway.dto.ServerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DynamicRouteService {

    @Autowired
    private WebClient webClient;
    // 用于发布事件
    @Autowired
    private ApplicationEventPublisher publisher;

    @Value("${gateway.route.server.url:http://localhost:6000/route/server/list}")
    private String serverApiUrl;

    /**
     * 缓存的路由数据
     */
    private List<ServerDto> cachedRoutes = new ArrayList<>();
    private long lastFetchTime = 0;
    private static final long CACHE_TTL = 10 * 60 * 1000; // 缓存有效期（毫秒）

    /**
     * 从外部 API 获取路由规则（带缓存）
     *
     * 由于Nacos心跳机制触发路由刷新，会调用次此方法，增加缓存机制
     */
    public Flux<ServerDto> fetchRoutesFromExternalApi() {
        long currentTime = System.currentTimeMillis();

        // 如果缓存有效且未过期，直接返回缓存数据
        if (!cachedRoutes.isEmpty() && (currentTime - lastFetchTime < CACHE_TTL)) {
            log.info("使用缓存路由配置数据，缓存条数: {}", cachedRoutes.size());
            return Flux.fromIterable(cachedRoutes);
        }

        // 发起 HTTP 请求
        return webClient.post()
                .uri(serverApiUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ServerDto>>>() {
                })
                .flatMapMany(response -> {
                    // 状态码检查
                    if (response.getCode() != 0) {
                        log.error("路由配置请求失败 - 状态码: {}, 消息: {}",
                                response.getCode(), response.getMessage());

                        // 返回缓存数据
                        log.info("使用缓存数据，缓存条数: {}", cachedRoutes.size());
                        return Flux.fromIterable(cachedRoutes);
                    }
                    // 空数据检查
                    if (response.getData() == null || response.getData().isEmpty()) {
                        log.warn("获取到的路由配置数据为空");

                        // 返回缓存数据
                        return Flux.fromIterable(cachedRoutes);
                    }
                    log.info("成功获取到{}条路由配置", response.getData().size());

                    // 更新缓存
                    cachedRoutes = response.getData();
                    lastFetchTime = currentTime;

                    // 返回新数据
                    return Flux.fromIterable(response.getData());
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // 捕获 WebClient 异常
                    log.error("请求路由配置时发生错误 - 状态码: {}, 消息: {}",
                            ex.getStatusCode(), ex.getMessage());

                    // 返回缓存数据
                    return Flux.fromIterable(cachedRoutes);
                })
                .onErrorResume(e -> {
                    // 捕获其他类型的异常
                    log.error("请求路由配置时发生未知错误: {}", e.getMessage());

                    // 返回缓存数据
                    return Flux.fromIterable(cachedRoutes);
                });
    }


    /**
     * 将ServerDto转换为RouteDefinition
     */
    public RouteDefinition convertToRouteDefinition(ServerDto serverDto) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId("dynamic-route-" + serverDto.getCode());
//        definition.setUri(URI.create(serverDto.getUrl()));
        definition.setUri(URI.create("http://localhost:6000"));

        // 设置路径断言
//        PredicateDefinition predicate = new PredicateDefinition();
//        predicate.setName("Path");
//        predicate.addArg(NameUtils.generateName(0), "/route/api");
////        predicate.addArg(NameUtils.generateName(0), "/" + serverDto.getCode() + "/api"); // 根据服务配置不同路径
//        definition.getPredicates().add(predicate);

        // 设置请求头断言 - 确保 api 头不能为空
        PredicateDefinition xApiHeaderPredicate = new PredicateDefinition();
        xApiHeaderPredicate.setName("Header");
        xApiHeaderPredicate.addArg(NameUtils.generateName(0), "API");
        // 正则表达式确保头以服务code开头
        xApiHeaderPredicate.addArg(NameUtils.generateName(1), "^(" + serverDto.getCode() + "\\..+)$");
        definition.getPredicates().add(xApiHeaderPredicate);


        // 可以添加更多转换逻辑，如Filters等
        return definition;
    }

    // 触发路由刷新
    private void refreshRoutes() {
        if (publisher != null) {
            // 清空使用缓存数据标识
            lastFetchTime = 0;

            // 发布事件通知网关刷新路由
            publisher.publishEvent(new RefreshRoutesEvent(this));
        }
    }
}
