package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.master.entity.MdmCustomer;
import com.opus.erp.master.mapper.MdmCustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CustomerService 单元测试
 * 测试客户档案管理 CRUD 逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService 单元测试")
class CustomerServiceImplTest {

    @Mock
    private MdmCustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private MdmCustomer testCustomer;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(customerService, "baseMapper", customerMapper);

        testCustomer = new MdmCustomer();
        testCustomer.setId(1L);
        testCustomer.setCustomerCode("CUS001");
        testCustomer.setCustomerName("客户A");
        testCustomer.setContactPerson("李四");
        testCustomer.setPhone("13900139000");
        testCustomer.setRating("A");
        testCustomer.setStatus(1);
    }

    @Nested
    @DisplayName("查询客户列表测试")
    class ListCustomersTests {

        @Test
        @DisplayName("分页查询客户列表 - 无筛选条件")
        void listCustomers_noFilter_success() {
            // given
            Page<MdmCustomer> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testCustomer));
            expectedPage.setTotal(1);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmCustomer> result = customerService.listCustomers(1, 10, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getCustomerCode()).isEqualTo("CUS001");
        }

        @Test
        @DisplayName("分页查询客户列表 - 按客户编码筛选")
        void listCustomers_withCodeFilter_success() {
            // given
            Page<MdmCustomer> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testCustomer));
            expectedPage.setTotal(1);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmCustomer> result = customerService.listCustomers(1, 10, "CUS001", null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建客户测试")
    class CreateCustomerTests {

        @Test
        @DisplayName("创建客户成功")
        void createCustomer_success() {
            // given
            MdmCustomer newCustomer = new MdmCustomer();
            newCustomer.setCustomerCode("CUS002");
            newCustomer.setCustomerName("客户B");

            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(customerMapper.insert(any(MdmCustomer.class))).thenReturn(1);

            // when
            MdmCustomer result = customerService.createCustomer(newCustomer);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCustomerCode()).isEqualTo("CUS002");
            verify(customerMapper).insert(any(MdmCustomer.class));
        }

        @Test
        @DisplayName("创建客户失败 - 客户编码已存在")
        void createCustomer_duplicateCode_throwsException() {
            // given
            MdmCustomer newCustomer = new MdmCustomer();
            newCustomer.setCustomerCode("CUS001");

            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> customerService.createCustomer(newCustomer))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("客户编码已存在");
        }
    }

    @Nested
    @DisplayName("更新客户测试")
    class UpdateCustomerTests {

        @Test
        @DisplayName("更新客户成功")
        void updateCustomer_success() {
            // given
            MdmCustomer updateCustomer = new MdmCustomer();
            updateCustomer.setId(1L);
            updateCustomer.setCustomerCode("CUS001");
            updateCustomer.setCustomerName("客户A-更新");

            when(customerMapper.selectById(1L)).thenReturn(testCustomer);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(customerMapper.updateById(any(MdmCustomer.class))).thenReturn(1);

            // when
            MdmCustomer result = customerService.updateCustomer(updateCustomer);

            // then
            assertThat(result).isNotNull();
            verify(customerMapper).updateById(any(MdmCustomer.class));
        }

        @Test
        @DisplayName("更新客户失败 - 客户不存在")
        void updateCustomer_notFound_throwsException() {
            // given
            MdmCustomer updateCustomer = new MdmCustomer();
            updateCustomer.setId(999L);

            when(customerMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> customerService.updateCustomer(updateCustomer))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("客户不存在");
        }

        @Test
        @DisplayName("更新客户失败 - 客户编码重复")
        void updateCustomer_duplicateCode_throwsException() {
            // given
            MdmCustomer updateCustomer = new MdmCustomer();
            updateCustomer.setId(1L);
            updateCustomer.setCustomerCode("CUS002");

            when(customerMapper.selectById(1L)).thenReturn(testCustomer);
            when(customerMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> customerService.updateCustomer(updateCustomer))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("客户编码已存在");
        }
    }

    @Nested
    @DisplayName("删除客户测试")
    class DeleteCustomerTests {

        @Test
        @DisplayName("删除客户成功")
        void deleteCustomer_success() {
            // given
            when(customerMapper.selectById(1L)).thenReturn(testCustomer);
            when(customerMapper.deleteById(1L)).thenReturn(1);

            // when
            customerService.deleteCustomer(1L);

            // then
            verify(customerMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除客户失败 - 客户不存在")
        void deleteCustomer_notFound_throwsException() {
            // given
            when(customerMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> customerService.deleteCustomer(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("客户不存在");
        }
    }
}
