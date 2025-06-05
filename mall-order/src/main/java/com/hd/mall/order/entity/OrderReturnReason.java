package com.hd.mall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 退货原因
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Getter
@Setter
@ToString
@TableName("oms_order_return_reason")
public class OrderReturnReason implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 退货原因名
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 启用状态
     */
    private Boolean status;

    /**
     * create_time
     */
    private LocalDateTime createTime;
}
