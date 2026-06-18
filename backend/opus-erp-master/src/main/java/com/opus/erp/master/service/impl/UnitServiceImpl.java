package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.master.entity.MdmUnit;
import com.opus.erp.master.mapper.MdmUnitMapper;
import com.opus.erp.master.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 计量单位服务实现类
 */
@Slf4j
@Service
public class UnitServiceImpl extends ServiceImpl<MdmUnitMapper, MdmUnit> implements UnitService {

    @Override
    public List<MdmUnit> listAllUnits() {
        LambdaQueryWrapper<MdmUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmUnit::getStatus, 1);
        wrapper.orderByAsc(MdmUnit::getUnitCode);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Page<MdmUnit> listUnits(int pageNum, int pageSize, String unitCode, String unitName, Integer status) {
        Page<MdmUnit> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MdmUnit> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(unitCode)) {
            wrapper.like(MdmUnit::getUnitCode, unitCode);
        }
        if (StringUtils.hasText(unitName)) {
            wrapper.like(MdmUnit::getUnitName, unitName);
        }
        if (status != null) {
            wrapper.eq(MdmUnit::getStatus, status);
        }

        wrapper.orderByAsc(MdmUnit::getUnitCode);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public MdmUnit createUnit(MdmUnit unit) {
        // 检查单位编码是否已存在
        LambdaQueryWrapper<MdmUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmUnit::getUnitCode, unit.getUnitCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "单位编码已存在");
        }

        this.save(unit);
        log.info("创建单位成功: unitCode={}", unit.getUnitCode());
        return unit;
    }

    @Override
    public MdmUnit updateUnit(MdmUnit unit) {
        MdmUnit existingUnit = baseMapper.selectById(unit.getId());
        if (existingUnit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "单位不存在");
        }

        this.updateById(unit);
        log.info("更新单位成功: unitId={}", unit.getId());
        return unit;
    }

    @Override
    public void deleteUnit(Long unitId) {
        MdmUnit unit = baseMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "单位不存在");
        }

        this.removeById(unitId);
        log.info("删除单位成功: unitId={}", unitId);
    }
}
