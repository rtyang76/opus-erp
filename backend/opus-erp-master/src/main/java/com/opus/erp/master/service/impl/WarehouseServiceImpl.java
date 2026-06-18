package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.master.dto.WarehouseDTO;
import com.opus.erp.master.entity.MdmWarehouse;
import com.opus.erp.master.mapper.MdmWarehouseMapper;
import com.opus.erp.master.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 仓库服务实现类
 */
@Slf4j
@Service
public class WarehouseServiceImpl extends ServiceImpl<MdmWarehouseMapper, MdmWarehouse> implements WarehouseService {

    @Override
    public Page<MdmWarehouse> listWarehouses(int pageNum, int pageSize, String warehouseCode,
                                              String warehouseName, String warehouseType, Integer status) {
        Page<MdmWarehouse> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MdmWarehouse> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(warehouseCode)) {
            wrapper.like(MdmWarehouse::getWarehouseCode, warehouseCode);
        }
        if (StringUtils.hasText(warehouseName)) {
            wrapper.like(MdmWarehouse::getWarehouseName, warehouseName);
        }
        if (StringUtils.hasText(warehouseType)) {
            wrapper.eq(MdmWarehouse::getWarehouseType, warehouseType);
        }
        if (status != null) {
            wrapper.eq(MdmWarehouse::getStatus, status);
        }

        wrapper.orderByDesc(MdmWarehouse::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public MdmWarehouse createWarehouse(MdmWarehouse warehouse) {
        // 检查仓库编码是否已存在
        LambdaQueryWrapper<MdmWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmWarehouse::getWarehouseCode, warehouse.getWarehouseCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "仓库编码已存在");
        }

        this.save(warehouse);
        log.info("创建仓库成功: warehouseCode={}", warehouse.getWarehouseCode());
        return warehouse;
    }

    @Override
    public MdmWarehouse updateWarehouse(MdmWarehouse warehouse) {
        // 检查仓库是否存在
        MdmWarehouse existingWarehouse = this.getById(warehouse.getId());
        if (existingWarehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "仓库不存在");
        }

        // 检查编码是否重复（排除自身）
        LambdaQueryWrapper<MdmWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmWarehouse::getWarehouseCode, warehouse.getWarehouseCode())
               .ne(MdmWarehouse::getId, warehouse.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "仓库编码已存在");
        }

        this.updateById(warehouse);
        log.info("更新仓库成功: warehouseId={}", warehouse.getId());
        return warehouse;
    }

    @Override
    public void deleteWarehouse(Long warehouseId) {
        MdmWarehouse warehouse = baseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "仓库不存在");
        }

        baseMapper.deleteById(warehouseId);
        log.info("删除仓库成功: warehouseId={}", warehouseId);
    }

    @Override
    public MdmWarehouse createFromDTO(WarehouseDTO dto) {
        MdmWarehouse warehouse = new MdmWarehouse();
        BeanUtils.copyProperties(dto, warehouse);
        return createWarehouse(warehouse);
    }

    @Override
    public MdmWarehouse updateFromDTO(Long id, WarehouseDTO dto) {
        MdmWarehouse warehouse = new MdmWarehouse();
        BeanUtils.copyProperties(dto, warehouse);
        warehouse.setId(id);
        return updateWarehouse(warehouse);
    }
}
