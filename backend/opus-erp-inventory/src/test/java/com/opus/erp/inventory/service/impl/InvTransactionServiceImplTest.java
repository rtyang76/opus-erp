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
 * InvTransactionService 单元测试
 * 测试库存交易核心逻辑：入库、出库、调拨、调整
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvTransactionService 单元测试")
class InvTransactionServiceImplTest {

    @Mock
    private InvStockMapper stockMapper;

    @Mock
    private InvTransactionMapper transactionMapper;

    @InjectMocks
    private InvTransactionServiceImpl invTransactionService;

    private Long itemId;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal unitCost;

    @BeforeEach
    void setUp() {
        itemId = 1L;
        warehouseId = 1L;
        quantity = new BigDecimal("100");
        unitCost = new BigDecimal("10.50");
    }

    @Nested
    @DisplayName("入库测试")
    class CreateReceiptTests {

        @Test
        @DisplayName("成功入库 - 库存记录已存在")
        void createReceipt_existingStock_success() {
            // given
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
            assertThat(result.getTransactionType()).isEqualTo("RECEIPT");
            assertThat(result.getQuantity()).isEqualByComparingTo(quantity);
            assertThat(result.getUnitCost()).isEqualByComparingTo(unitCost);
            verify(stockMapper, never()).insert(any());
            verify(transactionMapper).insert(any());
        }

        @Test
        @DisplayName("成功入库 - 库存记录不存在，创建新记录")
        void createReceipt_newStock_success() {
            // given
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(0);
            when(stockMapper.insert(any(InvStock.class)))
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
            verify(stockMapper).insert(any(InvStock.class));
        }

        @Test
        @DisplayName("入库失败 - 数量为0")
        void createReceipt_zeroQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createReceipt(
                    itemId, warehouseId, null, null,
                    BigDecimal.ZERO, unitCost,
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("入库数量必须大于0");
        }

        @Test
        @DisplayName("入库失败 - 数量为负数")
        void createReceipt_negativeQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createReceipt(
                    itemId, warehouseId, null, null,
                    new BigDecimal("-10"), unitCost,
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("入库数量必须大于0");
        }

        @Test
        @DisplayName("入库失败 - 成本为负数")
        void createReceipt_negativeCost_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createReceipt(
                    itemId, warehouseId, null, null,
                    quantity, new BigDecimal("-1"),
                    "PO", 1L, "PO-001",
                    null, "测试入库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("单位成本不能为负数");
        }
    }

    @Nested
    @DisplayName("出库测试")
    class CreateIssueTests {

        @Test
        @DisplayName("成功出库")
        void createIssue_success() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("200"));
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    quantity, unitCost,
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTransactionType()).isEqualTo("ISSUE");
            assertThat(result.getQuantity()).isEqualByComparingTo(quantity.negate());
        }

        @Test
        @DisplayName("出库失败 - 库存不足")
        void createIssue_insufficientStock_throwsException() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("50"));

            // when & then
            assertThatThrownBy(() -> invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    quantity, unitCost,
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("库存不足");
        }

        @Test
        @DisplayName("出库失败 - 数量为0")
        void createIssue_zeroQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createIssue(
                    itemId, warehouseId, null, null,
                    BigDecimal.ZERO, unitCost,
                    "SO", 1L, "SO-001",
                    null, "测试出库"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("出库数量必须大于0");
        }
    }

    @Nested
    @DisplayName("调拨测试")
    class CreateTransferTests {

        @Test
        @DisplayName("成功调拨")
        void createTransfer_success() {
            // given
            Long toWarehouseId = 2L;
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("200"));
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            invTransactionService.createTransfer(
                    itemId, warehouseId, toWarehouseId, null, null,
                    quantity, unitCost,
                    "TRANSFER", 1L, "TR-001",
                    "测试调拨"
            );

            // then
            // 调拨应产生两条流水：一条出库（负数），一条入库（正数）
            verify(transactionMapper, times(2)).insert(any(InvTransaction.class));
            // 调出仓库扣减库存
            verify(stockMapper).updateQuantity(eq(itemId), eq(warehouseId), any(), any(),
                    eq(quantity.negate()), any());
            // 调入仓库增加库存
            verify(stockMapper).updateQuantity(eq(itemId), eq(toWarehouseId), any(), any(),
                    eq(quantity), any());
        }

        @Test
        @DisplayName("调拨失败 - 调出仓库库存不足")
        void createTransfer_insufficientStock_throwsException() {
            // given
            Long toWarehouseId = 2L;
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("50"));

            // when & then
            assertThatThrownBy(() -> invTransactionService.createTransfer(
                    itemId, warehouseId, toWarehouseId, null, null,
                    quantity, unitCost,
                    "TRANSFER", 1L, "TR-001",
                    "测试调拨"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("调出仓库库存不足");
        }

        @Test
        @DisplayName("调拨失败 - 数量为0")
        void createTransfer_zeroQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createTransfer(
                    itemId, warehouseId, 2L, null, null,
                    BigDecimal.ZERO, unitCost,
                    "TRANSFER", 1L, "TR-001",
                    "测试调拨"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("调拨数量必须大于0");
        }
    }

    @Nested
    @DisplayName("库存调整测试")
    class CreateAdjustmentTests {

        @Test
        @DisplayName("盘盈入库 - 成功")
        void createAdjustment_positiveQuantity_success() {
            // given
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    new BigDecimal("10"), unitCost,
                    "STOCKTAKE", 1L, "ST-001",
                    "SURPLUS", "盘盈"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTransactionType()).isEqualTo("ADJUSTMENT");
            assertThat(result.getQuantity()).isEqualByComparingTo(new BigDecimal("10"));
        }

        @Test
        @DisplayName("盘亏出库 - 成功")
        void createAdjustment_negativeQuantity_success() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("200"));
            when(stockMapper.updateQuantity(anyLong(), anyLong(), any(), any(), any(), any()))
                    .thenReturn(1);
            when(transactionMapper.insert(any(InvTransaction.class)))
                    .thenReturn(1);

            // when
            InvTransaction result = invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    new BigDecimal("-10"), unitCost,
                    "STOCKTAKE", 1L, "ST-001",
                    "SHORTAGE", "盘亏"
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getQuantity()).isEqualByComparingTo(new BigDecimal("-10"));
        }

        @Test
        @DisplayName("盘亏失败 - 库存不足")
        void createAdjustment_negativeQuantity_insufficientStock_throwsException() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("5"));

            // when & then
            assertThatThrownBy(() -> invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    new BigDecimal("-10"), unitCost,
                    "STOCKTAKE", 1L, "ST-001",
                    "SHORTAGE", "盘亏"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("盘亏数量超过可用库存");
        }

        @Test
        @DisplayName("调整失败 - 数量为0")
        void createAdjustment_zeroQuantity_throwsException() {
            // when & then
            assertThatThrownBy(() -> invTransactionService.createAdjustment(
                    itemId, warehouseId, null, null,
                    BigDecimal.ZERO, unitCost,
                    "STOCKTAKE", 1L, "ST-001",
                    null, "调整"
            )).isInstanceOf(BusinessException.class)
                    .hasMessageContaining("调整数量不能为0");
        }
    }

    @Nested
    @DisplayName("库存校验测试")
    class CheckAvailableStockTests {

        @Test
        @DisplayName("库存充足 - 返回true")
        void checkAvailableStock_sufficient_returnsTrue() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("100"));

            // when
            boolean result = invTransactionService.checkAvailableStock(
                    itemId, warehouseId, null, null, new BigDecimal("50"));

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("库存不足 - 返回false")
        void checkAvailableStock_insufficient_returnsFalse() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(new BigDecimal("30"));

            // when
            boolean result = invTransactionService.checkAvailableStock(
                    itemId, warehouseId, null, null, new BigDecimal("50"));

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("库存记录不存在 - 返回false")
        void checkAvailableStock_noRecord_returnsFalse() {
            // given
            when(stockMapper.getAvailableQuantity(anyLong(), anyLong(), any(), any()))
                    .thenReturn(null);

            // when
            boolean result = invTransactionService.checkAvailableStock(
                    itemId, warehouseId, null, null, new BigDecimal("50"));

            // then
            assertThat(result).isFalse();
        }
    }
}
