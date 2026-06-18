package com.opus.erp.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.purchase.dto.PoReceiptDTO;
import com.opus.erp.purchase.entity.PoReceipt;
import com.opus.erp.purchase.service.PoReceiptService;
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
 * 采购入库控制器
 */
@RestController
@RequestMapping("/api/purchase/receipts")
@RequiredArgsConstructor
public class PoReceiptController {

    private final PoReceiptService receiptService;

    /**
     * 分页查询采购入库单
     */
    @GetMapping
    public R<Page<PoReceipt>> listReceipts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String receiptNo,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String status) {
        Page<PoReceipt> page = receiptService.listReceipts(pageNum, pageSize, receiptNo,
                orderId, supplierId, status);
        return R.ok(page);
    }

    /**
     * 查询入库单详情
     */
    @GetMapping("/{id}")
    public R<PoReceipt> getReceipt(@PathVariable Long id) {
        PoReceipt receipt = receiptService.getReceiptDetail(id);
        return R.ok(receipt);
    }

    /**
     * 创建采购入库单
     */
    @PostMapping
    public R<PoReceipt> createReceipt(@Valid @RequestBody PoReceiptDTO dto) {
        PoReceipt createdReceipt = receiptService.createFromDTO(dto);
        return R.ok("创建成功", createdReceipt);
    }

    /**
     * 审核采购入库单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditReceipt(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        receiptService.auditReceipt(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 取消采购入库单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelReceipt(@PathVariable Long id) {
        receiptService.cancelReceipt(id);
        return R.okMsg("取消成功");
    }
}
