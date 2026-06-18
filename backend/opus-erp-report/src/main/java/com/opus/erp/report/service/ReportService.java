package com.opus.erp.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.report.dto.InventorySummaryDTO;
import com.opus.erp.report.dto.ProductionProgressDTO;
import com.opus.erp.report.dto.PurchaseSummaryDTO;
import com.opus.erp.report.dto.SalesProfitDTO;

import java.time.LocalDate;

/**
 * 报表服务接口
 * 所有报表查询通过此服务处理
 */
public interface ReportService {

    /**
     * 收发存汇总报表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param itemId 物料ID（可选）
     * @param warehouseId 仓库ID（可选）
     * @return 分页结果
     */
    Page<InventorySummaryDTO> getInventorySummary(int pageNum, int pageSize,
                                                   LocalDate startDate, LocalDate endDate,
                                                   Long itemId, Long warehouseId);

    /**
     * 销售毛利简表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param customerId 客户ID（可选）
     * @return 分页结果
     */
    Page<SalesProfitDTO> getSalesProfit(int pageNum, int pageSize,
                                         LocalDate startDate, LocalDate endDate,
                                         Long customerId);

    /**
     * 采购汇总表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param supplierId 供应商ID（可选）
     * @return 分页结果
     */
    Page<PurchaseSummaryDTO> getPurchaseSummary(int pageNum, int pageSize,
                                                 LocalDate startDate, LocalDate endDate,
                                                 Long supplierId);

    /**
     * 生产进度表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<ProductionProgressDTO> getProductionProgress(int pageNum, int pageSize, String status);
}
