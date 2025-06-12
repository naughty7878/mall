package com.hd.mall.common.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通用请求对象
 */

@Data
public class ApiRequest<T> {

    @Valid
    @NotNull(message = "data must not be empty")
    private T data;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private BasePage page;
}