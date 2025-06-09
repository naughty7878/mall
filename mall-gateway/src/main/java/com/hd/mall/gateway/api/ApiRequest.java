package com.hd.mall.gateway.api;

import lombok.Data;

/**
 * 通用请求对象
 */
@Data
public class ApiRequest<T> {

    private T data;

}