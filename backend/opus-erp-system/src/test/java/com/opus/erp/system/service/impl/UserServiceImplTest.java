package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.system.entity.SysUser;
import com.opus.erp.system.entity.SysUserRole;
import com.opus.erp.system.mapper.SysUserMapper;
import com.opus.erp.system.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 测试用户管理 CRUD、密码重置、状态更新等逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceImplTest {

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$encrypted_password");
        testUser.setNickname("管理员");
        testUser.setStatus(1);
    }

    @Nested
    @DisplayName("查询用户列表测试")
    class ListUsersTests {

        @Test
        @DisplayName("分页查询用户列表 - 无筛选条件")
        void listUsers_noFilter_success() {
            // given
            Page<SysUser> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testUser));
            expectedPage.setTotal(1);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<SysUser> result = userService.listUsers(1, 10, null, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getUsername()).isEqualTo("admin");
        }

        @Test
        @DisplayName("分页查询用户列表 - 按用户名筛选")
        void listUsers_withUsernameFilter_success() {
            // given
            Page<SysUser> expectedPage = new Page<>(1, 10);
            expectedPage.setRecords(Arrays.asList(testUser));
            expectedPage.setTotal(1);

            when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(expectedPage);

            // when
            Page<SysUser> result = userService.listUsers(1, 10, "admin", null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("创建用户测试")
    class CreateUserTests {

        @Test
        @DisplayName("创建用户成功")
        void createUser_success() {
            // given
            SysUser newUser = new SysUser();
            newUser.setUsername("newuser");
            newUser.setPassword("123456");
            newUser.setNickname("新用户");
            newUser.setRoleIds(Arrays.asList(1L, 2L));

            when(userMapper.selectByUsername("newuser")).thenReturn(null);
            when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encoded_password");
            when(userMapper.insert(any(SysUser.class))).thenReturn(1);
            when(userRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);

            // when
            SysUser result = userService.createUser(newUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("newuser");
            verify(passwordEncoder).encode("123456");
            verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
        }

        @Test
        @DisplayName("创建用户失败 - 用户名已存在")
        void createUser_duplicateUsername_throwsException() {
            // given
            SysUser newUser = new SysUser();
            newUser.setUsername("admin");
            newUser.setPassword("123456");

            when(userMapper.selectByUsername("admin")).thenReturn(testUser);

            // when & then
            assertThatThrownBy(() -> userService.createUser(newUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户名已存在");
        }
    }

    @Nested
    @DisplayName("更新用户测试")
    class UpdateUserTests {

        @Test
        @DisplayName("更新用户成功")
        void updateUser_success() {
            // given
            SysUser updateUser = new SysUser();
            updateUser.setId(1L);
            updateUser.setNickname("新昵称");
            updateUser.setRoleIds(Arrays.asList(1L, 3L));

            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(SysUser.class))).thenReturn(1);
            when(userRoleMapper.deleteByUserId(1L)).thenReturn(2);
            when(userRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);

            // when
            SysUser result = userService.updateUser(updateUser);

            // then
            assertThat(result).isNotNull();
            verify(userRoleMapper).deleteByUserId(1L);
            verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
        }

        @Test
        @DisplayName("更新用户失败 - 用户不存在")
        void updateUser_notFound_throwsException() {
            // given
            SysUser updateUser = new SysUser();
            updateUser.setId(999L);

            when(userMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(updateUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("删除用户测试")
    class DeleteUserTests {

        @Test
        @DisplayName("删除用户成功")
        void deleteUser_success() {
            // given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.deleteById(1L)).thenReturn(1);
            when(userRoleMapper.deleteByUserId(1L)).thenReturn(2);

            // when
            userService.deleteUser(1L);

            // then
            verify(userMapper).deleteById(1L);
            verify(userRoleMapper).deleteByUserId(1L);
        }

        @Test
        @DisplayName("删除用户失败 - 用户不存在")
        void deleteUser_notFound_throwsException() {
            // given
            when(userMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("重置密码测试")
    class ResetPasswordTests {

        @Test
        @DisplayName("重置密码成功")
        void resetPassword_success() {
            // given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(passwordEncoder.encode("new_password")).thenReturn("$2a$10$new_encoded");
            when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

            // when
            userService.resetPassword(1L, "new_password");

            // then
            verify(passwordEncoder).encode("new_password");
            verify(userMapper).updateById(any(SysUser.class));
        }

        @Test
        @DisplayName("重置密码失败 - 用户不存在")
        void resetPassword_notFound_throwsException() {
            // given
            when(userMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.resetPassword(999L, "new_password"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("更新状态测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("更新状态成功")
        void updateStatus_success() {
            // given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

            // when
            userService.updateStatus(1L, 0);

            // then
            verify(userMapper).updateById(any(SysUser.class));
        }

        @Test
        @DisplayName("更新状态失败 - 用户不存在")
        void updateStatus_notFound_throwsException() {
            // given
            when(userMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.updateStatus(999L, 0))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("查询用户详情测试")
    class GetUserDetailTests {

        @Test
        @DisplayName("查询用户详情成功")
        void getUserDetail_success() {
            // given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.selectRoleIdsByUserId(1L)).thenReturn(Arrays.asList(1L, 2L));

            // when
            SysUser result = userService.getUserDetail(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getRoleIds()).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("查询用户详情失败 - 用户不存在")
        void getUserDetail_notFound_throwsException() {
            // given
            when(userMapper.selectById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.getUserDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("根据用户名查询测试")
    class SelectByUsernameTests {

        @Test
        @DisplayName("根据用户名查询成功")
        void selectByUsername_success() {
            // given
            when(userMapper.selectByUsername("admin")).thenReturn(testUser);

            // when
            SysUser result = userService.selectByUsername("admin");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("admin");
        }

        @Test
        @DisplayName("根据用户名查询 - 用户不存在")
        void selectByUsername_notFound_returnsNull() {
            // given
            when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

            // when
            SysUser result = userService.selectByUsername("nonexistent");

            // then
            assertThat(result).isNull();
        }
    }
}
