package com.opus.erp.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售出库单 DTO
 */
@Data
public class SoShipmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联销售订单ID
     */
    private Long orderId;

    /**
     * 客户ID
     */
    @NotNull(message = "客户不能为空")
    private Long customerId;

    /**
     * 出库仓库ID
     */
    @NotNull(message = "出库仓库不能为空")
    private Long warehouseId;

    /**
     * 出库日期
     */
    @NotNull(message = "出库日期不能为空")
    private LocalDate shipmentDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 出库明细
     */
    @NotNull(message = "出库明细不能为空")
    private List<SoShipmentDetailDTO> details;

    /**
     * 销售出库明细 DTO
     */
    @Data
    public static class SoShipmentDetailDTO implements Serializable {

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
         * 出库数量
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
         * 出库单价
         */
        @DecimalMin(value = "0", message = "单价不能为负数")
        private BigDecimal unitPrice;

        /**
         * 备注
         */
        private String remark;
    }
}
