package com.opus.erp.sales.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.sales.dto.SoShipmentDTO;
import com.opus.erp.sales.entity.SoShipment;

/**
 * 销售出库服务接口
 */
public interface SoShipmentService extends IService<SoShipment> {

    /**
     * 分页查询销售出库单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param shipmentNo 出库单号
     * @param orderId 销售订单ID
     * @param customerId 客户ID
     * @param status 状态
     * @return 分页结果
     */
    Page<SoShipment> listShipments(int pageNum, int pageSize, String shipmentNo,
                                    Long orderId, Long customerId, String status);

    /**
     * 查询出库单详情（包含明细）
     * @param shipmentId 出库单ID
     * @return 出库单详情
     */
    SoShipment getShipmentDetail(Long shipmentId);

    /**
     * 创建销售出库单
     * @param shipment 出库单信息
     * @return 创建的出库单
     */
    SoShipment createShipment(SoShipment shipment);

    /**
     * 审核销售出库单（调用 InvTransactionService）
     * @param shipmentId 出库单ID
     * @param auditorId 审核人ID
     */
    void auditShipment(Long shipmentId, Long auditorId);

    /**
     * 取消销售出库单
     * @param shipmentId 出库单ID
     */
    void cancelShipment(Long shipmentId);

    /**
     * 根据 DTO 创建销售出库单
     * @param dto 销售出库单 DTO
     * @return 创建的出库单
     */
    SoShipment createFromDTO(SoShipmentDTO dto);
}
