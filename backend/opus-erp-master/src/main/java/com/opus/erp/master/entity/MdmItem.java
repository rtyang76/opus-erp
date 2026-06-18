package com.opus.erp.master.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物料档案实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mdm_item")
public class MdmItem extends BaseEntity {

    /**
     * 物料编码
     */
    private String itemCode;

    /**
     * 物料名称
     */
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

    // ========== 非数据库字段 ==========

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 主单位名称
     */
    @TableField(exist = false)
    private String unitName;

    /**
     * 辅助单位名称
     */
    @TableField(exist = false)
    private String auxUnitName;

    /**
     * 默认仓库名称
     */
    @TableField(exist = false)
    private String defaultWarehouseName;
}
