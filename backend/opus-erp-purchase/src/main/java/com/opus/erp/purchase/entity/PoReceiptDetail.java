package com.opus.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 采购入库单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("po_receipt_detail")
public class PoReceiptDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 入库单ID
     */
    private Long receiptId;

    /**
     * 关联订单明细ID
     */
    private Long orderDetailId;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 批次号
     */
    private String lotNo;

    /**
     * 入库数量
     */
    private BigDecimal quantity;

    /**
     * 单位ID
     */
    private Long unitId;

    /**
     * 入库成本
     */
    private BigDecimal unitCost;

    /**
     * 备注
     */
    private String remark;

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
