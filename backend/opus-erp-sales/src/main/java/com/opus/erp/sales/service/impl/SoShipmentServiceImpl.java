package com.opus.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.OrderNoGenerator;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.sales.dto.SoShipmentDTO;
import com.opus.erp.sales.entity.SoShipment;
import com.opus.erp.sales.entity.SoShipmentDetail;
import com.opus.erp.sales.enums.SoShipmentStatus;
import com.opus.erp.sales.mapper.SoShipmentDetailMapper;
import com.opus.erp.sales.mapper.SoShipmentMapper;
import com.opus.erp.sales.service.SoOrderService;
import com.opus.erp.sales.service.SoShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 销售出库服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoShipmentServiceImpl extends ServiceImpl<SoShipmentMapper, SoShipment> implements SoShipmentService {

    private final SoShipmentDetailMapper shipmentDetailMapper;
    private final SoOrderService orderService;
    private final InvTransactionService transactionService;

    @Override
    public Page<SoShipment> listShipments(int pageNum, int pageSize, String shipmentNo,
                                           Long orderId, Long customerId, String status) {
        Page<SoShipment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SoShipment> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(shipmentNo)) {
            wrapper.like(SoShipment::getShipmentNo, shipmentNo);
        }
        if (orderId != null) {
            wrapper.eq(SoShipment::getOrderId, orderId);
        }
        if (customerId != null) {
            wrapper.eq(SoShipment::getCustomerId, customerId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SoShipment::getStatus, status);
        }

        wrapper.orderByDesc(SoShipment::getCreatedAt);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public SoShipment getShipmentDetail(Long shipmentId) {
        SoShipment shipment = baseMapper.selectById(shipmentId);
        if (shipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售出库单不存在");
        }

        // 查询明细
        LambdaQueryWrapper<SoShipmentDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoShipmentDetail::getShipmentId, shipmentId);
        shipment.setDetails(shipmentDetailMapper.selectList(wrapper));

        return shipment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoShipment createShipment(SoShipment shipment) {
        // 生成出库单号
        shipment.setShipmentNo(generateShipmentNo());
        shipment.setStatus(SoShipmentStatus.DRAFT.getCode());

        // 保存主表
        baseMapper.insert(shipment);

        // 保存明细
        if (shipment.getDetails() != null) {
            for (SoShipmentDetail detail : shipment.getDetails()) {
                detail.setShipmentId(shipment.getId());
                shipmentDetailMapper.insert(detail);
            }
        }

        log.info("创建销售出库单成功: shipmentNo={}", shipment.getShipmentNo());
        return shipment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditShipment(Long shipmentId, Long auditorId) {
        SoShipment shipment = baseMapper.selectById(shipmentId);
        if (shipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售出库单不存在");
        }
        if (!SoShipmentStatus.DRAFT.getCode().equals(shipment.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的出库单可以审核");
        }

        // 查询明细
        LambdaQueryWrapper<SoShipmentDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoShipmentDetail::getShipmentId, shipmentId);
        var details = shipmentDetailMapper.selectList(wrapper);

        // 调用库存服务出库
        for (SoShipmentDetail detail : details) {
            // 校验可用库存
            if (!transactionService.checkAvailableStock(
                    detail.getItemId(), shipment.getWarehouseId(), null, detail.getLotNo(), detail.getQuantity())) {
                throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                        String.format("物料[%d] 库存不足", detail.getItemId()));
            }

            transactionService.createIssue(
                    detail.getItemId(),
                    shipment.getWarehouseId(),
                    null,
                    detail.getLotNo(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    "SO",
                    shipment.getOrderId(),
                    shipment.getShipmentNo(),
                    "SALES_SHIPMENT",
                    detail.getRemark()
            );

            // 更新销售订单明细的已出库数量
            if (detail.getOrderDetailId() != null) {
                orderService.updateShippedQuantity(detail.getOrderDetailId(), detail.getQuantity());
            }
        }

        // 更新出库单状态
        shipment.setStatus(SoShipmentStatus.AUDITED.getCode());
        shipment.setAuditedBy(auditorId);
        shipment.setAuditedAt(LocalDateTime.now());
        baseMapper.updateById(shipment);

        log.info("审核销售出库单成功: shipmentId={}", shipmentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShipment(Long shipmentId) {
        SoShipment shipment = baseMapper.selectById(shipmentId);
        if (shipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "销售出库单不存在");
        }
        if (!SoShipmentStatus.DRAFT.getCode().equals(shipment.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "只有草稿状态的出库单可以取消");
        }

        shipment.setStatus(SoShipmentStatus.CANCELLED.getCode());
        baseMapper.updateById(shipment);

        log.info("取消销售出库单成功: shipmentId={}", shipmentId);
    }

    /**
     * 生成销售出库单号
     */
    private String generateShipmentNo() {
        return OrderNoGenerator.generateSoShipmentNo();
    }

    @Override
    public SoShipment createFromDTO(SoShipmentDTO dto) {
        SoShipment shipment = new SoShipment();
        BeanUtils.copyProperties(dto, shipment);

        // 转换明细
        if (dto.getDetails() != null) {
            java.util.List<SoShipmentDetail> details = new java.util.ArrayList<>();
            for (SoShipmentDTO.SoShipmentDetailDTO detailDTO : dto.getDetails()) {
                SoShipmentDetail detail = new SoShipmentDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                details.add(detail);
            }
            shipment.setDetails(details);
        }

        return createShipment(shipment);
    }
}
