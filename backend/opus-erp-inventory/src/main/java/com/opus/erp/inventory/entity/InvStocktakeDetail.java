package com.opus.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 盘点单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_stocktake_detail")
public class InvStocktakeDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点单ID
     */
    private Long stocktakeId;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 批次号
     */
    private String lotNo;

    /**
     * 系统数量
     */
    private BigDecimal systemQuantity;

    /**
     * 实盘数量
     */
    private BigDecimal actualQuantity;

    /**
     * 备注
     */
    private String remark;

    // ========== 计算列（数据库自动计算） ==========

    /**
     * 差异数量 = 实盘数量 - 系统数量
     */
    @TableField(exist = false)
    private BigDecimal diffQuantity;

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
}
