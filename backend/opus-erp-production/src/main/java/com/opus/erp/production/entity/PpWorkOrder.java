package com.opus.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产工单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pp_work_order")
public class PpWorkOrder extends BaseEntity {

    /**
     * 工单号
     */
    private String orderNo;

    /**
     * BOM ID
     */
    private Long bomId;

    /**
     * 生产成品ID
     */
    private Long itemId;

    /**
     * 计划数量
     */
    private BigDecimal quantity;

    /**
     * 完工数量
     */
    private BigDecimal completedQuantity;

    /**
     * 入库仓库ID
     */
    private Long warehouseId;

    /**
     * 计划开始日期
     */
    private LocalDate planStartDate;

    /**
     * 计划结束日期
     */
    private LocalDate planEndDate;

    /**
     * 实际开始日期
     */
    private LocalDate actualStartDate;

    /**
     * 实际结束日期
     */
    private LocalDate actualEndDate;

    /**
     * 状态（PENDING/RELEASED/IN_PROGRESS/COMPLETED/CLOSED）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 下达人ID
     */
    private Long releasedBy;

    /**
     * 下达时间
     */
    private LocalDateTime releasedAt;

    // ========== 关联查询字段 ==========

    /**
     * BOM编码
     */
    @TableField(exist = false)
    private String bomCode;

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
     * 规格型号
     */
    @TableField(exist = false)
    private String specification;

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;
}
