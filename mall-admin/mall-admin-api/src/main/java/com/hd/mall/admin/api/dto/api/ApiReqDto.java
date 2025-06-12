package com.hd.mall.admin.api.dto.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API请求参数
 */
@Data
public class ApiReqDto {

    /**
     * CODE
     */
    @NotBlank(message = "code 不能为空")
    private String code;
}
