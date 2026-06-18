package com.opus.erp.inventory.enums;

import lombok.Getter;

/**
 * 库存变动原因代码枚举
 */
@Getter
public enum InvReasonCode {

    /**
     * 采购入库
     */
    PURCHASE_RECEIPT("PURCHASE_RECEIPT", "采购入库"),

    /**
     * 生产入库
     */
    PRODUCTION_RECEIPT("PRODUCTION_RECEIPT", "生产入库"),

    /**
     * 销售出库
     */
    SALES_SHIPMENT("SALES_SHIPMENT", "销售出库"),

    /**
     * 生产领料
     */
    PRODUCTION_ISSUE("PRODUCTION_ISSUE", "生产领料"),

    /**
     * 调拨出库
     */
    TRANSFER_OUT("TRANSFER_OUT", "调拨出库"),

    /**
     * 调拨入库
     */
    TRANSFER_IN("TRANSFER_IN", "调拨入库"),

    /**
     * 盘盈
     */
    STOCKTAKE_PROFIT("STOCKTAKE_PROFIT", "盘盈"),

    /**
     * 盘亏
     */
    STOCKTAKE_LOSS("STOCKTAKE_LOSS", "盘亏"),

    /**
     * 其他入库
     */
    OTHER_RECEIPT("OTHER_RECEIPT", "其他入库"),

    /**
     * 其他出库
     */
    OTHER_ISSUE("OTHER_ISSUE", "其他出库");

    private final String code;
    private final String name;

    InvReasonCode(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
