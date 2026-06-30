package com.opus.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.finance.entity.FinReceivable;
import com.opus.erp.finance.mapper.FinReceivableMapper;
import com.opus.erp.master.entity.MdmCustomer;
import com.opus.erp.master.service.CustomerService;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FinReceivableService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinReceivableService 单元测试")
class FinReceivableServiceImplTest {

    @Mock
    private FinReceivableMapper receivableMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private FinReceivableServiceImpl receivableService;

    private FinReceivable testReceivable;
    private MdmCustomer testCustomer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(receivableService, "baseMapper", receivableMapper);

        testReceivable = new FinReceivable();
        testReceivable.setId(1L);
        testReceivable.setReceivableNo("RV20240118000001");
        testReceivable.setCustomerId(1L);
        testReceivable.setAmount(new BigDecimal("10000.00"));
        testReceivable.setPaidAmount(BigDecimal.ZERO);
        testReceivable.setCurrency("CNY");
        testReceivable.setDueDate(LocalDate.now().plusDays(30));
        testReceivable.setStatus("PENDING");
        testReceivable.setCreatedAt(LocalDateTime.now());
        testReceivable.setDeleted(0);

        testCustomer = new MdmCustomer();
        testCustomer.setId(1L);
        testCustomer.setCustomerCode("CUS001");
        testCustomer.setCustomerName("测试客户");
    }

    @Nested
    @DisplayName("分页查询应收单测试")
    class GetPageTests {

        @Test
        @DisplayName("分页查询 - 无筛选条件")
        void getPage_noFilter_success() {
            Page<FinReceivable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testReceivable));
            expectedPage.setTotal(1);

            when(receivableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);
            when(customerService.listByIds(anyList()))
                    .thenReturn(Arrays.asList(testCustomer));

            Page<FinReceivable> result = receivableService.getPage(1, 10, null, null);

            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getCustomerName()).isEqualTo("测试客户");
        }

        @Test
        @DisplayName("分页查询 - 空结果")
        void getPage_emptyResult_success() {
            Page<FinReceivable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Collections.emptyList());
            expectedPage.setTotal(0);

            when(receivableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            Page<FinReceivable> result = receivableService.getPage(1, 10, null, null);

            assertThat(result).isNotNull();
            assertThat(result.getRecords()).isEmpty();
            verify(customerService, never()).listByIds(anyList());
        }

        @Test
        @DisplayName("分页查询 - 客户名称填充失败不影响主查询")
        void getPage_customerFillFail_shouldNotAffectMainQuery() {
            Page<FinReceivable> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testReceivable));
            expectedPage.setTotal(1);

            when(receivableMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);
            when(customerService.listByIds(anyList()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            Page<FinReceivable> result = receivableService.getPage(1, 10, null, null);

            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getCustomerName()).isNull();
        }
    }

    @Nested
    @DisplayName("创建应收单测试")
    class CreateReceivableTests {

        @Test
        @DisplayName("创建应收单成功")
        void createReceivable_success() {
            FinReceivable newReceivable = new FinReceivable();
            newReceivable.setCustomerId(1L);
            newReceivable.setAmount(new BigDecimal("5000.00"));

            when(receivableMapper.insert(any(FinReceivable.class))).thenReturn(1);

            FinReceivable result = receivableService.createReceivable(newReceivable);

            assertThat(result).isNotNull();
            assertThat(result.getReceivableNo()).isNotNull();
            assertThat(result.getReceivableNo()).startsWith("RV");
            assertThat(result.getPaidAmount()).isEqualTo(BigDecimal.ZERO);
            assertThat(result.getCurrency()).isEqualTo("CNY");
            assertThat(result.getStatus()).isEqualTo("PENDING");
            verify(receivableMapper).insert(any(FinReceivable.class));
        }
    }

    @Nested
    @DisplayName("更新应收单测试")
    class UpdateReceivableTests {

        @Test
        @DisplayName("更新应收单成功")
        void updateReceivable_success() {
            FinReceivable updateData = new FinReceivable();
            updateData.setAmount(new BigDecimal("15000.00"));

            when(receivableMapper.selectById(1L)).thenReturn(testReceivable);
            when(receivableMapper.updateById(any(FinReceivable.class))).thenReturn(1);

            FinReceivable result = receivableService.updateReceivable(1L, updateData);

            assertThat(result).isNotNull();
            verify(receivableMapper).updateById(any(FinReceivable.class));
        }

        @Test
        @DisplayName("更新应收单失败 - 不存在")
        void updateReceivable_notFound_throwsException() {
            when(receivableMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> receivableService.updateReceivable(999L, new FinReceivable()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("应收单不存在");
        }
    }

    @Nested
    @DisplayName("删除应收单测试")
    class DeleteReceivableTests {

        @Test
        @DisplayName("删除应收单成功")
        void deleteReceivable_success() {
            when(receivableMapper.selectById(1L)).thenReturn(testReceivable);
            when(receivableMapper.deleteById(1L)).thenReturn(1);

            receivableService.deleteReceivable(1L);

            verify(receivableMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除应收单失败 - 不存在")
        void deleteReceivable_notFound_throwsException() {
            when(receivableMapper.selectById(999L)).thenReturn(null);

            assertThatThrownBy(() -> receivableService.deleteReceivable(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("应收单不存在");
        }
    }

    @Nested
    @DisplayName("IService 继承方法测试")
    class IserviceMethodTests {

        @Test
        @DisplayName("getById 方法可用")
        void getById_success() {
            when(receivableMapper.selectById(1L)).thenReturn(testReceivable);

            FinReceivable result = receivableService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("save 方法可用")
        void save_success() {
            FinReceivable newReceivable = new FinReceivable();
            newReceivable.setCustomerId(1L);

            when(receivableMapper.insert(any(FinReceivable.class))).thenReturn(1);

            boolean result = receivableService.save(newReceivable);

            assertThat(result).isTrue();
            verify(receivableMapper).insert(any(FinReceivable.class));
        }
    }
}
