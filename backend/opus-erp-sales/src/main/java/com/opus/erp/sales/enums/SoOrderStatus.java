package com.opus.erp.sales.enums;

import lombok.Getter;

/**
 * 销售订单状态枚举
 */
@Getter
public enum SoOrderStatus {

    /**
     * 草稿
     */
    DRAFT("DRAFT", "草稿"),

    /**
     * 已审核
     */
    AUDITED("AUDITED", "已审核"),

    /**
     * 已发货
     */
    SHIPPED("SHIPPED", "已发货"),

    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String name;

    SoOrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
