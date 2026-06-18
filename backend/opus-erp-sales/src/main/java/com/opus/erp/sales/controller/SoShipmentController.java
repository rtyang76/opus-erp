package com.opus.erp.sales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.sales.dto.SoShipmentDTO;
import com.opus.erp.sales.entity.SoShipment;
import com.opus.erp.sales.service.SoShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 销售出库控制器
 */
@RestController
@RequestMapping("/api/sales/shipments")
@RequiredArgsConstructor
public class SoShipmentController {

    private final SoShipmentService shipmentService;

    /**
     * 分页查询销售出库单
     */
    @GetMapping
    public R<Page<SoShipment>> listShipments(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String shipmentNo,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status) {
        Page<SoShipment> page = shipmentService.listShipments(pageNum, pageSize, shipmentNo,
                orderId, customerId, status);
        return R.ok(page);
    }

    /**
     * 查询出库单详情
     */
    @GetMapping("/{id}")
    public R<SoShipment> getShipment(@PathVariable Long id) {
        SoShipment shipment = shipmentService.getShipmentDetail(id);
        return R.ok(shipment);
    }

    /**
     * 创建销售出库单
     */
    @PostMapping
    public R<SoShipment> createShipment(@Valid @RequestBody SoShipmentDTO dto) {
        SoShipment createdShipment = shipmentService.createFromDTO(dto);
        return R.ok("创建成功", createdShipment);
    }

    /**
     * 审核销售出库单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditShipment(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        shipmentService.auditShipment(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 取消销售出库单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelShipment(@PathVariable Long id) {
        shipmentService.cancelShipment(id);
        return R.okMsg("取消成功");
    }
}
