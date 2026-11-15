# mall-gateway

`mall-gateway` 是商城系统的统一网关，基于 Spring Cloud Gateway 和响应式 WebFlux，将客户端请求路由到后端服务。它使用 Nacos 进行服务发现和配置加载，并可从 `mall-admin` 获取服务和 API 配置，动态生成路由。

## 主要功能

- **服务发现和负载均衡**：通过 Nacos 发现服务，使用 `lb://服务名` 转发请求；同时开启基于服务发现的自动路由。
- **动态路由**：从后台获取服务列表，为每个服务生成服务级路由和 API 级路由。
- **API 信息缓存**：根据请求头中的 API 编码查找接口配置，优先读取 Redis，未命中时请求后台并写入缓存。
- **路径改写**：API 级路由使用后台配置的 URL 改写请求路径，再转发到目标服务。
- **基础处理**：提供请求耗时记录、超时处理、跨域配置和统一异常响应。

## 基本架构

```text
                         ┌── Nacos：服务发现、配置 ──┐
客户端 ──> mall-gateway ──┤                         ├──> 后端服务
                         ├── Redis：API 配置缓存     │
                         └── mall-admin：路由配置 ───┘
```

`mall-admin` 负责提供服务及 API 的配置数据，网关加载这些数据并执行路由。网关访问后台配置接口使用独立的 HTTP 地址，默认指向本机的 `10001` 端口。

## 请求进入网关后的流程

网关基于 Spring WebFlux 接收普通 HTTP 请求。WebFlux 的 `WebFilter` 和 Spring Cloud Gateway 的过滤器处在不同层次：

```text
客户端 HTTP 请求
    ↓
WebFlux 接收请求
    ↓
WebFilter 层
    └─ Spring Security
         ├─ CORS：检查跨域规则；预检请求在此返回
         └─ 正式请求：验证 Bearer Token、检查访问规则
    ↓
Gateway 匹配路由（例如路径和 X-Api-Code）
    ↓
Gateway 过滤器层（仅匹配到路由后执行）
    ├─ GlobalFilter
    └─ 当前路由的 GatewayFilter
    ↓
转发到下游服务，响应沿过滤链返回客户端
```

1. **CORS 跨域处理**：浏览器在部分跨域请求前会先发送 `OPTIONS` 预检。Spring Security 使用 `GlobalCorsConfig` 提供的规则检查来源、方法和请求头；允许的预检直接返回跨域响应，不进入路由或下游。非预检请求继续执行，非浏览器客户端也无需自行发送预检。
2. **认证与访问规则**：健康检查和配置的匿名路径允许不带 Token 访问；其他请求需要 Keycloak 签发的有效 Bearer Token。网关验证签名、时间、issuer 和 audience；认证失败时返回 401，请求不会继续进入 Gateway 路由过滤器。允许预检不代表放开正式业务请求的认证。
3. **路由匹配**：安全检查通过后，Gateway 根据路由断言查找目标服务。例如 API 级路由根据入口路径和 `X-Api-Code` 匹配。未匹配到路由时，不会执行该路由的 Gateway 过滤器。
4. **Gateway 过滤器层**：匹配路由后，Gateway 合并全局过滤器和该路由配置的过滤器，按各自的顺序执行。例如 `TimeoutFilter` 是全局过滤器，`ApiInfoFilter` 和 `DynamicApiFilter` 用于 API 级路由，分别获取 API 配置和改写路径。
5. **转发和响应**：过滤器链继续执行后，由网关转发到目标服务。响应或错误会沿已执行的过滤链返回，过滤器可以记录耗时或处理错误。

`SecurityConfig` 是配置类，由它创建的 Spring Security 过滤链执行认证；它本身不是每次请求都重新执行的过滤器。WebFilter 与 Gateway 过滤器是不同层次，不能直接用 Gateway 过滤器的 `getOrder()` 数字比较它们的执行先后。健康检查允许匿名访问，但如果请求主动携带无效 Bearer Token，仍可能在认证阶段被拒绝。

当前 CORS 规则允许任意来源、请求方法和请求头，并允许携带凭据；正式部署时应根据实际前端域名收紧。

## 路由加载流程

1. `DynamicLocator` 实现 Spring Cloud Gateway 的 `RouteDefinitionLocator`，在网关获取路由定义时调用 `DynamicRouteService`。
2. `DynamicRouteService` 请求后台的服务列表接口，默认地址为 `http://localhost:10001/route/server/list`，请求方法为 POST。
3. 服务列表在网关进程内缓存，默认有效期为 10 分钟。请求失败、响应失败或返回空列表时，代码尝试使用已有缓存；首次加载失败时缓存可能为空。
4. 每条服务配置转换成两条路由，目标均为 `lb://` 加服务名，由服务发现和负载均衡选择实例。单条配置转换失败不会阻止其他配置转换。

| 路由类型 | 匹配条件 | 行为 |
| --- | --- | --- |
| 服务级 | 请求头 `X-Server` 的值等于服务编码 | 转发至对应服务，保持请求路径 |
| API 级 | 请求路径为 `/openapi/execute` 或 `/internal/execute`，`X-Api-Code` 匹配 `服务编码.后缀` | 查询 API 配置，以配置的 URL 改写路径后转发 |

## API 请求处理流程

1. 外部调用请求 `/openapi/execute`，内部调用请求 `/internal/execute`，在 `X-Api-Code` 请求头中携带 API 编码，例如 `member.coupons`。编码需要与后台实际配置一致。`X-Api-Version` 可省略，省略时网关补充 `v1`，传入时保留原值。
2. 路由匹配后，`ApiInfoFilter` 以 `gateway:api:` 加 API 编码作为 Redis 键查询接口信息。
3. 缓存未命中时，网关向后台发送 POST 请求，默认地址为 `http://localhost:10001/route/api/get`，请求体中的 `data.code` 为 API 编码。成功后将结果写入 Redis，默认缓存 60 分钟。
4. `DynamicApiFilter` 读取接口配置中的 `url`，改写请求路径并继续过滤器链，由网关转发到目标服务。
5. 接口信息获取失败、信息缺失或 URL 为空时，网关返回错误响应。

服务级路由则通过 `X-Server` 请求头选择服务，不经过上述 API 信息查询和路径改写流程。服务级和 API 级动态路由均包含耗时记录过滤器。

## 配置项

本地配置位于 `src/main/resources/application.yml`。Nacos 中的配置可能覆盖或补充本地配置，实际值以运行环境为准。

| 配置项 | 默认值 | 作用 |
| --- | --- | --- |
| `server.port` | `8888` | 网关监听端口 |
| `spring.application.name` | `mall-gateway` | 服务名和 Nacos 配置名 |
| `spring.cloud.nacos.discovery.*` | 见 `application.yml` | Nacos 服务发现连接配置 |
| `spring.cloud.nacos.config.*` | 见 `application.yml` | Nacos 配置中心连接配置 |
| `spring.data.redis.*` | 见 `application.yml` | API 信息缓存使用的 Redis 连接配置 |
| `gateway.route.server.url` | `http://localhost:10001/route/server/list` | 获取服务路由列表的地址 |
| `gateway.route.api.url` | `http://localhost:10001/route/api/get` | 获取单个 API 配置的地址 |
| `gateway.route.cache.ttl` | `600000` | 服务列表本地缓存时间，单位毫秒 |
| `gateway.route.cache.expiry` | `60` | API 信息 Redis 缓存时间，单位分钟 |
| `gateway.expiry` | `60` | 全局超时过滤器的超时时间，单位秒 |
| `gateway.security.anonymous-paths` | `["POST /callbacks/payment/provider-a"]` | 免 Keycloak Token 的路径规则数组 |

匿名规则按 HTTP 方法和完整路径精确匹配，方法使用大写，不支持通配符。网关在每次请求时从当前环境读取配置；在 Nacos 的 `mall-gateway.yaml` 中维护此项，配置正常刷新后无需重启即可生效：

```yaml
gateway:
  security:
    anonymous-paths:
      - "GET /public/info"
      - "POST /callbacks/payment/provider-a"
```

清空规则时请在 Nacos 显式设置 `anonymous-paths: []`，避免回退到本地默认值。原配置项 `gateway.security.public-callbacks` 不再生效，请将已配置的规则迁移到新配置项。

此配置只控制是否要求 Keycloak Token，不会创建路由或完成第三方验签。实际回调需要配置可到达业务服务的路由，并由业务服务或网关按第三方协议验证签名、处理重放和重复通知。若将 `/openapi/execute` 或 `/internal/execute` 设为匿名，会放行该方法下使用这一入口的所有 API 编码。

## 启动与使用

需要 JDK 17、Maven，以及可访问的 Nacos、Redis 和 `mall-admin`。按实际环境调整本地或 Nacos 配置，并确保后台已有对应服务和 API 配置、目标服务已注册到 Nacos。网关的 POM 导入了项目根 POM；构建前需确保该 POM 可从本地或远程 Maven 仓库解析。

在本模块目录下运行：

```bash
mvn spring-boot:run
```

默认监听 `8888` 端口。示例请求中的编码和接口方法仅作说明，必须与后台配置和目标接口一致：

```bash
# 服务级路由：请求路径直接传递给目标服务
curl -X POST http://localhost:8888/member/coupons -H "X-Server: member" -H "Authorization: Bearer <access_token>"

# 外部 API 级路由：由 API 配置决定实际转发路径
curl -X POST http://localhost:8888/openapi/execute -H "X-Api-Code: member.coupons" -H "Authorization: Bearer <access_token>"

# 内部 API 级路由
curl -X POST http://localhost:8888/internal/execute -H "X-Api-Code: member.coupons" -H "Authorization: Bearer <access_token>"
```

## 当前实现注意事项

- 服务和 API 的缓存时间不同，后台配置变化不会保证立即在所有网关实例生效。服务列表请求失败时会沿用已有缓存，返回空列表也会沿用已有缓存。
- Redis 未命中时 API 路由依赖 `mall-admin`；网关启动时若无法获取服务列表且没有本地缓存，动态路由可能不可用。
- 网关通过 Keycloak JWT 验证请求，健康检查及配置的匿名路径不要求 Token，其他路径要求有效 Token。通过 `KEYCLOAK_ISSUER_URI` 和 `GATEWAY_SECURITY_AUDIENCE` 可覆盖默认 issuer 和受众；Token 的 issuer 必须完全匹配，网关需要访问 Keycloak 的发现文档和公钥。网关当前只检查身份，尚未按 API 编码检查权限。
- 两个 API 入口目前使用相同的路由逻辑，路径名称本身不提供内外网隔离或鉴权。后台目前仅按 API 编码查询，版本头不参与 API 配置的选择。
- 本地配置包含环境相关的内网地址和凭据，部署时应按环境调整并妥善管理敏感配置。
