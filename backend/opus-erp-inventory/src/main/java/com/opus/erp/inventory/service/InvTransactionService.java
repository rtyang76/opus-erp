package com.opus.erp.inventory.service;

import com.opus.erp.inventory.entity.InvTransaction;

import java.math.BigDecimal;

/**
 * 库存交易服务接口（核心）
 * 所有库存变动必须通过此服务处理
 */
public interface InvTransactionService {

    /**
     * 入库（采购入库、生产入库、其他入库）
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param quantity 入库数量（正数）
     * @param unitCost 单位成本
     * @param referenceType 参考单据类型（PO/WO/MANUAL）
     * @param referenceId 参考单据ID
     * @param referenceNo 参考单据号
     * @param reasonCode 原因代码
     * @param remark 备注
     * @return 库存交易记录
     */
    InvTransaction createReceipt(Long itemId, Long warehouseId, Long binId, String lotNo,
                                 BigDecimal quantity, BigDecimal unitCost,
                                 String referenceType, Long referenceId, String referenceNo,
                                 String reasonCode, String remark);

    /**
     * 出库（销售出库、生产领料、其他出库）
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param quantity 出库数量（正数，内部转为负数）
     * @param unitCost 单位成本（可空，使用移动加权平均成本）
     * @param referenceType 参考单据类型（SO/WO/MANUAL）
     * @param referenceId 参考单据ID
     * @param referenceNo 参考单据号
     * @param reasonCode 原因代码
     * @param remark 备注
     * @return 库存交易记录
     */
    InvTransaction createIssue(Long itemId, Long warehouseId, Long binId, String lotNo,
                               BigDecimal quantity, BigDecimal unitCost,
                               String referenceType, Long referenceId, String referenceNo,
                               String reasonCode, String remark);

    /**
     * 调拨（一出一入两条流水）
     * @param itemId 物料ID
     * @param fromWarehouseId 调出仓库ID
     * @param toWarehouseId 调入仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param quantity 调拨数量（正数）
     * @param unitCost 单位成本
     * @param referenceType 参考单据类型（TRANSFER）
     * @param referenceId 参考单据ID
     * @param referenceNo 参考单据号
     * @param remark 备注
     */
    void createTransfer(Long itemId, Long fromWarehouseId, Long toWarehouseId,
                        Long binId, String lotNo, BigDecimal quantity, BigDecimal unitCost,
                        String referenceType, Long referenceId, String referenceNo, String remark);

    /**
     * 调整（盘盈入库、盘亏出库）
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param quantity 调整数量（正数=盘盈入库，负数=盘亏出库）
     * @param unitCost 单位成本
     * @param referenceType 参考单据类型（STOCKTAKE）
     * @param referenceId 参考单据ID
     * @param referenceNo 参考单据号
     * @param reasonCode 原因代码
     * @param remark 备注
     * @return 库存交易记录
     */
    InvTransaction createAdjustment(Long itemId, Long warehouseId, Long binId, String lotNo,
                                    BigDecimal quantity, BigDecimal unitCost,
                                    String referenceType, Long referenceId, String referenceNo,
                                    String reasonCode, String remark);

    /**
     * 校验可用库存是否充足
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param binId 库位ID（可空）
     * @param lotNo 批次号（可空）
     * @param requiredQuantity 需要数量
     * @return true=充足，false=不足
     */
    boolean checkAvailableStock(Long itemId, Long warehouseId, Long binId, String lotNo,
                                BigDecimal requiredQuantity);
}
