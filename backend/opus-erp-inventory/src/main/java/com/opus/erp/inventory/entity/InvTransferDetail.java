package com.opus.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 调拨单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_transfer_detail")
public class InvTransferDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨单ID
     */
    private Long transferId;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 批次号
     */
    private String lotNo;

    /**
     * 调拨数量
     */
    private BigDecimal quantity;

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
}
