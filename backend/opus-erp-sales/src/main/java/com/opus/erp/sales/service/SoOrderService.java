package com.opus.erp.sales.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.sales.dto.SoOrderDTO;
import com.opus.erp.sales.entity.SoOrder;

/**
 * 销售订单服务接口
 */
public interface SoOrderService extends IService<SoOrder> {

    /**
     * 分页查询销售订单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param orderNo 订单号
     * @param customerId 客户ID
     * @param salesmanId 业务员ID
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    Page<SoOrder> listOrders(int pageNum, int pageSize, String orderNo,
                             Long customerId, Long salesmanId, String status,
                             String startDate, String endDate);

    /**
     * 查询订单详情（包含明细）
     * @param orderId 订单ID
     * @return 订单详情
     */
    SoOrder getOrderDetail(Long orderId);

    /**
     * 创建销售订单
     * @param order 订单信息
     * @return 创建的订单
     */
    SoOrder createOrder(SoOrder order);

    /**
     * 更新销售订单
     * @param order 订单信息
     * @return 更新的订单
     */
    SoOrder updateOrder(SoOrder order);

    /**
     * 审核销售订单
     * @param orderId 订单ID
     * @param auditorId 审核人ID
     */
    void auditOrder(Long orderId, Long auditorId);

    /**
     * 销售发货
     * @param orderId 订单ID
     */
    void shipOrder(Long orderId);

    /**
     * 取消销售订单
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);

    /**
     * 更新订单明细的已出库数量
     * @param orderDetailId 订单明细ID
     * @param quantity 出库数量
     */
    void updateShippedQuantity(Long orderDetailId, java.math.BigDecimal quantity);

    /**
     * 根据 DTO 创建销售订单
     * @param dto 销售订单 DTO
     * @return 创建的订单
     */
    SoOrder createFromDTO(SoOrderDTO dto);

    /**
     * 根据 DTO 更新销售订单
     * @param id 订单ID
     * @param dto 销售订单 DTO
     * @return 更新的订单
     */
    SoOrder updateFromDTO(Long id, SoOrderDTO dto);
}
