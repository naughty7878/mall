package com.hd.mall.gateway.service;

import com.hd.mall.gateway.constants.GatewayConstants;
import com.hd.mall.gateway.dto.ApiDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TimingService {

    public void logRequestTiming(ServerWebExchange exchange, boolean isError) {
        Long startTime = exchange.getAttribute(GatewayConstants.START_TIME_ATTR);
        Long forwardStartTime = exchange.getAttribute(GatewayConstants.FORWARD_START_TIME_ATTR);
        Long forwardEndTime = exchange.getAttribute(GatewayConstants.FORWARD_END_TIME_ATTR);

        if (startTime != null) {
            long totalTime = System.nanoTime() - startTime;
            long gatewayProcessingTime = forwardStartTime != null ? forwardStartTime - startTime : 0;
            long forwardProcessingTime = forwardStartTime != null && forwardEndTime != null ? forwardEndTime - forwardStartTime : 0;
            long networkTime = totalTime - gatewayProcessingTime - forwardProcessingTime;

            ApiDto apiDto = (ApiDto) exchange.getAttributes().get(GatewayConstants.API_INFO_ATTRIBUTE);
            if (apiDto != null) {
                printApiTiming(exchange, apiDto, totalTime, gatewayProcessingTime, forwardProcessingTime, networkTime, isError);
            } else {
                String serverCode = exchange.getRequest().getHeaders().getFirst(GatewayConstants.HEADER_SERVER);
                if (serverCode != null && !serverCode.isBlank()) {
                    printServerTiming(exchange, serverCode, totalTime, gatewayProcessingTime, forwardProcessingTime, networkTime, isError);
                }
            }
        }
    }

    // 打印Server耗时
    private void printServerTiming(ServerWebExchange exchange, String serverCode, long totalTime, long gatewayProcessingTime,
                                   long forwardProcessingTime, long networkTime, boolean isError)  {
        String path = exchange.getRequest().getPath().toString();

        int status = exchange.getResponse().getStatusCode() != null ?
                exchange.getResponse().getStatusCode().value() : 0;

        log.info("[Timing] | Server: {} | Path: {} | Status: {} | Total: {}ms | Gateway: {}ms | Downstream: {}ms | Network: {}ms | Error: {}",
                serverCode, path, status,
                TimeUnit.NANOSECONDS.toMillis(totalTime),
                TimeUnit.NANOSECONDS.toMillis(gatewayProcessingTime),
                TimeUnit.NANOSECONDS.toMillis(forwardProcessingTime),
                TimeUnit.NANOSECONDS.toMillis(networkTime),
                isError);
    }

    // 打印Api耗时
    private void printApiTiming(ServerWebExchange exchange, ApiDto apiDto, long totalTime, long gatewayProcessingTime,
                                long forwardProcessingTime, long networkTime, boolean isError) {
        String path = apiDto.getUrl();
        String apiCode = apiDto.getCode();
        int status = exchange.getResponse().getStatusCode() != null ?
                exchange.getResponse().getStatusCode().value() : 0;

        log.info("[Timing] | Api: {} | Path: {} | Status: {} | Total: {}ms | Gateway: {}ms | Downstream: {}ms | Network: {}ms | Error: {}",
                apiCode, path, status,
                TimeUnit.NANOSECONDS.toMillis(totalTime),
                TimeUnit.NANOSECONDS.toMillis(gatewayProcessingTime),
                TimeUnit.NANOSECONDS.toMillis(forwardProcessingTime),
                TimeUnit.NANOSECONDS.toMillis(networkTime),
                isError);
    }
}
