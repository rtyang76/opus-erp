package com.opus.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.production.dto.WorkOrderDTO;
import com.opus.erp.production.entity.PpWorkOrder;
import com.opus.erp.production.service.PpWorkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 生产工单控制器
 */
@RestController
@RequestMapping("/api/production/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final PpWorkOrderService workOrderService;

    /**
     * 分页查询工单列表
     */
    @GetMapping
    public R<Page<PpWorkOrder>> listWorkOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) String status) {
        Page<PpWorkOrder> page = workOrderService.listWorkOrders(pageNum, pageSize, orderNo, itemId, status);
        return R.ok(page);
    }

    /**
     * 查询工单详情
     */
    @GetMapping("/{id}")
    public R<PpWorkOrder> getWorkOrder(@PathVariable Long id) {
        PpWorkOrder order = workOrderService.getWorkOrderDetail(id);
        return R.ok(order);
    }

    /**
     * 创建工单
     */
    @PostMapping
    public R<PpWorkOrder> createWorkOrder(@Valid @RequestBody WorkOrderDTO dto) {
        PpWorkOrder createdOrder = workOrderService.createFromDTO(dto);
        return R.ok("创建成功", createdOrder);
    }

    /**
     * 下达工单
     */
    @PostMapping("/{id}/release")
    public R<Void> releaseWorkOrder(@PathVariable Long id) {
        Long releasedBy = SecurityUtils.getCurrentUserId();
        workOrderService.releaseWorkOrder(id, releasedBy);
        return R.okMsg("下达成功");
    }

    /**
     * 开工
     */
    @PostMapping("/{id}/start")
    public R<Void> startWorkOrder(@PathVariable Long id) {
        workOrderService.startWorkOrder(id);
        return R.okMsg("开工成功");
    }

    /**
     * 完工入库
     */
    @PostMapping("/{id}/complete")
    public R<Void> completeWorkOrder(@PathVariable Long id, @RequestParam BigDecimal completedQuantity) {
        workOrderService.completeWorkOrder(id, completedQuantity);
        return R.okMsg("完工成功");
    }

    /**
     * 关闭工单
     */
    @PostMapping("/{id}/close")
    public R<Void> closeWorkOrder(@PathVariable Long id) {
        workOrderService.closeWorkOrder(id);
        return R.okMsg("关闭成功");
    }
}
