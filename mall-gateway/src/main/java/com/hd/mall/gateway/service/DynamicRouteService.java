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
     * 从外部 API 获取路由规则
     */
    public Flux<ServerDto> fetchRoutesFromExternalApi() {
        return webClient.post()
                .uri(serverApiUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ServerDto>>>() {})
                .flatMapMany(response -> {
                    // 状态码检查
                    if (response.getCode() != 0) {
                        log.error("路由配置请求失败 - 状态码: {}, 消息: {}",
                                response.getCode(), response.getMessage());
                        return Flux.empty();
                    }

                    // 空数据检查
                    if (response.getData() == null || response.getData().isEmpty()) {
                        log.warn("获取到的路由配置数据为空");
                        return Flux.empty();
                    }

                    log.info("成功获取到{}条路由配置", response.getData().size());

                    // 将数据转换为 Flux
                    return Flux.fromIterable(response.getData());
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // 捕获 WebClient 异常
                    log.error("请求路由配置时发生错误 - 状态码: {}, 消息: {}",
                            ex.getStatusCode(), ex.getMessage());
                    return Flux.empty();
                })
                .onErrorResume(e -> {
                    // 捕获其他类型的异常
                    log.error("请求路由配置时发生未知错误: {}", e.getMessage());
                    return Flux.empty();
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
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Path");
        predicate.addArg(NameUtils.generateName(0), "/route/api");
//        predicate.addArg(NameUtils.generateName(0), "/" + serverDto.getCode() + "/api"); // 根据服务配置不同路径
        definition.getPredicates().add(predicate);

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
            // 发布事件通知网关刷新路由
            publisher.publishEvent(new RefreshRoutesEvent(this));
        }
    }
}
