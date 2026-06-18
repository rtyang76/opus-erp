package com.opus.erp.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.master.entity.MdmItemCategory;

import java.util.List;

/**
 * 物料分类服务接口
 */
public interface ItemCategoryService extends IService<MdmItemCategory> {

    /**
     * 查询分类树
     * @return 分类树
     */
    List<MdmItemCategory> getCategoryTree();

    /**
     * 查询所有分类（平铺）
     * @return 分类列表
     */
    List<MdmItemCategory> listAllCategories();

    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建的分类
     */
    MdmItemCategory createCategory(MdmItemCategory category);

    /**
     * 更新分类
     * @param category 分类信息
     * @return 更新的分类
     */
    MdmItemCategory updateCategory(MdmItemCategory category);

    /**
     * 删除分类（逻辑删除）
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
}
