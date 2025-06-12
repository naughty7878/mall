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
        ApiDto apiDto = (ApiDto) exchange.getAttributes().get(GatewayConstants.API_INFO_ATTRIBUTE);

        if (startTime != null && apiDto != null) {
            long totalTime = System.nanoTime() - startTime;
            long gatewayProcessingTime = forwardStartTime != null ? forwardStartTime - startTime : 0;
            long forwardProcessingTime = forwardStartTime != null && forwardEndTime != null ? forwardEndTime - forwardStartTime : 0;
            long networkTime = totalTime - gatewayProcessingTime - forwardProcessingTime;

            String path = apiDto.getUrl();
            String method = apiDto.getCode();
            int status = exchange.getResponse().getStatusCode() != null ?
                    exchange.getResponse().getStatusCode().value() : 0;

            log.info("[Timing] {} {} | Status: {} | Total: {}ms | Gateway: {}ms | Downstream: {}ms | Network: {}ms | Error: {}",
                    method, path, status,
                    TimeUnit.NANOSECONDS.toMillis(totalTime),
                    TimeUnit.NANOSECONDS.toMillis(gatewayProcessingTime),
                    TimeUnit.NANOSECONDS.toMillis(forwardProcessingTime),
                    TimeUnit.NANOSECONDS.toMillis(networkTime),
                    isError);
        }
    }
}
