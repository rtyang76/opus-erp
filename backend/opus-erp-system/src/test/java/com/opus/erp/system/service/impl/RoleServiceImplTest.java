package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.system.dto.RoleDTO;
import com.opus.erp.system.entity.SysRole;
import com.opus.erp.system.mapper.SysRoleMapper;
import com.opus.erp.system.mapper.SysRoleMenuMapper;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RoleService 单元测试
 * 测试角色管理 CRUD、查询等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService 单元测试")
class RoleServiceImplTest {

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private SysRole testRole;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(roleService, "baseMapper", roleMapper);

        testRole = new SysRole();
        testRole.setId(1L);
        testRole.setRoleCode("ADMIN");
        testRole.setRoleName("管理员");
        testRole.setDescription("系统管理员");
        testRole.setSortOrder(1);
        testRole.setStatus(1);
    }

    @Nested
    @DisplayName("查询角色列表测试")
    class ListRolesTests {

        @Test
        @DisplayName("分页查询角色列表 - 无筛选条件")
        void listRoles_noFilter_success() {
            // given
            Page<SysRole> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testRole));
            expectedPage.setTotal(1);

            when(roleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<SysRole> result = roleService.listRoles(1, 10, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getRoleCode()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("分页查询角色列表 - 按角色名称筛选")
        void listRoles_withRoleNameFilter_success() {
            // given
            Page<SysRole> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testRole));
            expectedPage.setTotal(1);

            when(roleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<SysRole> result = roleService.listRoles(1, 10, "管理员", null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("查询所有角色测试")
    class ListAllRolesTests {

        @Test
        @DisplayName("查询所有启用角色")
        void listAllRoles_success() {
            // given
            when(roleMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testRole));

            // when
            List<SysRole> result = roleService.listAllRoles();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("查询角色详情测试")
    class GetRoleDetailTests {

        @Test
        @DisplayName("查询角色详情成功")
        void getRoleDetail_success() {
            // given
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMapper.selectMenuIdsByRoleId(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

            // when
            SysRole result = roleService.getRoleDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMenuIds()).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("查询角色详情失败 - 角色不存在")
        void getRoleDetail_notFound_throwsException() {
            // given
            when(roleMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> roleService.getRoleDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("角色不存在");
        }
    }

    @Nested
    @DisplayName("创建角色测试")
    class CreateRoleTests {

        @Test
        @DisplayName("创建角色成功")
        void createRole_success() {
            // given
            RoleDTO newDto = new RoleDTO();
            newDto.setRoleCode("USER");
            newDto.setRoleName("普通用户");

            when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

            // when
            SysRole result = roleService.createRole(newDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRoleCode()).isEqualTo("USER");
            verify(roleMapper).insert(any(SysRole.class));
        }

        @Test
        @DisplayName("创建角色失败 - 角色编码已存在")
        void createRole_duplicateCode_throwsException() {
            // given
            RoleDTO newDto = new RoleDTO();
            newDto.setRoleCode("ADMIN");
            newDto.setRoleName("管理员2");

            when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> roleService.createRole(newDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("角色编码已存在");
        }
    }

    @Nested
    @DisplayName("更新角色测试")
    class UpdateRoleTests {

        @Test
        @DisplayName("更新角色成功")
        void updateRole_success() {
            // given
            RoleDTO updateDto = new RoleDTO();
            updateDto.setRoleCode("ADMIN");
            updateDto.setRoleName("超级管理员");

            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

            // when
            SysRole result = roleService.updateRole(1L, updateDto);

            // then
            assertThat(result).isNotNull();
            verify(roleMapper).updateById(any(SysRole.class));
        }

        @Test
        @DisplayName("更新角色失败 - 角色不存在")
        void updateRole_notFound_throwsException() {
            // given
            RoleDTO updateDto = new RoleDTO();
            updateDto.setRoleCode("ADMIN");

            when(roleMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> roleService.updateRole(999L, updateDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("角色不存在");
        }

        @Test
        @DisplayName("更新角色失败 - 角色编码重复")
        void updateRole_duplicateCode_throwsException() {
            // given
            RoleDTO updateDto = new RoleDTO();
            updateDto.setRoleCode("USER");

            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            assertThatThrownBy(() -> roleService.updateRole(1L, updateDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("角色编码已存在");
        }
    }

    @Nested
    @DisplayName("删除角色测试")
    class DeleteRoleTests {

        @Test
        @DisplayName("删除角色成功")
        void deleteRole_success() {
            // given
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMenuMapper.deleteByRoleId(1L)).thenReturn(1);
            when(roleMapper.deleteById(1L)).thenReturn(1);

            // when
            roleService.deleteRole(1L);

            // then
            verify(roleMenuMapper).deleteByRoleId(1L);
            verify(roleMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除角色失败 - 角色不存在")
        void deleteRole_notFound_throwsException() {
            // given
            when(roleMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> roleService.deleteRole(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("角色不存在");
        }
    }

    @Nested
    @DisplayName("根据用户ID查询角色测试")
    class SelectRolesByUserIdTests {

        @Test
        @DisplayName("根据用户ID查询角色成功")
        void selectRolesByUserId_success() {
            // given
            when(roleMapper.selectRolesByUserId(1L)).thenReturn(Arrays.asList(testRole));

            // when
            List<SysRole> result = roleService.selectRolesByUserId(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRoleCode()).isEqualTo("ADMIN");
        }
    }
}
