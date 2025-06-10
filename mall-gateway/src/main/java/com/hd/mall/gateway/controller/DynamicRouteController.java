package com.hd.mall.gateway.controller;

import com.hd.mall.gateway.config.DynamicRouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gateway/routes")
public class DynamicRouteController {

    private final DynamicRouteDefinitionLocator routeDefinitionLocator;

    public DynamicRouteController(DynamicRouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    // 添加或更新路由
    @PostMapping
    public String addRoute(@RequestBody RouteDefinition routeDefinition) {
        routeDefinitionLocator.addRouteDefinition(routeDefinition);
        return "Route added/updated successfully";
    }

    // 删除路由
    @DeleteMapping("/{routeId}")
    public String deleteRoute(@PathVariable String routeId) {
        routeDefinitionLocator.deleteRouteDefinition(routeId);
        return "Route deleted successfully";
    }
}
