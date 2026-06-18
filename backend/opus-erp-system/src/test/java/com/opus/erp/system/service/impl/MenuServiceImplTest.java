package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.system.entity.SysMenu;
import com.opus.erp.system.mapper.SysMenuMapper;
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
 * MenuService 单元测试
 * 测试菜单管理 CRUD、树形结构构建等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MenuService 单元测试")
class MenuServiceImplTest {

    @Mock
    private SysMenuMapper menuMapper;

    @InjectMocks
    private MenuServiceImpl menuService;

    private SysMenu testMenu;

    @BeforeEach
    void setUp() {
        // 手动注入 baseMapper 到 ServiceImpl
        ReflectionTestUtils.setField(menuService, "baseMapper", menuMapper);

        testMenu = new SysMenu();
        testMenu.setId(1L);
        testMenu.setParentId(0L);
        testMenu.setMenuName("系统管理");
        testMenu.setMenuType("M");
        testMenu.setPath("/system");
        testMenu.setIcon("setting");
        testMenu.setSortOrder(1);
        testMenu.setVisible(1);
        testMenu.setStatus(1);
    }

    @Nested
    @DisplayName("查询菜单树测试")
    class GetMenuTreeTests {

        @Test
        @DisplayName("查询菜单树成功")
        void getMenuTree_success() {
            // given
            SysMenu childMenu = new SysMenu();
            childMenu.setId(2L);
            childMenu.setParentId(1L);
            childMenu.setMenuName("用户管理");
            childMenu.setMenuType("C");
            childMenu.setSortOrder(1);
            childMenu.setStatus(1);

            when(menuMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testMenu, childMenu));

            // when
            List<SysMenu> result = menuService.getMenuTree();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getChildren()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("查询用户菜单树测试")
    class GetUserMenuTreeTests {

        @Test
        @DisplayName("查询用户菜单树成功")
        void getUserMenuTree_success() {
            // given
            when(menuMapper.selectMenusByUserId(1L))
                    .thenReturn(Arrays.asList(testMenu));

            // when
            List<SysMenu> result = menuService.getUserMenuTree(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("查询所有菜单测试")
    class ListAllMenusTests {

        @Test
        @DisplayName("查询所有菜单成功")
        void listAllMenus_success() {
            // given
            when(menuMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testMenu));

            // when
            List<SysMenu> result = menuService.listAllMenus();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("查询菜单详情测试")
    class GetMenuDetailTests {

        @Test
        @DisplayName("查询菜单详情成功")
        void getMenuDetail_success() {
            // given
            when(menuMapper.selectById(1L)).thenReturn(testMenu);

            // when
            SysMenu result = menuService.getMenuDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMenuName()).isEqualTo("系统管理");
        }

        @Test
        @DisplayName("查询菜单详情失败 - 菜单不存在")
        void getMenuDetail_notFound_throwsException() {
            // given
            when(menuMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> menuService.getMenuDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("菜单不存在");
        }
    }

    @Nested
    @DisplayName("创建菜单测试")
    class CreateMenuTests {

        @Test
        @DisplayName("创建菜单成功")
        void createMenu_success() {
            // given
            SysMenu newMenu = new SysMenu();
            newMenu.setParentId(1L);
            newMenu.setMenuName("角色管理");
            newMenu.setMenuType("C");
            newMenu.setSortOrder(2);

            when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

            // when
            SysMenu result = menuService.createMenu(newMenu);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getMenuName()).isEqualTo("角色管理");
            verify(menuMapper).insert(any(SysMenu.class));
        }
    }

    @Nested
    @DisplayName("更新菜单测试")
    class UpdateMenuTests {

        @Test
        @DisplayName("更新菜单成功")
        void updateMenu_success() {
            // given
            SysMenu updateMenu = new SysMenu();
            updateMenu.setId(1L);
            updateMenu.setMenuName("系统管理-更新");

            when(menuMapper.selectById(1L)).thenReturn(testMenu);
            when(menuMapper.updateById(any(SysMenu.class))).thenReturn(1);

            // when
            SysMenu result = menuService.updateMenu(updateMenu);

            // then
            assertThat(result).isNotNull();
            verify(menuMapper).updateById(any(SysMenu.class));
        }

        @Test
        @DisplayName("更新菜单失败 - 菜单不存在")
        void updateMenu_notFound_throwsException() {
            // given
            SysMenu updateMenu = new SysMenu();
            updateMenu.setId(999L);

            when(menuMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> menuService.updateMenu(updateMenu))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("菜单不存在");
        }
    }

    @Nested
    @DisplayName("删除菜单测试")
    class DeleteMenuTests {

        @Test
        @DisplayName("删除菜单成功")
        void deleteMenu_success() {
            // given
            when(menuMapper.selectById(1L)).thenReturn(testMenu);
            when(menuMapper.deleteById(1L)).thenReturn(1);

            // when
            menuService.deleteMenu(1L);

            // then
            verify(menuMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除菜单失败 - 菜单不存在")
        void deleteMenu_notFound_throwsException() {
            // given
            when(menuMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> menuService.deleteMenu(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("菜单不存在");
        }
    }

    @Nested
    @DisplayName("查询用户权限测试")
    class SelectPermsByUserIdTests {

        @Test
        @DisplayName("查询用户权限成功")
        void selectPermsByUserId_success() {
            // given
            List<String> perms = Arrays.asList("system:user:list", "system:user:add", "system:role:list");
            when(menuMapper.selectPermsByUserId(1L)).thenReturn(perms);

            // when
            List<String> result = menuService.selectPermsByUserId(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).contains("system:user:list");
        }
    }
}
