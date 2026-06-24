package com.opus.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.inventory.entity.InvStock;
import com.opus.erp.inventory.entity.InvTransaction;
import com.opus.erp.inventory.enums.InvReasonCode;
import com.opus.erp.inventory.enums.InvTransactionType;
import com.opus.erp.inventory.mapper.InvStockMapper;
import com.opus.erp.inventory.mapper.InvTransactionMapper;
import com.opus.erp.inventory.service.InvTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 库存交易服务实现类（核心）
 * 所有库存变动必须通过此服务处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvTransactionServiceImpl implements InvTransactionService {

    private final InvStockMapper stockMapper;
    private final InvTransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvTransaction createReceipt(Long itemId, Long warehouseId, Long binId, String lotNo,
                                        BigDecimal quantity, BigDecimal unitCost,
                                        String referenceType, Long referenceId, String referenceNo,
                                        String reasonCode, String remark) {
        // 校验参数
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "入库数量必须大于0");
        }
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "单位成本不能为负数");
        }

        // 更新库存（移动加权平均法）
        // 并发控制说明：
        // 1. updateQuantity 使用乐观锁（WHERE quantity >= -quantity for 出库）
        // 2. 如果 update 返回 0，说明库存记录不存在，需要插入
        // 3. 插入时可能遇到并发冲突（唯一约束 uq_inv_stock），捕获异常后重试 update
        int affected = stockMapper.updateQuantity(itemId, warehouseId, binId, lotNo, quantity, unitCost);
        if (affected == 0) {
            // 库存记录不存在，创建新记录
            // 注意：高并发场景下可能触发唯一约束异常，需要重试
            InvStock stock = new InvStock();
            stock.setItemId(itemId);
            stock.setWarehouseId(warehouseId);
            stock.setBinId(binId);
            stock.setLotNo(lotNo);
            stock.setQuantity(quantity);
            stock.setAvgCost(unitCost);
            stock.setLockedQuantity(BigDecimal.ZERO);
            try {
                stockMapper.insert(stock);
            } catch (Exception e) {
                // 并发插入冲突，重试 update
                affected = stockMapper.updateQuantity(itemId, warehouseId, binId, lotNo, quantity, unitCost);
                if (affected == 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "入库失败，请重试");
                }
            }
        }

        // 记录流水
        InvTransaction transaction = createTransaction(
                InvTransactionType.RECEIPT.getCode(), itemId, warehouseId, binId, lotNo,
                quantity, unitCost,
                referenceType, referenceId, referenceNo,
                reasonCode, remark
        );

        log.info("入库成功: itemId={}, warehouseId={}, quantity={}, unitCost={}",
                itemId, warehouseId, quantity, unitCost);

        return transaction;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvTransaction createIssue(Long itemId, Long warehouseId, Long binId, String lotNo,
                                      BigDecimal quantity, BigDecimal unitCost,
                                      String referenceType, Long referenceId, String referenceNo,
                                      String reasonCode, String remark) {
        // 校验参数
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "出库数量必须大于0");
        }

        // 校验可用库存
        if (!checkAvailableStock(itemId, warehouseId, binId, lotNo, quantity)) {
            throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                    String.format("库存不足: 物料[%d] 在仓库[%d] 可用数量不足，请求出库数量为 %s",
                            itemId, warehouseId, quantity.toPlainString()));
        }

        // 获取当前平均成本（用于出库成本计算）
        BigDecimal currentAvgCost = getCurrentAvgCost(itemId, warehouseId, binId, lotNo);
        BigDecimal actualUnitCost = unitCost != null ? unitCost : currentAvgCost;

        // 更新库存（带负数保护）
        // 使用 updateQuantityForIssue 方法，只有当可用库存 >= 出库数量时才允许更新
        int affected = stockMapper.updateQuantityForIssue(itemId, warehouseId, binId, lotNo, quantity, actualUnitCost);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                    String.format("库存不足: 物料[%d] 在仓库[%d] 可用数量不足，请求出库数量为 %s",
                            itemId, warehouseId, quantity.toPlainString()));
        }

        // 记录流水（数量为负数）
        BigDecimal negativeQuantity = quantity.negate();
        InvTransaction transaction = createTransaction(
                InvTransactionType.ISSUE.getCode(), itemId, warehouseId, binId, lotNo,
                negativeQuantity, actualUnitCost,
                referenceType, referenceId, referenceNo,
                reasonCode, remark
        );

        log.info("出库成功: itemId={}, warehouseId={}, quantity={}",
                itemId, warehouseId, quantity);

        return transaction;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransfer(Long itemId, Long fromWarehouseId, Long toWarehouseId,
                               Long binId, String lotNo, BigDecimal quantity, BigDecimal unitCost,
                               String referenceType, Long referenceId, String referenceNo, String remark) {
        // 校验参数
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "调拨数量必须大于0");
        }

        // 校验调出仓库可用库存
        if (!checkAvailableStock(itemId, fromWarehouseId, binId, lotNo, quantity)) {
            throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                    String.format("调出仓库库存不足: 物料[%d] 在仓库[%d] 可用数量不足",
                            itemId, fromWarehouseId));
        }

        // 获取当前平均成本
        // 调拨成本逻辑说明：
        // 1. 优先使用传入的 unitCost（如果有）
        // 2. 如果未传入，使用调出仓库的移动加权平均成本
        // 3. 调入仓库使用相同成本（成本跟随物料转移）
        BigDecimal currentAvgCost = getCurrentAvgCost(itemId, fromWarehouseId, binId, lotNo);
        BigDecimal actualUnitCost = unitCost != null ? unitCost : currentAvgCost;

        // 调出（出库）- 从调出仓库扣减库存
        BigDecimal negativeQuantity = quantity.negate();
        stockMapper.updateQuantity(itemId, fromWarehouseId, binId, lotNo, negativeQuantity, actualUnitCost);
        createTransaction(
                InvTransactionType.TRANSFER.getCode(), itemId, fromWarehouseId, binId, lotNo,
                negativeQuantity, actualUnitCost,
                referenceType, referenceId, referenceNo,
                InvReasonCode.TRANSFER_OUT.getCode(), remark
        );

        // 调入（入库）- 向调入仓库增加库存，使用相同成本
        stockMapper.updateQuantity(itemId, toWarehouseId, binId, lotNo, quantity, actualUnitCost);
        createTransaction(
                InvTransactionType.TRANSFER.getCode(), itemId, toWarehouseId, binId, lotNo,
                quantity, actualUnitCost,
                referenceType, referenceId, referenceNo,
                InvReasonCode.TRANSFER_IN.getCode(), remark
        );

        log.info("调拨成功: itemId={}, from={}, to={}, quantity={}",
                itemId, fromWarehouseId, toWarehouseId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvTransaction createAdjustment(Long itemId, Long warehouseId, Long binId, String lotNo,
                                           BigDecimal quantity, BigDecimal unitCost,
                                           String referenceType, Long referenceId, String referenceNo,
                                           String reasonCode, String remark) {
        // 校验参数
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "调整数量不能为0");
        }

        // 获取当前平均成本
        BigDecimal currentAvgCost = getCurrentAvgCost(itemId, warehouseId, binId, lotNo);
        BigDecimal actualUnitCost = unitCost != null ? unitCost : currentAvgCost;

        // 如果是盘亏（负数），校验可用库存
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal absQuantity = quantity.abs();
            if (!checkAvailableStock(itemId, warehouseId, binId, lotNo, absQuantity)) {
                throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                        "盘亏数量超过可用库存");
            }
        }

        // 更新库存
        int affected = stockMapper.updateQuantity(itemId, warehouseId, binId, lotNo, quantity, actualUnitCost);
        if (affected == 0 && quantity.compareTo(BigDecimal.ZERO) > 0) {
            // 库存记录不存在，创建新记录（盘盈）
            // 盘盈时如果成本为零，使用默认成本（避免零成本库存）
            if (actualUnitCost.compareTo(BigDecimal.ZERO) == 0) {
                actualUnitCost = new BigDecimal("0.01");  // 默认最小成本
            }
            InvStock stock = new InvStock();
            stock.setItemId(itemId);
            stock.setWarehouseId(warehouseId);
            stock.setBinId(binId);
            stock.setLotNo(lotNo);
            stock.setQuantity(quantity);
            stock.setAvgCost(actualUnitCost);
            stock.setLockedQuantity(BigDecimal.ZERO);
            stockMapper.insert(stock);
        }

        // 记录流水
        InvTransaction transaction = createTransaction(
                InvTransactionType.ADJUSTMENT.getCode(), itemId, warehouseId, binId, lotNo,
                quantity, actualUnitCost,
                referenceType, referenceId, referenceNo,
                reasonCode, remark
        );

        log.info("库存调整成功: itemId={}, warehouseId={}, quantity={}, reason={}",
                itemId, warehouseId, quantity, reasonCode);

        return transaction;
    }

    @Override
    public boolean checkAvailableStock(Long itemId, Long warehouseId, Long binId, String lotNo,
                                       BigDecimal requiredQuantity) {
        BigDecimal available = stockMapper.getAvailableQuantity(itemId, warehouseId, binId, lotNo);
        return available != null && available.compareTo(requiredQuantity) >= 0;
    }

    /**
     * 创建交易流水记录
     */
    private InvTransaction createTransaction(String transactionType, Long itemId, Long warehouseId,
                                              Long binId, String lotNo, BigDecimal quantity,
                                              BigDecimal unitCost, String referenceType,
                                              Long referenceId, String referenceNo,
                                              String reasonCode, String remark) {
        InvTransaction transaction = new InvTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setItemId(itemId);
        transaction.setWarehouseId(warehouseId);
        transaction.setBinId(binId);
        transaction.setLotNo(lotNo);
        transaction.setQuantity(quantity);
        transaction.setUnitCost(unitCost);
        transaction.setTotalCost(quantity.abs().multiply(unitCost).setScale(2, RoundingMode.HALF_UP));
        transaction.setReferenceType(referenceType);
        transaction.setReferenceId(referenceId);
        transaction.setReferenceNo(referenceNo);
        transaction.setReasonCode(reasonCode);
        transaction.setRemark(remark);
        transaction.setCreatedBy(SecurityUtils.getCurrentUserId());
        transaction.setPosted(false);

        transactionMapper.insert(transaction);
        return transaction;
    }

    /**
     * 生成交易流水号
     */
    private String generateTransactionNo() {
        return OrderNoGenerator.generateTransactionNo();
    }

    /**
     * 获取当前平均成本
     * 从 inv_stock 表查询当前库存的移动加权平均成本
     */
    private BigDecimal getCurrentAvgCost(Long itemId, Long warehouseId, Long binId, String lotNo) {
        LambdaQueryWrapper<InvStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvStock::getItemId, itemId);
        wrapper.eq(InvStock::getWarehouseId, warehouseId);
        if (binId != null) {
            wrapper.eq(InvStock::getBinId, binId);
        }
        if (StringUtils.hasText(lotNo)) {
            wrapper.eq(InvStock::getLotNo, lotNo);
        }
        InvStock stock = stockMapper.selectOne(wrapper);
        return stock != null ? stock.getAvgCost() : BigDecimal.ZERO;
    }
}
