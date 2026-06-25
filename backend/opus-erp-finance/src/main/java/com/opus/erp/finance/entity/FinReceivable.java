package com.opus.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应收单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_receivable")
public class FinReceivable extends BaseEntity {

    /**
     * 应收单号
     */
    private String receivableNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称（关联查询）
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 参考类型：SO/RETURN
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
     * 应收金额
     */
    private BigDecimal amount;

    /**
     * 已收金额
     */
    private BigDecimal paidAmount;

    /**
     * 未收金额（计算列）
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
