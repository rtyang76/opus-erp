package com.opus.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售订单明细实体
 */
@Data
@TableName("so_order_detail")
public class SoOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 已出库数量
     */
    private BigDecimal shippedQuantity;

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
