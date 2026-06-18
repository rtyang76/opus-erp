package com.opus.erp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.inventory.dto.InvStocktakeDTO;
import com.opus.erp.inventory.entity.InvStocktake;

/**
 * 盘点服务接口
 */
public interface InvStocktakeService extends IService<InvStocktake> {

    /**
     * 分页查询盘点单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param stocktakeNo 盘点单号
     * @param warehouseId 仓库ID
     * @param status 状态
     * @return 分页结果
     */
    Page<InvStocktake> listStocktakes(int pageNum, int pageSize, String stocktakeNo,
                                      Long warehouseId, String status);

    /**
     * 创建盘点单
     * @param stocktake 盘点单信息
     * @return 创建的盘点单
     */
    InvStocktake createStocktake(InvStocktake stocktake);

    /**
     * 审核盘点单（处理盈亏）
     * @param stocktakeId 盘点单ID
     * @param auditorId 审核人ID
     */
    void auditStocktake(Long stocktakeId, Long auditorId);

    /**
     * 取消盘点单
     * @param stocktakeId 盘点单ID
     */
    void cancelStocktake(Long stocktakeId);

    /**
     * 根据 DTO 创建盘点单
     * @param dto 盘点单 DTO
     * @return 创建的盘点单
     */
    InvStocktake createFromDTO(InvStocktakeDTO dto);
}
