package com.hd.mall.gateway.config;

import com.hd.mall.gateway.service.DynamicRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class DynamicLocator implements RouteDefinitionLocator {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return dynamicRouteService.fetchRoutesFromExternalApi()
                .map(dto -> dynamicRouteService.convertToRouteDefinition(dto)) // 转换并直接返回
                .onErrorResume(e -> {
                    log.error("Failed to fetch routes from external API", e);
                    return Flux.empty(); // 如果出错，返回空的路由定义
                });
    }
}

