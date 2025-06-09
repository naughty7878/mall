package com.hd.mall.gateway.dto;

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
     * URL
     */
    private String url;

}
