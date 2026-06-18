package com.opus.erp.sales.entity;

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
 * 销售订单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("so_order")
public class SoOrder extends BaseEntity {

    /**
     * 销售订单号
     */
    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 订单日期
     */
    private LocalDate orderDate;

    /**
     * 交货日期
     */
    private LocalDate deliveryDate;

    /**
     * 业务员ID
     */
    private Long salesmanId;

    /**
     * 状态（DRAFT/AUDITED/SHIPPED/COMPLETED/CANCELLED）
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
     * 客户名称
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 业务员姓名
     */
    @TableField(exist = false)
    private String salesmanName;

    /**
     * 订单明细列表
     */
    @TableField(exist = false)
    private List<SoOrderDetail> details;
}
