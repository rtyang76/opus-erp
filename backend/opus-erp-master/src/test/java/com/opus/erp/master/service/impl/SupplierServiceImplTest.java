package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.master.entity.MdmSupplier;
import com.opus.erp.master.mapper.MdmSupplierMapper;
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
 * SupplierService 单元测试
 * 测试供应商档案管理 CRUD 逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService 单元测试")
class SupplierServiceImplTest {

    @Mock
    private MdmSupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private MdmSupplier testSupplier;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(supplierService, "baseMapper", supplierMapper);

        testSupplier = new MdmSupplier();
        testSupplier.setId(1L);
        testSupplier.setSupplierCode("SUP001");
        testSupplier.setSupplierName("供应商A");
        testSupplier.setContactPerson("张三");
        testSupplier.setPhone("13800138000");
        testSupplier.setRating("A");
        testSupplier.setStatus(1);
    }

    @Nested
    @DisplayName("查询供应商列表测试")
    class ListSuppliersTests {

        @Test
        @DisplayName("分页查询供应商列表 - 无筛选条件")
        void listSuppliers_noFilter_success() {
            // given
            Page<MdmSupplier> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testSupplier));
            expectedPage.setTotal(1);

            when(supplierMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmSupplier> result = supplierService.listSuppliers(1, 10, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getSupplierCode()).isEqualTo("SUP001");
        }

        @Test
        @DisplayName("分页查询供应商列表 - 按供应商编码筛选")
        void listSuppliers_withCodeFilter_success() {
            // given
            Page<MdmSupplier> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testSupplier));
            expectedPage.setTotal(1);

            when(supplierMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmSupplier> result = supplierService.listSuppliers(1, 10, "SUP001", null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建供应商测试")
    class CreateSupplierTests {

        @Test
        @DisplayName("创建供应商成功")
        void createSupplier_success() {
            // given
            MdmSupplier newSupplier = new MdmSupplier();
            newSupplier.setSupplierCode("SUP002");
            newSupplier.setSupplierName("供应商B");

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.insert(any(MdmSupplier.class))).thenReturn(1);

            // when
            MdmSupplier result = supplierService.createSupplier(newSupplier);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getSupplierCode()).isEqualTo("SUP002");
            verify(supplierMapper).insert(any(MdmSupplier.class));
        }

        @Test
        @DisplayName("创建供应商失败 - 供应商编码已存在")
        void createSupplier_duplicateCode_throwsException() {
            // given
            MdmSupplier newSupplier = new MdmSupplier();
            newSupplier.setSupplierCode("SUP001");

            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> supplierService.createSupplier(newSupplier))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("供应商编码已存在");
        }
    }

    @Nested
    @DisplayName("更新供应商测试")
    class UpdateSupplierTests {

        @Test
        @DisplayName("更新供应商成功")
        void updateSupplier_success() {
            // given
            MdmSupplier updateSupplier = new MdmSupplier();
            updateSupplier.setId(1L);
            updateSupplier.setSupplierCode("SUP001");
            updateSupplier.setSupplierName("供应商A-更新");

            when(supplierMapper.selectById(1L)).thenReturn(testSupplier);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(supplierMapper.updateById(any(MdmSupplier.class))).thenReturn(1);

            // when
            MdmSupplier result = supplierService.updateSupplier(updateSupplier);

            // then
            assertThat(result).isNotNull();
            verify(supplierMapper).updateById(any(MdmSupplier.class));
        }

        @Test
        @DisplayName("更新供应商失败 - 供应商不存在")
        void updateSupplier_notFound_throwsException() {
            // given
            MdmSupplier updateSupplier = new MdmSupplier();
            updateSupplier.setId(999L);

            when(supplierMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> supplierService.updateSupplier(updateSupplier))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("供应商不存在");
        }

        @Test
        @DisplayName("更新供应商失败 - 供应商编码重复")
        void updateSupplier_duplicateCode_throwsException() {
            // given
            MdmSupplier updateSupplier = new MdmSupplier();
            updateSupplier.setId(1L);
            updateSupplier.setSupplierCode("SUP002");

            when(supplierMapper.selectById(1L)).thenReturn(testSupplier);
            when(supplierMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> supplierService.updateSupplier(updateSupplier))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("供应商编码已存在");
        }
    }

    @Nested
    @DisplayName("删除供应商测试")
    class DeleteSupplierTests {

        @Test
        @DisplayName("删除供应商成功")
        void deleteSupplier_success() {
            // given
            when(supplierMapper.selectById(1L)).thenReturn(testSupplier);
            when(supplierMapper.deleteById(1L)).thenReturn(1);

            // when
            supplierService.deleteSupplier(1L);

            // then
            verify(supplierMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除供应商失败 - 供应商不存在")
        void deleteSupplier_notFound_throwsException() {
            // given
            when(supplierMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> supplierService.deleteSupplier(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("供应商不存在");
        }
    }
}
