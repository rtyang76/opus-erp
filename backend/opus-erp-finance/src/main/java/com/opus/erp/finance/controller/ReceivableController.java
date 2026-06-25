package com.opus.erp.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.finance.entity.FinReceivable;
import com.opus.erp.finance.service.FinReceivableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 应收单 Controller
 */
@RestController
@RequestMapping("/api/finance/receivables")
@RequiredArgsConstructor
public class ReceivableController {

    private final FinReceivableService receivableService;

    /**
     * 分页查询应收单
     */
    @GetMapping
    public R<Page<FinReceivable>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status) {
        return R.ok(receivableService.getPage(pageNum, pageSize, customerId, status));
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public R<FinReceivable> getById(@PathVariable Long id) {
        return R.ok(receivableService.getById(id));
    }

    /**
     * 创建应收单
     */
    @PostMapping
    public R<FinReceivable> create(@Valid @RequestBody FinReceivable receivable) {
        return R.ok(receivableService.create(receivable));
    }

    /**
     * 更新应收单
     */
    @PutMapping("/{id}")
    public R<FinReceivable> update(@PathVariable Long id, @Valid @RequestBody FinReceivable receivable) {
        return R.ok(receivableService.update(id, receivable));
    }

    /**
     * 删除应收单
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        receivableService.delete(id);
        return R.ok();
    }
}
