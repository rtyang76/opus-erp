package com.opus.erp.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.finance.entity.FinPayable;

/**
 * 应付单 Service 接口
 */
public interface FinPayableService extends IService<FinPayable> {

    /**
     * 分页查询应付单
     */
    Page<FinPayable> getPage(int pageNum, int pageSize, Long supplierId, String status);

    /**
     * 创建应付单
     */
    FinPayable createPayable(FinPayable payable);

    /**
     * 更新应付单
     */
    FinPayable updatePayable(Long id, FinPayable payable);

    /**
     * 删除应付单（逻辑删除）
     */
    void deletePayable(Long id);
}
