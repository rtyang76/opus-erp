package com.opus.erp.system.controller;

import com.opus.erp.common.result.R;
import com.opus.erp.system.dto.DictDataDTO;
import com.opus.erp.system.dto.DictTypeDTO;
import com.opus.erp.system.entity.SysDictData;
import com.opus.erp.system.entity.SysDictType;
import com.opus.erp.system.service.DictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典管理控制器
 */
@RestController
@RequestMapping("/api/system/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    // ========== 字典类型 ==========

    /**
     * 查询字典类型列表
     */
    @GetMapping("/types")
    public R<List<SysDictType>> listDictTypes() {
        List<SysDictType> dictTypes = dictService.listDictTypes();
        return R.ok(dictTypes);
    }

    /**
     * 查询字典类型详情
     */
    @GetMapping("/types/{id}")
    public R<SysDictType> getDictType(@PathVariable Long id) {
        SysDictType dictType = dictService.getDictTypeDetail(id);
        return R.ok(dictType);
    }

    /**
     * 创建字典类型
     */
    @PostMapping("/types")
    public R<SysDictType> createDictType(@Valid @RequestBody DictTypeDTO dto) {
        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(dto, dictType);
        SysDictType createdDictType = dictService.createDictType(dictType);
        return R.ok("创建成功", createdDictType);
    }

    /**
     * 更新字典类型
     */
    @PutMapping("/types/{id}")
    public R<SysDictType> updateDictType(@PathVariable Long id, @Valid @RequestBody DictTypeDTO dto) {
        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(dto, dictType);
        dictType.setId(id);
        SysDictType updatedDictType = dictService.updateDictType(dictType);
        return R.ok("更新成功", updatedDictType);
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/types/{id}")
    public R<Void> deleteDictType(@PathVariable Long id) {
        dictService.deleteDictType(id);
        return R.okMsg("删除成功");
    }

    // ========== 字典数据 ==========

    /**
     * 根据字典类型查询字典数据列表
     */
    @GetMapping("/data/{dictType}")
    public R<List<SysDictData>> listDictDataByType(@PathVariable String dictType) {
        List<SysDictData> dictDataList = dictService.listDictDataByType(dictType);
        return R.ok(dictDataList);
    }

    /**
     * 查询字典数据详情
     */
    @GetMapping("/data/detail/{id}")
    public R<SysDictData> getDictData(@PathVariable Long id) {
        SysDictData dictData = dictService.getDictDataDetail(id);
        return R.ok(dictData);
    }

    /**
     * 创建字典数据
     */
    @PostMapping("/data")
    public R<SysDictData> createDictData(@Valid @RequestBody DictDataDTO dto) {
        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(dto, dictData);
        SysDictData createdDictData = dictService.createDictData(dictData);
        return R.ok("创建成功", createdDictData);
    }

    /**
     * 更新字典数据
     */
    @PutMapping("/data/{id}")
    public R<SysDictData> updateDictData(@PathVariable Long id, @Valid @RequestBody DictDataDTO dto) {
        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(dto, dictData);
        dictData.setId(id);
        SysDictData updatedDictData = dictService.updateDictData(dictData);
        return R.ok("更新成功", updatedDictData);
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/data/{id}")
    public R<Void> deleteDictData(@PathVariable Long id) {
        dictService.deleteDictData(id);
        return R.okMsg("删除成功");
    }
}
