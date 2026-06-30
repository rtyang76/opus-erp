package com.opus.erp.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.finance.entity.FinReceivable;

/**
 * 应收单 Service 接口
 */
public interface FinReceivableService extends IService<FinReceivable> {

    /**
     * 分页查询应收单
     */
    Page<FinReceivable> getPage(int pageNum, int pageSize, Long customerId, String status);

    /**
     * 创建应收单
     */
    FinReceivable createReceivable(FinReceivable receivable);

    /**
     * 更新应收单
     */
    FinReceivable updateReceivable(Long id, FinReceivable receivable);

    /**
     * 删除应收单（逻辑删除）
     */
    void deleteReceivable(Long id);
}
