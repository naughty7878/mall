package com.hd.mall.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(properties = {"spring.cloud.nacos.config.enabled=false", "spring.cloud.nacos.discovery.enabled=false",
        "spring.config.import=optional:classpath:/test-override.yml", "logging.level.root=INFO"})
@Import({SecurityConfig.class, AnonymousAccessMatcher.class, SecurityConfigTest.TestController.class})
class SecurityConfigTest {
    @Autowired
    private WebTestClient client;

    @Autowired
    private ConfigurableEnvironment environment;

    @MockBean
    private ReactiveJwtDecoder decoder;

    @Test
    void healthIsPublicAndOtherEndpointsRequireValidToken() {
        when(decoder.decode(anyString())).thenReturn(Mono.error(new BadJwtException("invalid")));
        when(decoder.decode("valid")).thenReturn(Mono.just(jwt(List.of("mall-gateway"))));

        client.get().uri("/actuator/health").exchange().expectStatus().isOk();
        client.get().uri("/openapi/execute").exchange().expectStatus().isUnauthorized();
        client.get().uri("/openapi/execute").headers(h -> h.setBearerAuth("invalid"))
                .exchange().expectStatus().isUnauthorized();
        client.get().uri("/openapi/execute").headers(h -> h.setBearerAuth("valid"))
                .exchange().expectStatus().isOk();
    }

    @Test
    void audienceMustIncludeGateway() {
        var validator = SecurityConfig.audienceValidator("mall-gateway");
        assertThat(validator.validate(jwt(List.of("account", "mall-gateway"))).hasErrors()).isFalse();
        assertThat(validator.validate(jwt(List.of("account"))).hasErrors()).isTrue();
        assertThat(validator.validate(Jwt.withTokenValue("test").header("alg", "RS256")
                .claim("sub", "test").build()).hasErrors()).isTrue();
    }

    @Test
    void anonymousRulesCanChangeWithoutRebuildingSecurityChain() throws Exception {
        try {
            TestPropertyValues.of("gateway.security.anonymous-paths[0]=GET /public/example",
                            "gateway.security.anonymous-paths[1]=POST /callbacks/provider")
                    .applyTo(environment);
            client.get().uri("/public/example").exchange().expectStatus().isOk();
            client.post().uri("/public/example").exchange().expectStatus().isUnauthorized();
            client.get().uri("/public/example/other").exchange().expectStatus().isUnauthorized();
            client.post().uri("/callbacks/provider").exchange().expectStatus().isOk();
            environment.getPropertySources().remove("test");
            TestPropertyValues.of("gateway.security.anonymous-paths[0]=GET /openapi/execute")
                    .applyTo(environment);
            client.get().uri("/public/example").exchange().expectStatus().isUnauthorized();
            client.get().uri("/openapi/execute").exchange().expectStatus().isOk();
            environment.getPropertySources().remove("test");
            var emptyYaml = new ByteArrayResource(
                    "gateway:\n  security:\n    anonymous-paths: []\n".getBytes(StandardCharsets.UTF_8));
            environment.getPropertySources().addFirst(new YamlPropertySourceLoader()
                    .load("empty-yaml", emptyYaml).get(0));
            client.get().uri("/openapi/execute").exchange().expectStatus().isUnauthorized();
            client.post().uri("/callbacks/payment/provider-a").exchange().expectStatus().isUnauthorized();
        } finally {
            environment.getPropertySources().remove("test");
            environment.getPropertySources().remove("empty-yaml");
        }
    }

    private Jwt jwt(List<String> audience) {
        return Jwt.withTokenValue("test").header("alg", "RS256").subject("test")
                .audience(audience).issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
    }

    @RestController
    static class TestController {
        @GetMapping({"/actuator/health", "/openapi/execute"})
        String endpoint() {
            return "ok";
        }

        @RequestMapping(path = {"/public/example", "/callbacks/provider"},
                method = {RequestMethod.GET, RequestMethod.POST})
        String callback() {
            return "ok";
        }
    }
}
