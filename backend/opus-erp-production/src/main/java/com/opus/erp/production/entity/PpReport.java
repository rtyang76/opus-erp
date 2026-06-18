package com.opus.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报工记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pp_report")
public class PpReport extends BaseEntity {

    /**
     * 报工单号
     */
    private String reportNo;

    /**
     * 关联工单ID
     */
    private Long workOrderId;

    /**
     * 报工日期
     */
    private LocalDate reportDate;

    /**
     * 合格数量
     */
    private BigDecimal qualifiedQuantity;

    /**
     * 不良数量
     */
    private BigDecimal defectQuantity;

    /**
     * 工时
     */
    private BigDecimal workHours;

    /**
     * 备注
     */
    private String remark;

    // ========== 关联查询字段 ==========

    /**
     * 工单号
     */
    @TableField(exist = false)
    private String workOrderNo;

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
}
