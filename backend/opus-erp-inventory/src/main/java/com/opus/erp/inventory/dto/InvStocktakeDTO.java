package com.opus.erp.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 盘点单 DTO
 */
@Data
public class InvStocktakeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    /**
     * 盘点日期
     */
    @NotNull(message = "盘点日期不能为空")
    private LocalDate stocktakeDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 盘点明细
     */
    @NotNull(message = "盘点明细不能为空")
    private List<InvStocktakeDetailDTO> details;

    /**
     * 盘点明细 DTO
     */
    @Data
    public static class InvStocktakeDetailDTO implements Serializable {

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
         * 系统数量（自动带出）
         */
        private BigDecimal systemQuantity;

        /**
         * 实盘数量
         */
        @NotNull(message = "实盘数量不能为空")
        @DecimalMin(value = "0", message = "实盘数量不能为负数")
        private BigDecimal actualQuantity;

        /**
         * 备注
         */
        private String remark;
    }
}
