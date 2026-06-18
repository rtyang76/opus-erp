package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.system.entity.SysDictData;
import com.opus.erp.system.entity.SysDictType;
import com.opus.erp.system.mapper.SysDictDataMapper;
import com.opus.erp.system.mapper.SysDictTypeMapper;
import com.opus.erp.system.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;

    // ========== 字典类型 ==========

    @Override
    public List<SysDictType> listDictTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysDictType::getCreatedAt);
        return dictTypeMapper.selectList(wrapper);
    }

    @Override
    public SysDictType getDictTypeDetail(Long typeId) {
        SysDictType dictType = dictTypeMapper.selectById(typeId);
        if (dictType == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "字典类型不存在");
        }
        return dictType;
    }

    @Override
    public SysDictType createDictType(SysDictType dictType) {
        // 检查字典类型编码是否已存在
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        if (dictTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "字典类型编码已存在");
        }

        dictTypeMapper.insert(dictType);
        log.info("创建字典类型成功: dictType={}", dictType.getDictType());
        return dictType;
    }

    @Override
    public SysDictType updateDictType(SysDictType dictType) {
        SysDictType existingDictType = dictTypeMapper.selectById(dictType.getId());
        if (existingDictType == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "字典类型不存在");
        }

        dictTypeMapper.updateById(dictType);
        log.info("更新字典类型成功: dictTypeId={}", dictType.getId());
        return dictType;
    }

    @Override
    public void deleteDictType(Long typeId) {
        SysDictType dictType = dictTypeMapper.selectById(typeId);
        if (dictType == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "字典类型不存在");
        }

        dictTypeMapper.deleteById(typeId);
        log.info("删除字典类型成功: dictTypeId={}", typeId);
    }

    // ========== 字典数据 ==========

    @Override
    public List<SysDictData> listDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.eq(SysDictData::getStatus, 1);
        wrapper.orderByAsc(SysDictData::getSortOrder);
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    public SysDictData getDictDataDetail(Long dataId) {
        SysDictData dictData = dictDataMapper.selectById(dataId);
        if (dictData == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "字典数据不存在");
        }
        return dictData;
    }

    @Override
    public SysDictData createDictData(SysDictData dictData) {
        dictDataMapper.insert(dictData);
        log.info("创建字典数据成功: dictType={}, dictValue={}", dictData.getDictType(), dictData.getDictValue());
        return dictData;
    }

    @Override
    public SysDictData updateDictData(SysDictData dictData) {
        SysDictData existingDictData = dictDataMapper.selectById(dictData.getId());
        if (existingDictData == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "字典数据不存在");
        }

        dictDataMapper.updateById(dictData);
        log.info("更新字典数据成功: dictDataId={}", dictData.getId());
        return dictData;
    }

    @Override
    public void deleteDictData(Long dataId) {
        SysDictData dictData = dictDataMapper.selectById(dataId);
        if (dictData == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "字典数据不存在");
        }

        dictDataMapper.deleteById(dataId);
        log.info("删除字典数据成功: dictDataId={}", dataId);
    }
}
