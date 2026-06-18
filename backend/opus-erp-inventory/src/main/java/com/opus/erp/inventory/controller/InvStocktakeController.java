package com.opus.erp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.inventory.dto.InvStocktakeDTO;
import com.opus.erp.inventory.entity.InvStocktake;
import com.opus.erp.inventory.service.InvStocktakeService;
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
 * 盘点管理控制器
 */
@RestController
@RequestMapping("/api/inventory/stocktakes")
@RequiredArgsConstructor
public class InvStocktakeController {

    private final InvStocktakeService stocktakeService;

    /**
     * 分页查询盘点单
     */
    @GetMapping
    public R<Page<InvStocktake>> listStocktakes(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String stocktakeNo,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String status) {
        Page<InvStocktake> page = stocktakeService.listStocktakes(pageNum, pageSize,
                stocktakeNo, warehouseId, status);
        return R.ok(page);
    }

    /**
     * 创建盘点单
     */
    @PostMapping
    public R<InvStocktake> createStocktake(@Valid @RequestBody InvStocktakeDTO dto) {
        InvStocktake createdStocktake = stocktakeService.createFromDTO(dto);
        return R.ok("创建成功", createdStocktake);
    }

    /**
     * 审核盘点单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditStocktake(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        stocktakeService.auditStocktake(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 取消盘点单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelStocktake(@PathVariable Long id) {
        stocktakeService.cancelStocktake(id);
        return R.okMsg("取消成功");
    }
}
