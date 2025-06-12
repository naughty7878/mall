package com.hd.mall.gateway.constants;

public class GatewayConstants {
    private GatewayConstants() {} // 防止实例化
    
    // API信息属性键
    public static final String API_INFO_ATTRIBUTE = "API-INFO";
    
    // Redis缓存KEY-API
    public static final String REDIS_KEY_GATEWAY_API = "gateway:api:";

    // 请求开始时间
    public static final String START_TIME_ATTR = "requestStartTime";
    // 转发开始时间
    public static final String FORWARD_START_TIME_ATTR = "forwardStartTime";
    // 转发结束时间
    public static final String FORWARD_END_TIME_ATTR = "forwardEndTime";

}