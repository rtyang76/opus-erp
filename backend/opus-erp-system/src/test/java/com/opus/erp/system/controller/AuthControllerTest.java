package com.opus.erp.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.exception.GlobalExceptionHandler;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.system.dto.LoginRequest;
import com.opus.erp.system.dto.LoginResponse;
import com.opus.erp.system.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 单元测试
 * 测试认证接口的请求处理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 单元测试")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("登录接口测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功")
        void login_success() throws Exception {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123456");

            LoginResponse response = new LoginResponse();
            response.setAccessToken("access_token");
            response.setRefreshToken("refresh_token");
            response.setExpiresIn(7200L);

            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(1L);
            userInfo.setUsername("admin");
            userInfo.setNickname("管理员");
            userInfo.setRoles(Arrays.asList("ADMIN"));
            userInfo.setPermissions(Arrays.asList("system:user:list"));
            response.setUserInfo(userInfo);

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.msg").value("登录成功"))
                    .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                    .andExpect(jsonPath("$.data.userInfo.username").value("admin"));
        }

        @Test
        @DisplayName("登录失败 - 用户名或密码错误")
        void login_failed() throws Exception {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("wrong_password");

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new BusinessException(ErrorCode.LOGIN_FAILED));

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(2006))
                    .andExpect(jsonPath("$.msg").value("用户名或密码错误"));
        }

        @Test
        @DisplayName("登录失败 - 账号已禁用")
        void login_accountDisabled() throws Exception {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("disabled_user");
            request.setPassword("123456");

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new BusinessException(ErrorCode.ACCOUNT_DISABLED));

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(2005))
                    .andExpect(jsonPath("$.msg").value("账号已被禁用"));
        }

        @Test
        @DisplayName("登录失败 - 用户名为空")
        void login_emptyUsername() throws Exception {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("");
            request.setPassword("123456");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1001));
        }

        @Test
        @DisplayName("登录失败 - 密码为空")
        void login_emptyPassword() throws Exception {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1001));
        }

        @Test
        @DisplayName("登录失败 - 密码太短")
        void login_passwordTooShort() throws Exception {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(1001));
        }
    }

    @Nested
    @DisplayName("登出接口测试")
    class LogoutTests {

        @Test
        @DisplayName("登出成功")
        void logout_success() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.msg").value("登出成功"));
        }
    }
}
