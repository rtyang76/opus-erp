package com.opus.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.purchase.dto.PoOrderDTO;
import com.opus.erp.purchase.entity.PoOrder;
import com.opus.erp.purchase.entity.PoOrderDetail;
import com.opus.erp.purchase.enums.PoOrderStatus;
import com.opus.erp.purchase.mapper.PoOrderDetailMapper;
import com.opus.erp.purchase.mapper.PoOrderMapper;
import com.opus.erp.purchase.service.PoOrderService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 采购订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PoOrderServiceImpl extends ServiceImpl<PoOrderMapper, PoOrder> implements PoOrderService {

    private final PoOrderDetailMapper orderDetailMapper;

    @Override
    public Page<PoOrder> listOrders(int pageNum, int pageSize, String orderNo,
                                     Long supplierId, String status, String startDate, String endDate) {
        Page<PoOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PoOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(PoOrder::getOrderNo, orderNo);
        }
        if (supplierId != null) {
            wrapper.eq(PoOrder::getSupplierId, supplierId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PoOrder::getStatus, status);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(PoOrder::getOrderDate, LocalDate.parse(startDate));
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(PoOrder::getOrderDate, LocalDate.parse(endDate));
        }

        wrapper.orderByDesc(PoOrder::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public PoOrder getOrderDetail(Long orderId) {
        PoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购订单不存在");
        }

        // 查询明细
        LambdaQueryWrapper<PoOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PoOrderDetail::getOrderId, orderId);
        order.setDetails(orderDetailMapper.selectList(wrapper));

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PoOrder createOrder(PoOrder order) {
        // 生成订单号
        order.setOrderNo(generateOrderNo());
        order.setStatus(PoOrderStatus.DRAFT.getCode());

        // 计算总金额和税额
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        // 保存主表
        baseMapper.insert(order);

        // 保存明细
        if (order.getDetails() != null) {
            for (PoOrderDetail detail : order.getDetails()) {
                detail.setOrderId(order.getId());
                detail.setReceivedQuantity(BigDecimal.ZERO);
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

        log.info("创建采购订单成功: orderNo={}", order.getOrderNo());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PoOrder updateOrder(PoOrder order) {
        PoOrder existingOrder = baseMapper.selectById(order.getId());
        if (existingOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购订单不存在");
        }
        if (!PoOrderStatus.DRAFT.getCode().equals(existingOrder.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的订单可以修改");
        }

        // 更新主表
        baseMapper.updateById(order);

        // 删除旧明细，插入新明细
        if (order.getDetails() != null) {
            LambdaQueryWrapper<PoOrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PoOrderDetail::getOrderId, order.getId());
            orderDetailMapper.delete(wrapper);

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal taxAmount = BigDecimal.ZERO;

            for (PoOrderDetail detail : order.getDetails()) {
                detail.setOrderId(order.getId());
                detail.setReceivedQuantity(BigDecimal.ZERO);
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

        log.info("更新采购订单成功: orderId={}", order.getId());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long orderId, Long auditorId) {
        PoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购订单不存在");
        }
        if (!PoOrderStatus.DRAFT.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的订单可以审核");
        }

        order.setStatus(PoOrderStatus.AUDITED.getCode());
        order.setAuditedBy(auditorId);
        order.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(order);

        log.info("审核采购订单成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        PoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购订单不存在");
        }
        if (!PoOrderStatus.DRAFT.getCode().equals(order.getStatus()) && !PoOrderStatus.AUDITED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿或已审核的订单可以取消");
        }

        // TODO: 检查是否有入库记录
        // 如果已有入库记录，应该不允许取消，或者需要先取消入库单
        // 查询 po_receipt 表是否有该订单的已审核入库记录

        order.setStatus(PoOrderStatus.CANCELLED.getCode());
        baseMapper.updateById(order);

        log.info("取消采购订单成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(Long orderId) {
        PoOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购订单不存在");
        }
        if (!PoOrderStatus.AUDITED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有已审核的订单可以关闭");
        }

        order.setStatus(PoOrderStatus.CLOSED.getCode());
        baseMapper.updateById(order);

        log.info("关闭采购订单成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReceivedQuantity(Long orderDetailId, BigDecimal quantity) {
        PoOrderDetail orderDetail = orderDetailMapper.selectById(orderDetailId);
        if (orderDetail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "采购订单明细不存在");
        }

        orderDetail.setReceivedQuantity(orderDetail.getReceivedQuantity().add(quantity));
        orderDetailMapper.updateById(orderDetail);

        log.info("更新采购订单明细已收货数量: orderDetailId={}, quantity={}", orderDetailId, quantity);
    }

    /**
     * 生成采购订单号
     */
    private String generateOrderNo() {
        return OrderNoGenerator.generatePoOrderNo();
    }

    @Override
    public PoOrder createFromDTO(PoOrderDTO dto) {
        PoOrder order = new PoOrder();
        BeanUtils.copyProperties(dto, order);

        // 转换明细
        if (dto.getDetails() != null) {
            List<PoOrderDetail> details = new ArrayList<>();
            for (PoOrderDTO.PoOrderDetailDTO detailDTO : dto.getDetails()) {
                PoOrderDetail detail = new PoOrderDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            order.setDetails(details);
        }

        return createOrder(order);
    }

    @Override
    public PoOrder updateFromDTO(Long id, PoOrderDTO dto) {
        PoOrder order = new PoOrder();
        BeanUtils.copyProperties(dto, order);
        order.setId(id);

        // 转换明细
        if (dto.getDetails() != null) {
            List<PoOrderDetail> details = new ArrayList<>();
            for (PoOrderDTO.PoOrderDetailDTO detailDTO : dto.getDetails()) {
                PoOrderDetail detail = new PoOrderDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            order.setDetails(details);
        }

        return updateOrder(order);
    }
}
