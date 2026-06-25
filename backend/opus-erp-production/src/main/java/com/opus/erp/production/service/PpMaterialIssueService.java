package com.opus.erp.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.production.dto.MaterialIssueDTO;
import com.opus.erp.production.entity.PpMaterialIssue;

/**
 * 领料单服务接口
 */
public interface PpMaterialIssueService extends IService<PpMaterialIssue> {

    /**
     * 分页查询领料单列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param issueNo 领料单号
     * @param workOrderId 工单ID
     * @param issueType 类型（ISSUE/RETURN）
     * @param status 状态
     * @return 分页结果
     */
    Page<PpMaterialIssue> listIssues(int pageNum, int pageSize, String issueNo,
                                      Long workOrderId, String issueType, String status);

    /**
     * 查询领料单详情
     * @param issueId 领料单ID
     * @return 领料单详情
     */
    PpMaterialIssue getIssueDetail(Long issueId);

    /**
     * 创建领料单
     * @param issue 领料单信息
     * @return 创建的领料单
     */
    PpMaterialIssue createIssue(PpMaterialIssue issue);

    /**
     * 审核领料单（调用 InvTransactionService）
     * @param issueId 领料单ID
     * @param auditorId 审核人ID
     */
    void auditIssue(Long issueId, Long auditorId);

    /**
     * 取消领料单
     * @param issueId 领料单ID
     */
    void cancelIssue(Long issueId);

    /**
     * 根据 DTO 创建领料单
     * @param dto 领料单 DTO
     * @return 创建的领料单
     */
    PpMaterialIssue createFromDTO(MaterialIssueDTO dto);
}
