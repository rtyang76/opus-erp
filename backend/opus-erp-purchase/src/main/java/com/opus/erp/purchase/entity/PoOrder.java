package com.opus.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购订单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("po_order")
public class PoOrder extends BaseEntity {

    /**
     * 采购订单号
     */
    private String orderNo;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 订单日期
     */
    private LocalDate orderDate;

    /**
     * 交货日期
     */
    private LocalDate deliveryDate;

    /**
     * 状态（DRAFT/AUDITED/CLOSED/CANCELLED）
     */
    private String status;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

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
     * 供应商名称
     */
    @TableField(exist = false)
    private String supplierName;

    /**
     * 订单明细列表
     */
    @TableField(exist = false)
    private List<PoOrderDetail> details;
}
