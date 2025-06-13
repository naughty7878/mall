package com.hd.mall.admin.api.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 系统API表
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Data
@Schema(description = "API传输对象")
public class ApiDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "ID", example = "123", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    /**
     * CODE
     */
    @Schema(description = "编码", example = "admin.route.server.list")
    private String code;

    /**
     * 路径
     */
    @Schema(description = "路径", example = "/route/server/list")
    private String url;

    /**
     * 是否记录日志(0:否,1:是)
     */
    @Schema(description = "是否记录日志", example = "0", allowableValues = {"0", "1"}, type = "number")
    private Integer logEnabled;

    /**
     * 接口鉴权类型(0:需登录鉴权,1:需登录免鉴权,2:开放免登录免鉴权)
     */
    @Schema(description = "接口鉴权类型", example = "0", allowableValues = {"0", "1", "2"}, type = "number")
    private Integer authType;

}
