package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.master.entity.MdmItemCategory;
import com.opus.erp.master.mapper.MdmItemCategoryMapper;
import com.opus.erp.master.service.ItemCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物料分类服务实现类
 */
@Slf4j
@Service
public class ItemCategoryServiceImpl extends ServiceImpl<MdmItemCategoryMapper, MdmItemCategory> implements ItemCategoryService {

    @Override
    public List<MdmItemCategory> getCategoryTree() {
        LambdaQueryWrapper<MdmItemCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmItemCategory::getStatus, 1);
        wrapper.orderByAsc(MdmItemCategory::getSortOrder);
        List<MdmItemCategory> categories = baseMapper.selectList(wrapper);

        return buildTree(categories, 0L);
    }

    @Override
    public List<MdmItemCategory> listAllCategories() {
        LambdaQueryWrapper<MdmItemCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MdmItemCategory::getSortOrder);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public MdmItemCategory createCategory(MdmItemCategory category) {
        // 检查分类编码是否已存在
        LambdaQueryWrapper<MdmItemCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmItemCategory::getCategoryCode, category.getCategoryCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "分类编码已存在");
        }

        this.save(category);
        log.info("创建分类成功: categoryCode={}", category.getCategoryCode());
        return category;
    }

    @Override
    public MdmItemCategory updateCategory(MdmItemCategory category) {
        MdmItemCategory existingCategory = baseMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "分类不存在");
        }

        this.updateById(category);
        log.info("更新分类成功: categoryId={}", category.getId());
        return category;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        MdmItemCategory category = baseMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "分类不存在");
        }

        this.removeById(categoryId);
        log.info("删除分类成功: categoryId={}", categoryId);
    }

    /**
     * 构建树形结构
     */
    private List<MdmItemCategory> buildTree(List<MdmItemCategory> categories, Long parentId) {
        Map<Long, List<MdmItemCategory>> grouped = categories.stream()
                .collect(Collectors.groupingBy(MdmItemCategory::getParentId));

        return buildChildren(grouped, parentId);
    }

    /**
     * 递归构建子节点
     */
    private List<MdmItemCategory> buildChildren(Map<Long, List<MdmItemCategory>> grouped, Long parentId) {
        List<MdmItemCategory> children = grouped.getOrDefault(parentId, new ArrayList<>());
        for (MdmItemCategory child : children) {
            child.setChildren(buildChildren(grouped, child.getId()));
        }
        return children;
    }
}
