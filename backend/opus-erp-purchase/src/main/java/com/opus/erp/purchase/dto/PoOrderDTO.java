package com.opus.erp.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 采购订单 DTO
 */
@Data
public class PoOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 默认税率
     */
    public static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("13.00");

    /**
     * 供应商ID
     */
    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    /**
     * 订单日期
     */
    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;

    /**
     * 交货日期
     */
    private LocalDate deliveryDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单明细
     */
    @NotNull(message = "订单明细不能为空")
    private List<PoOrderDetailDTO> details;

    /**
     * 采购订单明细 DTO
     */
    @Data
    public static class PoOrderDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 物料ID
         */
        @NotNull(message = "物料不能为空")
        private Long itemId;

        /**
         * 规格型号
         */
        private String specification;

        /**
         * 订购数量
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
         * 单价
         */
        @NotNull(message = "单价不能为空")
        @DecimalMin(value = "0.000001", message = "单价必须大于0")
        private BigDecimal unitPrice;

        /**
         * 税率
         */
        private BigDecimal taxRate = DEFAULT_TAX_RATE;

        /**
         * 备注
         */
        private String remark;
    }
}
