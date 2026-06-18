package com.opus.erp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售毛利简表 DTO
 */
@Data
public class SalesProfitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

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
     * 销售数量
     */
    private BigDecimal salesQuantity;

    /**
     * 销售金额
     */
    private BigDecimal salesAmount;

    /**
     * 成本金额
     */
    private BigDecimal costAmount;

    /**
     * 毛利
     */
    private BigDecimal profit;

    /**
     * 毛利率
     */
    private BigDecimal profitRate;
}
