package com.opus.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.inventory.dto.InvTransferDTO;
import com.opus.erp.inventory.entity.InvTransfer;
import com.opus.erp.inventory.service.InvTransferService;
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
 * 调拨管理控制器
 */
@RestController
@RequestMapping("/api/inventory/transfers")
@RequiredArgsConstructor
public class InvTransferController {

    private final InvTransferService transferService;

    /**
     * 分页查询调拨单
     */
    @GetMapping
    public R<Page<InvTransfer>> listTransfers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String transferNo,
            @RequestParam(required = false) Long fromWarehouseId,
            @RequestParam(required = false) Long toWarehouseId,
            @RequestParam(required = false) String status) {
        Page<InvTransfer> page = transferService.listTransfers(pageNum, pageSize,
                transferNo, fromWarehouseId, toWarehouseId, status);
        return R.ok(page);
    }

    /**
     * 创建调拨单
     */
    @PostMapping
    public R<InvTransfer> createTransfer(@Valid @RequestBody InvTransferDTO dto) {
        InvTransfer createdTransfer = transferService.createFromDTO(dto);
        return R.ok("创建成功", createdTransfer);
    }

    /**
     * 审核调拨单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditTransfer(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        transferService.auditTransfer(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 取消调拨单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelTransfer(@PathVariable Long id) {
        transferService.cancelTransfer(id);
        return R.okMsg("取消成功");
    }
}
