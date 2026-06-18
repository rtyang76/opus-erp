package com.opus.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.sales.dto.SoOrderDTO;
import com.opus.erp.sales.entity.SoOrder;
import com.opus.erp.sales.entity.SoOrderDetail;
import com.opus.erp.sales.enums.SoOrderStatus;
import com.opus.erp.sales.mapper.SoOrderDetailMapper;
import com.opus.erp.sales.mapper.SoOrderMapper;
import com.opus.erp.sales.service.SoOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 销售订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoOrderServiceImpl extends ServiceImpl<SoOrderMapper, SoOrder> implements SoOrderService {

    private final SoOrderDetailMapper orderDetailMapper;

    @Override
    public Page<SoOrder> listOrders(int pageNum, int pageSize, String orderNo,
                                     Long customerId, Long salesmanId, String status,
                                     String startDate, String endDate) {
        Page<SoOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SoOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(SoOrder::getOrderNo, orderNo);
        }
        if (customerId != null) {
            wrapper.eq(SoOrder::getCustomerId, customerId);
        }
        if (salesmanId != null) {
            wrapper.eq(SoOrder::getSalesmanId, salesmanId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SoOrder::getStatus, status);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(SoOrder::getOrderDate, LocalDate.parse(startDate));
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(SoOrder::getOrderDate, LocalDate.parse(endDate));
        }

        wrapper.orderByDesc(SoOrder::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public SoOrder getOrderDetail(Long orderId) {
        SoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单不存在");
        }

        // 查询明细
        LambdaQueryWrapper<SoOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoOrderDetail::getOrderId, orderId);
        order.setDetails(orderDetailMapper.selectList(wrapper));

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoOrder createOrder(SoOrder order) {
        // 生成订单号
        order.setOrderNo(generateOrderNo());
        order.setStatus(SoOrderStatus.DRAFT.getCode());

        // 计算总金额和税额
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        // 保存主表
        baseMapper.insert(order);

        // 保存明细
        if (order.getDetails() != null) {
            for (SoOrderDetail detail : order.getDetails()) {
                detail.setOrderId(order.getId());
                detail.setShippedQuantity(BigDecimal.ZERO);
                orderDetailMapper.insert(detail);

                // 累加金额
                BigDecimal amount = detail.getQuantity().multiply(detail.getUnitPrice());
                BigDecimal detailTax = amount.multiply(detail.getTaxRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                totalAmount = totalAmount.add(amount);
                taxAmount = taxAmount.add(detailTax);
            }
        }

        // 更新总金额
        order.setTotalAmount(totalAmount);
        order.setTaxAmount(taxAmount);
        baseMapper.updateById(order);

        log.info("创建销售订单成功: orderNo={}", order.getOrderNo());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoOrder updateOrder(SoOrder order) {
        SoOrder existingOrder = baseMapper.selectById(order.getId());
        if (existingOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单不存在");
        }
        if (!SoOrderStatus.DRAFT.getCode().equals(existingOrder.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的订单可以修改");
        }

        // 更新主表
        baseMapper.updateById(order);

        // 删除旧明细，插入新明细
        if (order.getDetails() != null) {
            LambdaQueryWrapper<SoOrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SoOrderDetail::getOrderId, order.getId());
            orderDetailMapper.delete(wrapper);

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal taxAmount = BigDecimal.ZERO;

            for (SoOrderDetail detail : order.getDetails()) {
                detail.setOrderId(order.getId());
                detail.setShippedQuantity(BigDecimal.ZERO);
                orderDetailMapper.insert(detail);

                BigDecimal amount = detail.getQuantity().multiply(detail.getUnitPrice());
                BigDecimal detailTax = amount.multiply(detail.getTaxRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                totalAmount = totalAmount.add(amount);
                taxAmount = taxAmount.add(detailTax);
            }

            order.setTotalAmount(totalAmount);
            order.setTaxAmount(taxAmount);
            baseMapper.updateById(order);
        }

        log.info("更新销售订单成功: orderId={}", order.getId());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long orderId, Long auditorId) {
        SoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单不存在");
        }
        if (!SoOrderStatus.DRAFT.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的订单可以审核");
        }

        order.setStatus(SoOrderStatus.AUDITED.getCode());
        order.setAuditedBy(auditorId);
        order.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(order);

        log.info("审核销售订单成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId) {
        SoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单不存在");
        }
        if (!SoOrderStatus.AUDITED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有已审核的订单可以发货");
        }

        // TODO: 创建出库单，调用 InvTransactionService
        // 注意：当前实现仅更新订单状态，实际业务需要：
        // 1. 创建 SoShipment 出库单
        // 2. 调用 InvTransactionService.createIssue() 扣减库存
        // 3. 更新订单明细的已出库数量
        // 此功能需要在 Phase 4 完善

        order.setStatus(SoOrderStatus.SHIPPED.getCode());
        baseMapper.updateById(order);

        log.info("销售订单发货成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        SoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单不存在");
        }
        if (!SoOrderStatus.DRAFT.getCode().equals(order.getStatus()) && !SoOrderStatus.AUDITED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿或已审核的订单可以取消");
        }

        // TODO: 检查是否有出库记录
        // 如果已有出库记录，应该不允许取消，或者需要先取消出库单
        // 查询 so_shipment 表是否有该订单的已审核出库记录

        order.setStatus(SoOrderStatus.CANCELLED.getCode());
        baseMapper.updateById(order);

        log.info("取消销售订单成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShippedQuantity(Long orderDetailId, BigDecimal quantity) {
        SoOrderDetail orderDetail = orderDetailMapper.selectById(orderDetailId);
        if (orderDetail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售订单明细不存在");
        }

        orderDetail.setShippedQuantity(orderDetail.getShippedQuantity().add(quantity));
        orderDetailMapper.updateById(orderDetail);

        log.info("更新销售订单明细已出库数量: orderDetailId={}, quantity={}", orderDetailId, quantity);
    }

    /**
     * 生成销售订单号
     */
    private String generateOrderNo() {
        return OrderNoGenerator.generateSoOrderNo();
    }

    @Override
    public SoOrder createFromDTO(SoOrderDTO dto) {
        SoOrder order = new SoOrder();
        BeanUtils.copyProperties(dto, order);

        // 转换明细
        if (dto.getDetails() != null) {
            java.util.List<SoOrderDetail> details = new java.util.ArrayList<>();
            for (SoOrderDTO.SoOrderDetailDTO detailDTO : dto.getDetails()) {
                SoOrderDetail detail = new SoOrderDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            order.setDetails(details);
        }

        return createOrder(order);
    }

    @Override
    public SoOrder updateFromDTO(Long id, SoOrderDTO dto) {
        SoOrder order = new SoOrder();
        BeanUtils.copyProperties(dto, order);
        order.setId(id);

        // 转换明细
        if (dto.getDetails() != null) {
            java.util.List<SoOrderDetail> details = new java.util.ArrayList<>();
            for (SoOrderDTO.SoOrderDetailDTO detailDTO : dto.getDetails()) {
                SoOrderDetail detail = new SoOrderDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            order.setDetails(details);
        }

        return updateOrder(order);
    }
}
