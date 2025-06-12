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
public class ApiReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * CODE
     */
    private String code;

}
