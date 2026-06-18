package com.opus.erp.master.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物料 DTO
 */
@Data
public class ItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物料编码
     */
    @NotBlank(message = "物料编码不能为空")
    @Size(max = 50, message = "物料编码长度不能超过50个字符")
    private String itemCode;

    /**
     * 物料名称
     */
    @NotBlank(message = "物料名称不能为空")
    @Size(max = 200, message = "物料名称长度不能超过200个字符")
    private String itemName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 主单位ID
     */
    private Long unitId;

    /**
     * 辅助单位ID
     */
    private Long auxUnitId;

    /**
     * 单位换算系数
     */
    private BigDecimal unitFactor;

    /**
     * 安全库存
     */
    private BigDecimal safetyStock;

    /**
     * ABC分类（A/B/C）
     */
    private String abcClass;

    /**
     * 物料类型（RAW/SEMI/FINISHED/AUXILIARY）
     */
    private String itemType;

    /**
     * 默认仓库ID
     */
    private Long defaultWarehouseId;

    /**
     * 保质期天数
     */
    private Integer shelfLifeDays;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
