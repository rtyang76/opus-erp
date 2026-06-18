package com.opus.erp.inventory.enums;

import lombok.Getter;

/**
 * 库存交易类型枚举
 */
@Getter
public enum InvTransactionType {

    /**
     * 入库
     */
    RECEIPT("RECEIPT", "入库"),

    /**
     * 出库
     */
    ISSUE("ISSUE", "出库"),

    /**
     * 调拨
     */
    TRANSFER("TRANSFER", "调拨"),

    /**
     * 调整
     */
    ADJUSTMENT("ADJUSTMENT", "调整"),

    /**
     * 退货
     */
    RETURN("RETURN", "退货");

    private final String code;
    private final String name;

    InvTransactionType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
