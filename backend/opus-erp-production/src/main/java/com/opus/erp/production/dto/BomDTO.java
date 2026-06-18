package com.opus.erp.production.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * BOM DTO
 */
@Data
public class BomDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * BOM编码
     */
    @NotBlank(message = "BOM编码不能为空")
    private String bomCode;

    /**
     * 成品/半成品物料ID
     */
    @NotNull(message = "物料不能为空")
    private Long itemId;

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 基准数量
     */
    @DecimalMin(value = "1", message = "基准数量必须大于0")
    private BigDecimal baseQuantity = BigDecimal.ONE;

    /**
     * 状态
     */
    private Integer status = 1;

    /**
     * 备注
     */
    private String remark;

    /**
     * BOM明细
     */
    @NotNull(message = "BOM明细不能为空")
    private List<BomDetailDTO> details;

    /**
     * BOM明细 DTO
     */
    @Data
    public static class BomDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 子件物料ID
         */
        @NotNull(message = "子件物料不能为空")
        private Long itemId;

        /**
         * 单位用量
         */
        @NotNull(message = "单位用量不能为空")
        @DecimalMin(value = "0.000001", message = "单位用量必须大于0")
        private BigDecimal quantity;

        /**
         * 损耗率%
         */
        @DecimalMin(value = "0", message = "损耗率不能为负数")
        private BigDecimal lossRate = BigDecimal.ZERO;

        /**
         * 备注
         */
        private String remark;
    }
}
