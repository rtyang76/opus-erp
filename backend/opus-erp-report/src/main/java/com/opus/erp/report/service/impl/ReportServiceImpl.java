package com.opus.erp.report.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.master.entity.MdmItem;
import com.opus.erp.master.service.MdmItemService;
import com.opus.erp.production.entity.PpWorkOrder;
import com.opus.erp.production.enums.WorkOrderStatus;
import com.opus.erp.production.service.PpWorkOrderService;
import com.opus.erp.report.dto.InventorySummaryDTO;
import com.opus.erp.report.dto.ProductionProgressDTO;
import com.opus.erp.report.dto.PurchaseSummaryDTO;
import com.opus.erp.report.dto.SalesProfitDTO;
import com.opus.erp.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表服务实现类
 * 注意：报表查询通过 Service 接口获取数据，不直接跨模块调用 Mapper
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PpWorkOrderService workOrderService;
    private final MdmItemService itemService;

    @Override
    public Page<InventorySummaryDTO> getInventorySummary(int pageNum, int pageSize,
                                                          LocalDate startDate, LocalDate endDate,
                                                          Long itemId, Long warehouseId) {
        Page<InventorySummaryDTO> page = new Page<>(pageNum, pageSize);

        // TODO: 实现完整的收发存汇总查询
        // 方案：在 report 模块创建自定义 Mapper，编写复杂聚合 SQL
        // 需要：
        // 1. 查询期初库存（startDate 之前的累计）
        // 2. 查询本期入库（RECEIPT 类型）
        // 3. 查询本期出库（ISSUE 类型）
        // 4. 计算期末库存

        log.info("查询收发存汇总: startDate={}, endDate={}, itemId={}, warehouseId={}",
                startDate, endDate, itemId, warehouseId);

        return page;
    }

    @Override
    public Page<SalesProfitDTO> getSalesProfit(int pageNum, int pageSize,
                                                LocalDate startDate, LocalDate endDate,
                                                Long customerId) {
        Page<SalesProfitDTO> page = new Page<>(pageNum, pageSize);

        // TODO: 实现完整的销售毛利查询
        // 方案：在 report 模块创建自定义 Mapper，编写复杂聚合 SQL
        // 需要：
        // 1. 查询销售出库记录（SO 类型）
        // 2. 按客户/物料汇总销售金额
        // 3. 计算成本（使用移动加权平均成本）
        // 4. 计算毛利和毛利率

        log.info("查询销售毛利: startDate={}, endDate={}, customerId={}",
                startDate, endDate, customerId);

        return page;
    }

    @Override
    public Page<PurchaseSummaryDTO> getPurchaseSummary(int pageNum, int pageSize,
                                                        LocalDate startDate, LocalDate endDate,
                                                        Long supplierId) {
        Page<PurchaseSummaryDTO> page = new Page<>(pageNum, pageSize);

        // TODO: 实现完整的采购汇总查询
        // 方案：在 report 模块创建自定义 Mapper，编写复杂聚合 SQL
        // 需要：
        // 1. 查询采购入库记录（PO 类型）
        // 2. 按供应商汇总采购金额
        // 3. 统计订单数量

        log.info("查询采购汇总: startDate={}, endDate={}, supplierId={}",
                startDate, endDate, supplierId);

        return page;
    }

    @Override
    public Page<ProductionProgressDTO> getProductionProgress(int pageNum, int pageSize, String status) {
        // 通过 Service 接口查询工单列表
        Page<PpWorkOrder> workOrderPage = workOrderService.listWorkOrders(pageNum, pageSize, null, null, status);

        // 转换为 DTO
        Page<ProductionProgressDTO> result = new Page<>(pageNum, pageSize);
        result.setTotal(workOrderPage.getTotal());

        List<ProductionProgressDTO> dtoList = new ArrayList<>();
        for (PpWorkOrder order : workOrderPage.getRecords()) {
            ProductionProgressDTO dto = new ProductionProgressDTO();
            dto.setWorkOrderId(order.getId());
            dto.setOrderNo(order.getOrderNo());

            // 关联查询物料信息
            if (order.getItemId() != null) {
                MdmItem item = itemService.getById(order.getItemId());
                if (item != null) {
                    dto.setItemCode(item.getItemCode());
                    dto.setItemName(item.getItemName());
                } else {
                    dto.setItemCode("");
                    dto.setItemName("");
                }
            } else {
                dto.setItemCode("");
                dto.setItemName("");
            }

            dto.setPlanQuantity(order.getQuantity());
            dto.setCompletedQuantity(order.getCompletedQuantity());
            dto.setPlanStartDate(order.getPlanStartDate());
            dto.setPlanEndDate(order.getPlanEndDate());
            dto.setStatus(order.getStatus());
            // 使用枚举获取状态名称
            dto.setStatusName(WorkOrderStatus.valueOf(order.getStatus()).getName());

            // 计算完成率
            if (order.getQuantity() != null && order.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = order.getCompletedQuantity()
                        .divide(order.getQuantity(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                dto.setCompletionRate(rate);
            } else {
                dto.setCompletionRate(BigDecimal.ZERO);
            }

            dtoList.add(dto);
        }
        result.setRecords(dtoList);

        log.info("查询生产进度: status={}", status);
        return result;
    }
}
