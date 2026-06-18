package com.opus.erp.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.inventory.entity.InvTransfer;
import com.opus.erp.inventory.entity.InvTransferDetail;
import com.opus.erp.inventory.enums.InvDocStatus;
import com.opus.erp.inventory.mapper.InvTransferDetailMapper;
import com.opus.erp.inventory.mapper.InvTransferMapper;
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
 * InvTransferService 单元测试
 * 测试调拨管理 CRUD、审核、取消等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvTransferService 单元测试")
class InvTransferServiceImplTest {

    @Mock
    private InvTransferMapper transferMapper;

    @Mock
    private InvTransferDetailMapper transferDetailMapper;

    @Mock
    private InvTransactionService transactionService;

    @InjectMocks
    private InvTransferServiceImpl transferService;

    private InvTransfer testTransfer;
    private InvTransferDetail testDetail;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(transferService, "baseMapper", transferMapper);

        // 准备测试调拨单
        testTransfer = new InvTransfer();
        testTransfer.setId(1L);
        testTransfer.setTransferNo("TR20240118001");
        testTransfer.setFromWarehouseId(1L);
        testTransfer.setToWarehouseId(2L);
        testTransfer.setTransferDate(LocalDate.now());
        testTransfer.setStatus(InvDocStatus.DRAFT.getCode());

        // 准备测试明细
        testDetail = new InvTransferDetail();
        testDetail.setId(1L);
        testDetail.setTransferId(1L);
        testDetail.setItemId(1L);
        testDetail.setQuantity(new BigDecimal("100"));
    }

    @Nested
    @DisplayName("查询调拨单列表测试")
    class ListTransfersTests {

        @Test
        @DisplayName("分页查询调拨单列表 - 无筛选条件")
        void listTransfers_noFilter_success() {
            // given
            Page<InvTransfer> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testTransfer));
            expectedPage.setTotal(1);

            when(transferMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<InvTransfer> result = transferService.listTransfers(1, 10, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getTransferNo()).isEqualTo("TR20240118001");
        }

        @Test
        @DisplayName("分页查询调拨单列表 - 按调拨单号筛选")
        void listTransfers_withTransferNoFilter_success() {
            // given
            Page<InvTransfer> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testTransfer));
            expectedPage.setTotal(1);

            when(transferMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<InvTransfer> result = transferService.listTransfers(1, 10, "TR20240118001", null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建调拨单测试")
    class CreateTransferTests {

        @Test
        @DisplayName("创建调拨单成功")
        void createTransfer_success() {
            // given
            InvTransfer newTransfer = new InvTransfer();
            newTransfer.setFromWarehouseId(1L);
            newTransfer.setToWarehouseId(2L);
            newTransfer.setTransferDate(LocalDate.now());
            newTransfer.setDetails(Arrays.asList(testDetail));

            when(transferMapper.insert(any(InvTransfer.class))).thenReturn(1);
            when(transferDetailMapper.insert(any(InvTransferDetail.class))).thenReturn(1);

            // when
            InvTransfer result = transferService.createTransfer(newTransfer);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTransferNo()).isNotNull();
            assertThat(result.getStatus()).isEqualTo(InvDocStatus.DRAFT.getCode());
            verify(transferMapper).insert(any(InvTransfer.class));
            verify(transferDetailMapper).insert(any(InvTransferDetail.class));
        }

        @Test
        @DisplayName("创建调拨单成功 - 无明细")
        void createTransfer_noDetails_success() {
            // given
            InvTransfer newTransfer = new InvTransfer();
            newTransfer.setFromWarehouseId(1L);
            newTransfer.setToWarehouseId(2L);
            newTransfer.setTransferDate(LocalDate.now());

            when(transferMapper.insert(any(InvTransfer.class))).thenReturn(1);

            // when
            InvTransfer result = transferService.createTransfer(newTransfer);

            // then
            assertThat(result).isNotNull();
            verify(transferDetailMapper, never()).insert(any(InvTransferDetail.class));
        }
    }

    @Nested
    @DisplayName("审核调拨单测试")
    class AuditTransferTests {

        @Test
        @DisplayName("审核调拨单成功")
        void auditTransfer_success() {
            // given
            when(transferMapper.selectById(1L)).thenReturn(testTransfer);
            when(transferDetailMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDetail));
            doNothing().when(transactionService).createTransfer(
                    anyLong(), anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any());
            when(transferMapper.updateById(any(InvTransfer.class))).thenReturn(1);

            // when
            transferService.auditTransfer(1L, 1L);

            // then
            verify(transactionService).createTransfer(
                    anyLong(), anyLong(), anyLong(), any(), any(),
                    any(), any(), any(), anyLong(), any(), any());
            verify(transferMapper).updateById(any(InvTransfer.class));
        }

        @Test
        @DisplayName("审核调拨单失败 - 调拨单不存在")
        void auditTransfer_notFound_throwsException() {
            // given
            when(transferMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> transferService.auditTransfer(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("调拨单不存在");
        }

        @Test
        @DisplayName("审核调拨单失败 - 非草稿状态")
        void auditTransfer_nonDraftStatus_throwsException() {
            // given
            testTransfer.setStatus(InvDocStatus.AUDITED.getCode());
            when(transferMapper.selectById(1L)).thenReturn(testTransfer);

            // when & then
            assertThatThrownBy(() -> transferService.auditTransfer(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的调拨单可以审核");
        }
    }

    @Nested
    @DisplayName("取消调拨单测试")
    class CancelTransferTests {

        @Test
        @DisplayName("取消调拨单成功")
        void cancelTransfer_success() {
            // given
            when(transferMapper.selectById(1L)).thenReturn(testTransfer);
            when(transferMapper.updateById(any(InvTransfer.class))).thenReturn(1);

            // when
            transferService.cancelTransfer(1L);

            // then
            verify(transferMapper).updateById(any(InvTransfer.class));
        }

        @Test
        @DisplayName("取消调拨单失败 - 调拨单不存在")
        void cancelTransfer_notFound_throwsException() {
            // given
            when(transferMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> transferService.cancelTransfer(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("调拨单不存在");
        }

        @Test
        @DisplayName("取消调拨单失败 - 非草稿状态")
        void cancelTransfer_nonDraftStatus_throwsException() {
            // given
            testTransfer.setStatus(InvDocStatus.AUDITED.getCode());
            when(transferMapper.selectById(1L)).thenReturn(testTransfer);

            // when & then
            assertThatThrownBy(() -> transferService.cancelTransfer(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("只有草稿状态的调拨单可以取消");
        }
    }
}
