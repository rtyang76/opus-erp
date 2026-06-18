package com.opus.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.inventory.dto.InvStocktakeDTO;
import com.opus.erp.inventory.entity.InvStocktake;
import com.opus.erp.inventory.entity.InvStocktakeDetail;
import com.opus.erp.inventory.enums.InvDocStatus;
import com.opus.erp.inventory.enums.InvReasonCode;
import com.opus.erp.inventory.mapper.InvStocktakeDetailMapper;
import com.opus.erp.inventory.mapper.InvStocktakeMapper;
import com.opus.erp.inventory.service.InvStocktakeService;
import com.opus.erp.inventory.service.InvTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 盘点服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvStocktakeServiceImpl extends ServiceImpl<InvStocktakeMapper, InvStocktake> implements InvStocktakeService {

    private final InvStocktakeDetailMapper stocktakeDetailMapper;
    private final InvTransactionService transactionService;

    @Override
    public Page<InvStocktake> listStocktakes(int pageNum, int pageSize, String stocktakeNo,
                                              Long warehouseId, String status) {
        Page<InvStocktake> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InvStocktake> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(stocktakeNo)) {
            wrapper.like(InvStocktake::getStocktakeNo, stocktakeNo);
        }
        if (warehouseId != null) {
            wrapper.eq(InvStocktake::getWarehouseId, warehouseId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(InvStocktake::getStatus, status);
        }

        wrapper.orderByDesc(InvStocktake::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvStocktake createStocktake(InvStocktake stocktake) {
        // 生成盘点单号
        stocktake.setStocktakeNo(generateStocktakeNo());
        stocktake.setStatus(InvDocStatus.DRAFT.getCode());

        // 保存主表
        baseMapper.insert(stocktake);

        // 保存明细
        if (stocktake.getDetails() != null) {
            for (InvStocktakeDetail detail : stocktake.getDetails()) {
                detail.setStocktakeId(stocktake.getId());
                stocktakeDetailMapper.insert(detail);
            }
        }

        log.info("创建盘点单成功: stocktakeNo={}", stocktake.getStocktakeNo());
        return stocktake;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditStocktake(Long stocktakeId, Long auditorId) {
        InvStocktake stocktake = baseMapper.selectById(stocktakeId);
        if (stocktake == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "盘点单不存在");
        }
        if (!InvDocStatus.DRAFT.getCode().equals(stocktake.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的盘点单可以审核");
        }

        // 查询明细
        LambdaQueryWrapper<InvStocktakeDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvStocktakeDetail::getStocktakeId, stocktakeId);
        var details = stocktakeDetailMapper.selectList(wrapper);

        // 处理盈亏
        for (InvStocktakeDetail detail : details) {
            if (detail.getActualQuantity() == null) {
                continue;
            }

            BigDecimal diff = detail.getActualQuantity().subtract(
                    detail.getSystemQuantity() != null ? detail.getSystemQuantity() : BigDecimal.ZERO);

            if (diff.compareTo(BigDecimal.ZERO) != 0) {
                // 有差异，调用调整接口
                transactionService.createAdjustment(
                        detail.getItemId(),
                        stocktake.getWarehouseId(),
                        null,
                        detail.getLotNo(),
                        diff,  // 正数=盘盈入库，负数=盘亏出库
                        null,  // 使用移动加权平均成本
                        "STOCKTAKE",
                        stocktakeId,
                        stocktake.getStocktakeNo(),
                        diff.compareTo(BigDecimal.ZERO) > 0 ? InvReasonCode.STOCKTAKE_PROFIT.getCode() : InvReasonCode.STOCKTAKE_LOSS.getCode(),
                        detail.getRemark()
                );
            }
        }

        // 更新状态
        stocktake.setStatus(InvDocStatus.AUDITED.getCode());
        stocktake.setAuditedBy(auditorId);
        stocktake.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(stocktake);

        log.info("审核盘点单成功: stocktakeId={}", stocktakeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelStocktake(Long stocktakeId) {
        InvStocktake stocktake = baseMapper.selectById(stocktakeId);
        if (stocktake == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "盘点单不存在");
        }
        if (!InvDocStatus.DRAFT.getCode().equals(stocktake.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的盘点单可以取消");
        }

        stocktake.setStatus(InvDocStatus.CANCELLED.getCode());
        baseMapper.updateById(stocktake);

        log.info("取消盘点单成功: stocktakeId={}", stocktakeId);
    }

    /**
     * 生成盘点单号
     */
    private String generateStocktakeNo() {
        return OrderNoGenerator.generateStocktakeNo();
    }

    @Override
    public InvStocktake createFromDTO(InvStocktakeDTO dto) {
        InvStocktake stocktake = new InvStocktake();
        BeanUtils.copyProperties(dto, stocktake);

        // 转换明细
        if (dto.getDetails() != null) {
            java.util.List<InvStocktakeDetail> details = new java.util.ArrayList<>();
            for (InvStocktakeDTO.InvStocktakeDetailDTO detailDTO : dto.getDetails()) {
                InvStocktakeDetail detail = new InvStocktakeDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            stocktake.setDetails(details);
        }

        return createStocktake(stocktake);
    }
}
