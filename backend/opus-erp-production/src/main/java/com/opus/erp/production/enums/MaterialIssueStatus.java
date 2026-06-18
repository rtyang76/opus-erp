package com.opus.erp.production.enums;

import lombok.Getter;

/**
 * 领料单状态枚举
 */
@Getter
public enum MaterialIssueStatus {

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

    MaterialIssueStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
