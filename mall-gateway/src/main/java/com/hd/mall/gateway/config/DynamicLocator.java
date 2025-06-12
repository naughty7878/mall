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

    // 由于Nacos心跳机制触发路由刷新，会调用次此方法
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info("getRouteDefinitions() called");
//        log.debug("Call stack: ", new Exception("Stack Trace"));
        return dynamicRouteService.fetchRoutesFromExternalApi()
                .flatMap(serverDto -> {
                    try {
                        // 调用转换方法，返回包含多个RouteDefinition的Flux
                        return Flux.fromIterable(dynamicRouteService.convertToMultipleRouteDefinitions(serverDto));
                    } catch (Exception e) {
                        log.error("Failed to convert serverDto to route definitions: {}", serverDto, e);
                        return Flux.empty(); // 单个转换失败不影响其他
                    }
                })
                .onErrorResume(e -> {
                    log.error("Failed to fetch routes from external API", e);
                    return Flux.empty(); // 如果出错，返回空的路由定义
                });
    }
}

