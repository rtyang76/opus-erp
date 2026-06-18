package com.opus.erp.sales.enums;

import lombok.Getter;

/**
 * 销售出库单状态枚举
 */
@Getter
public enum SoShipmentStatus {

    /**
     * 草稿
     */
    DRAFT("DRAFT", "草稿"),

    /**
     * 已审核
     */
    AUDITED("AUDITED", "已审核"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String name;

    SoShipmentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
