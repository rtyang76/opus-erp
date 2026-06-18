package com.opus.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购入库单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("po_receipt")
public class PoReceipt extends BaseEntity {

    /**
     * 入库单号
     */
    private String receiptNo;

    /**
     * 关联采购订单ID
     */
    private Long orderId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 入库仓库ID
     */
    private Long warehouseId;

    /**
     * 入库日期
     */
    private LocalDate receiptDate;

    /**
     * 状态（DRAFT/AUDITED/CANCELLED）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核人ID
     */
    private Long auditedBy;

    /**
     * 审核时间
     */
    private LocalDateTime auditedAt;

    // ========== 关联查询字段 ==========

    /**
     * 采购订单号
     */
    @TableField(exist = false)
    private String orderNo;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    private String supplierName;

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;

    /**
     * 入库明细列表
     */
    @TableField(exist = false)
    private List<PoReceiptDetail> details;
}
