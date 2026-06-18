package com.opus.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.inventory.service.InvTransactionService;
import com.opus.erp.purchase.entity.PoReceipt;
import com.opus.erp.purchase.entity.PoReceiptDetail;
import com.opus.erp.purchase.enums.PoReceiptStatus;
import com.opus.erp.purchase.mapper.PoReceiptDetailMapper;
import com.opus.erp.purchase.mapper.PoReceiptMapper;
import com.opus.erp.purchase.service.PoOrderService;
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
 * PoReceiptService 单元测试
 * 测试采购入库管理 CRUD、审核、取消等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PoReceiptService 单元测试")
class PoReceiptServiceImplTest {

    @Mock
    private PoReceiptMapper receiptMapper;

    @Mock
    private PoReceiptDetailMapper receiptDetailMapper;

    @Mock
    private PoOrderService orderService;

    @Mock
    private InvTransactionService transactionService;

    @InjectMocks
    private PoReceiptServiceImpl receiptService;

    private PoReceipt testReceipt;
    private PoReceiptDetail testDetail;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(receiptService, "baseMapper", receiptMapper);

        // 准备测试入库单
        testReceipt = new PoReceipt();
        testReceipt.setId(1L);
        testReceipt.setReceiptNo("REC20240118001");
        testReceipt.setOrderId(1L);
        testReceipt.setSupplierId(1L);
        testReceipt.setWarehouseId(1L);
        testReceipt.setReceiptDate(LocalDate.now());
        testReceipt.setStatus(PoReceiptStatus.DRAFT.getCode());

        // 准备测试明细
        testDetail = new PoReceiptDetail();
        testDetail.setId(1L);
        testDetail.setReceiptId(1L);
        testDetail.setItemId(1L);
        testDetail.setOrderDetailId(1L);
        testDetail.setQuantity(new BigDecimal("100"));
        testDetail.setUnitCost(new BigDecimal("10.00"));
    }

    @Nested
    @DisplayName("查询入库单列表测试")
    class ListReceiptsTests {

        @Test
        @DisplayName("分页查询入库单列表 - 无筛选条件")
        void listReceipts_noFilter_success() {
            // given
            Page<PoReceipt> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testReceipt));
            expectedPage.setTotal(1);

            when(receiptMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<PoReceipt> result = receiptService.listReceipts(1, 10, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getReceiptNo()).isEqualTo("REC20240118001");
        }
    }

    @Nested
    @DisplayName("查询入库单详情测试")
    class GetReceiptDetailTests {

        @Test
        @DisplayName("查询入库单详情成功")
        void getReceiptDetail_success() {
            // given
            when(receiptMapper.selectById(1L)).thenReturn(testReceipt);
            when(receiptDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));

            // when
            PoReceipt result = receiptService.getReceiptDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDetails()).hasSize(1);
        }

        @Test
        @DisplayName("查询入库单详情失败 - 入库单不存在")
        void getReceiptDetail_notFound_throwsException() {
            // given
            when(receiptMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> receiptService.getReceiptDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购入库单不存在");
        }
    }

    @Nested
    @DisplayName("创建入库单测试")
    class CreateReceiptTests {

        @Test
        @DisplayName("创建入库单成功")
        void createReceipt_success() {
            // given
            PoReceipt newReceipt = new PoReceipt();
            newReceipt.setOrderId(1L);
            newReceipt.setSupplierId(1L);
            newReceipt.setWarehouseId(1L);
            newReceipt.setReceiptDate(LocalDate.now());
            newReceipt.setDetails(Arrays.asList(testDetail));

            when(receiptMapper.insert(any(PoReceipt.class))).thenReturn(1);
            when(receiptDetailMapper.insert(any(PoReceiptDetail.class))).thenReturn(1);

            // when
            PoReceipt result = receiptService.createReceipt(newReceipt);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getReceiptNo()).isNotNull();
            assertThat(result.getStatus()).isEqualTo(PoReceiptStatus.DRAFT.getCode());
            verify(receiptMapper).insert(any(PoReceipt.class));
            verify(receiptDetailMapper).insert(any(PoReceiptDetail.class));
        }
    }

    @Nested
    @DisplayName("审核入库单测试")
    class AuditReceiptTests {

        @Test
        @DisplayName("审核入库单成功")
        void auditReceipt_success() {
            // given
            when(receiptMapper.selectById(1L)).thenReturn(testReceipt);
            when(receiptDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            when(transactionService.createReceipt(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any()))
                    .thenReturn(null);
            doNothing().when(orderService).updateReceivedQuantity(anyLong(), any());
            when(receiptMapper.updateById(any(PoReceipt.class))).thenReturn(1);

            // when
            receiptService.auditReceipt(1L, 1L);

            // then
            verify(transactionService).createReceipt(
                    anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any(), any());
            verify(orderService).updateReceivedQuantity(anyLong(), any());
            verify(receiptMapper).updateById(any(PoReceipt.class));
        }

        @Test
        @DisplayName("审核入库单失败 - 入库单不存在")
        void auditReceipt_notFound_throwsException() {
            // given
            when(receiptMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> receiptService.auditReceipt(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购入库单不存在");
        }

        @Test
        @DisplayName("审核入库单失败 - 非草稿状态")
        void auditReceipt_nonDraftStatus_throwsException() {
            // given
            testReceipt.setStatus(PoReceiptStatus.AUDITED.getCode());
            when(receiptMapper.selectById(1L)).thenReturn(testReceipt);

            // when & then
            assertThatThrownBy(() -> receiptService.auditReceipt(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的入库单可以审核");
        }
    }

    @Nested
    @DisplayName("取消入库单测试")
    class CancelReceiptTests {

        @Test
        @DisplayName("取消入库单成功")
        void cancelReceipt_success() {
            // given
            when(receiptMapper.selectById(1L)).thenReturn(testReceipt);
            when(receiptMapper.updateById(any(PoReceipt.class))).thenReturn(1);

            // when
            receiptService.cancelReceipt(1L);

            // then
            verify(receiptMapper).updateById(any(PoReceipt.class));
        }

        @Test
        @DisplayName("取消入库单失败 - 入库单不存在")
        void cancelReceipt_notFound_throwsException() {
            // given
            when(receiptMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> receiptService.cancelReceipt(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("采购入库单不存在");
        }

        @Test
        @DisplayName("取消入库单失败 - 非草稿状态")
        void cancelReceipt_nonDraftStatus_throwsException() {
            // given
            testReceipt.setStatus(PoReceiptStatus.AUDITED.getCode());
            when(receiptMapper.selectById(1L)).thenReturn(testReceipt);

            // when & then
            assertThatThrownBy(() -> receiptService.cancelReceipt(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的入库单可以取消");
        }
    }
}
