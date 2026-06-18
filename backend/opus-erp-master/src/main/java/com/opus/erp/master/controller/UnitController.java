package com.opus.erp.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.master.entity.MdmUnit;
import com.opus.erp.master.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 计量单位控制器
 */
@RestController
@RequestMapping("/api/master/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    /**
     * 查询所有单位（不分页）
     */
    @GetMapping("/all")
    public R<List<MdmUnit>> listAllUnits() {
        List<MdmUnit> units = unitService.listAllUnits();
        return R.ok(units);
    }

    /**
     * 分页查询单位列表
     */
    @GetMapping
    public R<Page<MdmUnit>> listUnits(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String unitName,
            @RequestParam(required = false) Integer status) {
        Page<MdmUnit> page = unitService.listUnits(pageNum, pageSize, unitCode, unitName, status);
        return R.ok(page);
    }

    /**
     * 查询单位详情
     */
    @GetMapping("/{id}")
    public R<MdmUnit> getUnit(@PathVariable Long id) {
        MdmUnit unit = unitService.getById(id);
        if (unit == null) {
            return R.notFound("单位不存在");
        }
        return R.ok(unit);
    }

    /**
     * 创建单位
     */
    @PostMapping
    public R<MdmUnit> createUnit(@Valid @RequestBody MdmUnit unit) {
        MdmUnit createdUnit = unitService.createUnit(unit);
        return R.ok("创建成功", createdUnit);
    }

    /**
     * 更新单位
     */
    @PutMapping("/{id}")
    public R<MdmUnit> updateUnit(@PathVariable Long id, @Valid @RequestBody MdmUnit unit) {
        unit.setId(id);
        MdmUnit updatedUnit = unitService.updateUnit(unit);
        return R.ok("更新成功", updatedUnit);
    }

    /**
     * 删除单位
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return R.okMsg("删除成功");
    }
}
