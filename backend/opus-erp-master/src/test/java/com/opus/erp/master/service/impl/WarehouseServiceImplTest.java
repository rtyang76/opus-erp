package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.master.entity.MdmWarehouse;
import com.opus.erp.master.mapper.MdmWarehouseMapper;
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
 * WarehouseService 单元测试
 * 测试仓库档案管理 CRUD 逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService 单元测试")
class WarehouseServiceImplTest {

    @Mock
    private MdmWarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private MdmWarehouse testWarehouse;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(warehouseService, "baseMapper", warehouseMapper);

        testWarehouse = new MdmWarehouse();
        testWarehouse.setId(1L);
        testWarehouse.setWarehouseCode("WH001");
        testWarehouse.setWarehouseName("原材料仓库");
        testWarehouse.setWarehouseType("RAW");
        testWarehouse.setStatus(1);
    }

    @Nested
    @DisplayName("查询仓库列表测试")
    class ListWarehousesTests {

        @Test
        @DisplayName("分页查询仓库列表 - 无筛选条件")
        void listWarehouses_noFilter_success() {
            // given
            Page<MdmWarehouse> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testWarehouse));
            expectedPage.setTotal(1);

            when(warehouseMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmWarehouse> result = warehouseService.listWarehouses(1, 10, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getWarehouseCode()).isEqualTo("WH001");
        }

        @Test
        @DisplayName("分页查询仓库列表 - 按仓库编码筛选")
        void listWarehouses_withCodeFilter_success() {
            // given
            Page<MdmWarehouse> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testWarehouse));
            expectedPage.setTotal(1);

            when(warehouseMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmWarehouse> result = warehouseService.listWarehouses(1, 10, "WH001", null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建仓库测试")
    class CreateWarehouseTests {

        @Test
        @DisplayName("创建仓库成功")
        void createWarehouse_success() {
            // given
            MdmWarehouse newWarehouse = new MdmWarehouse();
            newWarehouse.setWarehouseCode("WH002");
            newWarehouse.setWarehouseName("成品仓库");

            when(warehouseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(warehouseMapper.insert(any(MdmWarehouse.class))).thenReturn(1);

            // when
            MdmWarehouse result = warehouseService.createWarehouse(newWarehouse);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getWarehouseCode()).isEqualTo("WH002");
            verify(warehouseMapper).insert(any(MdmWarehouse.class));
        }

        @Test
        @DisplayName("创建仓库失败 - 仓库编码已存在")
        void createWarehouse_duplicateCode_throwsException() {
            // given
            MdmWarehouse newWarehouse = new MdmWarehouse();
            newWarehouse.setWarehouseCode("WH001");

            when(warehouseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> warehouseService.createWarehouse(newWarehouse))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("仓库编码已存在");
        }
    }

    @Nested
    @DisplayName("更新仓库测试")
    class UpdateWarehouseTests {

        @Test
        @DisplayName("更新仓库成功")
        void updateWarehouse_success() {
            // given
            MdmWarehouse updateWarehouse = new MdmWarehouse();
            updateWarehouse.setId(1L);
            updateWarehouse.setWarehouseCode("WH001");
            updateWarehouse.setWarehouseName("原材料仓库-更新");

            when(warehouseMapper.selectById(1L)).thenReturn(testWarehouse);
            when(warehouseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(warehouseMapper.updateById(any(MdmWarehouse.class))).thenReturn(1);

            // when
            MdmWarehouse result = warehouseService.updateWarehouse(updateWarehouse);

            // then
            assertThat(result).isNotNull();
            verify(warehouseMapper).updateById(any(MdmWarehouse.class));
        }

        @Test
        @DisplayName("更新仓库失败 - 仓库不存在")
        void updateWarehouse_notFound_throwsException() {
            // given
            MdmWarehouse updateWarehouse = new MdmWarehouse();
            updateWarehouse.setId(999L);

            when(warehouseMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> warehouseService.updateWarehouse(updateWarehouse))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("仓库不存在");
        }

        @Test
        @DisplayName("更新仓库失败 - 仓库编码重复")
        void updateWarehouse_duplicateCode_throwsException() {
            // given
            MdmWarehouse updateWarehouse = new MdmWarehouse();
            updateWarehouse.setId(1L);
            updateWarehouse.setWarehouseCode("WH002");

            when(warehouseMapper.selectById(1L)).thenReturn(testWarehouse);
            when(warehouseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> warehouseService.updateWarehouse(updateWarehouse))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("仓库编码已存在");
        }
    }

    @Nested
    @DisplayName("删除仓库测试")
    class DeleteWarehouseTests {

        @Test
        @DisplayName("删除仓库成功")
        void deleteWarehouse_success() {
            // given
            when(warehouseMapper.selectById(1L)).thenReturn(testWarehouse);
            when(warehouseMapper.deleteById(1L)).thenReturn(1);

            // when
            warehouseService.deleteWarehouse(1L);

            // then
            verify(warehouseMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除仓库失败 - 仓库不存在")
        void deleteWarehouse_notFound_throwsException() {
            // given
            when(warehouseMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> warehouseService.deleteWarehouse(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("仓库不存在");
        }
    }
}
