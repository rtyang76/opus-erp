package com.opus.erp.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.master.dto.ItemDTO;
import com.opus.erp.master.entity.MdmItem;
import com.opus.erp.master.service.MdmItemService;
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
 * 物料管理控制器
 */
@RestController
@RequestMapping("/api/master/items")
@RequiredArgsConstructor
public class ItemController {

    private final MdmItemService itemService;

    /**
     * 分页查询物料列表
     */
    @GetMapping
    public R<Page<MdmItem>> listItems(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String itemType,
            @RequestParam(required = false) Integer status) {
        Page<MdmItem> page = itemService.listItems(pageNum, pageSize, itemCode, itemName,
                categoryId, itemType, status);
        return R.ok(page);
    }

    /**
     * 查询物料详情
     */
    @GetMapping("/{id}")
    public R<MdmItem> getItem(@PathVariable Long id) {
        MdmItem item = itemService.getItemDetail(id);
        return R.ok(item);
    }

    /**
     * 创建物料
     */
    @PostMapping
    public R<MdmItem> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        MdmItem createdItem = itemService.createFromDTO(itemDTO);
        return R.ok("创建成功", createdItem);
    }

    /**
     * 更新物料
     */
    @PutMapping("/{id}")
    public R<MdmItem> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDTO) {
        MdmItem updatedItem = itemService.updateFromDTO(id, itemDTO);
        return R.ok("更新成功", updatedItem);
    }

    /**
     * 删除物料
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return R.okMsg("删除成功");
    }
}
