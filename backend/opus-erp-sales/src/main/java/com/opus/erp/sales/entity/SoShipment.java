package com.opus.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售出库单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("so_shipment")
public class SoShipment extends BaseEntity {

    /**
     * 出库单号
     */
    private String shipmentNo;

    /**
     * 关联销售订单ID
     */
    private Long orderId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 出库仓库ID
     */
    private Long warehouseId;

    /**
     * 出库日期
     */
    private LocalDate shipmentDate;

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
     * 销售订单号
     */
    @TableField(exist = false)
    private String orderNo;

    /**
     * 客户名称
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;

    /**
     * 出库明细列表
     */
    @TableField(exist = false)
    private List<SoShipmentDetail> details;
}
