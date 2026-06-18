package com.opus.erp.purchase.enums;

import lombok.Getter;

/**
 * 采购订单状态枚举
 */
@Getter
public enum PoOrderStatus {

    /**
     * 草稿
     */
    DRAFT("DRAFT", "草稿"),

    /**
     * 已审核
     */
    AUDITED("AUDITED", "已审核"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED", "已关闭"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String name;

    PoOrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
