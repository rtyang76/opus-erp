package com.opus.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.purchase.dto.PoReceiptDTO;
import com.opus.erp.purchase.entity.PoReceipt;
import com.opus.erp.purchase.entity.PoReceiptDetail;
import com.opus.erp.purchase.enums.PoReceiptStatus;
import com.opus.erp.purchase.mapper.PoReceiptDetailMapper;
import com.opus.erp.purchase.mapper.PoReceiptMapper;
import com.opus.erp.purchase.service.PoOrderService;
import com.opus.erp.purchase.service.PoReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 采购入库服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PoReceiptServiceImpl extends ServiceImpl<PoReceiptMapper, PoReceipt> implements PoReceiptService {

    private final PoReceiptDetailMapper receiptDetailMapper;
    private final PoOrderService orderService;
    private final InvTransactionService transactionService;

    @Override
    public Page<PoReceipt> listReceipts(int pageNum, int pageSize, String receiptNo,
                                         Long orderId, Long supplierId, String status) {
        Page<PoReceipt> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PoReceipt> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(receiptNo)) {
            wrapper.like(PoReceipt::getReceiptNo, receiptNo);
        }
        if (orderId != null) {
            wrapper.eq(PoReceipt::getOrderId, orderId);
        }
        if (supplierId != null) {
            wrapper.eq(PoReceipt::getSupplierId, supplierId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PoReceipt::getStatus, status);
        }

        wrapper.orderByDesc(PoReceipt::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public PoReceipt getReceiptDetail(Long receiptId) {
        PoReceipt receipt = baseMapper.selectById(receiptId);
        if (receipt == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购入库单不存在");
        }

        // 查询明细
        LambdaQueryWrapper<PoReceiptDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PoReceiptDetail::getReceiptId, receiptId);
        receipt.setDetails(receiptDetailMapper.selectList(wrapper));

        return receipt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PoReceipt createReceipt(PoReceipt receipt) {
        // 生成入库单号
        receipt.setReceiptNo(generateReceiptNo());
        receipt.setStatus(PoReceiptStatus.DRAFT.getCode());

        // 保存主表
        baseMapper.insert(receipt);

        // 保存明细
        if (receipt.getDetails() != null) {
            for (PoReceiptDetail detail : receipt.getDetails()) {
                detail.setReceiptId(receipt.getId());
                receiptDetailMapper.insert(detail);
            }
        }

        log.info("创建采购入库单成功: receiptNo={}", receipt.getReceiptNo());
        return receipt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReceipt(Long receiptId, Long auditorId) {
        PoReceipt receipt = baseMapper.selectById(receiptId);
        if (receipt == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购入库单不存在");
        }
        if (!PoReceiptStatus.DRAFT.getCode().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的入库单可以审核");
        }

        // 查询明细
        LambdaQueryWrapper<PoReceiptDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PoReceiptDetail::getReceiptId, receiptId);
        var details = receiptDetailMapper.selectList(wrapper);

        // 调用库存服务入库
        for (PoReceiptDetail detail : details) {
            transactionService.createReceipt(
                    detail.getItemId(),
                    receipt.getWarehouseId(),
                    null,
                    detail.getLotNo(),
                    detail.getQuantity(),
                    detail.getUnitCost(),
                    "PO",
                    receipt.getOrderId(),
                    receipt.getReceiptNo(),
                    "PURCHASE_RECEIPT",
                    detail.getRemark()
            );

            // 更新采购订单明细的已收货数量
            if (detail.getOrderDetailId() != null) {
                orderService.updateReceivedQuantity(detail.getOrderDetailId(), detail.getQuantity());
            }
        }

        // 更新入库单状态
        receipt.setStatus(PoReceiptStatus.AUDITED.getCode());
        receipt.setAuditedBy(auditorId);
        receipt.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(receipt);

        log.info("审核采购入库单成功: receiptId={}", receiptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReceipt(Long receiptId) {
        PoReceipt receipt = baseMapper.selectById(receiptId);
        if (receipt == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购入库单不存在");
        }
        if (!PoReceiptStatus.DRAFT.getCode().equals(receipt.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的入库单可以取消");
        }

        receipt.setStatus(PoReceiptStatus.CANCELLED.getCode());
        baseMapper.updateById(receipt);

        log.info("取消采购入库单成功: receiptId={}", receiptId);
    }

    /**
     * 生成采购入库单号
     */
    private String generateReceiptNo() {
        return OrderNoGenerator.generatePoReceiptNo();
    }

    @Override
    public PoReceipt createFromDTO(PoReceiptDTO dto) {
        PoReceipt receipt = new PoReceipt();
        BeanUtils.copyProperties(dto, receipt);

        // 转换明细
        if (dto.getDetails() != null) {
            java.util.List<PoReceiptDetail> details = new java.util.ArrayList<>();
            for (PoReceiptDTO.PoReceiptDetailDTO detailDTO : dto.getDetails()) {
                PoReceiptDetail detail = new PoReceiptDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            receipt.setDetails(details);
        }

        return createReceipt(receipt);
    }
}
