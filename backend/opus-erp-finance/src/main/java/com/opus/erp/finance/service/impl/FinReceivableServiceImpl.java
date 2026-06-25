package com.opus.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.finance.entity.FinReceivable;
import com.opus.erp.finance.mapper.FinReceivableMapper;
import com.opus.erp.finance.service.FinReceivableService;
import com.opus.erp.master.entity.MdmCustomer;
import com.opus.erp.master.service.CustomerService;
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
 * 应收单 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinReceivableServiceImpl implements FinReceivableService {

    private final FinReceivableMapper receivableMapper;
    private final CustomerService customerService;

    @Override
    public Page<FinReceivable> getPage(int pageNum, int pageSize, Long customerId, String status) {
        log.info("查询应收单列表: pageNum={}, pageSize={}, customerId={}, status={}", pageNum, pageSize, customerId, status);

        try {
            Page<FinReceivable> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<FinReceivable> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(customerId != null, FinReceivable::getCustomerId, customerId)
                    .eq(StringUtils.hasText(status), FinReceivable::getStatus, status)
                    .orderByDesc(FinReceivable::getCreatedAt);

            Page<FinReceivable> result = receivableMapper.selectPage(page, wrapper);
            log.info("查询应收单成功: total={}, records={}", result.getTotal(), result.getRecords().size());

            // 批量填充客户名称（避免 N+1 查询）
            try {
                List<Long> customerIds = result.getRecords().stream()
                        .map(FinReceivable::getCustomerId)
                        .filter(id -> id != null)
                        .distinct()
                        .collect(Collectors.toList());

                log.debug("需要查询的客户ID: {}", customerIds);

                if (!customerIds.isEmpty()) {
                    List<MdmCustomer> customers = customerService.listByIds(customerIds);
                    log.debug("查询到客户数量: {}", customers.size());

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
                log.warn("填充客户名称失败，但不影响主查询: {}", e.getMessage(), e);
                // 客户名称填充失败不影响主查询结果
            }

            return result;
        } catch (Exception e) {
            log.error("查询应收单列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询应收单列表失败: " + e.getMessage());
        }
    }

    @Override
    public FinReceivable getById(Long id) {
        log.info("查询应收单详情: id={}", id);

        try {
            FinReceivable receivable = receivableMapper.selectById(id);
            if (receivable == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "应收单不存在");
            }

            // 填充客户名称
            try {
                if (receivable.getCustomerId() != null) {
                    MdmCustomer customer = customerService.getById(receivable.getCustomerId());
                    if (customer != null) {
                        receivable.setCustomerName(customer.getCustomerName());
                    }
                }
            } catch (Exception e) {
                log.warn("填充客户名称失败: {}", e.getMessage());
            }

            return receivable;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询应收单详情失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询应收单详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinReceivable create(FinReceivable receivable) {
        log.info("创建应收单: customerId={}, amount={}", receivable.getCustomerId(), receivable.getAmount());

        try {
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

            receivableMapper.insert(receivable);
            log.info("创建应收单成功: id={}, receivableNo={}", receivable.getId(), receivable.getReceivableNo());
            return receivable;
        } catch (Exception e) {
            log.error("创建应收单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建应收单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinReceivable update(Long id, FinReceivable receivable) {
        log.info("更新应收单: id={}", id);

        try {
            FinReceivable existing = receivableMapper.selectById(id);
            if (existing == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "应收单不存在");
            }

            receivable.setId(id);
            receivableMapper.updateById(receivable);
            log.info("更新应收单成功: id={}", id);
            return receivableMapper.selectById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新应收单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新应收单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除应收单: id={}", id);

        try {
            FinReceivable existing = receivableMapper.selectById(id);
            if (existing == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "应收单不存在");
            }

            // 逻辑删除
            existing.setDeleted(1);
            receivableMapper.updateById(existing);
            log.info("删除应收单成功: id={}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除应收单失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除应收单失败: " + e.getMessage());
        }
    }
}
