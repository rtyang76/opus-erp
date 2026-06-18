package com.opus.erp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收发存汇总报表 DTO
 */
@Data
public class InventorySummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物料ID
     */
    private Long itemId;

    /**
     * 物料编码
     */
    private String itemCode;

    /**
     * 物料名称
     */
    private String itemName;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 期初数量
     */
    private BigDecimal beginQuantity;

    /**
     * 期初金额
     */
    private BigDecimal beginAmount;

    /**
     * 本期入库数量
     */
    private BigDecimal receiptQuantity;

    /**
     * 本期入库金额
     */
    private BigDecimal receiptAmount;

    /**
     * 本期出库数量
     */
    private BigDecimal issueQuantity;

    /**
     * 本期出库金额
     */
    private BigDecimal issueAmount;

    /**
     * 期末数量
     */
    private BigDecimal endQuantity;

    /**
     * 期末金额
     */
    private BigDecimal endAmount;
}
