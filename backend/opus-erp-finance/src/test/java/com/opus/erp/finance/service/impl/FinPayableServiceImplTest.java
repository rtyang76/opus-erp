package com.opus.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.finance.entity.FinPayable;
import com.opus.erp.finance.mapper.FinPayableMapper;
import com.opus.erp.master.entity.MdmSupplier;
import com.opus.erp.master.service.SupplierService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FinPayableService 单元测试
 * 用于排查 finance 模块 500 错误的根本原因
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinPayableService 单元测试")
class FinPayableServiceImplTest {

    @Mock
    private FinPayableMapper payableMapper;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private FinPayableServiceImpl payableService;

    private FinPayable testPayable;
    private MdmSupplier testSupplier;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl（关键！）
        ReflectionTestUtils.setField(payableService, "baseMapper", payableMapper);

        // 准备测试应付单
        testPayable = new FinPayable();
        testPayable.setId(1L);
        testPayable.setPayableNo("PY20240118000001");
        testPayable.setSupplierId(1L);
        testPayable.setAmount(new BigDecimal("10000.00"));
        testPayable.setPaidAmount(BigDecimal.ZERO);
        testPayable.setCurrency("CNY");
        testPayable.setDueDate(LocalDate.now().plusDays(30));
        testPayable.setStatus("PENDING");
        testPayable.setCreatedAt(LocalDateTime.now());
        testPayable.setDeleted(0);

        // 准备测试供应商
        testSupplier = new MdmSupplier();
        testSupplier.setId(1L);
        testSupplier.setSupplierCode("SUP001");
        testSupplier.setSupplierName("测试供应商");
    }

    // ========== 查询测试 ==========

    @Nested
    @DisplayName("分页查询应付单测试")
    class GetPageTests {

        @Test
        @DisplayName("分页查询 - 无筛选条件")
        void getPage_noFilter_success() {
            // given
            Page<FinPayable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testPayable));
            expectedPage.setTotal(1);

            when(payableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);
            when(supplierService.listByIds(anyList()))
                    .thenReturn(Arrays.asList(testSupplier));

            // when
            Page<FinPayable> result = payableService.getPage(1, 10, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getSupplierName()).isEqualTo("测试供应商");
        }

        @Test
        @DisplayName("分页查询 - 按供应商筛选")
        void getPage_withSupplierFilter_success() {
            // given
            Page<FinPayable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testPayable));
            expectedPage.setTotal(1);

            when(payableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);
            when(supplierService.listByIds(anyList()))
                    .thenReturn(Arrays.asList(testSupplier));

            // when
            Page<FinPayable> result = payableService.getPage(1, 10, 1L, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }

        @Test
        @DisplayName("分页查询 - 按状态筛选")
        void getPage_withStatusFilter_success() {
            // given
            Page<FinPayable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testPayable));
            expectedPage.setTotal(1);

            when(payableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);
            when(supplierService.listByIds(anyList()))
                    .thenReturn(Arrays.asList(testSupplier));

            // when
            Page<FinPayable> result = payableService.getPage(1, 10, null, "PENDING");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }

        @Test
        @DisplayName("分页查询 - 空结果")
        void getPage_emptyResult_success() {
            // given
            Page<FinPayable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Collections.emptyList());
            expectedPage.setTotal(0);

            when(payableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<FinPayable> result = payableService.getPage(1, 10, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).isEmpty();
            // 不应该调用 supplierService
            verify(supplierService, never()).listByIds(anyList());
        }

        @Test
        @DisplayName("分页查询 - 供应商名称填充失败不影响主查询")
        void getPage_supplierFillFail_shouldNotAffectMainQuery() {
            // given
            Page<FinPayable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testPayable));
            expectedPage.setTotal(1);

            when(payableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);
            when(supplierService.listByIds(anyList()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            // when - 不应该抛出异常
            Page<FinPayable> result = payableService.getPage(1, 10, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            // supplierName 应该是 null（因为填充失败）
            assertThat(result.getRecords().get(0).getSupplierName()).isNull();
        }
    }

    // ========== 创建测试 ==========

    @Nested
    @DisplayName("创建应付单测试")
    class CreatePayableTests {

        @Test
        @DisplayName("创建应付单成功 - 自动生成单号")
        void createPayable_autoGenerateNo_success() {
            // given
            FinPayable newPayable = new FinPayable();
            newPayable.setSupplierId(1L);
            newPayable.setAmount(new BigDecimal("5000.00"));

            when(payableMapper.insert(any(FinPayable.class))).thenReturn(1);

            // when
            FinPayable result = payableService.createPayable(newPayable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPayableNo()).isNotNull();
            assertThat(result.getPayableNo()).startsWith("PY");
            assertThat(result.getPaidAmount()).isEqualTo(BigDecimal.ZERO);
            assertThat(result.getCurrency()).isEqualTo("CNY");
            assertThat(result.getStatus()).isEqualTo("PENDING");
            verify(payableMapper).insert(any(FinPayable.class));
        }

        @Test
        @DisplayName("创建应付单成功 - 指定单号")
        void createPayable_specifiedNo_success() {
            // given
            FinPayable newPayable = new FinPayable();
            newPayable.setPayableNo("PY-CUSTOM-001");
            newPayable.setSupplierId(1L);
            newPayable.setAmount(new BigDecimal("5000.00"));

            when(payableMapper.insert(any(FinPayable.class))).thenReturn(1);

            // when
            FinPayable result = payableService.createPayable(newPayable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPayableNo()).isEqualTo("PY-CUSTOM-001");
        }

        @Test
        @DisplayName("创建应付单 - Mapper 抛出异常")
        void createPayable_mapperThrows_shouldPropagate() {
            // given
            FinPayable newPayable = new FinPayable();
            newPayable.setSupplierId(1L);
            newPayable.setAmount(new BigDecimal("5000.00"));

            when(payableMapper.insert(any(FinPayable.class)))
                    .thenThrow(new RuntimeException("数据库错误"));

            // when & then
            assertThatThrownBy(() -> payableService.createPayable(newPayable))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("数据库错误");
        }
    }

    // ========== 更新测试 ==========

    @Nested
    @DisplayName("更新应付单测试")
    class UpdatePayableTests {

        @Test
        @DisplayName("更新应付单成功")
        void updatePayable_success() {
            // given
            FinPayable updateData = new FinPayable();
            updateData.setAmount(new BigDecimal("15000.00"));

            when(payableMapper.selectById(1L)).thenReturn(testPayable);
            when(payableMapper.updateById(any(FinPayable.class))).thenReturn(1);
            when(payableMapper.selectById(1L)).thenReturn(testPayable);

            // when
            FinPayable result = payableService.updatePayable(1L, updateData);

            // then
            assertThat(result).isNotNull();
            verify(payableMapper).updateById(any(FinPayable.class));
        }

        @Test
        @DisplayName("更新应付单失败 - 不存在")
        void updatePayable_notFound_throwsException() {
            // given
            when(payableMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> payableService.updatePayable(999L, new FinPayable()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("应付单不存在");
        }
    }

    // ========== 删除测试 ==========

    @Nested
    @DisplayName("删除应付单测试")
    class DeletePayableTests {

        @Test
        @DisplayName("删除应付单成功")
        void deletePayable_success() {
            // given
            when(payableMapper.selectById(1L)).thenReturn(testPayable);
            when(payableMapper.deleteById(1L)).thenReturn(1);

            // when
            payableService.deletePayable(1L);

            // then
            verify(payableMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除应付单失败 - 不存在")
        void deletePayable_notFound_throwsException() {
            // given
            when(payableMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> payableService.deletePayable(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("应付单不存在");
        }
    }

    // ========== IService 继承方法测试 ==========

    @Nested
    @DisplayName("IService 继承方法测试")
    class IserviceMethodTests {

        @Test
        @DisplayName("getById 方法可用（继承自 IService）")
        void getById_success() {
            // given
            when(payableMapper.selectById(1L)).thenReturn(testPayable);

            // when
            FinPayable result = payableService.getById(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("getById 返回 null（数据不存在）")
        void getById_notFound_returnsNull() {
            // given
            when(payableMapper.selectById(999L)).thenReturn(null);

            // when
            FinPayable result = payableService.getById(999L);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("save 方法可用（继承自 IService）")
        void save_success() {
            // given
            FinPayable newPayable = new FinPayable();
            newPayable.setSupplierId(1L);
            newPayable.setAmount(new BigDecimal("5000.00"));

            when(payableMapper.insert(any(FinPayable.class))).thenReturn(1);

            // when
            boolean result = payableService.save(newPayable);

            // then
            assertThat(result).isTrue();
            verify(payableMapper).insert(any(FinPayable.class));
        }

        @Test
        @DisplayName("listByIds 方法可用（继承自 IService）")
        void listByIds_success() {
            // given
            when(payableMapper.selectBatchIds(anyCollection()))
                    .thenReturn(Arrays.asList(testPayable));

            // when
            List<FinPayable> result = payableService.listByIds(Arrays.asList(1L));

            // then
            assertThat(result).hasSize(1);
        }
    }
}
