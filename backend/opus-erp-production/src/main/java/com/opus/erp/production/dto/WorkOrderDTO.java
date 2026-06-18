package com.opus.erp.production.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产工单 DTO
 */
@Data
public class WorkOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * BOM ID
     */
    @NotNull(message = "BOM不能为空")
    private Long bomId;

    /**
     * 生产成品ID
     */
    @NotNull(message = "生产成品不能为空")
    private Long itemId;

    /**
     * 计划数量
     */
    @NotNull(message = "计划数量不能为空")
    @DecimalMin(value = "0.0001", message = "计划数量必须大于0")
    private BigDecimal quantity;

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
     * 备注
     */
    private String remark;
}
