package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.master.dto.ItemDTO;
import com.opus.erp.master.entity.MdmItem;
import com.opus.erp.master.mapper.MdmItemMapper;
import com.opus.erp.master.service.MdmItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 物料服务实现类
 */
@Slf4j
@Service
public class MdmItemServiceImpl extends ServiceImpl<MdmItemMapper, MdmItem> implements MdmItemService {

    @Override
    public Page<MdmItem> listItems(int pageNum, int pageSize, String itemCode, String itemName,
                                   Long categoryId, String itemType, Integer status) {
        Page<MdmItem> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MdmItem> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(itemCode)) {
            wrapper.like(MdmItem::getItemCode, itemCode);
        }
        if (StringUtils.hasText(itemName)) {
            wrapper.like(MdmItem::getItemName, itemName);
        }
        if (categoryId != null) {
            wrapper.eq(MdmItem::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(itemType)) {
            wrapper.eq(MdmItem::getItemType, itemType);
        }
        if (status != null) {
            wrapper.eq(MdmItem::getStatus, status);
        }

        wrapper.orderByDesc(MdmItem::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public MdmItem createItem(MdmItem item) {
        // 检查物料编码是否已存在
        LambdaQueryWrapper<MdmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmItem::getItemCode, item.getItemCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "物料编码已存在");
        }

        this.save(item);
        log.info("创建物料成功: itemCode={}", item.getItemCode());
        return item;
    }

    @Override
    public MdmItem updateItem(MdmItem item) {
        // 检查物料是否存在
        MdmItem existingItem = this.getById(item.getId());
        if (existingItem == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物料不存在");
        }

        // 检查编码是否重复（排除自身）
        LambdaQueryWrapper<MdmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmItem::getItemCode, item.getItemCode())
               .ne(MdmItem::getId, item.getId());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "物料编码已存在");
        }

        this.updateById(item);
        log.info("更新物料成功: itemId={}", item.getId());
        return item;
    }

    @Override
    public void deleteItem(Long itemId) {
        MdmItem item = baseMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物料不存在");
        }

        baseMapper.deleteById(itemId);
        log.info("删除物料成功: itemId={}", itemId);
    }

    @Override
    public MdmItem getItemDetail(Long itemId) {
        MdmItem item = baseMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物料不存在");
        }
        return item;
    }

    @Override
    public MdmItem createFromDTO(ItemDTO dto) {
        MdmItem item = new MdmItem();
        BeanUtils.copyProperties(dto, item);
        return createItem(item);
    }

    @Override
    public MdmItem updateFromDTO(Long id, ItemDTO dto) {
        MdmItem item = new MdmItem();
        BeanUtils.copyProperties(dto, item);
        item.setId(id);
        return updateItem(item);
    }
}
