package com.opus.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.purchase.entity.PoOrder;
import com.opus.erp.purchase.entity.PoOrderDetail;
import com.opus.erp.purchase.enums.PoOrderStatus;
import com.opus.erp.purchase.mapper.PoOrderDetailMapper;
import com.opus.erp.purchase.mapper.PoOrderMapper;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PoOrderService 单元测试
 * 测试采购订单管理 CRUD、审核、取消、关闭等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PoOrderService 单元测试")
class PoOrderServiceImplTest {

    @Mock
    private PoOrderMapper orderMapper;

    @Mock
    private PoOrderDetailMapper orderDetailMapper;

    @InjectMocks
    private PoOrderServiceImpl orderService;

    private PoOrder testOrder;
    private PoOrderDetail testDetail;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(orderService, "baseMapper", orderMapper);

        // 准备测试订单
        testOrder = new PoOrder();
        testOrder.setId(1L);
        testOrder.setOrderNo("PO20240118001");
        testOrder.setSupplierId(1L);
        testOrder.setOrderDate(LocalDate.now());
        testOrder.setStatus(PoOrderStatus.DRAFT.getCode());
        testOrder.setTotalAmount(new BigDecimal("10000.00"));
        testOrder.setTaxAmount(new BigDecimal("1300.00"));

        // 准备测试明细
        testDetail = new PoOrderDetail();
        testDetail.setId(1L);
        testDetail.setOrderId(1L);
        testDetail.setItemId(1L);
        testDetail.setQuantity(new BigDecimal("100"));
        testDetail.setUnitPrice(new BigDecimal("100.00"));
        testDetail.setTaxRate(new BigDecimal("13"));
        testDetail.setReceivedQuantity(BigDecimal.ZERO);
    }

    @Nested
    @DisplayName("查询订单列表测试")
    class ListOrdersTests {

        @Test
        @DisplayName("分页查询订单列表 - 无筛选条件")
        void listOrders_noFilter_success() {
            // given
            Page<PoOrder> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testOrder));
            expectedPage.setTotal(1);

            when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<PoOrder> result = orderService.listOrders(1, 10, null, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getOrderNo()).isEqualTo("PO20240118001");
        }

        @Test
        @DisplayName("分页查询订单列表 - 按订单号筛选")
        void listOrders_withOrderNoFilter_success() {
            // given
            Page<PoOrder> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testOrder));
            expectedPage.setTotal(1);

            when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<PoOrder> result = orderService.listOrders(1, 10, "PO20240118001", null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("查询订单详情测试")
    class GetOrderDetailTests {

        @Test
        @DisplayName("查询订单详情成功")
        void getOrderDetail_success() {
            // given
            when(orderMapper.selectById(1L)).thenReturn(testOrder);
            when(orderDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));

            // when
            PoOrder result = orderService.getOrderDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDetails()).hasSize(1);
        }

        @Test
        @DisplayName("查询订单详情失败 - 订单不存在")
        void getOrderDetail_notFound_throwsException() {
            // given
            when(orderMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> orderService.getOrderDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购订单不存在");
        }
    }

    @Nested
    @DisplayName("创建订单测试")
    class CreateOrderTests {

        @Test
        @DisplayName("创建订单成功")
        void createOrder_success() {
            // given
            PoOrder newOrder = new PoOrder();
            newOrder.setSupplierId(1L);
            newOrder.setOrderDate(LocalDate.now());
            newOrder.setDetails(Arrays.asList(testDetail));

            when(orderMapper.insert(any(PoOrder.class))).thenReturn(1);
            when(orderDetailMapper.insert(any(PoOrderDetail.class))).thenReturn(1);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);

            // when
            PoOrder result = orderService.createOrder(newOrder);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getOrderNo()).isNotNull();
            assertThat(result.getStatus()).isEqualTo(PoOrderStatus.DRAFT.getCode());
            verify(orderMapper).insert(any(PoOrder.class));
            verify(orderDetailMapper).insert(any(PoOrderDetail.class));
        }

        @Test
        @DisplayName("创建订单成功 - 无明细")
        void createOrder_noDetails_success() {
            // given
            PoOrder newOrder = new PoOrder();
            newOrder.setSupplierId(1L);
            newOrder.setOrderDate(LocalDate.now());

            when(orderMapper.insert(any(PoOrder.class))).thenReturn(1);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);

            // when
            PoOrder result = orderService.createOrder(newOrder);

            // then
            assertThat(result).isNotNull();
            verify(orderDetailMapper, never()).insert(any(PoOrderDetail.class));
        }
    }

    @Nested
    @DisplayName("更新订单测试")
    class UpdateOrderTests {

        @Test
        @DisplayName("更新订单成功 - 草稿状态")
        void updateOrder_draftStatus_success() {
            // given
            PoOrder updateOrder = new PoOrder();
            updateOrder.setId(1L);
            updateOrder.setSupplierId(2L);
            updateOrder.setDetails(Arrays.asList(testDetail));

            when(orderMapper.selectById(1L)).thenReturn(testOrder);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);
            when(orderDetailMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
            when(orderDetailMapper.insert(any(PoOrderDetail.class))).thenReturn(1);

            // when
            PoOrder result = orderService.updateOrder(updateOrder);

            // then
            assertThat(result).isNotNull();
            verify(orderDetailMapper).delete(any(LambdaQueryWrapper.class));
            verify(orderDetailMapper).insert(any(PoOrderDetail.class));
        }

        @Test
        @DisplayName("更新订单失败 - 订单不存在")
        void updateOrder_notFound_throwsException() {
            // given
            PoOrder updateOrder = new PoOrder();
            updateOrder.setId(999L);

            when(orderMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> orderService.updateOrder(updateOrder))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购订单不存在");
        }

        @Test
        @DisplayName("更新订单失败 - 非草稿状态")
        void updateOrder_nonDraftStatus_throwsException() {
            // given
            testOrder.setStatus(PoOrderStatus.AUDITED.getCode());
            PoOrder updateOrder = new PoOrder();
            updateOrder.setId(1L);

            when(orderMapper.selectById(1L)).thenReturn(testOrder);

            // when & then
            assertThatThrownBy(() -> orderService.updateOrder(updateOrder))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的订单可以修改");
        }
    }

    @Nested
    @DisplayName("审核订单测试")
    class AuditOrderTests {

        @Test
        @DisplayName("审核订单成功")
        void auditOrder_success() {
            // given
            when(orderMapper.selectById(1L)).thenReturn(testOrder);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);

            // when
            orderService.auditOrder(1L, 1L);

            // then
            verify(orderMapper).updateById(any(PoOrder.class));
        }

        @Test
        @DisplayName("审核订单失败 - 订单不存在")
        void auditOrder_notFound_throwsException() {
            // given
            when(orderMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> orderService.auditOrder(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购订单不存在");
        }

        @Test
        @DisplayName("审核订单失败 - 非草稿状态")
        void auditOrder_nonDraftStatus_throwsException() {
            // given
            testOrder.setStatus(PoOrderStatus.AUDITED.getCode());
            when(orderMapper.selectById(1L)).thenReturn(testOrder);

            // when & then
            assertThatThrownBy(() -> orderService.auditOrder(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的订单可以审核");
        }
    }

    @Nested
    @DisplayName("取消订单测试")
    class CancelOrderTests {

        @Test
        @DisplayName("取消订单成功 - 草稿状态")
        void cancelOrder_draftStatus_success() {
            // given
            when(orderMapper.selectById(1L)).thenReturn(testOrder);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);

            // when
            orderService.cancelOrder(1L);

            // then
            verify(orderMapper).updateById(any(PoOrder.class));
        }

        @Test
        @DisplayName("取消订单成功 - 已审核状态")
        void cancelOrder_auditedStatus_success() {
            // given
            testOrder.setStatus(PoOrderStatus.AUDITED.getCode());
            when(orderMapper.selectById(1L)).thenReturn(testOrder);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);

            // when
            orderService.cancelOrder(1L);

            // then
            verify(orderMapper).updateById(any(PoOrder.class));
        }

        @Test
        @DisplayName("取消订单失败 - 订单不存在")
        void cancelOrder_notFound_throwsException() {
            // given
            when(orderMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购订单不存在");
        }

        @Test
        @DisplayName("取消订单失败 - 已关闭状态")
        void cancelOrder_closedStatus_throwsException() {
            // given
            testOrder.setStatus(PoOrderStatus.CLOSED.getCode());
            when(orderMapper.selectById(1L)).thenReturn(testOrder);

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿或已审核的订单可以取消");
        }
    }

    @Nested
    @DisplayName("关闭订单测试")
    class CloseOrderTests {

        @Test
        @DisplayName("关闭订单成功")
        void closeOrder_success() {
            // given
            testOrder.setStatus(PoOrderStatus.AUDITED.getCode());
            when(orderMapper.selectById(1L)).thenReturn(testOrder);
            when(orderMapper.updateById(any(PoOrder.class))).thenReturn(1);

            // when
            orderService.closeOrder(1L);

            // then
            verify(orderMapper).updateById(any(PoOrder.class));
        }

        @Test
        @DisplayName("关闭订单失败 - 订单不存在")
        void closeOrder_notFound_throwsException() {
            // given
            when(orderMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> orderService.closeOrder(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购订单不存在");
        }

        @Test
        @DisplayName("关闭订单失败 - 非已审核状态")
        void closeOrder_nonAuditedStatus_throwsException() {
            // given
            when(orderMapper.selectById(1L)).thenReturn(testOrder);

            // when & then
            assertThatThrownBy(() -> orderService.closeOrder(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有已审核的订单可以关闭");
        }
    }

    @Nested
    @DisplayName("更新已收货数量测试")
    class UpdateReceivedQuantityTests {

        @Test
        @DisplayName("更新已收货数量成功")
        void updateReceivedQuantity_success() {
            // given
            when(orderDetailMapper.selectById(1L)).thenReturn(testDetail);
            when(orderDetailMapper.updateById(any(PoOrderDetail.class))).thenReturn(1);

            // when
            orderService.updateReceivedQuantity(1L, new BigDecimal("50"));

            // then
            verify(orderDetailMapper).updateById(any(PoOrderDetail.class));
        }

        @Test
        @DisplayName("更新已收货数量失败 - 明细不存在")
        void updateReceivedQuantity_notFound_throwsException() {
            // given
            when(orderDetailMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> orderService.updateReceivedQuantity(999L, new BigDecimal("50")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购订单明细不存在");
        }
    }
}
