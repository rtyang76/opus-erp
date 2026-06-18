package com.opus.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 盘点单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_stocktake")
public class InvStocktake extends BaseEntity {

    /**
     * 盘点单号
     */
    private String stocktakeNo;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 盘点日期
     */
    private LocalDate stocktakeDate;

    /**
     * 状态（DRAFT/AUDITED/CANCELLED）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核人ID
     */
    private Long auditedBy;

    /**
     * 审核时间
     */
    private LocalDateTime auditedAt;

    // ========== 关联查询字段 ==========

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;

    /**
     * 盘点明细列表
     */
    @TableField(exist = false)
    private List<InvStocktakeDetail> details;
}
