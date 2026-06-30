package com.opus.erp.system.service;

import com.opus.erp.system.dto.DictDataDTO;
import com.opus.erp.system.dto.DictTypeDTO;
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
     * @param dto 字典类型DTO
     * @return 创建的字典类型
     */
    SysDictType createDictType(DictTypeDTO dto);

    /**
     * 更新字典类型
     * @param id 字典类型ID
     * @param dto 字典类型DTO
     * @return 更新的字典类型
     */
    SysDictType updateDictType(Long id, DictTypeDTO dto);

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
     * @param dto 字典数据DTO
     * @return 创建的字典数据
     */
    SysDictData createDictData(DictDataDTO dto);

    /**
     * 更新字典数据
     * @param id 字典数据ID
     * @param dto 字典数据DTO
     * @return 更新的字典数据
     */
    SysDictData updateDictData(Long id, DictDataDTO dto);

    /**
     * 删除字典数据（逻辑删除）
     * @param dataId 字典数据ID
     */
    void deleteDictData(Long dataId);
}
