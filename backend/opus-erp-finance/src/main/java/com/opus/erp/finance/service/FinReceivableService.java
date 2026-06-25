package com.opus.erp.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.finance.entity.FinReceivable;

/**
 * 应收单 Service 接口
 */
public interface FinReceivableService {

    /**
     * 分页查询应收单
     */
    Page<FinReceivable> getPage(int pageNum, int pageSize, Long customerId, String status);

    /**
     * 根据ID查询
     */
    FinReceivable getById(Long id);

    /**
     * 创建应收单
     */
    FinReceivable create(FinReceivable receivable);

    /**
     * 更新应收单
     */
    FinReceivable update(Long id, FinReceivable receivable);

    /**
     * 删除应收单
     */
    void delete(Long id);
}
