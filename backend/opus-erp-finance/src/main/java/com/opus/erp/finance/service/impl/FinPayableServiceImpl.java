package com.opus.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.finance.entity.FinPayable;
import com.opus.erp.finance.mapper.FinPayableMapper;
import com.opus.erp.finance.service.FinPayableService;
import com.opus.erp.master.entity.MdmSupplier;
import com.opus.erp.master.service.SupplierService;
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
public class FinPayableServiceImpl extends ServiceImpl<FinPayableMapper, FinPayable>
        implements FinPayableService {

    private final SupplierService supplierService;

    public FinPayableServiceImpl(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Override
    public Page<FinPayable> getPage(int pageNum, int pageSize, Long supplierId, String status) {
        log.info("查询应付单列表: pageNum={}, pageSize={}, supplierId={}, status={}", pageNum, pageSize, supplierId, status);

        Page<FinPayable> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FinPayable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(supplierId != null, FinPayable::getSupplierId, supplierId)
                .eq(StringUtils.hasText(status), FinPayable::getStatus, status)
                .orderByDesc(FinPayable::getCreatedAt);

        Page<FinPayable> result = baseMapper.selectPage(page, wrapper);
        log.info("查询应付单成功: total={}, records={}", result.getTotal(), result.getRecords().size());

        // 批量填充供应商名称（避免 N+1 查询）
        fillSupplierNames(result);

        return result;
    }

    /**
     * 创建应付单
     */
    @Transactional(rollbackFor = Exception.class)
    public FinPayable createPayable(FinPayable payable) {
        log.info("创建应付单: supplierId={}, amount={}", payable.getSupplierId(), payable.getAmount());

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

        baseMapper.insert(payable);
        log.info("创建应付单成功: id={}, payableNo={}", payable.getId(), payable.getPayableNo());
        return payable;
    }

    /**
     * 更新应付单
     */
    @Transactional(rollbackFor = Exception.class)
    public FinPayable updatePayable(Long id, FinPayable payable) {
        log.info("更新应付单: id={}", id);

        FinPayable existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "应付单不存在");
        }

        payable.setId(id);
        baseMapper.updateById(payable);
        log.info("更新应付单成功: id={}", id);
        return baseMapper.selectById(id);
    }

    /**
     * 删除应付单（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePayable(Long id) {
        log.info("删除应付单: id={}", id);

        FinPayable existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "应付单不存在");
        }

        // 使用 MyBatis-Plus 内置的逻辑删除
        baseMapper.deleteById(id);
        log.info("删除应付单成功: id={}", id);
    }

    /**
     * 批量填充供应商名称
     */
    private void fillSupplierNames(Page<FinPayable> result) {
        try {
            List<Long> supplierIds = result.getRecords().stream()
                    .map(FinPayable::getSupplierId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());

            if (!supplierIds.isEmpty()) {
                List<MdmSupplier> suppliers = supplierService.listByIds(supplierIds);
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
            log.warn("填充供应商名称失败，但不影响主查询: {}", e.getMessage());
        }
    }
}
