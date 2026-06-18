package com.opus.erp.production.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 领料单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pp_material_issue")
public class PpMaterialIssue extends BaseEntity {

    /**
     * 领料单号
     */
    private String issueNo;

    /**
     * 关联工单ID
     */
    private Long workOrderId;

    /**
     * 领料仓库ID
     */
    private Long warehouseId;

    /**
     * 领料日期
     */
    private LocalDate issueDate;

    /**
     * 类型（ISSUE=领料, RETURN=退料）
     */
    private String issueType;

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
     * 工单号
     */
    @TableField(exist = false)
    private String workOrderNo;

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;

    /**
     * 领料明细列表
     */
    @TableField(exist = false)
    private List<PpMaterialIssueDetail> details;
}
