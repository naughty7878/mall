package com.hd.mall.common.api;

import lombok.Data;

/**
 * 通用请求对象
 */
@Data
public class ApiRequest<T> {

    private T data;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private BasePage page;
}