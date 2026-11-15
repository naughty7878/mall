package com.hd.mall.gateway.config;

import org.springframework.core.env.Environment;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/** 从当前配置读取匿名访问规则，精确匹配方法和路径。 */
@Component
public class AnonymousAccessMatcher implements ServerWebExchangeMatcher {
    private final Environment environment;

    public AnonymousAccessMatcher(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        List<String> rules = Binder.get(environment)
                .bind("gateway.security.anonymous-paths", Bindable.listOf(String.class))
                .orElse(List.of());
        HttpMethod method = exchange.getRequest().getMethod();
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        for (String rule : rules) {
            String[] parts = rule.trim().split("\\s+", 2);
            if (parts.length == 2 && parts[1].startsWith("/")
                    && parts[1].equals(path) && method != null && method.name().equals(parts[0])) {
                return MatchResult.match();
            }
        }
        return MatchResult.notMatch();
    }
}
