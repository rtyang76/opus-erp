package com.opus.erp.sales.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opus.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 销售出库单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("so_shipment_detail")
public class SoShipmentDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 出库单ID
     */
    private Long shipmentId;

    /**
     * 关联订单明细ID
     */
    private Long orderDetailId;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 批次号
     */
    private String lotNo;

    /**
     * 出库数量
     */
    private BigDecimal quantity;

    /**
     * 单位ID
     */
    private Long unitId;

    /**
     * 出库单价
     */
    private BigDecimal unitPrice;

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
     * 单位名称
     */
    @TableField(exist = false)
    private String unitName;
}
