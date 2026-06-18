package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.master.dto.SupplierDTO;
import com.opus.erp.master.entity.MdmSupplier;
import com.opus.erp.master.mapper.MdmSupplierMapper;
import com.opus.erp.master.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 供应商服务实现类
 */
@Slf4j
@Service
public class SupplierServiceImpl extends ServiceImpl<MdmSupplierMapper, MdmSupplier> implements SupplierService {

    @Override
    public Page<MdmSupplier> listSuppliers(int pageNum, int pageSize, String supplierCode,
                                            String supplierName, String rating, Integer status) {
        Page<MdmSupplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MdmSupplier> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(supplierCode)) {
            wrapper.like(MdmSupplier::getSupplierCode, supplierCode);
        }
        if (StringUtils.hasText(supplierName)) {
            wrapper.like(MdmSupplier::getSupplierName, supplierName);
        }
        if (StringUtils.hasText(rating)) {
            wrapper.eq(MdmSupplier::getRating, rating);
        }
        if (status != null) {
            wrapper.eq(MdmSupplier::getStatus, status);
        }

        wrapper.orderByDesc(MdmSupplier::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public MdmSupplier createSupplier(MdmSupplier supplier) {
        // 检查供应商编码是否已存在
        LambdaQueryWrapper<MdmSupplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmSupplier::getSupplierCode, supplier.getSupplierCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "供应商编码已存在");
        }

        this.save(supplier);
        log.info("创建供应商成功: supplierCode={}", supplier.getSupplierCode());
        return supplier;
    }

    @Override
    public MdmSupplier updateSupplier(MdmSupplier supplier) {
        // 检查供应商是否存在
        MdmSupplier existingSupplier = this.getById(supplier.getId());
        if (existingSupplier == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "供应商不存在");
        }

        // 检查编码是否重复（排除自身）
        LambdaQueryWrapper<MdmSupplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmSupplier::getSupplierCode, supplier.getSupplierCode())
               .ne(MdmSupplier::getId, supplier.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "供应商编码已存在");
        }

        this.updateById(supplier);
        log.info("更新供应商成功: supplierId={}", supplier.getId());
        return supplier;
    }

    @Override
    public void deleteSupplier(Long supplierId) {
        MdmSupplier supplier = baseMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "供应商不存在");
        }

        baseMapper.deleteById(supplierId);
        log.info("删除供应商成功: supplierId={}", supplierId);
    }

    @Override
    public MdmSupplier createFromDTO(SupplierDTO dto) {
        MdmSupplier supplier = new MdmSupplier();
        BeanUtils.copyProperties(dto, supplier);
        return createSupplier(supplier);
    }

    @Override
    public MdmSupplier updateFromDTO(Long id, SupplierDTO dto) {
        MdmSupplier supplier = new MdmSupplier();
        BeanUtils.copyProperties(dto, supplier);
        supplier.setId(id);
        return updateSupplier(supplier);
    }
}
