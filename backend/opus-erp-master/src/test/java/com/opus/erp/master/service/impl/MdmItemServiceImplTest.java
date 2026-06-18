package com.opus.erp.master.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.master.entity.MdmItem;
import com.opus.erp.master.mapper.MdmItemMapper;
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
 * MdmItemService 单元测试
 * 测试物料档案管理 CRUD 逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MdmItemService 单元测试")
class MdmItemServiceImplTest {

    @Mock
    private MdmItemMapper itemMapper;

    @InjectMocks
    private MdmItemServiceImpl itemService;

    private MdmItem testItem;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(itemService, "baseMapper", itemMapper);

        testItem = new MdmItem();
        testItem.setId(1L);
        testItem.setItemCode("M001");
        testItem.setItemName("物料A");
        testItem.setCategoryId(1L);
        testItem.setItemType("RAW");
        testItem.setStatus(1);
    }

    @Nested
    @DisplayName("查询物料列表测试")
    class ListItemsTests {

        @Test
        @DisplayName("分页查询物料列表 - 无筛选条件")
        void listItems_noFilter_success() {
            // given
            Page<MdmItem> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testItem));
            expectedPage.setTotal(1);

            when(itemMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmItem> result = itemService.listItems(1, 10, null, null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getItemCode()).isEqualTo("M001");
        }

        @Test
        @DisplayName("分页查询物料列表 - 按物料编码筛选")
        void listItems_withItemCodeFilter_success() {
            // given
            Page<MdmItem> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testItem));
            expectedPage.setTotal(1);

            when(itemMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmItem> result = itemService.listItems(1, 10, "M001", null, null, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }

        @Test
        @DisplayName("分页查询物料列表 - 按分类筛选")
        void listItems_withCategoryFilter_success() {
            // given
            Page<MdmItem> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testItem));
            expectedPage.setTotal(1);

            when(itemMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<MdmItem> result = itemService.listItems(1, 10, null, null, 1L, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建物料测试")
    class CreateItemTests {

        @Test
        @DisplayName("创建物料成功")
        void createItem_success() {
            // given
            MdmItem newItem = new MdmItem();
            newItem.setItemCode("M002");
            newItem.setItemName("物料B");

            when(itemMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(itemMapper.insert(any(MdmItem.class))).thenReturn(1);

            // when
            MdmItem result = itemService.createItem(newItem);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getItemCode()).isEqualTo("M002");
            verify(itemMapper).insert(any(MdmItem.class));
        }

        @Test
        @DisplayName("创建物料失败 - 物料编码已存在")
        void createItem_duplicateCode_throwsException() {
            // given
            MdmItem newItem = new MdmItem();
            newItem.setItemCode("M001");
            newItem.setItemName("物料B");

            when(itemMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> itemService.createItem(newItem))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("物料编码已存在");
        }
    }

    @Nested
    @DisplayName("更新物料测试")
    class UpdateItemTests {

        @Test
        @DisplayName("更新物料成功")
        void updateItem_success() {
            // given
            MdmItem updateItem = new MdmItem();
            updateItem.setId(1L);
            updateItem.setItemCode("M001");
            updateItem.setItemName("物料A-更新");

            when(itemMapper.selectById(1L)).thenReturn(testItem);
            when(itemMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(itemMapper.updateById(any(MdmItem.class))).thenReturn(1);

            // when
            MdmItem result = itemService.updateItem(updateItem);

            // then
            assertThat(result).isNotNull();
            verify(itemMapper).updateById(any(MdmItem.class));
        }

        @Test
        @DisplayName("更新物料失败 - 物料不存在")
        void updateItem_notFound_throwsException() {
            // given
            MdmItem updateItem = new MdmItem();
            updateItem.setId(999L);
            updateItem.setItemCode("M001");

            when(itemMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> itemService.updateItem(updateItem))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("物料不存在");
        }

        @Test
        @DisplayName("更新物料失败 - 物料编码重复")
        void updateItem_duplicateCode_throwsException() {
            // given
            MdmItem updateItem = new MdmItem();
            updateItem.setId(1L);
            updateItem.setItemCode("M002");

            when(itemMapper.selectById(1L)).thenReturn(testItem);
            when(itemMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> itemService.updateItem(updateItem))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("物料编码已存在");
        }
    }

    @Nested
    @DisplayName("删除物料测试")
    class DeleteItemTests {

        @Test
        @DisplayName("删除物料成功")
        void deleteItem_success() {
            // given
            when(itemMapper.selectById(1L)).thenReturn(testItem);
            when(itemMapper.deleteById(1L)).thenReturn(1);

            // when
            itemService.deleteItem(1L);

            // then
            verify(itemMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除物料失败 - 物料不存在")
        void deleteItem_notFound_throwsException() {
            // given
            when(itemMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> itemService.deleteItem(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("物料不存在");
        }
    }

    @Nested
    @DisplayName("查询物料详情测试")
    class GetItemDetailTests {

        @Test
        @DisplayName("查询物料详情成功")
        void getItemDetail_success() {
            // given
            when(itemMapper.selectById(1L)).thenReturn(testItem);

            // when
            MdmItem result = itemService.getItemDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getItemCode()).isEqualTo("M001");
        }

        @Test
        @DisplayName("查询物料详情失败 - 物料不存在")
        void getItemDetail_notFound_throwsException() {
            // given
            when(itemMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> itemService.getItemDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("物料不存在");
        }
    }
}
