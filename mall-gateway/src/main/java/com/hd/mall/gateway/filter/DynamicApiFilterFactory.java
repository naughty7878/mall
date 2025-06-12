package com.hd.mall.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DynamicApiFilterFactory extends AbstractGatewayFilterFactory<DynamicApiFilterFactory.Config> {

    @Autowired
    private DynamicApiFilter dynamicApiFilter;

    public DynamicApiFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return dynamicApiFilter;
        // 每次创建新的过滤器实例，传入当前配置，扩展写法
//        return new DynamicFilter(config.getServerCode());
    }

    // 过滤器配置类
    public static class Config {
        private String serverCode;

        // getters and setters
        public String getServerCode() {
            return serverCode;
        }

        public void setServerCode(String serverCode) {
            this.serverCode = serverCode;
        }
    }
}