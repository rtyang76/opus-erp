package com.opus.erp.report.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.report.dto.InventorySummaryDTO;
import com.opus.erp.report.dto.ProductionProgressDTO;
import com.opus.erp.report.dto.PurchaseSummaryDTO;
import com.opus.erp.report.dto.SalesProfitDTO;
import com.opus.erp.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 报表控制器
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private static final int MAX_PAGE_SIZE = 200;

    private final ReportService reportService;

    /**
     * 收发存汇总报表
     */
    @GetMapping("/inventory-summary")
    public R<Page<InventorySummaryDTO>> getInventorySummary(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long warehouseId) {
        // 限制 pageSize 上限，防止全表扫描
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        Page<InventorySummaryDTO> page = reportService.getInventorySummary(
                pageNum, pageSize, startDate, endDate, itemId, warehouseId);
        return R.ok(page);
    }

    /**
     * 销售毛利简表
     */
    @GetMapping("/sales-profit")
    public R<Page<SalesProfitDTO>> getSalesProfit(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        // 限制 pageSize 上限，防止全表扫描
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        Page<SalesProfitDTO> page = reportService.getSalesProfit(
                pageNum, pageSize, startDate, endDate, customerId);
        return R.ok(page);
    }

    /**
     * 采购汇总表
     */
    @GetMapping("/purchase-summary")
    public R<Page<PurchaseSummaryDTO>> getPurchaseSummary(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long supplierId) {
        // 限制 pageSize 上限，防止全表扫描
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        Page<PurchaseSummaryDTO> page = reportService.getPurchaseSummary(
                pageNum, pageSize, startDate, endDate, supplierId);
        return R.ok(page);
    }

    /**
     * 生产进度表
     */
    @GetMapping("/production-progress")
    public R<Page<ProductionProgressDTO>> getProductionProgress(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        // 限制 pageSize 上限，防止全表扫描
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        Page<ProductionProgressDTO> page = reportService.getProductionProgress(pageNum, pageSize, status);
        return R.ok(page);
    }
}
