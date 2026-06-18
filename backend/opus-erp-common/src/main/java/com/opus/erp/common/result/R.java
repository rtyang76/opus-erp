package com.opus.erp.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码（0=成功，非0=失败）
     */
    private int code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 私有构造方法
     */
    private R() {}

    /**
     * 成功（无数据）
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功（有数据）
     */
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    /**
     * 成功（自定义消息，无数据）
     */
    public static <T> R<T> okMsg(String msg) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    /**
     * 成功（自定义消息和数据）
     */
    public static <T> R<T> ok(String msg, T data) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 失败（错误码）
     */
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    /**
     * 失败（使用 ErrorCode 枚举）
     */
    public static <T> R<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMsg());
    }

    /**
     * 失败（使用 ErrorCode 枚举 + 自定义消息）
     */
    public static <T> R<T> fail(ErrorCode errorCode, String msg) {
        return fail(errorCode.getCode(), msg);
    }

    /**
     * 参数错误
     */
    public static <T> R<T> paramError(String msg) {
        return fail(ErrorCode.PARAM_ERROR.getCode(), msg);
    }

    /**
     * 数据不存在
     */
    public static <T> R<T> notFound(String msg) {
        return fail(ErrorCode.NOT_FOUND.getCode(), msg);
    }

    /**
     * 数据重复
     */
    public static <T> R<T> duplicate(String msg) {
        return fail(ErrorCode.DUPLICATE.getCode(), msg);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == 0;
    }
}
