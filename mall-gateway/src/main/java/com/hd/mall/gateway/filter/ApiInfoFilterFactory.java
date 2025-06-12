package com.hd.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiInfoFilterFactory extends AbstractGatewayFilterFactory<ApiInfoFilterFactory.Config> {

    @Autowired
    private ApiInfoFilter apiInfoFilter;

    public ApiInfoFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return apiInfoFilter;
    }

    // 过滤器配置类
    public static class Config {
        // 可以添加配置字段
    }
}