package com.opus.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.purchase.dto.PoOrderDTO;
import com.opus.erp.purchase.entity.PoOrder;
import com.opus.erp.purchase.service.PoOrderService;
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
 * 采购订单控制器
 */
@RestController
@RequestMapping("/api/purchase/orders")
@RequiredArgsConstructor
public class PoOrderController {

    private final PoOrderService orderService;

    /**
     * 分页查询采购订单
     */
    @GetMapping
    public R<Page<PoOrder>> listOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<PoOrder> page = orderService.listOrders(pageNum, pageSize, orderNo,
                supplierId, status, startDate, endDate);
        return R.ok(page);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public R<PoOrder> getOrder(@PathVariable Long id) {
        PoOrder order = orderService.getOrderDetail(id);
        return R.ok(order);
    }

    /**
     * 创建采购订单
     */
    @PostMapping
    public R<PoOrder> createOrder(@Valid @RequestBody PoOrderDTO dto) {
        PoOrder createdOrder = orderService.createFromDTO(dto);
        return R.ok("创建成功", createdOrder);
    }

    /**
     * 更新采购订单
     */
    @PutMapping("/{id}")
    public R<PoOrder> updateOrder(@PathVariable Long id, @Valid @RequestBody PoOrderDTO dto) {
        PoOrder updatedOrder = orderService.updateFromDTO(id, dto);
        return R.ok("更新成功", updatedOrder);
    }

    /**
     * 审核采购订单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditOrder(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        orderService.auditOrder(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 取消采购订单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return R.okMsg("取消成功");
    }

    /**
     * 关闭采购订单
     */
    @PostMapping("/{id}/close")
    public R<Void> closeOrder(@PathVariable Long id) {
        orderService.closeOrder(id);
        return R.okMsg("关闭成功");
    }
}
