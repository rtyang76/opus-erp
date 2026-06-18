package com.opus.erp.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存交易流水实体（不可变日志）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_transaction")
public class InvTransaction extends BaseEntity {

    /**
     * 交易流水号
     */
    private String transactionNo;

    /**
     * 交易类型（RECEIPT/ISSUE/TRANSFER/ADJUSTMENT/RETURN）
     */
    private String transactionType;

    /**
     * 交易日期
     */
    private LocalDate transactionDate;

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
     * 数量（正数入库，负数出库）
     */
    private BigDecimal quantity;

    /**
     * 单位成本
     */
    private BigDecimal unitCost;

    /**
     * 总成本
     */
    private BigDecimal totalCost;

    /**
     * 参考单据类型（PO/SO/WO/MANUAL/TRANSFER）
     */
    private String referenceType;

    /**
     * 参考单据ID
     */
    private Long referenceId;

    /**
     * 参考单据号
     */
    private String referenceNo;

    /**
     * 原因代码
     */
    private String reasonCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 已过账标识
     */
    private Boolean posted;

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
     * 仓库名称
     */
    @TableField(exist = false)
    private String warehouseName;
}
