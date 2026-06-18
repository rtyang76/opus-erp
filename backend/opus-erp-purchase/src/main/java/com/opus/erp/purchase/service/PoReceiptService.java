package com.opus.erp.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.purchase.dto.PoReceiptDTO;
import com.opus.erp.purchase.entity.PoReceipt;

/**
 * 采购入库服务接口
 */
public interface PoReceiptService extends IService<PoReceipt> {

    /**
     * 分页查询采购入库单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param receiptNo 入库单号
     * @param orderId 采购订单ID
     * @param supplierId 供应商ID
     * @param status 状态
     * @return 分页结果
     */
    Page<PoReceipt> listReceipts(int pageNum, int pageSize, String receiptNo,
                                  Long orderId, Long supplierId, String status);

    /**
     * 查询入库单详情（包含明细）
     * @param receiptId 入库单ID
     * @return 入库单详情
     */
    PoReceipt getReceiptDetail(Long receiptId);

    /**
     * 创建采购入库单
     * @param receipt 入库单信息
     * @return 创建的入库单
     */
    PoReceipt createReceipt(PoReceipt receipt);

    /**
     * 审核采购入库单（调用 InvTransactionService）
     * @param receiptId 入库单ID
     * @param auditorId 审核人ID
     */
    void auditReceipt(Long receiptId, Long auditorId);

    /**
     * 取消采购入库单
     * @param receiptId 入库单ID
     */
    void cancelReceipt(Long receiptId);

    /**
     * 根据 DTO 创建采购入库单
     * @param dto 采购入库单 DTO
     * @return 创建的入库单
     */
    PoReceipt createFromDTO(PoReceiptDTO dto);
}
