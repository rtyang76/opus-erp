package com.opus.erp.common.result;

import lombok.Getter;

/**
 * 错误码枚举
 * 定义系统中所有的错误码和对应的错误消息
 */
@Getter
public enum ErrorCode {

    // 成功
    SUCCESS(0, "操作成功"),

    // 通用错误 1xxx
    PARAM_ERROR(1001, "参数错误"),
    NOT_FOUND(1002, "数据不存在"),
    DUPLICATE(1003, "数据重复"),
    OPTIMISTIC_LOCK(1004, "数据已被他人修改，请刷新后重试"),

    // 认证授权 2xxx
    UNAUTHORIZED(2001, "未登录或登录已过期"),
    FORBIDDEN(2002, "无权限执行此操作"),
    TOKEN_EXPIRED(2003, "Token已过期"),
    TOKEN_INVALID(2004, "Token无效"),
    ACCOUNT_DISABLED(2005, "账号已被禁用"),
    LOGIN_FAILED(2006, "用户名或密码错误"),

    // 业务错误 3xxx
    STOCK_INSUFFICIENT(3001, "库存不足"),
    ORDER_STATUS_INVALID(3002, "单据状态不允许此操作"),
    BOM_CIRCULAR(3003, "BOM存在循环引用"),
    CREDIT_EXCEEDED(3004, "超出信用额度"),
    RECEIPT_QUANTITY_EXCEED(3005, "入库数量超过可收数量"),
    SHIPMENT_QUANTITY_EXCEED(3006, "出库数量超过可用库存"),
    TRANSFER_QUANTITY_EXCEED(3007, "调拨数量超过可用库存"),

    // 系统错误 9xxx
    SYSTEM_ERROR(9999, "系统内部错误");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
