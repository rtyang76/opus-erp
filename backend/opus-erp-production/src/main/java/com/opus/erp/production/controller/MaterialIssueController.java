package com.opus.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.production.dto.MaterialIssueDTO;
import com.opus.erp.production.entity.PpMaterialIssue;
import com.opus.erp.production.service.PpMaterialIssueService;
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
 * 领料单控制器
 */
@RestController
@RequestMapping("/api/production/material-issues")
@RequiredArgsConstructor
public class MaterialIssueController {

    private final PpMaterialIssueService issueService;

    /**
     * 分页查询领料单列表
     */
    @GetMapping
    public R<Page<PpMaterialIssue>> listIssues(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String issueNo,
            @RequestParam(required = false) Long workOrderId,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String status) {
        Page<PpMaterialIssue> page = issueService.listIssues(pageNum, pageSize, issueNo,
                workOrderId, issueType, status);
        return R.ok(page);
    }

    /**
     * 查询领料单详情
     */
    @GetMapping("/{id}")
    public R<PpMaterialIssue> getIssue(@PathVariable Long id) {
        PpMaterialIssue issue = issueService.getIssueDetail(id);
        return R.ok(issue);
    }

    /**
     * 创建领料单
     */
    @PostMapping
    public R<PpMaterialIssue> createIssue(@Valid @RequestBody MaterialIssueDTO dto) {
        PpMaterialIssue createdIssue = issueService.createFromDTO(dto);
        return R.ok("创建成功", createdIssue);
    }

    /**
     * 审核领料单
     */
    @PostMapping("/{id}/audit")
    public R<Void> auditIssue(@PathVariable Long id) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        issueService.auditIssue(id, auditorId);
        return R.okMsg("审核成功");
    }

    /**
     * 取消领料单
     */
    @PostMapping("/{id}/cancel")
    public R<Void> cancelIssue(@PathVariable Long id) {
        issueService.cancelIssue(id);
        return R.okMsg("取消成功");
    }
}
