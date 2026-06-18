package com.opus.erp.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 调拨单 DTO
 */
@Data
public class InvTransferDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调出仓库ID
     */
    @NotNull(message = "调出仓库不能为空")
    private Long fromWarehouseId;

    /**
     * 调入仓库ID
     */
    @NotNull(message = "调入仓库不能为空")
    private Long toWarehouseId;

    /**
     * 调拨日期
     */
    @NotNull(message = "调拨日期不能为空")
    private LocalDate transferDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 调拨明细
     */
    @NotNull(message = "调拨明细不能为空")
    private List<InvTransferDetailDTO> details;

    /**
     * 调拨明细 DTO
     */
    @Data
    public static class InvTransferDetailDTO implements Serializable {

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
         * 调拨数量
         */
        @NotNull(message = "调拨数量不能为空")
        @DecimalMin(value = "0.0001", message = "调拨数量必须大于0")
        private BigDecimal quantity;

        /**
         * 备注
         */
        private String remark;
    }
}
