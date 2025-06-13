package com.hd.mall.admin.api.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 系统服务表
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Data
@Schema(description = "SERVER传输对象")
public class ServerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * CODE
     */
    private String code;

    /**
     * 名字
     */
    private String name;

    /**
     * URL
     */
    private String url;

}
