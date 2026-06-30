package com.opus.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.finance.entity.FinReceivable;
import com.opus.erp.finance.mapper.FinReceivableMapper;
import com.opus.erp.finance.service.FinReceivableService;
import com.opus.erp.master.entity.MdmCustomer;
import com.opus.erp.master.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应收单 Service 实现
 */
@Slf4j
@Service
public class FinReceivableServiceImpl extends ServiceImpl<FinReceivableMapper, FinReceivable>
        implements FinReceivableService {

    private final CustomerService customerService;

    public FinReceivableServiceImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public Page<FinReceivable> getPage(int pageNum, int pageSize, Long customerId, String status) {
        log.info("查询应收单列表: pageNum={}, pageSize={}, customerId={}, status={}", pageNum, pageSize, customerId, status);

        Page<FinReceivable> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FinReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(customerId != null, FinReceivable::getCustomerId, customerId)
                .eq(StringUtils.hasText(status), FinReceivable::getStatus, status)
                .orderByDesc(FinReceivable::getCreatedAt);

        Page<FinReceivable> result = baseMapper.selectPage(page, wrapper);
        log.info("查询应收单成功: total={}, records={}", result.getTotal(), result.getRecords().size());

        // 批量填充客户名称（避免 N+1 查询）
        fillCustomerNames(result);

        return result;
    }

    /**
     * 创建应收单
     */
    @Transactional(rollbackFor = Exception.class)
    public FinReceivable createReceivable(FinReceivable receivable) {
        log.info("创建应收单: customerId={}, amount={}", receivable.getCustomerId(), receivable.getAmount());

        // 生成应收单号
        if (!StringUtils.hasText(receivable.getReceivableNo())) {
            receivable.setReceivableNo(OrderNoGenerator.generateReceivableNo());
        }
        // 初始化金额
        if (receivable.getPaidAmount() == null) {
            receivable.setPaidAmount(BigDecimal.ZERO);
        }
        if (!StringUtils.hasText(receivable.getCurrency())) {
            receivable.setCurrency("CNY");
        }
        if (!StringUtils.hasText(receivable.getStatus())) {
            receivable.setStatus("PENDING");
        }

        baseMapper.insert(receivable);
        log.info("创建应收单成功: id={}, receivableNo={}", receivable.getId(), receivable.getReceivableNo());
        return receivable;
    }

    /**
     * 更新应收单
     */
    @Transactional(rollbackFor = Exception.class)
    public FinReceivable updateReceivable(Long id, FinReceivable receivable) {
        log.info("更新应收单: id={}", id);

        FinReceivable existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "应收单不存在");
        }

        receivable.setId(id);
        baseMapper.updateById(receivable);
        log.info("更新应收单成功: id={}", id);
        return baseMapper.selectById(id);
    }

    /**
     * 删除应收单（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteReceivable(Long id) {
        log.info("删除应收单: id={}", id);

        FinReceivable existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "应收单不存在");
        }

        // 使用 MyBatis-Plus 内置的逻辑删除
        baseMapper.deleteById(id);
        log.info("删除应收单成功: id={}", id);
    }

    /**
     * 批量填充客户名称
     */
    private void fillCustomerNames(Page<FinReceivable> result) {
        try {
            List<Long> customerIds = result.getRecords().stream()
                    .map(FinReceivable::getCustomerId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());

            if (!customerIds.isEmpty()) {
                List<MdmCustomer> customers = customerService.listByIds(customerIds);
                Map<Long, MdmCustomer> customerMap = customers.stream()
                        .collect(Collectors.toMap(MdmCustomer::getId, c -> c, (existing, replacement) -> existing));

                result.getRecords().forEach(receivable -> {
                    if (receivable.getCustomerId() != null) {
                        MdmCustomer customer = customerMap.get(receivable.getCustomerId());
                        if (customer != null) {
                            receivable.setCustomerName(customer.getCustomerName());
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.warn("填充客户名称失败，但不影响主查询: {}", e.getMessage());
        }
    }
}
