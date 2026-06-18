package com.opus.erp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购汇总表 DTO
 */
@Data
public class PurchaseSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 采购订单数
     */
    private Integer orderCount;

    /**
     * 采购数量
     */
    private BigDecimal purchaseQuantity;

    /**
     * 采购金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 入库数量
     */
    private BigDecimal receiptQuantity;

    /**
     * 入库金额
     */
    private BigDecimal receiptAmount;
}
