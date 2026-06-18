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
 * 调拨单主表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_transfer")
public class InvTransfer extends BaseEntity {

    /**
     * 调拨单号
     */
    private String transferNo;

    /**
     * 调出仓库ID
     */
    private Long fromWarehouseId;

    /**
     * 调入仓库ID
     */
    private Long toWarehouseId;

    /**
     * 调拨日期
     */
    private LocalDate transferDate;

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
     * 调出仓库名称
     */
    @TableField(exist = false)
    private String fromWarehouseName;

    /**
     * 调入仓库名称
     */
    @TableField(exist = false)
    private String toWarehouseName;

    /**
     * 调拨明细列表
     */
    @TableField(exist = false)
    private List<InvTransferDetail> details;
}
