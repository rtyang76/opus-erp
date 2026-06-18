package com.opus.erp.system.service.impl;

import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.utils.JwtUtils;
import com.opus.erp.system.dto.LoginRequest;
import com.opus.erp.system.dto.LoginResponse;
import com.opus.erp.system.entity.SysRole;
import com.opus.erp.system.entity.SysUser;
import com.opus.erp.system.service.MenuService;
import com.opus.erp.system.service.RoleService;
import com.opus.erp.system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 * 测试认证登录、Token 刷新、登出逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private MenuService menuService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private SysUser testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 准备测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$encrypted_password");
        testUser.setNickname("管理员");
        testUser.setAvatar("avatar.png");
        testUser.setStatus(1);

        // 准备登录请求
        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");
    }

    @Nested
    @DisplayName("登录测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功")
        void login_success() {
            // given
            when(userService.selectByUsername("admin")).thenReturn(testUser);
            when(passwordEncoder.matches("123456", "$2a$10$encrypted_password")).thenReturn(true);

            SysRole role = new SysRole();
            role.setRoleCode("ADMIN");
            when(roleService.selectRolesByUserId(1L)).thenReturn(Arrays.asList(role));

            List<String> perms = Arrays.asList("system:user:list", "system:user:add");
            when(menuService.selectPermsByUserId(1L)).thenReturn(perms);

            when(jwtUtils.generateToken(1L, "admin")).thenReturn("access_token");
            when(jwtUtils.generateRefreshToken(1L, "admin")).thenReturn("refresh_token");

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            // when
            LoginResponse response = authService.login(loginRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("access_token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
            assertThat(response.getExpiresIn()).isEqualTo(7200L);
            assertThat(response.getUserInfo()).isNotNull();
            assertThat(response.getUserInfo().getId()).isEqualTo(1L);
            assertThat(response.getUserInfo().getUsername()).isEqualTo("admin");
            assertThat(response.getUserInfo().getRoles()).contains("ADMIN");
            assertThat(response.getUserInfo().getPermissions()).contains("system:user:list");

            verify(valueOperations).set(eq("user:token:1"), eq("access_token"), eq(2L), eq(TimeUnit.HOURS));
            verify(valueOperations).set(eq("user:perms:1"), eq(perms), eq(2L), eq(TimeUnit.HOURS));
        }

        @Test
        @DisplayName("登录失败 - 用户不存在")
        void login_userNotFound_throwsException() {
            // given
            when(userService.selectByUsername("nonexistent")).thenReturn(null);
            loginRequest.setUsername("nonexistent");

            // when & then
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void login_wrongPassword_throwsException() {
            // given
            when(userService.selectByUsername("admin")).thenReturn(testUser);
            when(passwordEncoder.matches("wrong_password", "$2a$10$encrypted_password")).thenReturn(false);
            loginRequest.setPassword("wrong_password");

            // when & then
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("登录失败 - 账号已禁用")
        void login_accountDisabled_throwsException() {
            // given
            testUser.setStatus(0);
            when(userService.selectByUsername("admin")).thenReturn(testUser);
            when(passwordEncoder.matches("123456", "$2a$10$encrypted_password")).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("刷新Token测试")
    class RefreshTokenTests {

        @Test
        @DisplayName("刷新Token成功")
        void refreshToken_success() {
            // given
            String refreshToken = "valid_refresh_token";
            when(jwtUtils.validateToken(refreshToken)).thenReturn(true);
            when(jwtUtils.getUserIdFromToken(refreshToken)).thenReturn(1L);
            when(jwtUtils.getUsernameFromToken(refreshToken)).thenReturn("admin");
            when(jwtUtils.generateToken(1L, "admin")).thenReturn("new_access_token");

            // when
            String newToken = authService.refreshToken(refreshToken);

            // then
            assertThat(newToken).isEqualTo("new_access_token");
        }

        @Test
        @DisplayName("刷新Token失败 - Token无效")
        void refreshToken_invalidToken_throwsException() {
            // given
            String invalidToken = "invalid_token";
            when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(invalidToken))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("登出测试")
    class LogoutTests {

        @Test
        @DisplayName("登出成功")
        void logout_success() {
            // given
            when(redisTemplate.delete("user:token:1")).thenReturn(true);
            when(redisTemplate.delete("user:perms:1")).thenReturn(true);

            // when
            authService.logout(1L);

            // then
            verify(redisTemplate).delete("user:token:1");
            verify(redisTemplate).delete("user:perms:1");
        }
    }
}
