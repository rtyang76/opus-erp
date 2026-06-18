package com.opus.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 即时库存实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_stock")
public class InvStock extends BaseEntity {

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 库位ID
     */
    private Long binId;

    /**
     * 批次号
     */
    private String lotNo;

    /**
     * 库存数量
     */
    private BigDecimal quantity;

    /**
     * 移动加权平均成本
     */
    private BigDecimal avgCost;

    /**
     * 锁定数量（待出库）
     */
    private BigDecimal lockedQuantity;

    // ========== 计算列（数据库自动计算） ==========

    /**
     * 库存金额 = 数量 × 平均成本
     */
    @TableField(exist = false)
    private BigDecimal totalCost;

    /**
     * 可用数量 = 库存数量 - 锁定数量
     */
    @TableField(exist = false)
    private BigDecimal availableQuantity;

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
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;
}
