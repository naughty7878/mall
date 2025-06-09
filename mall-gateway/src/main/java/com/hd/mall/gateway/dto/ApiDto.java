package com.hd.mall.gateway.dto;

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
public class ApiDto implements Serializable {

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
     * 路径
     */
    private String url;

    /**
     * 是否记录日志(0:否,1:是)
     */
    private Integer logEnabled;

    /**
     * 接口鉴权类型(0:需登录鉴权,1:需登录免鉴权,2:开放免登录免鉴权)
     */
    private Integer authType;

}
