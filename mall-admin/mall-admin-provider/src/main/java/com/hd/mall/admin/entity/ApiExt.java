package com.hd.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * API扩展表
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Getter
@Setter
@ToString
@TableName("sys_api_ext")
public class ApiExt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的API主表ID
     */
    private Long apiId;

    /**
     * 是否转换(0:不转换,1:转换)
     */
    private Integer convertEnabled;

    /**
     * 转换内容(JSON格式)
     */
    private Integer convertContent;

    /**
     * 是否启用缓存(0:禁用,1:启用)
     */
    private Boolean cacheEnabled;

    /**
     * 缓存时间(秒)
     */
    private Integer cacheTtl;

    /**
     * 参数Schema(JSON格式)
     */
    private String paramSchema;

    /**
     * 响应Schema(JSON格式)
     */
    private String responseSchema;

    /**
     * 自定义扩展数据(JSON格式)
     */
    private String customData;

    /**
     * 删除标志(0:未删除,1:已删除)
     */
    private Integer deletedFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}
