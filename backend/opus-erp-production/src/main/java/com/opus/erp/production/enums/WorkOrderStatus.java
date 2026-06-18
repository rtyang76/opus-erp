package com.opus.erp.production.enums;

import lombok.Getter;

/**
 * 生产工单状态枚举
 */
@Getter
public enum WorkOrderStatus {

    /**
     * 待下达
     */
    PENDING("PENDING", "待下达"),

    /**
     * 已下达
     */
    RELEASED("RELEASED", "已下达"),

    /**
     * 生产中
     */
    IN_PROGRESS("IN_PROGRESS", "生产中"),

    /**
     * 已完工
     */
    COMPLETED("COMPLETED", "已完工"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED", "已关闭");

    private final String code;
    private final String name;

    WorkOrderStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
