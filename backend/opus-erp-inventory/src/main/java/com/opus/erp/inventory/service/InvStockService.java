package com.opus.erp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.inventory.entity.InvStock;
import com.opus.erp.inventory.entity.InvTransaction;

/**
 * 库存服务接口
 */
public interface InvStockService extends IService<InvStock> {

    /**
     * 分页查询即时库存
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param lotNo 批次号
     * @param minQuantity 最小数量
     * @return 分页结果
     */
    Page<InvStock> listStock(int pageNum, int pageSize, Long itemId, Long warehouseId,
                             String lotNo, Integer minQuantity);

    /**
     * 分页查询库存流水
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param transactionType 交易类型
     * @param itemId 物料ID
     * @param warehouseId 仓库ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    Page<InvTransaction> listTransactions(int pageNum, int pageSize, String transactionType,
                                          Long itemId, Long warehouseId,
                                          String startDate, String endDate);
}
