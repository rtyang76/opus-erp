package com.opus.erp.master.controller;

import com.opus.erp.common.result.R;
import com.opus.erp.master.entity.MdmItemCategory;
import com.opus.erp.master.service.ItemCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * 物料分类控制器
 */
@RestController
@RequestMapping("/api/master/categories")
@RequiredArgsConstructor
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    /**
     * 查询分类树
     */
    @GetMapping("/tree")
    public R<List<MdmItemCategory>> getCategoryTree() {
        List<MdmItemCategory> tree = itemCategoryService.getCategoryTree();
        return R.ok(tree);
    }

    /**
     * 查询所有分类（平铺）
     */
    @GetMapping
    public R<List<MdmItemCategory>> listCategories() {
        List<MdmItemCategory> categories = itemCategoryService.listAllCategories();
        return R.ok(categories);
    }

    /**
     * 查询分类详情
     */
    @GetMapping("/{id}")
    public R<MdmItemCategory> getCategory(@PathVariable Long id) {
        MdmItemCategory category = itemCategoryService.getById(id);
        if (category == null) {
            return R.notFound("分类不存在");
        }
        return R.ok(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public R<MdmItemCategory> createCategory(@Valid @RequestBody MdmItemCategory category) {
        MdmItemCategory createdCategory = itemCategoryService.createCategory(category);
        return R.ok("创建成功", createdCategory);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public R<MdmItemCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody MdmItemCategory category) {
        category.setId(id);
        MdmItemCategory updatedCategory = itemCategoryService.updateCategory(category);
        return R.ok("更新成功", updatedCategory);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteCategory(@PathVariable Long id) {
        itemCategoryService.deleteCategory(id);
        return R.okMsg("删除成功");
    }
}
