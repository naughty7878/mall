package com.hd.mall.coupon.provider.entity;

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
 * 秒杀活动场次
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Getter
@Setter
@ToString
@TableName("sms_seckill_session")
public class SeckillSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 每日开始时间
     */
    private LocalDateTime startTime;

    /**
     * 每日结束时间
     */
    private LocalDateTime endTime;

    /**
     * 启用状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
