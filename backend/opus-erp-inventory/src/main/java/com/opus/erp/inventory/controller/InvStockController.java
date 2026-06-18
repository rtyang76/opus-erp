package com.opus.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.inventory.entity.InvStock;
import com.opus.erp.inventory.entity.InvTransaction;
import com.opus.erp.inventory.service.InvStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存查询控制器
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InvStockController {

    private final InvStockService stockService;

    /**
     * 查询即时库存列表
     */
    @GetMapping("/stock")
    public R<Page<InvStock>> listStock(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String lotNo,
            @RequestParam(required = false) Integer minQuantity) {
        Page<InvStock> page = stockService.listStock(pageNum, pageSize, itemId, warehouseId, lotNo, minQuantity);
        return R.ok(page);
    }

    /**
     * 查询库存流水
     */
    @GetMapping("/transactions")
    public R<Page<InvTransaction>> listTransactions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<InvTransaction> page = stockService.listTransactions(pageNum, pageSize,
                transactionType, itemId, warehouseId, startDate, endDate);
        return R.ok(page);
    }
}
