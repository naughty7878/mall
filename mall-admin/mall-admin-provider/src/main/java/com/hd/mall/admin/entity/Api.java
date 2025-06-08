package com.hd.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统API表
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Getter
@Setter
@ToString
@TableName("sys_api")
public class Api implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * CODE
     */
    private String code;

    /**
     * 服务ID
     */
    private Long serverId;

    /**
     * 名字
     */
    private String name;

    /**
     * 路径
     */
    private String url;

    /**
     * 接口鉴权类型(0:需登录鉴权,1:需登录免鉴权,2:开放免登录免鉴权)
     */
    private Integer authType;

    /**
     * 备注
     */
    private String remark;

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
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
