package com.opus.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.production.entity.PpMaterialIssue;
import com.opus.erp.production.entity.PpMaterialIssueDetail;
import com.opus.erp.production.enums.MaterialIssueStatus;
import com.opus.erp.production.enums.MaterialIssueType;
import com.opus.erp.production.mapper.PpMaterialIssueDetailMapper;
import com.opus.erp.production.mapper.PpMaterialIssueMapper;
import com.opus.erp.production.service.PpMaterialIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 领料单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PpMaterialIssueServiceImpl extends ServiceImpl<PpMaterialIssueMapper, PpMaterialIssue> implements PpMaterialIssueService {

    private final PpMaterialIssueDetailMapper issueDetailMapper;
    private final InvTransactionService transactionService;

    @Override
    public Page<PpMaterialIssue> listIssues(int pageNum, int pageSize, String issueNo,
                                             Long workOrderId, String issueType, String status) {
        Page<PpMaterialIssue> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PpMaterialIssue> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(issueNo)) {
            wrapper.like(PpMaterialIssue::getIssueNo, issueNo);
        }
        if (workOrderId != null) {
            wrapper.eq(PpMaterialIssue::getWorkOrderId, workOrderId);
        }
        if (StringUtils.hasText(issueType)) {
            wrapper.eq(PpMaterialIssue::getIssueType, issueType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PpMaterialIssue::getStatus, status);
        }

        wrapper.orderByDesc(PpMaterialIssue::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public PpMaterialIssue getIssueDetail(Long issueId) {
        PpMaterialIssue issue = baseMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "领料单不存在");
        }

        // 查询明细
        LambdaQueryWrapper<PpMaterialIssueDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PpMaterialIssueDetail::getIssueId, issueId);
        issue.setDetails(issueDetailMapper.selectList(wrapper));

        return issue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PpMaterialIssue createIssue(PpMaterialIssue issue) {
        // 生成领料单号
        issue.setIssueNo(generateIssueNo());
        issue.setStatus(MaterialIssueStatus.DRAFT.getCode());

        // 保存主表
        baseMapper.insert(issue);

        // 保存明细
        if (issue.getDetails() != null) {
            for (PpMaterialIssueDetail detail : issue.getDetails()) {
                detail.setIssueId(issue.getId());
                issueDetailMapper.insert(detail);
            }
        }

        log.info("创建领料单成功: issueNo={}", issue.getIssueNo());
        return issue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditIssue(Long issueId, Long auditorId) {
        PpMaterialIssue issue = baseMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "领料单不存在");
        }
        if (!MaterialIssueStatus.DRAFT.getCode().equals(issue.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的领料单可以审核");
        }

        // 查询明细
        LambdaQueryWrapper<PpMaterialIssueDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PpMaterialIssueDetail::getIssueId, issueId);
        var details = issueDetailMapper.selectList(wrapper);

        // 调用库存服务
        for (PpMaterialIssueDetail detail : details) {
            if (MaterialIssueType.ISSUE.getCode().equals(issue.getIssueType())) {
                // 领料出库
                // 校验可用库存
                if (!transactionService.checkAvailableStock(
                        detail.getItemId(), issue.getWarehouseId(), null, detail.getLotNo(), detail.getQuantity())) {
                    throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                            String.format("物料[%d] 库存不足", detail.getItemId()));
                }

                transactionService.createIssue(
                        detail.getItemId(),
                        issue.getWarehouseId(),
                        null,
                        detail.getLotNo(),
                        detail.getQuantity(),
                        null,  // 使用移动加权平均成本
                        "WO",
                        issue.getWorkOrderId(),
                        issue.getIssueNo(),
                        "PRODUCTION_ISSUE",
                        detail.getRemark()
                );
            } else {
                // 退料入库
                transactionService.createReceipt(
                        detail.getItemId(),
                        issue.getWarehouseId(),
                        null,
                        detail.getLotNo(),
                        detail.getQuantity(),
                        null,  // 使用移动加权平均成本
                        "WO",
                        issue.getWorkOrderId(),
                        issue.getIssueNo(),
                        "PRODUCTION_RETURN",
                        detail.getRemark()
                );
            }
        }

        // 更新领料单状态
        issue.setStatus(MaterialIssueStatus.AUDITED.getCode());
        issue.setAuditedBy(auditorId);
        issue.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(issue);

        log.info("审核领料单成功: issueId={}", issueId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelIssue(Long issueId) {
        PpMaterialIssue issue = baseMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "领料单不存在");
        }
        if (!MaterialIssueStatus.DRAFT.getCode().equals(issue.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的领料单可以取消");
        }

        issue.setStatus(MaterialIssueStatus.CANCELLED.getCode());
        baseMapper.updateById(issue);

        log.info("取消领料单成功: issueId={}", issueId);
    }

    /**
     * 生成领料单号
     * 使用统一的 OrderNoGenerator 工具类
     */
    private String generateIssueNo() {
        return OrderNoGenerator.generateMaterialIssueNo();
    }
}
