package com.hd.mall.common.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 请求响应日志过滤器
 * 请求日志在 doFilter 前打印，响应日志在 doFilter 后打印
 * 可通过配置 request.log.enabled=true/false 控制是否启用
 */
@Slf4j
@Component
@Order(1)
@ConditionalOnProperty(
        prefix = "logging.request",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
@ConfigurationProperties(prefix = "logging.request")
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Getter
    @Setter
    private boolean enabled = false;  // 默认不启用

    @Getter
    @Setter
    private boolean logHeaders = true;

    @Getter
    @Setter
    private boolean logRequestBody = true;

    @Getter
    @Setter
    private boolean logResponseBody = true;

    @Getter
    @Setter
    private int maxBodyLength = 1000;

    @Getter
    @Setter
    private List<String> excludePaths = new ArrayList<>();

    // 请求ID，用于关联请求和响应
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    // 敏感参数
    private final Set<String> sensitiveParams = new HashSet<>(Arrays.asList(
            "password", "pwd", "passwd", "secret", "token",
            "key", "credential", "auth"
    ));

    private final Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList(
            "authorization", "cookie", "set-cookie", "password",
            "token", "secret", "apikey", "x-api-key"
    ));

    private final List<String> defaultExcludePaths = Arrays.asList(
            "/actuator/health", "/actuator/prometheus",
            "/swagger-ui", "/v3/api-docs", "/favicon.ico",
            "/static/", "/css/", "/js/", "/images/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!enabled || shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 生成请求ID
        String requestId = generateRequestId();
        REQUEST_ID.set(requestId);

        // 包装请求和响应
        CachedBodyHttpServletRequest cachedBodyRequest = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // 记录开始时间
        TimeInterval timer = DateUtil.timer();

        try {
            // 1. 在 doFilter 前打印请求日志
            printRequestLog(requestId, cachedBodyRequest);

            // 2. 执行过滤器链
            filterChain.doFilter(cachedBodyRequest, wrappedResponse);

        } finally {
            // 3. 在 doFilter 后打印响应日志（包含处理时长）
            long duration = timer.intervalMs();
            printResponseLog(requestId, cachedBodyRequest, wrappedResponse, duration);

            // 复制响应内容到原始响应
            wrappedResponse.copyBodyToResponse();

            // 清理ThreadLocal
            REQUEST_ID.remove();
        }
    }

    /**
     * 打印请求日志（在 doFilter 前）
     */
    private void printRequestLog(String requestId, CachedBodyHttpServletRequest request) {
        try {
            StringBuilder logBuilder = new StringBuilder("\n");
            logBuilder.append("═════════════════════════ 请求开始 ═════════════════════════\n");
            logBuilder.append(String.format("请求ID: %s\n", requestId));
            logBuilder.append(String.format("请求时间: %s\n", DateUtil.now()));
            logBuilder.append(String.format("客户端IP: %s\n", getClientRealIp(request)));
            logBuilder.append(String.format("请求方法: %s\n", request.getMethod()));
            logBuilder.append(String.format("请求路径: %s\n", request.getRequestURI()));

            // 查询参数
            String queryString = request.getQueryString();
            if (StrUtil.isNotBlank(queryString)) {
                logBuilder.append(String.format("查询参数: %s\n", queryString));
            }

            // 请求头
            if (logHeaders) {
                printRequestHeaders(request, logBuilder);
            }

            // 表单参数
            printRequestParameters(request, logBuilder);

            // 请求体
            if (logRequestBody && shouldLogBody(request.getContentType())) {
                printRequestBody(request, logBuilder);
            }

            logBuilder.append("══════════════════════════════════════════════════════════\n");

            log.info(logBuilder.toString());

        } catch (Exception e) {
            log.error("打印请求日志失败", e);
        }
    }

    /**
     * 打印响应日志（在 doFilter 后，包含处理时长）
     */
    private void printResponseLog(String requestId, HttpServletRequest request, ContentCachingResponseWrapper response,
                                  long duration) {
        try {
            StringBuilder logBuilder = new StringBuilder("\n");
            logBuilder.append("═════════════════════════ 响应开始 ═════════════════════════\n");
            logBuilder.append(String.format("请求ID: %s\n", requestId));
            logBuilder.append(String.format("请求路径: %s %s\n", request.getMethod(), request.getRequestURI()));
            logBuilder.append(String.format("响应时间: %s\n", DateUtil.now()));
            logBuilder.append(String.format("处理时长: %dms\n", duration));
            logBuilder.append(String.format("响应状态: %d %s\n",
                    response.getStatus(), getStatusText(response.getStatus())));

            // 响应头
            if (logHeaders) {
                printResponseHeaders(response, logBuilder);
            }

            // 响应体
            if (logResponseBody && shouldLogBody(response.getContentType())) {
                printResponseBody(response, logBuilder);
            }

            logBuilder.append("══════════════════════════════════════════════════════════\n");

            // 根据状态码选择日志级别
            if (response.getStatus() >= 400) {
                log.warn(logBuilder.toString());
            } else {
                log.info(logBuilder.toString());
            }

        } catch (Exception e) {
            log.error("打印响应日志失败", e);
        }
    }

    /**
     * 打印请求头
     */
    private void printRequestHeaders(HttpServletRequest request, StringBuilder logBuilder) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames.hasMoreElements()) {
            logBuilder.append("请求头:\n");
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                logBuilder.append(String.format("  %s: %s\n",
                        name, isSensitiveHeader(name) ? "***" : truncate(value, 100)));
            }
        }
    }

    /**
     * 打印响应头
     */
    private void printResponseHeaders(ContentCachingResponseWrapper response, StringBuilder logBuilder) {
        Collection<String> headerNames = response.getHeaderNames();
        if (!headerNames.isEmpty()) {
            logBuilder.append("响应头:\n");
            for (String name : headerNames) {
                String value = response.getHeader(name);
                logBuilder.append(String.format("  %s: %s\n",
                        name, isSensitiveHeader(name) ? "***" : truncate(value, 100)));
            }
        }
    }

    /**
     * 打印请求参数
     */
    private void printRequestParameters(HttpServletRequest request, StringBuilder logBuilder) {
        Map<String, String[]> params = request.getParameterMap();
        if (!params.isEmpty()) {
            logBuilder.append("表单参数:\n");
            params.forEach((key, values) -> {
                if (isSensitiveParam(key)) {
                    logBuilder.append(String.format("  %s = ***\n", key));
                } else if (values.length == 1) {
                    logBuilder.append(String.format("  %s = %s\n", key, values[0]));
                } else {
                    logBuilder.append(String.format("  %s = %s\n", key, Arrays.toString(values)));
                }
            });
        }
    }

    /**
     * 打印请求体
     */
    private void printRequestBody(CachedBodyHttpServletRequest request, StringBuilder logBuilder) {
        String requestBody = request.getCachedBody();
        if (StrUtil.isNotBlank(requestBody)) {
            logBuilder.append("请求体:\n");
            String[] bodyLines = truncate(requestBody, maxBodyLength).split("\n");
            for (String line : bodyLines) {
                logBuilder.append(String.format("  %s\n", line));
            }
        }
    }

    /**
     * 打印响应体
     */
    private void printResponseBody(ContentCachingResponseWrapper response, StringBuilder logBuilder) {
        String responseBody = getBodyContent(response.getContentAsByteArray());
        if (StrUtil.isNotBlank(responseBody)) {
            logBuilder.append("响应体:\n");
            String truncatedBody = truncate(responseBody, maxBodyLength);
            String[] lines = truncatedBody.split("\n");
            for (String line : lines) {
                logBuilder.append(String.format("  %s\n", line));
            }
        }
    }

    /**
     * 生成请求ID（简化版）
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取客户端真实IP（处理代理情况）
     */
    public String getClientRealIp(HttpServletRequest request) {
        // Hutool 会自动处理 X-Forwarded-For、X-Real-IP 等代理头
        String ip = JakartaServletUtil.getClientIP(request);

        // 如果是本地或无效IP，可以进一步处理
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 获取状态码文本
     */
    private String getStatusText(int status) {
        String statusText;
        switch (status) {
            case 200:
                statusText = "OK";
                break;
            case 201:
                statusText = "Created";
                break;
            case 204:
                statusText = "No Content";
                break;
            case 301:
                statusText = "Moved Permanently";
                break;
            case 302:
                statusText = "Found";
                break;
            case 304:
                statusText = "Not Modified";
                break;
            case 400:
                statusText = "Bad Request";
                break;
            case 401:
                statusText = "Unauthorized";
                break;
            case 403:
                statusText = "Forbidden";
                break;
            case 404:
                statusText = "Not Found";
                break;
            case 405:
                statusText = "Method Not Allowed";
                break;
            case 500:
                statusText = "Internal Server Error";
                break;
            case 502:
                statusText = "Bad Gateway";
                break;
            case 503:
                statusText = "Service Unavailable";
                break;
            default:
                statusText = "Unknown";
        }
        return statusText;
    }

    /**
     * 获取Body内容
     */
    private String getBodyContent(byte[] content) {
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8).trim();
        }
        return "";
    }

    /**
     * 截断字符串
     */
    private String truncate(String text, int maxLength) {
        if (text != null && text.length() > maxLength) {
            return text.substring(0, maxLength) +
                    String.format("\n... [已截断，总长度:%d]", text.length());
        }
        return text != null ? text : "";
    }

    /**
     * 判断是否为敏感header
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return sensitiveHeaders.stream().anyMatch(lowerName::contains);
    }

    /**
     * 判断是否为敏感参数
     */
    private boolean isSensitiveParam(String paramName) {
        String lowerName = paramName.toLowerCase();
        return sensitiveParams.stream().anyMatch(lowerName::contains);
    }

    /**
     * 判断是否需要记录body
     */
    private boolean shouldLogBody(String contentType) {
        if (contentType == null) return true;
        String lowerType = contentType.toLowerCase();
        return !lowerType.contains("multipart/form-data") &&
                !lowerType.contains("application/octet-stream") &&
                !lowerType.contains("image/") &&
                !lowerType.contains("video/") &&
                !lowerType.contains("audio/");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return defaultExcludePaths.stream().anyMatch(path::startsWith) ||
                excludePaths.stream().anyMatch(path::startsWith);
    }

    /**
     * 获取当前请求ID（可用于业务代码中）
     */
    public static String getCurrentRequestId() {
        return REQUEST_ID.get();
    }


}