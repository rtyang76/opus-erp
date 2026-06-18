package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.system.entity.SysDictData;
import com.opus.erp.system.entity.SysDictType;
import com.opus.erp.system.mapper.SysDictDataMapper;
import com.opus.erp.system.mapper.SysDictTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DictService 单元测试
 * 测试字典类型和字典数据管理 CRUD 逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DictService 单元测试")
class DictServiceImplTest {

    @Mock
    private SysDictTypeMapper dictTypeMapper;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @InjectMocks
    private DictServiceImpl dictService;

    private SysDictType testDictType;
    private SysDictData testDictData;

    @BeforeEach
    void setUp() {
        // 准备测试字典类型
        testDictType = new SysDictType();
        testDictType.setId(1L);
        testDictType.setDictType("sys_user_status");
        testDictType.setDictName("用户状态");
        testDictType.setStatus(1);

        // 准备测试字典数据
        testDictData = new SysDictData();
        testDictData.setId(1L);
        testDictData.setDictType("sys_user_status");
        testDictData.setDictLabel("启用");
        testDictData.setDictValue("1");
        testDictData.setSortOrder(1);
        testDictData.setStatus(1);
    }

    @Nested
    @DisplayName("字典类型测试")
    class DictTypeTests {

        @Test
        @DisplayName("查询字典类型列表成功")
        void listDictTypes_success() {
            // given
            when(dictTypeMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDictType));

            // when
            List<SysDictType> result = dictService.listDictTypes();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDictType()).isEqualTo("sys_user_status");
        }

        @Test
        @DisplayName("查询字典类型详情成功")
        void getDictTypeDetail_success() {
            // given
            when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);

            // when
            SysDictType result = dictService.getDictTypeDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("查询字典类型详情失败 - 不存在")
        void getDictTypeDetail_notFound_throwsException() {
            // given
            when(dictTypeMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> dictService.getDictTypeDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典类型不存在");
        }

        @Test
        @DisplayName("创建字典类型成功")
        void createDictType_success() {
            // given
            SysDictType newDictType = new SysDictType();
            newDictType.setDictType("sys_user_type");
            newDictType.setDictName("用户类型");

            when(dictTypeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(dictTypeMapper.insert(any(SysDictType.class))).thenReturn(1);

            // when
            SysDictType result = dictService.createDictType(newDictType);

            // then
            assertThat(result).isNotNull();
            verify(dictTypeMapper).insert(any(SysDictType.class));
        }

        @Test
        @DisplayName("创建字典类型失败 - 编码已存在")
        void createDictType_duplicateCode_throwsException() {
            // given
            SysDictType newDictType = new SysDictType();
            newDictType.setDictType("sys_user_status");

            when(dictTypeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> dictService.createDictType(newDictType))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典类型编码已存在");
        }

        @Test
        @DisplayName("更新字典类型成功")
        void updateDictType_success() {
            // given
            SysDictType updateDictType = new SysDictType();
            updateDictType.setId(1L);
            updateDictType.setDictName("用户状态-更新");

            when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);
            when(dictTypeMapper.updateById(any(SysDictType.class))).thenReturn(1);

            // when
            SysDictType result = dictService.updateDictType(updateDictType);

            // then
            assertThat(result).isNotNull();
            verify(dictTypeMapper).updateById(any(SysDictType.class));
        }

        @Test
        @DisplayName("更新字典类型失败 - 不存在")
        void updateDictType_notFound_throwsException() {
            // given
            SysDictType updateDictType = new SysDictType();
            updateDictType.setId(999L);

            when(dictTypeMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> dictService.updateDictType(updateDictType))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典类型不存在");
        }

        @Test
        @DisplayName("删除字典类型成功")
        void deleteDictType_success() {
            // given
            when(dictTypeMapper.selectById(1L)).thenReturn(testDictType);
            when(dictTypeMapper.deleteById(1L)).thenReturn(1);

            // when
            dictService.deleteDictType(1L);

            // then
            verify(dictTypeMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除字典类型失败 - 不存在")
        void deleteDictType_notFound_throwsException() {
            // given
            when(dictTypeMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> dictService.deleteDictType(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典类型不存在");
        }
    }

    @Nested
    @DisplayName("字典数据测试")
    class DictDataTests {

        @Test
        @DisplayName("根据字典类型查询字典数据列表成功")
        void listDictDataByType_success() {
            // given
            when(dictDataMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testDictData));

            // when
            List<SysDictData> result = dictService.listDictDataByType("sys_user_status");

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDictLabel()).isEqualTo("启用");
        }

        @Test
        @DisplayName("查询字典数据详情成功")
        void getDictDataDetail_success() {
            // given
            when(dictDataMapper.selectById(1L)).thenReturn(testDictData);

            // when
            SysDictData result = dictService.getDictDataDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("查询字典数据详情失败 - 不存在")
        void getDictDataDetail_notFound_throwsException() {
            // given
            when(dictDataMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> dictService.getDictDataDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典数据不存在");
        }

        @Test
        @DisplayName("创建字典数据成功")
        void createDictData_success() {
            // given
            SysDictData newDictData = new SysDictData();
            newDictData.setDictType("sys_user_status");
            newDictData.setDictLabel("禁用");
            newDictData.setDictValue("0");

            when(dictDataMapper.insert(any(SysDictData.class))).thenReturn(1);

            // when
            SysDictData result = dictService.createDictData(newDictData);

            // then
            assertThat(result).isNotNull();
            verify(dictDataMapper).insert(any(SysDictData.class));
        }

        @Test
        @DisplayName("更新字典数据成功")
        void updateDictData_success() {
            // given
            SysDictData updateDictData = new SysDictData();
            updateDictData.setId(1L);
            updateDictData.setDictLabel("启用-更新");

            when(dictDataMapper.selectById(1L)).thenReturn(testDictData);
            when(dictDataMapper.updateById(any(SysDictData.class))).thenReturn(1);

            // when
            SysDictData result = dictService.updateDictData(updateDictData);

            // then
            assertThat(result).isNotNull();
            verify(dictDataMapper).updateById(any(SysDictData.class));
        }

        @Test
        @DisplayName("更新字典数据失败 - 不存在")
        void updateDictData_notFound_throwsException() {
            // given
            SysDictData updateDictData = new SysDictData();
            updateDictData.setId(999L);

            when(dictDataMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> dictService.updateDictData(updateDictData))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典数据不存在");
        }

        @Test
        @DisplayName("删除字典数据成功")
        void deleteDictData_success() {
            // given
            when(dictDataMapper.selectById(1L)).thenReturn(testDictData);
            when(dictDataMapper.deleteById(1L)).thenReturn(1);

            // when
            dictService.deleteDictData(1L);

            // then
            verify(dictDataMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除字典数据失败 - 不存在")
        void deleteDictData_notFound_throwsException() {
            // given
            when(dictDataMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> dictService.deleteDictData(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("字典数据不存在");
        }
    }
}
