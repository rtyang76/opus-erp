package com.opus.erp.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.production.dto.WorkOrderDTO;
import com.opus.erp.production.entity.PpWorkOrder;

/**
 * 生产工单服务接口
 */
public interface PpWorkOrderService extends IService<PpWorkOrder> {

    /**
     * 分页查询工单列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param orderNo 工单号
     * @param itemId 物料ID
     * @param status 状态
     * @return 分页结果
     */
    Page<PpWorkOrder> listWorkOrders(int pageNum, int pageSize, String orderNo, Long itemId, String status);

    /**
     * 查询工单详情
     * @param orderId 工单ID
     * @return 工单详情
     */
    PpWorkOrder getWorkOrderDetail(Long orderId);

    /**
     * 创建工单
     * @param workOrder 工单信息
     * @return 创建的工单
     */
    PpWorkOrder createWorkOrder(PpWorkOrder workOrder);

    /**
     * 下达工单
     * @param orderId 工单ID
     * @param releasedBy 下达人ID
     */
    void releaseWorkOrder(Long orderId, Long releasedBy);

    /**
     * 开工（更新状态为生产中）
     * @param orderId 工单ID
     */
    void startWorkOrder(Long orderId);

    /**
     * 完工入库
     * @param orderId 工单ID
     * @param completedQuantity 完工数量
     */
    void completeWorkOrder(Long orderId, java.math.BigDecimal completedQuantity);

    /**
     * 关闭工单
     * @param orderId 工单ID
     */
    void closeWorkOrder(Long orderId);

    /**
     * 根据 DTO 创建工单
     * @param dto 工单 DTO
     * @return 创建的工单
     */
    PpWorkOrder createFromDTO(WorkOrderDTO dto);
}
