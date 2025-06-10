package com.hd.mall.gateway.config;

import com.hd.mall.gateway.service.DynamicRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DynamicLocator implements RouteDefinitionLocator {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    private final Set<RouteDefinition> cachedRoutes = ConcurrentHashMap.newKeySet(); // 自动去重的线程安全集合
    private volatile long lastUpdateTime = 0;

    // 由于Nacos心跳机制触发路由刷新，增加缓存10分钟刷新一次
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info("getRouteDefinitions() called");
        log.info("cachedRoutes.size() = {}", cachedRoutes.size());
//        log.debug("Call stack: ", new Exception("Stack Trace"));
        // 10分钟缓存有效期
        if (System.currentTimeMillis() - lastUpdateTime > 600_000) {
            return dynamicRouteService.fetchRoutesFromExternalApi()
                    .map(dto -> dynamicRouteService.convertToRouteDefinition(dto)) // 转换并直接返回
                    .collectList()
                    .doOnNext(routes -> {
                        cachedRoutes.clear();
                        cachedRoutes.addAll(routes);
                        lastUpdateTime = System.currentTimeMillis();
                    })
                    .flatMapMany(Flux::fromIterable);
        }
        return Flux.fromIterable(cachedRoutes);
    }
}

