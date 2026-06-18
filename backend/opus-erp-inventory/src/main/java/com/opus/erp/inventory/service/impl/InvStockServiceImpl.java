package com.opus.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.inventory.entity.InvStock;
import com.opus.erp.inventory.entity.InvTransaction;
import com.opus.erp.inventory.mapper.InvStockMapper;
import com.opus.erp.inventory.mapper.InvTransactionMapper;
import com.opus.erp.inventory.service.InvStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 库存服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvStockServiceImpl extends ServiceImpl<InvStockMapper, InvStock> implements InvStockService {

    private final InvTransactionMapper transactionMapper;

    @Override
    public Page<InvStock> listStock(int pageNum, int pageSize, Long itemId, Long warehouseId,
                                    String lotNo, Integer minQuantity) {
        Page<InvStock> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InvStock> wrapper = new LambdaQueryWrapper<>();

        if (itemId != null) {
            wrapper.eq(InvStock::getItemId, itemId);
        }
        if (warehouseId != null) {
            wrapper.eq(InvStock::getWarehouseId, warehouseId);
        }
        if (StringUtils.hasText(lotNo)) {
            wrapper.like(InvStock::getLotNo, lotNo);
        }
        if (minQuantity != null) {
            wrapper.ge(InvStock::getQuantity, minQuantity);
        }

        wrapper.orderByDesc(InvStock::getUpdatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<InvTransaction> listTransactions(int pageNum, int pageSize, String transactionType,
                                                  Long itemId, Long warehouseId,
                                                  String startDate, String endDate) {
        Page<InvTransaction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InvTransaction> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(transactionType)) {
            wrapper.eq(InvTransaction::getTransactionType, transactionType);
        }
        if (itemId != null) {
            wrapper.eq(InvTransaction::getItemId, itemId);
        }
        if (warehouseId != null) {
            wrapper.eq(InvTransaction::getWarehouseId, warehouseId);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(InvTransaction::getTransactionDate, LocalDate.parse(startDate));
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(InvTransaction::getTransactionDate, LocalDate.parse(endDate));
        }

        wrapper.orderByDesc(InvTransaction::getCreatedAt);
        return transactionMapper.selectPage(page, wrapper);
    }
}
