package com.opus.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应付单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_payable")
public class FinPayable extends BaseEntity {

    /**
     * 应付单号
     */
    private String payableNo;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称（关联查询）
     */
    @TableField(exist = false)
    private String supplierName;

    /**
     * 参考类型：PO/RETURN
     */
    private String referenceType;

    /**
     * 参考ID
     */
    private Long referenceId;

    /**
     * 参考单号
     */
    private String referenceNo;

    /**
     * 应付金额
     */
    private BigDecimal amount;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 未付金额（计算列）
     */
    @TableField(exist = false)
    private BigDecimal unpaidAmount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 到期日
     */
    private LocalDate dueDate;

    /**
     * 状态：PENDING/PARTIAL/PAID
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
