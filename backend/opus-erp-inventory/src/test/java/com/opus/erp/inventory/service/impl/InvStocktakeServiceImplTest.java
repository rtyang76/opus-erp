package com.opus.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.inventory.entity.InvStocktake;
import com.opus.erp.inventory.entity.InvStocktakeDetail;
import com.opus.erp.inventory.enums.InvDocStatus;
import com.opus.erp.inventory.mapper.InvStocktakeDetailMapper;
import com.opus.erp.inventory.mapper.InvStocktakeMapper;
import com.opus.erp.inventory.service.InvTransactionService;
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
 * InvStocktakeService 单元测试
 * 测试盘点管理 CRUD、审核（盈亏处理）、取消等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvStocktakeService 单元测试")
class InvStocktakeServiceImplTest {

    @Mock
    private InvStocktakeMapper stocktakeMapper;

    @Mock
    private InvStocktakeDetailMapper stocktakeDetailMapper;

    @Mock
    private InvTransactionService transactionService;

    @InjectMocks
    private InvStocktakeServiceImpl stocktakeService;

    private InvStocktake testStocktake;
    private InvStocktakeDetail testDetail;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(stocktakeService, "baseMapper", stocktakeMapper);

        // 准备测试盘点单
        testStocktake = new InvStocktake();
        testStocktake.setId(1L);
        testStocktake.setStocktakeNo("ST20240118001");
        testStocktake.setWarehouseId(1L);
        testStocktake.setStocktakeDate(LocalDate.now());
        testStocktake.setStatus(InvDocStatus.DRAFT.getCode());

        // 准备测试明细
        testDetail = new InvStocktakeDetail();
        testDetail.setId(1L);
        testDetail.setStocktakeId(1L);
        testDetail.setItemId(1L);
        testDetail.setSystemQuantity(new BigDecimal("100"));
        testDetail.setActualQuantity(new BigDecimal("105"));
    }

    @Nested
    @DisplayName("查询盘点单列表测试")
    class ListStocktakesTests {

        @Test
        @DisplayName("分页查询盘点单列表 - 无筛选条件")
        void listStocktakes_noFilter_success() {
            // given
            Page<InvStocktake> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testStocktake));
            expectedPage.setTotal(1);

            when(stocktakeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<InvStocktake> result = stocktakeService.listStocktakes(1, 10, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getStocktakeNo()).isEqualTo("ST20240118001");
        }

        @Test
        @DisplayName("分页查询盘点单列表 - 按盘点单号筛选")
        void listStocktakes_withStocktakeNoFilter_success() {
            // given
            Page<InvStocktake> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testStocktake));
            expectedPage.setTotal(1);

            when(stocktakeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<InvStocktake> result = stocktakeService.listStocktakes(1, 10, "ST20240118001", null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建盘点单测试")
    class CreateStocktakeTests {

        @Test
        @DisplayName("创建盘点单成功")
        void createStocktake_success() {
            // given
            InvStocktake newStocktake = new InvStocktake();
            newStocktake.setWarehouseId(1L);
            newStocktake.setStocktakeDate(LocalDate.now());
            newStocktake.setDetails(Arrays.asList(testDetail));

            when(stocktakeMapper.insert(any(InvStocktake.class))).thenReturn(1);
            when(stocktakeDetailMapper.insert(any(InvStocktakeDetail.class))).thenReturn(1);

            // when
            InvStocktake result = stocktakeService.createStocktake(newStocktake);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStocktakeNo()).isNotNull();
            assertThat(result.getStatus()).isEqualTo(InvDocStatus.DRAFT.getCode());
            verify(stocktakeMapper).insert(any(InvStocktake.class));
            verify(stocktakeDetailMapper).insert(any(InvStocktakeDetail.class));
        }

        @Test
        @DisplayName("创建盘点单成功 - 无明细")
        void createStocktake_noDetails_success() {
            // given
            InvStocktake newStocktake = new InvStocktake();
            newStocktake.setWarehouseId(1L);
            newStocktake.setStocktakeDate(LocalDate.now());

            when(stocktakeMapper.insert(any(InvStocktake.class))).thenReturn(1);

            // when
            InvStocktake result = stocktakeService.createStocktake(newStocktake);

            // then
            assertThat(result).isNotNull();
            verify(stocktakeDetailMapper, never()).insert(any(InvStocktakeDetail.class));
        }
    }

    @Nested
    @DisplayName("审核盘点单测试")
    class AuditStocktakeTests {

        @Test
        @DisplayName("审核盘点单成功 - 盘盈")
        void auditStocktake_profit_success() {
            // given
            when(stocktakeMapper.selectById(1L)).thenReturn(testStocktake);
            when(stocktakeDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            when(transactionService.createAdjustment(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any()))
                    .thenReturn(null);
            when(stocktakeMapper.updateById(any(InvStocktake.class))).thenReturn(1);

            // when
            stocktakeService.auditStocktake(1L, 1L);

            // then
            verify(transactionService).createAdjustment(
                    eq(1L), eq(1L), any(), any(),
                    eq(new BigDecimal("5")), any(), any(), eq(1L), any(), any(), any());
            verify(stocktakeMapper).updateById(any(InvStocktake.class));
        }

        @Test
        @DisplayName("审核盘点单成功 - 盘亏")
        void auditStocktake_loss_success() {
            // given
            testDetail.setActualQuantity(new BigDecimal("95"));
            when(stocktakeMapper.selectById(1L)).thenReturn(testStocktake);
            when(stocktakeDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            when(transactionService.createAdjustment(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any()))
                    .thenReturn(null);
            when(stocktakeMapper.updateById(any(InvStocktake.class))).thenReturn(1);

            // when
            stocktakeService.auditStocktake(1L, 1L);

            // then
            verify(transactionService).createAdjustment(
                    eq(1L), eq(1L), any(), any(),
                    eq(new BigDecimal("-5")), any(), any(), eq(1L), any(), any(), any());
            verify(stocktakeMapper).updateById(any(InvStocktake.class));
        }

        @Test
        @DisplayName("审核盘点单成功 - 无差异")
        void auditStocktake_noDiff_success() {
            // given
            testDetail.setActualQuantity(new BigDecimal("100"));
            when(stocktakeMapper.selectById(1L)).thenReturn(testStocktake);
            when(stocktakeDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            when(stocktakeMapper.updateById(any(InvStocktake.class))).thenReturn(1);

            // when
            stocktakeService.auditStocktake(1L, 1L);

            // then
            verify(transactionService, never()).createAdjustment(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any());
            verify(stocktakeMapper).updateById(any(InvStocktake.class));
        }

        @Test
        @DisplayName("审核盘点单失败 - 盘点单不存在")
        void auditStocktake_notFound_throwsException() {
            // given
            when(stocktakeMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> stocktakeService.auditStocktake(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("盘点单不存在");
        }

        @Test
        @DisplayName("审核盘点单失败 - 非草稿状态")
        void auditStocktake_nonDraftStatus_throwsException() {
            // given
            testStocktake.setStatus(InvDocStatus.AUDITED.getCode());
            when(stocktakeMapper.selectById(1L)).thenReturn(testStocktake);

            // when & then
            assertThatThrownBy(() -> stocktakeService.auditStocktake(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的盘点单可以审核");
        }
    }

    @Nested
    @DisplayName("取消盘点单测试")
    class CancelStocktakeTests {

        @Test
        @DisplayName("取消盘点单成功")
        void cancelStocktake_success() {
            // given
            when(stocktakeMapper.selectById(1L)).thenReturn(testStocktake);
            when(stocktakeMapper.updateById(any(InvStocktake.class))).thenReturn(1);

            // when
            stocktakeService.cancelStocktake(1L);

            // then
            verify(stocktakeMapper).updateById(any(InvStocktake.class));
        }

        @Test
        @DisplayName("取消盘点单失败 - 盘点单不存在")
        void cancelStocktake_notFound_throwsException() {
            // given
            when(stocktakeMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> stocktakeService.cancelStocktake(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("盘点单不存在");
        }

        @Test
        @DisplayName("取消盘点单失败 - 非草稿状态")
        void cancelStocktake_nonDraftStatus_throwsException() {
            // given
            testStocktake.setStatus(InvDocStatus.AUDITED.getCode());
            when(stocktakeMapper.selectById(1L)).thenReturn(testStocktake);

            // when & then
            assertThatThrownBy(() -> stocktakeService.cancelStocktake(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的盘点单可以取消");
        }
    }
}
