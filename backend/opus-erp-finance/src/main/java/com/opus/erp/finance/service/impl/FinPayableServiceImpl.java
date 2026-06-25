package com.opus.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.finance.entity.FinPayable;
import com.opus.erp.finance.mapper.FinPayableMapper;
import com.opus.erp.finance.service.FinPayableService;
import com.opus.erp.master.entity.MdmSupplier;
import com.opus.erp.master.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应付单 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinPayableServiceImpl implements FinPayableService {

    private final FinPayableMapper payableMapper;
    private final SupplierService supplierService;

    @Override
    public Page<FinPayable> getPage(int pageNum, int pageSize, Long supplierId, String status) {
        log.info("查询应付单列表: pageNum={}, pageSize={}, supplierId={}, status={}", pageNum, pageSize, supplierId, status);

        try {
            Page<FinPayable> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<FinPayable> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(supplierId != null, FinPayable::getSupplierId, supplierId)
                    .eq(StringUtils.hasText(status), FinPayable::getStatus, status)
                    .orderByDesc(FinPayable::getCreatedAt);

            Page<FinPayable> result = payableMapper.selectPage(page, wrapper);
            log.info("查询应付单成功: total={}, records={}", result.getTotal(), result.getRecords().size());

            // 批量填充供应商名称（避免 N+1 查询）
            try {
                List<Long> supplierIds = result.getRecords().stream()
                        .map(FinPayable::getSupplierId)
                        .filter(id -> id != null)
                        .distinct()
                        .collect(Collectors.toList());

                log.debug("需要查询的供应商ID: {}", supplierIds);

                if (!supplierIds.isEmpty()) {
                    List<MdmSupplier> suppliers = supplierService.listByIds(supplierIds);
                    log.debug("查询到供应商数量: {}", suppliers.size());

                    Map<Long, MdmSupplier> supplierMap = suppliers.stream()
                            .collect(Collectors.toMap(MdmSupplier::getId, s -> s, (existing, replacement) -> existing));

                    result.getRecords().forEach(payable -> {
                        if (payable.getSupplierId() != null) {
                            MdmSupplier supplier = supplierMap.get(payable.getSupplierId());
                            if (supplier != null) {
                                payable.setSupplierName(supplier.getSupplierName());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.warn("填充供应商名称失败，但不影响主查询: {}", e.getMessage(), e);
                // 供应商名称填充失败不影响主查询结果
            }

            return result;
        } catch (Exception e) {
            log.error("查询应付单列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询应付单列表失败: " + e.getMessage());
        }
    }

    @Override
    public FinPayable getById(Long id) {
        log.info("查询应付单详情: id={}", id);

        try {
            FinPayable payable = payableMapper.selectById(id);
            if (payable == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "应付单不存在");
            }

            // 填充供应商名称
            try {
                if (payable.getSupplierId() != null) {
                    MdmSupplier supplier = supplierService.getById(payable.getSupplierId());
                    if (supplier != null) {
                        payable.setSupplierName(supplier.getSupplierName());
                    }
                }
            } catch (Exception e) {
                log.warn("填充供应商名称失败: {}", e.getMessage());
            }

            return payable;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询应付单详情失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询应付单详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinPayable create(FinPayable payable) {
        log.info("创建应付单: supplierId={}, amount={}", payable.getSupplierId(), payable.getAmount());

        try {
            // 生成应付单号
            if (!StringUtils.hasText(payable.getPayableNo())) {
                payable.setPayableNo(OrderNoGenerator.generatePayableNo());
            }
            // 初始化金额
            if (payable.getPaidAmount() == null) {
                payable.setPaidAmount(BigDecimal.ZERO);
            }
            if (!StringUtils.hasText(payable.getCurrency())) {
                payable.setCurrency("CNY");
            }
            if (!StringUtils.hasText(payable.getStatus())) {
                payable.setStatus("PENDING");
            }

            payableMapper.insert(payable);
            log.info("创建应付单成功: id={}, payableNo={}", payable.getId(), payable.getPayableNo());
            return payable;
        } catch (Exception e) {
            log.error("创建应付单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建应付单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinPayable update(Long id, FinPayable payable) {
        log.info("更新应付单: id={}", id);

        try {
            FinPayable existing = payableMapper.selectById(id);
            if (existing == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "应付单不存在");
            }

            payable.setId(id);
            payableMapper.updateById(payable);
            log.info("更新应付单成功: id={}", id);
            return payableMapper.selectById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新应付单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新应付单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除应付单: id={}", id);

        try {
            FinPayable existing = payableMapper.selectById(id);
            if (existing == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "应付单不存在");
            }

            // 逻辑删除
            existing.setDeleted(1);
            payableMapper.updateById(existing);
            log.info("删除应付单成功: id={}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除应付单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除应付单失败: " + e.getMessage());
        }
    }
}
