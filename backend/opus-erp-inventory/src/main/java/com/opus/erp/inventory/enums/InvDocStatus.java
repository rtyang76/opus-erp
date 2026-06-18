package com.opus.erp.inventory.enums;

import lombok.Getter;

/**
 * 库存单据状态枚举
 */
@Getter
public enum InvDocStatus {

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

    InvDocStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
