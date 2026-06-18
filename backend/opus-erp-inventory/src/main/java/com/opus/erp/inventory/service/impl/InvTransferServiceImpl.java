package com.opus.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.inventory.dto.InvTransferDTO;
import com.opus.erp.inventory.entity.InvTransfer;
import com.opus.erp.inventory.entity.InvTransferDetail;
import com.opus.erp.inventory.enums.InvDocStatus;
import com.opus.erp.inventory.mapper.InvTransferDetailMapper;
import com.opus.erp.inventory.mapper.InvTransferMapper;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.inventory.service.InvTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 调拨服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvTransferServiceImpl extends ServiceImpl<InvTransferMapper, InvTransfer> implements InvTransferService {

    private final InvTransferDetailMapper transferDetailMapper;
    private final InvTransactionService transactionService;

    @Override
    public Page<InvTransfer> listTransfers(int pageNum, int pageSize, String transferNo,
                                            Long fromWarehouseId, Long toWarehouseId, String status) {
        Page<InvTransfer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InvTransfer> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(transferNo)) {
            wrapper.like(InvTransfer::getTransferNo, transferNo);
        }
        if (fromWarehouseId != null) {
            wrapper.eq(InvTransfer::getFromWarehouseId, fromWarehouseId);
        }
        if (toWarehouseId != null) {
            wrapper.eq(InvTransfer::getToWarehouseId, toWarehouseId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(InvTransfer::getStatus, status);
        }

        wrapper.orderByDesc(InvTransfer::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvTransfer createTransfer(InvTransfer transfer) {
        // 生成调拨单号
        transfer.setTransferNo(generateTransferNo());
        transfer.setStatus(InvDocStatus.DRAFT.getCode());

        // 保存主表
        baseMapper.insert(transfer);

        // 保存明细
        if (transfer.getDetails() != null) {
            for (InvTransferDetail detail : transfer.getDetails()) {
                detail.setTransferId(transfer.getId());
                transferDetailMapper.insert(detail);
            }
        }

        log.info("创建调拨单成功: transferNo={}", transfer.getTransferNo());
        return transfer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditTransfer(Long transferId, Long auditorId) {
        InvTransfer transfer = baseMapper.selectById(transferId);
        if (transfer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "调拨单不存在");
        }
        if (!InvDocStatus.DRAFT.getCode().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的调拨单可以审核");
        }

        // 查询明细
        LambdaQueryWrapper<InvTransferDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvTransferDetail::getTransferId, transferId);
        var details = transferDetailMapper.selectList(wrapper);

        // 执行调拨（一出一入）
        for (InvTransferDetail detail : details) {
            transactionService.createTransfer(
                    detail.getItemId(),
                    transfer.getFromWarehouseId(),
                    transfer.getToWarehouseId(),
                    null,
                    detail.getLotNo(),
                    detail.getQuantity(),
                    null,  // 使用移动加权平均成本
                    "TRANSFER",
                    transferId,
                    transfer.getTransferNo(),
                    detail.getRemark()
            );
        }

        // 更新状态
        transfer.setStatus(InvDocStatus.AUDITED.getCode());
        transfer.setAuditedBy(auditorId);
        transfer.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(transfer);

        log.info("审核调拨单成功: transferId={}", transferId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTransfer(Long transferId) {
        InvTransfer transfer = baseMapper.selectById(transferId);
        if (transfer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "调拨单不存在");
        }
        if (!InvDocStatus.DRAFT.getCode().equals(transfer.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的调拨单可以取消");
        }

        transfer.setStatus(InvDocStatus.CANCELLED.getCode());
        baseMapper.updateById(transfer);

        log.info("取消调拨单成功: transferId={}", transferId);
    }

    /**
     * 生成调拨单号
     */
    private String generateTransferNo() {
        return OrderNoGenerator.generateTransferNo();
    }

    @Override
    public InvTransfer createFromDTO(InvTransferDTO dto) {
        InvTransfer transfer = new InvTransfer();
        BeanUtils.copyProperties(dto, transfer);

        // 转换明细
        if (dto.getDetails() != null) {
            java.util.List<InvTransferDetail> details = new java.util.ArrayList<>();
            for (InvTransferDTO.InvTransferDetailDTO detailDTO : dto.getDetails()) {
                InvTransferDetail detail = new InvTransferDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            transfer.setDetails(details);
        }

        return createTransfer(transfer);
    }
}
