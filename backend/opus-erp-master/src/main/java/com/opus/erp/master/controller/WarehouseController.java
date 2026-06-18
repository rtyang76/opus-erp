package com.opus.erp.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.master.dto.WarehouseDTO;
import com.opus.erp.master.entity.MdmWarehouse;
import com.opus.erp.master.service.WarehouseService;
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

/**
 * 仓库管理控制器
 */
@RestController
@RequestMapping("/api/master/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * 分页查询仓库列表
     */
    @GetMapping
    public R<Page<MdmWarehouse>> listWarehouses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String warehouseType,
            @RequestParam(required = false) Integer status) {
        Page<MdmWarehouse> page = warehouseService.listWarehouses(pageNum, pageSize,
                warehouseCode, warehouseName, warehouseType, status);
        return R.ok(page);
    }

    /**
     * 查询仓库详情
     */
    @GetMapping("/{id}")
    public R<MdmWarehouse> getWarehouse(@PathVariable Long id) {
        MdmWarehouse warehouse = warehouseService.getById(id);
        if (warehouse == null) {
            return R.notFound("仓库不存在");
        }
        return R.ok(warehouse);
    }

    /**
     * 创建仓库
     */
    @PostMapping
    public R<MdmWarehouse> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) {
        MdmWarehouse createdWarehouse = warehouseService.createFromDTO(warehouseDTO);
        return R.ok("创建成功", createdWarehouse);
    }

    /**
     * 更新仓库
     */
    @PutMapping("/{id}")
    public R<MdmWarehouse> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseDTO warehouseDTO) {
        MdmWarehouse updatedWarehouse = warehouseService.updateFromDTO(id, warehouseDTO);
        return R.ok("更新成功", updatedWarehouse);
    }

    /**
     * 删除仓库
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return R.okMsg("删除成功");
    }
}
