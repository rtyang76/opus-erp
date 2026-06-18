package com.opus.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * BOM 明细实体（子件）
 * 已添加 deleted 字段，继承 BaseEntity 支持逻辑删除
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pp_bom_detail")
public class PpBomDetail extends BaseEntity {

    /**
     * BOM主表ID
     */
    private Long bomId;

    /**
     * 子件物料ID
     */
    private Long itemId;

    /**
     * 单位用量
     */
    private BigDecimal quantity;

    /**
     * 损耗率%
     */
    private BigDecimal lossRate;

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
     * 单位名称
     */
    @TableField(exist = false)
    private String unitName;
}
