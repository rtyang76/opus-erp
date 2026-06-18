package com.opus.erp.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 单号生成器
 * 使用原子计数器确保并发安全
 */
public class OrderNoGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 生成单号
     * 格式：前缀 + 日期(8位) + 序列号(6位)
     * 示例：PO20240117000001
     *
     * @param prefix 单号前缀
     * @return 单号
     */
    public static String generate(String prefix) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long seq = SEQUENCE.incrementAndGet() % 1000000;
        return prefix + dateStr + String.format("%06d", seq);
    }

    /**
     * 生成采购订单号
     */
    public static String generatePoOrderNo() {
        return generate("PO");
    }

    /**
     * 生成采购入库单号
     */
    public static String generatePoReceiptNo() {
        return generate("PR");
    }

    /**
     * 生成销售订单号
     */
    public static String generateSoOrderNo() {
        return generate("SO");
    }

    /**
     * 生成销售出库单号
     */
    public static String generateSoShipmentNo() {
        return generate("SH");
    }

    /**
     * 生成库存调拨单号
     */
    public static String generateTransferNo() {
        return generate("TR");
    }

    /**
     * 生成库存盘点单号
     */
    public static String generateStocktakeNo() {
        return generate("ST");
    }

    /**
     * 生成库存交易号
     */
    public static String generateTransactionNo() {
        return generate("TX");
    }

    /**
     * 生成生产工单号
     */
    public static String generateWorkOrderNo() {
        return generate("WO");
    }

    /**
     * 生成领料单号
     */
    public static String generateMaterialIssueNo() {
        return generate("MI");
    }
}
