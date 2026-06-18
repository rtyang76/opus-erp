package com.opus.erp.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.master.dto.SupplierDTO;
import com.opus.erp.master.entity.MdmSupplier;
import com.opus.erp.master.service.SupplierService;
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
 * 供应商管理控制器
 */
@RestController
@RequestMapping("/api/master/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * 分页查询供应商列表
     */
    @GetMapping
    public R<Page<MdmSupplier>> listSuppliers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) Integer status) {
        Page<MdmSupplier> page = supplierService.listSuppliers(pageNum, pageSize,
                supplierCode, supplierName, rating, status);
        return R.ok(page);
    }

    /**
     * 查询供应商详情
     */
    @GetMapping("/{id}")
    public R<MdmSupplier> getSupplier(@PathVariable Long id) {
        MdmSupplier supplier = supplierService.getById(id);
        if (supplier == null) {
            return R.notFound("供应商不存在");
        }
        return R.ok(supplier);
    }

    /**
     * 创建供应商
     */
    @PostMapping
    public R<MdmSupplier> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        MdmSupplier createdSupplier = supplierService.createFromDTO(supplierDTO);
        return R.ok("创建成功", createdSupplier);
    }

    /**
     * 更新供应商
     */
    @PutMapping("/{id}")
    public R<MdmSupplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
        MdmSupplier updatedSupplier = supplierService.updateFromDTO(id, supplierDTO);
        return R.ok("更新成功", updatedSupplier);
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return R.okMsg("删除成功");
    }
}
