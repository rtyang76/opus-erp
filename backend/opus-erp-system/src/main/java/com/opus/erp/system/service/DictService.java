package com.opus.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.system.entity.SysDictData;
import com.opus.erp.system.entity.SysDictType;

import java.util.List;

/**
 * 字典服务接口
 */
public interface DictService {

    // ========== 字典类型 ==========

    /**
     * 查询字典类型列表
     * @return 字典类型列表
     */
    List<SysDictType> listDictTypes();

    /**
     * 查询字典类型详情
     * @param typeId 字典类型ID
     * @return 字典类型详情
     */
    SysDictType getDictTypeDetail(Long typeId);

    /**
     * 创建字典类型
     * @param dictType 字典类型信息
     * @return 创建的字典类型
     */
    SysDictType createDictType(SysDictType dictType);

    /**
     * 更新字典类型
     * @param dictType 字典类型信息
     * @return 更新的字典类型
     */
    SysDictType updateDictType(SysDictType dictType);

    /**
     * 删除字典类型（逻辑删除）
     * @param typeId 字典类型ID
     */
    void deleteDictType(Long typeId);

    // ========== 字典数据 ==========

    /**
     * 根据字典类型查询字典数据列表
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SysDictData> listDictDataByType(String dictType);

    /**
     * 查询字典数据详情
     * @param dataId 字典数据ID
     * @return 字典数据详情
     */
    SysDictData getDictDataDetail(Long dataId);

    /**
     * 创建字典数据
     * @param dictData 字典数据信息
     * @return 创建的字典数据
     */
    SysDictData createDictData(SysDictData dictData);

    /**
     * 更新字典数据
     * @param dictData 字典数据信息
     * @return 更新的字典数据
     */
    SysDictData updateDictData(SysDictData dictData);

    /**
     * 删除字典数据（逻辑删除）
     * @param dataId 字典数据ID
     */
    void deleteDictData(Long dataId);
}
