package com.hd.mall.admin.api.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API请求参数
 */
@Data
@Schema(description = "API请求DTO")
public class ApiReqDto {

    /**
     * CODE
     */
    @Schema(description = "编码", defaultValue = "admin.route.server.list")
    @NotBlank(message = "code 不能为空")
    private String code;
}
