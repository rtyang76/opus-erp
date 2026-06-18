package com.opus.erp.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.sales.dto.SoOrderDTO;
import com.opus.erp.sales.entity.SoOrder;
import com.opus.erp.sales.service.SoOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 销售订单控制器
 */
@RestController
@RequestMapping("/api/sales/orders")
@RequiredArgsConstructor
public class SoOrderController {

    private final SoOrderService orderService;

    /**
     * 分页查询销售订单
     */
    @GetMapping
    public R<Page<SoOrder>> listOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long salesmanId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<SoOrder> page = orderService.listOrders(pageNum, pageSize, orderNo,
                customerId, salesmanId, status, startDate, endDate);
        return R.ok(page);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public R<SoOrder> getOrder(@PathVariable Long id) {
        SoOrder order = orderService.getOrderDetail(id);
        return R.ok(order);
    }

    /**
     * 创建销售订单
     */
    @PostMapping
    public R<SoOrder> createOrder(@Valid @RequestBody SoOrderDTO dto) {
        SoOrder createdOrder = orderService.createFromDTO(dto);
        return R.ok("创建成功", createdOrder);
    }

    /**
     * 更新销售订单
     */
    @PutMapping("/{id}")
    public R<SoOrder> updateOrder(@PathVariable Long id, @Valid @RequestBody SoOrderDTO dto) {
        SoOrder updatedOrder = orderService.updateFromDTO(id, dto);
        return R.ok("更新成功", updatedOrder);
    }

    /**
     * 审核销售订单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditOrder(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        orderService.auditOrder(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 销售发货
     */
    @PostMapping("/{id}/ship")
    public R<Void> shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
        return R.okMsg("发货成功");
    }

    /**
     * 取消销售订单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return R.okMsg("取消成功");
    }
}
