package com.opus.erp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产进度表 DTO
 */
@Data
public class ProductionProgressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    private Long workOrderId;

    /**
     * 工单号
     */
    private String orderNo;

    /**
     * 物料编码
     */
    private String itemCode;

    /**
     * 物料名称
     */
    private String itemName;

    /**
     * 计划数量
     */
    private BigDecimal planQuantity;

    /**
     * 完工数量
     */
    private BigDecimal completedQuantity;

    /**
     * 完成率
     */
    private BigDecimal completionRate;

    /**
     * 计划开始日期
     */
    private LocalDate planStartDate;

    /**
     * 计划结束日期
     */
    private LocalDate planEndDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;
}
