package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.master.dto.CustomerDTO;
import com.opus.erp.master.entity.MdmCustomer;
import com.opus.erp.master.mapper.MdmCustomerMapper;
import com.opus.erp.master.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 客户服务实现类
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<MdmCustomerMapper, MdmCustomer> implements CustomerService {

    @Override
    public Page<MdmCustomer> listCustomers(int pageNum, int pageSize, String customerCode,
                                            String customerName, String rating, Integer status) {
        Page<MdmCustomer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MdmCustomer> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(customerCode)) {
            wrapper.like(MdmCustomer::getCustomerCode, customerCode);
        }
        if (StringUtils.hasText(customerName)) {
            wrapper.like(MdmCustomer::getCustomerName, customerName);
        }
        if (StringUtils.hasText(rating)) {
            wrapper.eq(MdmCustomer::getRating, rating);
        }
        if (status != null) {
            wrapper.eq(MdmCustomer::getStatus, status);
        }

        wrapper.orderByDesc(MdmCustomer::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public MdmCustomer createCustomer(MdmCustomer customer) {
        // 检查客户编码是否已存在
        LambdaQueryWrapper<MdmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmCustomer::getCustomerCode, customer.getCustomerCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "客户编码已存在");
        }

        this.save(customer);
        log.info("创建客户成功: customerCode={}", customer.getCustomerCode());
        return customer;
    }

    @Override
    public MdmCustomer updateCustomer(MdmCustomer customer) {
        // 检查客户是否存在
        MdmCustomer existingCustomer = this.getById(customer.getId());
        if (existingCustomer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客户不存在");
        }

        // 检查编码是否重复（排除自身）
        LambdaQueryWrapper<MdmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmCustomer::getCustomerCode, customer.getCustomerCode())
               .ne(MdmCustomer::getId, customer.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "客户编码已存在");
        }

        this.updateById(customer);
        log.info("更新客户成功: customerId={}", customer.getId());
        return customer;
    }

    @Override
    public void deleteCustomer(Long customerId) {
        MdmCustomer customer = baseMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客户不存在");
        }

        baseMapper.deleteById(customerId);
        log.info("删除客户成功: customerId={}", customerId);
    }

    @Override
    public MdmCustomer createFromDTO(CustomerDTO dto) {
        MdmCustomer customer = new MdmCustomer();
        BeanUtils.copyProperties(dto, customer);
        return createCustomer(customer);
    }

    @Override
    public MdmCustomer updateFromDTO(Long id, CustomerDTO dto) {
        MdmCustomer customer = new MdmCustomer();
        BeanUtils.copyProperties(dto, customer);
        customer.setId(id);
        return updateCustomer(customer);
    }
}
