package com.opus.erp.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.finance.entity.FinPayable;
import com.opus.erp.finance.service.FinPayableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 应付单 Controller
 */
@RestController
@RequestMapping("/api/finance/payables")
@RequiredArgsConstructor
public class PayableController {

    private final FinPayableService payableService;

    /**
     * 分页查询应付单
     */
    @GetMapping
    public R<Page<FinPayable>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String status) {
        return R.ok(payableService.getPage(pageNum, pageSize, supplierId, status));
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public R<FinPayable> getById(@PathVariable Long id) {
        return R.ok(payableService.getById(id));
    }

    /**
     * 创建应付单
     */
    @PostMapping
    public R<FinPayable> create(@Valid @RequestBody FinPayable payable) {
        return R.ok(payableService.create(payable));
    }

    /**
     * 更新应付单
     */
    @PutMapping("/{id}")
    public R<FinPayable> update(@PathVariable Long id, @Valid @RequestBody FinPayable payable) {
        return R.ok(payableService.update(id, payable));
    }

    /**
     * 删除应付单
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        payableService.delete(id);
        return R.ok();
    }
}
