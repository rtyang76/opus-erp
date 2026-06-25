package com.opus.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.production.dto.WorkOrderDTO;
import com.opus.erp.production.entity.PpWorkOrder;
import com.opus.erp.production.enums.WorkOrderStatus;
import com.opus.erp.production.mapper.PpWorkOrderMapper;
import com.opus.erp.production.service.PpWorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产工单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PpWorkOrderServiceImpl extends ServiceImpl<PpWorkOrderMapper, PpWorkOrder> implements PpWorkOrderService {

    private final InvTransactionService transactionService;

    @Override
    public Page<PpWorkOrder> listWorkOrders(int pageNum, int pageSize, String orderNo, Long itemId, String status) {
        Page<PpWorkOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PpWorkOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(PpWorkOrder::getOrderNo, orderNo);
        }
        if (itemId != null) {
            wrapper.eq(PpWorkOrder::getItemId, itemId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(PpWorkOrder::getStatus, status);
        }

        wrapper.orderByDesc(PpWorkOrder::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public PpWorkOrder getWorkOrderDetail(Long orderId) {
        PpWorkOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "生产工单不存在");
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PpWorkOrder createWorkOrder(PpWorkOrder workOrder) {
        // 生成工单号
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setStatus(WorkOrderStatus.PENDING.getCode());
        workOrder.setCompletedQuantity(BigDecimal.ZERO);

        baseMapper.insert(workOrder);

        log.info("创建生产工单成功: orderNo={}", workOrder.getOrderNo());
        return workOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseWorkOrder(Long orderId, Long releasedBy) {
        PpWorkOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "生产工单不存在");
        }
        if (!WorkOrderStatus.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有待下达的工单可以下达");
        }

        order.setStatus(WorkOrderStatus.RELEASED.getCode());
        order.setReleasedBy(releasedBy);
        order.setReleasedAt(LocalDateTime.now());
        baseMapper.updateById(order);

        log.info("下达生产工单成功: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startWorkOrder(Long orderId) {
        PpWorkOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "生产工单不存在");
        }
        if (!WorkOrderStatus.RELEASED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有已下达的工单可以开工");
        }

        order.setStatus(WorkOrderStatus.IN_PROGRESS.getCode());
        order.setActualStartDate(LocalDate.now());
        baseMapper.updateById(order);

        log.info("生产工单开工: orderId={}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeWorkOrder(Long orderId, BigDecimal completedQuantity) {
        PpWorkOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "生产工单不存在");
        }
        if (!WorkOrderStatus.IN_PROGRESS.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有生产中的工单可以完工");
        }

        // 调用库存服务入库成品
        if (order.getWarehouseId() != null) {
            transactionService.createReceipt(
                    order.getItemId(),
                    order.getWarehouseId(),
                    null,
                    null,  // 批次号
                    completedQuantity,
                    null,  // 使用移动加权平均成本
                    "WO",
                    orderId,
                    order.getOrderNo(),
                    "PRODUCTION_RECEIPT",
                    "生产完工入库"
            );
        }

        order.setCompletedQuantity(completedQuantity);
        order.setStatus(WorkOrderStatus.COMPLETED.getCode());
        order.setActualEndDate(LocalDate.now());
        baseMapper.updateById(order);

        log.info("生产工单完工: orderId={}, completedQuantity={}", orderId, completedQuantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeWorkOrder(Long orderId) {
        PpWorkOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "生产工单不存在");
        }
        if (!WorkOrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有已完工的工单可以关闭");
        }

        order.setStatus(WorkOrderStatus.CLOSED.getCode());
        baseMapper.updateById(order);

        log.info("关闭生产工单: orderId={}", orderId);
    }

    /**
     * 生成工单号
     * 使用统一的 OrderNoGenerator 工具类
     */
    private String generateOrderNo() {
        return OrderNoGenerator.generateWorkOrderNo();
    }

    @Override
    public PpWorkOrder createFromDTO(WorkOrderDTO dto) {
        PpWorkOrder workOrder = new PpWorkOrder();
        BeanUtils.copyProperties(dto, workOrder);
        return createWorkOrder(workOrder);
    }
}
