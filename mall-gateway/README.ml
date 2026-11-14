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

## 启动与使用

需要 JDK 17、Maven，以及可访问的 Nacos、Redis 和 `mall-admin`。按实际环境调整本地或 Nacos 配置，并确保后台已有对应服务和 API 配置、目标服务已注册到 Nacos。网关的 POM 导入了项目根 POM；构建前需确保该 POM 可从本地或远程 Maven 仓库解析。

在本模块目录下运行：

```bash
mvn spring-boot:run
```

默认监听 `8888` 端口。示例请求中的编码和接口方法仅作说明，必须与后台配置和目标接口一致：

```bash
# 服务级路由：请求路径直接传递给目标服务
curl -X POST http://localhost:8888/member/coupons -H "X-Server: member"

# 外部 API 级路由：由 API 配置决定实际转发路径
curl -X POST http://localhost:8888/openapi/execute -H "X-Api-Code: member.coupons" -H "X-Api-Version: v1"

# 内部 API 级路由
curl -X POST http://localhost:8888/internal/execute -H "X-Api-Code: member.coupons" -H "X-Api-Version: v1"
```

## 当前实现注意事项

- 服务和 API 的缓存时间不同，后台配置变化不会保证立即在所有网关实例生效。服务列表请求失败时会沿用已有缓存，返回空列表也会沿用已有缓存。
- Redis 未命中时 API 路由依赖 `mall-admin`；网关启动时若无法获取服务列表且没有本地缓存，动态路由可能不可用。
- 仓库中有 `AuthFilter` 示例，但当前源码中未发现该类注册为 Spring Bean，因此不能据此认为认证已启用。正式使用前需要完善并验证认证、授权和配置接口的访问控制。
- 两个 API 入口目前使用相同的路由逻辑，路径名称本身不提供内外网隔离或鉴权。后台目前仅按 API 编码查询，版本头不参与 API 配置的选择。
- 本地配置包含环境相关的内网地址和凭据，部署时应按环境调整并妥善管理敏感配置。
