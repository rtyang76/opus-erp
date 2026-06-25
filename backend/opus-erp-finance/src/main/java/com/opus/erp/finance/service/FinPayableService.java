package com.opus.erp.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.finance.entity.FinPayable;

/**
 * 应付单 Service 接口
 */
public interface FinPayableService {

    /**
     * 分页查询应付单
     */
    Page<FinPayable> getPage(int pageNum, int pageSize, Long supplierId, String status);

    /**
     * 根据ID查询
     */
    FinPayable getById(Long id);

    /**
     * 创建应付单
     */
    FinPayable create(FinPayable payable);

    /**
     * 更新应付单
     */
    FinPayable update(Long id, FinPayable payable);

    /**
     * 删除应付单
     */
    void delete(Long id);
}
