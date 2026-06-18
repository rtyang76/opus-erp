package com.opus.erp.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.purchase.dto.PoOrderDTO;
import com.opus.erp.purchase.entity.PoOrder;

/**
 * 采购订单服务接口
 */
public interface PoOrderService extends IService<PoOrder> {

    /**
     * 分页查询采购订单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param orderNo 订单号
     * @param supplierId 供应商ID
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    Page<PoOrder> listOrders(int pageNum, int pageSize, String orderNo,
                             Long supplierId, String status, String startDate, String endDate);

    /**
     * 查询订单详情（包含明细）
     * @param orderId 订单ID
     * @return 订单详情
     */
    PoOrder getOrderDetail(Long orderId);

    /**
     * 创建采购订单
     * @param order 订单信息
     * @return 创建的订单
     */
    PoOrder createOrder(PoOrder order);

    /**
     * 更新采购订单
     * @param order 订单信息
     * @return 更新的订单
     */
    PoOrder updateOrder(PoOrder order);

    /**
     * 审核采购订单
     * @param orderId 订单ID
     * @param auditorId 审核人ID
     */
    void auditOrder(Long orderId, Long auditorId);

    /**
     * 取消采购订单
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);

    /**
     * 关闭采购订单
     * @param orderId 订单ID
     */
    void closeOrder(Long orderId);

    /**
     * 更新订单明细的已收货数量
     * @param orderDetailId 订单明细ID
     * @param quantity 收货数量
     */
    void updateReceivedQuantity(Long orderDetailId, java.math.BigDecimal quantity);

    /**
     * 根据 DTO 创建采购订单
     * @param dto 采购订单 DTO
     * @return 创建的订单
     */
    PoOrder createFromDTO(PoOrderDTO dto);

    /**
     * 根据 DTO 更新采购订单
     * @param id 订单ID
     * @param dto 采购订单 DTO
     * @return 更新的订单
     */
    PoOrder updateFromDTO(Long id, PoOrderDTO dto);
}
