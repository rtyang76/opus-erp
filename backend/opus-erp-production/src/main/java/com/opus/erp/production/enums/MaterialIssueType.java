package com.opus.erp.production.enums;

import lombok.Getter;

/**
 * 领料类型枚举
 */
@Getter
public enum MaterialIssueType {

    /**
     * 领料
     */
    ISSUE("ISSUE", "领料"),

    /**
     * 退料
     */
    RETURN("RETURN", "退料");

    private final String code;
    private final String name;

    MaterialIssueType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
