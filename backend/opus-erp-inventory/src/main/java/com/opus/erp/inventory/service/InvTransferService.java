package com.opus.erp.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.inventory.dto.InvTransferDTO;
import com.opus.erp.inventory.entity.InvTransfer;

/**
 * 调拨服务接口
 */
public interface InvTransferService extends IService<InvTransfer> {

    /**
     * 分页查询调拨单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param transferNo 调拨单号
     * @param fromWarehouseId 调出仓库ID
     * @param toWarehouseId 调入仓库ID
     * @param status 状态
     * @return 分页结果
     */
    Page<InvTransfer> listTransfers(int pageNum, int pageSize, String transferNo,
                                    Long fromWarehouseId, Long toWarehouseId, String status);

    /**
     * 创建调拨单
     * @param transfer 调拨单信息
     * @return 创建的调拨单
     */
    InvTransfer createTransfer(InvTransfer transfer);

    /**
     * 审核调拨单
     * @param transferId 调拨单ID
     * @param auditorId 审核人ID
     */
    void auditTransfer(Long transferId, Long auditorId);

    /**
     * 取消调拨单
     * @param transferId 调拨单ID
     */
    void cancelTransfer(Long transferId);

    /**
     * 根据 DTO 创建调拨单
     * @param dto 调拨单 DTO
     * @return 创建的调拨单
     */
    InvTransfer createFromDTO(InvTransferDTO dto);
}
