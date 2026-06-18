package com.opus.erp.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.production.dto.MaterialIssueDTO;
import com.opus.erp.production.entity.PpMaterialIssue;
import com.opus.erp.production.entity.PpMaterialIssueDetail;
import com.opus.erp.production.service.PpMaterialIssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
     * TODO: DTO-to-Entity 转换应移入 Service 层，Controller 只做参数校验和调用 Service
     * 当前实现：Controller 中完成转换，后续重构为 Service 接受 DTO 参数
     */
    @PostMapping
    public R<PpMaterialIssue> createIssue(@Valid @RequestBody MaterialIssueDTO dto) {
        PpMaterialIssue issue = new PpMaterialIssue();
        BeanUtils.copyProperties(dto, issue);

        // 转换明细
        if (dto.getDetails() != null) {
            List<PpMaterialIssueDetail> details = new ArrayList<>();
            for (MaterialIssueDTO.MaterialIssueDetailDTO detailDTO : dto.getDetails()) {
                PpMaterialIssueDetail detail = new PpMaterialIssueDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            issue.setDetails(details);
        }

        PpMaterialIssue createdIssue = issueService.createIssue(issue);
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
