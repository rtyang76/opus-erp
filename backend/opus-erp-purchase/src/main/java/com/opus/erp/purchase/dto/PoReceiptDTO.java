package com.opus.erp.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 采购入库单 DTO
 */
@Data
public class PoReceiptDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联采购订单ID
     */
    private Long orderId;

    /**
     * 供应商ID
     */
    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    /**
     * 入库仓库ID
     */
    @NotNull(message = "入库仓库不能为空")
    private Long warehouseId;

    /**
     * 入库日期
     */
    @NotNull(message = "入库日期不能为空")
    private LocalDate receiptDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库明细
     */
    @NotNull(message = "入库明细不能为空")
    private List<PoReceiptDetailDTO> details;

    /**
     * 采购入库明细 DTO
     */
    @Data
    public static class PoReceiptDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 关联订单明细ID
         */
        private Long orderDetailId;

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
         * 入库数量
         */
        @NotNull(message = "数量不能为空")
        @DecimalMin(value = "0.0001", message = "数量必须大于0")
        private BigDecimal quantity;

        /**
         * 单位ID
         */
        @NotNull(message = "单位不能为空")
        private Long unitId;

        /**
         * 入库成本
         */
        @DecimalMin(value = "0", message = "成本不能为负数")
        private BigDecimal unitCost;

        /**
         * 备注
         */
        private String remark;
    }
}
