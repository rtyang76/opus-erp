package com.opus.erp.production.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 领料单 DTO
 */
@Data
public class MaterialIssueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联工单ID
     */
    @NotNull(message = "工单不能为空")
    private Long workOrderId;

    /**
     * 领料仓库ID
     */
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    /**
     * 领料日期
     */
    @NotNull(message = "领料日期不能为空")
    private LocalDate issueDate;

    /**
     * 类型（ISSUE=领料, RETURN=退料）
     */
    @NotBlank(message = "类型不能为空")
    private String issueType = "ISSUE";

    /**
     * 备注
     */
    private String remark;

    /**
     * 领料明细
     */
    @NotNull(message = "领料明细不能为空")
    private List<MaterialIssueDetailDTO> details;

    /**
     * 领料明细 DTO
     */
    @Data
    public static class MaterialIssueDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 物料ID
         */
        @NotNull(message = "物料不能为空")
        private Long itemId;

        /**
         * 批次号
         */
        private String lotNo;

        /**
         * 数量
         */
        @NotNull(message = "数量不能为空")
        @DecimalMin(value = "0.0001", message = "数量必须大于0")
        private BigDecimal quantity;

        /**
         * 备注
         */
        private String remark;
    }
}
