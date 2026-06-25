package com.opus.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 采购订单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("po_order_detail")
public class PoOrderDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 订购数量
     */
    private BigDecimal quantity;

    /**
     * 单位ID
     */
    private Long unitId;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 已收货数量
     */
    private BigDecimal receivedQuantity;

    /**
     * 备注
     */
    private String remark;

    // ========== 计算列（数据库自动计算） ==========

    /**
     * 金额 = 数量 × 单价
     */
    @TableField(exist = false)
    private BigDecimal amount;

    /**
     * 税额 = 数量 × 单价 × 税率 / 100
     */
    @TableField(exist = false)
    private BigDecimal taxAmount;

    // ========== 关联查询字段 ==========

    /**
     * 物料编码
     */
    @TableField(exist = false)
    private String itemCode;

    /**
     * 物料名称
     */
    @TableField(exist = false)
    private String itemName;

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String unitName;
}
