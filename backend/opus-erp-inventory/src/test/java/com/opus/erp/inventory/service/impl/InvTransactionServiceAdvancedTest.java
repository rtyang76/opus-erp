package com.opus.erp.inventory.service.impl;

import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.inventory.entity.InvStock;
import com.opus.erp.inventory.entity.InvTransaction;
import com.opus.erp.inventory.mapper.InvStockMapper;
import com.opus.erp.inventory.mapper.InvTransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * InvTransactionService 高级测试
 * 测试边界条件、异常场景和并发场景
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvTransactionService 高级测试")
class InvTransactionServiceAdvancedTest {

    @Mock
    private InvStockMapper stockMapper;

    @Mock
    private InvTransactionMapper transactionMapper;

    @InjectMocks
    private InvTransactionServiceImpl invTransactionService;

    private Long itemId;
    private Long warehouseId;

    @BeforeEach
    void setUp() {
        itemId = 1L;
        warehouseId = 1L;
    }

    @Nested
    @DisplayName("入库边界条件测试")
    class ReceiptEdgeCases {

        @Test
        @DisplayName("入库 - 数量为null")
        void createReceipt_nullQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createReceipt(
                    itemId, warehouseId, null, null,
                    null, new BigDecimal("10.00"),
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("入库数量必须大于0");
        }

        @Test
        @DisplayName("入库 - 成本为null")
        void createReceipt_nullCost_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createReceipt(
                    itemId, warehouseId, null, null,
                    new BigDecimal("100"), null,
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("单位成本不能为负数");
        }

        @Test
        @DisplayName("入库 - 精确数量（小数）")
        void createReceipt_decimalQuantity_success() {
            // given
            BigDecimal quantity = new BigDecimal("99.99");
            BigDecimal unitCost = new BigDecimal("10.55");

            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createReceipt(
                    itemId, warehouseId, null, null,
                    quantity, unitCost,
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getQuantity()).isEqualByComparingTo(quantity);
        }

        @Test
        @DisplayName("入库 - 带批次号")
        void createReceipt_withLotNo_success() {
            // given
            String lotNo = "LOT20240118001";

            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createReceipt(
                    itemId, warehouseId, null, lotNo,
                    new BigDecimal("100"), new BigDecimal("10.00"),
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getLotNo()).isEqualTo(lotNo);
        }

        @Test
        @DisplayName("入库 - 带库位")
        void createReceipt_withBinId_success() {
            // given
            Long binId = 1L;

            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createReceipt(
                    itemId, warehouseId, binId, null,
                    new BigDecimal("100"), new BigDecimal("10.00"),
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getBinId()).isEqualTo(binId);
        }
    }

    @Nested
    @DisplayName("出库边界条件测试")
    class IssueEdgeCases {

        @Test
        @DisplayName("出库 - 数量为null")
        void createIssue_nullQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    null, new BigDecimal("10.00"),
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("出库数量必须大于0");
        }

        @Test
        @DisplayName("出库 - 库存刚好足够")
        void createIssue_exactStock_success() {
            // given
            BigDecimal quantity = new BigDecimal("100");

            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(new BigDecimal("100"));
            when(stockMapper.updateQuantityForIssue(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    quantity, new BigDecimal("10.00"),
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getQuantity()).isEqualByComparingTo(quantity.negate());
        }

        @Test
        @DisplayName("出库 - 库存差1不足")
        void createIssue_insufficientByOne_throwsException() {
            // given
            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(new BigDecimal("99"));

            // when & then
            assertThatThrownBy(() -> invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    new BigDecimal("100"), new BigDecimal("10.00"),
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("库存不足");
        }

        @Test
        @DisplayName("出库 - 使用移动加权平均成本")
        void createIssue_withAvgCost_success() {
            // given
            BigDecimal quantity = new BigDecimal("50");

            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(new BigDecimal("100"));
            when(stockMapper.updateQuantityForIssue(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when - 不传入成本，使用移动加权平均
            InvTransaction result = invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    quantity, null,
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            );

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("调拨边界条件测试")
    class TransferEdgeCases {

        @Test
        @DisplayName("调拨 - 调出调入仓库相同")
        void createTransfer_sameWarehouse_success() {
            // given
            Long sameWarehouseId = 1L;
            BigDecimal quantity = new BigDecimal("100");

            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(new BigDecimal("200"));
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when - 调出调入相同仓库（虽然业务上不合理，但技术上应该能执行）
            invTransactionService.createTransfer(
                    itemId, sameWarehouseId, sameWarehouseId, null, null,
                    quantity, new BigDecimal("10.00"),
                    "TRANSFER", 1L, "TR-001",
                    "测试调拨"
            );

            // then
            verify(transactionMapper, times(2)).insert(any(InvTransaction.class));
        }

        @Test
        @DisplayName("调拨 - 数量为0")
        void createTransfer_zeroQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createTransfer(
                    itemId, 1L, 2L, null, null,
                    BigDecimal.ZERO, new BigDecimal("10.00"),
                    "TRANSFER", 1L, "TR-001",
                    "测试调拨"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("调拨数量必须大于0");
        }
    }

    @Nested
    @DisplayName("调整边界条件测试")
    class AdjustmentEdgeCases {

        @Test
        @DisplayName("调整 - 盘盈0.01")
        void createAdjustment_tinyProfit_success() {
            // given
            BigDecimal quantity = new BigDecimal("0.01");

            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    quantity, new BigDecimal("10.00"),
                    "STOCKTAKE", 1L, "ST-001",
                    "SURPLUS", "盘盈"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getQuantity()).isEqualByComparingTo(quantity);
        }

        @Test
        @DisplayName("调整 - 盘亏0.01")
        void createAdjustment_tinyLoss_success() {
            // given
            BigDecimal quantity = new BigDecimal("-0.01");

            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(new BigDecimal("100"));
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    quantity, new BigDecimal("10.00"),
                    "STOCKTAKE", 1L, "ST-001",
                    "SHORTAGE", "盘亏"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getQuantity()).isEqualByComparingTo(quantity);
        }

        @Test
        @DisplayName("调整 - 大数量盘盈")
        void createAdjustment_largeProfit_success() {
            // given
            BigDecimal quantity = new BigDecimal("999999.99");

            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(0);
            when(stockMapper.insert(any(InvStock.class)))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    quantity, new BigDecimal("10.00"),
                    "STOCKTAKE", 1L, "ST-001",
                    "SURPLUS", "盘盈"
            );

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("库存校验边界条件测试")
    class CheckStockEdgeCases {

        @Test
        @DisplayName("校验库存 - 可用库存为0")
        void checkAvailableStock_zeroStock_returnsFalse() {
            // given
            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(BigDecimal.ZERO);

            // when
            boolean result = invTransactionService.checkAvailableStock(
                    itemId, warehouseId, null, null, new BigDecimal("1"));

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("校验库存 - 需求为0")
        void checkAvailableStock_zeroRequired_returnsTrue() {
            // given
            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, null))
                    .thenReturn(new BigDecimal("100"));

            // when
            boolean result = invTransactionService.checkAvailableStock(
                    itemId, warehouseId, null, null, BigDecimal.ZERO);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("校验库存 - 带批次号")
        void checkAvailableStock_withLotNo_success() {
            // given
            String lotNo = "LOT001";

            when(stockMapper.getAvailableQuantity(itemId, warehouseId, null, lotNo))
                    .thenReturn(new BigDecimal("100"));

            // when
            boolean result = invTransactionService.checkAvailableStock(
                    itemId, warehouseId, null, lotNo, new BigDecimal("50"));

            // then
            assertThat(result).isTrue();
        }
    }
}
