package com.hd.mall.coupon.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品会员价格
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Getter
@Setter
@ToString
@TableName("sms_member_price")
public class MemberPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * sku_id
     */
    private Long skuId;

    /**
     * 会员等级id
     */
    private Long memberLevelId;

    /**
     * 会员等级名
     */
    private String memberLevelName;

    /**
     * 会员对应价格
     */
    private BigDecimal memberPrice;

    /**
     * 可否叠加其他优惠[0-不可叠加优惠，1-可叠加]
     */
    private Boolean addOther;
}
