package com.opus.erp.sales.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.sales.entity.SoShipment;
import com.opus.erp.sales.entity.SoShipmentDetail;
import com.opus.erp.sales.enums.SoShipmentStatus;
import com.opus.erp.sales.mapper.SoShipmentDetailMapper;
import com.opus.erp.sales.mapper.SoShipmentMapper;
import com.opus.erp.sales.service.SoOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SoShipmentService 单元测试
 * 测试销售出库管理 CRUD、审核、取消等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SoShipmentService 单元测试")
class SoShipmentServiceImplTest {

    @Mock
    private SoShipmentMapper shipmentMapper;

    @Mock
    private SoShipmentDetailMapper shipmentDetailMapper;

    @Mock
    private SoOrderService orderService;

    @Mock
    private InvTransactionService transactionService;

    @InjectMocks
    private SoShipmentServiceImpl shipmentService;

    private SoShipment testShipment;
    private SoShipmentDetail testDetail;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(shipmentService, "baseMapper", shipmentMapper);

        // 准备测试出库单
        testShipment = new SoShipment();
        testShipment.setId(1L);
        testShipment.setShipmentNo("SHP20240118001");
        testShipment.setOrderId(1L);
        testShipment.setCustomerId(1L);
        testShipment.setWarehouseId(1L);
        testShipment.setShipmentDate(LocalDate.now());
        testShipment.setStatus(SoShipmentStatus.DRAFT.getCode());

        // 准备测试明细
        testDetail = new SoShipmentDetail();
        testDetail.setId(1L);
        testDetail.setShipmentId(1L);
        testDetail.setItemId(1L);
        testDetail.setOrderDetailId(1L);
        testDetail.setQuantity(new BigDecimal("50"));
        testDetail.setUnitPrice(new BigDecimal("20.00"));
    }

    @Nested
    @DisplayName("查询出库单列表测试")
    class ListShipmentsTests {

        @Test
        @DisplayName("分页查询出库单列表 - 无筛选条件")
        void listShipments_noFilter_success() {
            // given
            Page<SoShipment> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testShipment));
            expectedPage.setTotal(1);

            when(shipmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<SoShipment> result = shipmentService.listShipments(1, 10, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getShipmentNo()).isEqualTo("SHP20240118001");
        }
    }

    @Nested
    @DisplayName("查询出库单详情测试")
    class GetShipmentDetailTests {

        @Test
        @DisplayName("查询出库单详情成功")
        void getShipmentDetail_success() {
            // given
            when(shipmentMapper.selectById(1L)).thenReturn(testShipment);
            when(shipmentDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));

            // when
            SoShipment result = shipmentService.getShipmentDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDetails()).hasSize(1);
        }

        @Test
        @DisplayName("查询出库单详情失败 - 出库单不存在")
        void getShipmentDetail_notFound_throwsException() {
            // given
            when(shipmentMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> shipmentService.getShipmentDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("销售出库单不存在");
        }
    }

    @Nested
    @DisplayName("创建出库单测试")
    class CreateShipmentTests {

        @Test
        @DisplayName("创建出库单成功")
        void createShipment_success() {
            // given
            SoShipment newShipment = new SoShipment();
            newShipment.setOrderId(1L);
            newShipment.setCustomerId(1L);
            newShipment.setWarehouseId(1L);
            newShipment.setShipmentDate(LocalDate.now());
            newShipment.setDetails(Arrays.asList(testDetail));

            when(shipmentMapper.insert(any(SoShipment.class))).thenReturn(1);
            when(shipmentDetailMapper.insert(any(SoShipmentDetail.class))).thenReturn(1);

            // when
            SoShipment result = shipmentService.createShipment(newShipment);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getShipmentNo()).isNotNull();
            assertThat(result.getStatus()).isEqualTo(SoShipmentStatus.DRAFT.getCode());
            verify(shipmentMapper).insert(any(SoShipment.class));
            verify(shipmentDetailMapper).insert(any(SoShipmentDetail.class));
        }
    }

    @Nested
    @DisplayName("审核出库单测试")
    class AuditShipmentTests {

        @Test
        @DisplayName("审核出库单成功")
        void auditShipment_success() {
            // given
            when(shipmentMapper.selectById(1L)).thenReturn(testShipment);
            when(shipmentDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            when(transactionService.checkAvailableStock(
                    anyLong(), anyLong(), any(), any(), any()))
                    .thenReturn(true);
            when(transactionService.createIssue(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any()))
                    .thenReturn(null);
            doNothing().when(orderService).updateShippedQuantity(anyLong(), any());
            when(shipmentMapper.updateById(any(SoShipment.class))).thenReturn(1);

            // when
            shipmentService.auditShipment(1L, 1L);

            // then
            verify(transactionService).checkAvailableStock(
                    anyLong(), anyLong(), any(), any(), any());
            verify(transactionService).createIssue(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any());
            verify(orderService).updateShippedQuantity(anyLong(), any());
            verify(shipmentMapper).updateById(any(SoShipment.class));
        }

        @Test
        @DisplayName("审核出库单失败 - 出库单不存在")
        void auditShipment_notFound_throwsException() {
            // given
            when(shipmentMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> shipmentService.auditShipment(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("销售出库单不存在");
        }

        @Test
        @DisplayName("审核出库单失败 - 非草稿状态")
        void auditShipment_nonDraftStatus_throwsException() {
            // given
            testShipment.setStatus(SoShipmentStatus.AUDITED.getCode());
            when(shipmentMapper.selectById(1L)).thenReturn(testShipment);

            // when & then
            assertThatThrownBy(() -> shipmentService.auditShipment(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的出库单可以审核");
        }

        @Test
        @DisplayName("审核出库单失败 - 库存不足")
        void auditShipment_insufficientStock_throwsException() {
            // given
            when(shipmentMapper.selectById(1L)).thenReturn(testShipment);
            when(shipmentDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            when(transactionService.checkAvailableStock(
                    anyLong(), anyLong(), any(), any(), any()))
                    .thenReturn(false);

            // when & then
            assertThatThrownBy(() -> shipmentService.auditShipment(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("库存不足");
        }
    }

    @Nested
    @DisplayName("取消出库单测试")
    class CancelShipmentTests {

        @Test
        @DisplayName("取消出库单成功")
        void cancelShipment_success() {
            // given
            when(shipmentMapper.selectById(1L)).thenReturn(testShipment);
            when(shipmentMapper.updateById(any(SoShipment.class))).thenReturn(1);

            // when
            shipmentService.cancelShipment(1L);

            // then
            verify(shipmentMapper).updateById(any(SoShipment.class));
        }

        @Test
        @DisplayName("取消出库单失败 - 出库单不存在")
        void cancelShipment_notFound_throwsException() {
            // given
            when(shipmentMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> shipmentService.cancelShipment(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("销售出库单不存在");
        }

        @Test
        @DisplayName("取消出库单失败 - 非草稿状态")
        void cancelShipment_nonDraftStatus_throwsException() {
            // given
            testShipment.setStatus(SoShipmentStatus.AUDITED.getCode());
            when(shipmentMapper.selectById(1L)).thenReturn(testShipment);

            // when & then
            assertThatThrownBy(() -> shipmentService.cancelShipment(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的出库单可以取消");
        }
    }
}
