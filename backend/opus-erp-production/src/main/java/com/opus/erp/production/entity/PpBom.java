package com.opus.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * BOM 主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pp_bom")
public class PpBom extends BaseEntity {

    /**
     * BOM编码
     */
    private String bomCode;

    /**
     * 成品/半成品物料ID
     */
    private Long itemId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 基准数量
     */
    private BigDecimal baseQuantity;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    // ========== 关联查询字段 ==========

    /**
     * 物料编码
     */
    @TableField(exist = false)
    private String itemCode;

    /**
     * 物料名称
     */
    @TableField(exist = false)
    private String itemName;

    /**
     * 规格型号
     */
    @TableField(exist = false)
    private String specification;

    /**
     * BOM明细列表
     */
    @TableField(exist = false)
    private List<PpBomDetail> details;
}
