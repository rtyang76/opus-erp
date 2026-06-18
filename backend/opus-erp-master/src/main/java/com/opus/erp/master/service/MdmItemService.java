package com.opus.erp.master.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.master.dto.ItemDTO;
import com.opus.erp.master.entity.MdmItem;

/**
 * 物料服务接口
 */
public interface MdmItemService extends IService<MdmItem> {

    /**
     * 分页查询物料列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param itemCode 物料编码（模糊查询）
     * @param itemName 物料名称（模糊查询）
     * @param categoryId 分类ID
     * @param itemType 物料类型
     * @param status 状态
     * @return 分页结果
     */
    Page<MdmItem> listItems(int pageNum, int pageSize, String itemCode, String itemName,
                           Long categoryId, String itemType, Integer status);

    /**
     * 创建物料
     * @param item 物料信息
     * @return 创建的物料
     */
    MdmItem createItem(MdmItem item);

    /**
     * 更新物料
     * @param item 物料信息
     * @return 更新的物料
     */
    MdmItem updateItem(MdmItem item);

    /**
     * 删除物料（逻辑删除）
     * @param itemId 物料ID
     */
    void deleteItem(Long itemId);

    /**
     * 查询物料详情
     * @param itemId 物料ID
     * @return 物料详情
     */
    MdmItem getItemDetail(Long itemId);

    /**
     * 根据 DTO 创建物料
     * @param dto 物料 DTO
     * @return 创建的物料
     */
    MdmItem createFromDTO(ItemDTO dto);

    /**
     * 根据 DTO 更新物料
     * @param id 物料ID
     * @param dto 物料 DTO
     * @return 更新的物料
     */
    MdmItem updateFromDTO(Long id, ItemDTO dto);
}
