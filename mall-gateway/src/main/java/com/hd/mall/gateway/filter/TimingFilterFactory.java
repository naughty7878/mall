package com.hd.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimingFilterFactory extends AbstractGatewayFilterFactory<TimingFilterFactory.Config> {

    @Autowired
    private TimingFilter timingFilter;

    public TimingFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return timingFilter;
    }

    // 过滤器配置类
    public static class Config {
        // 可以添加配置字段
    }
}