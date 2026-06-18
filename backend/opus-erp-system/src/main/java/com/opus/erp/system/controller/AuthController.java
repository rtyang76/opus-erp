package com.opus.erp.system.controller;

import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.system.dto.LoginRequest;
import com.opus.erp.system.dto.LoginResponse;
import com.opus.erp.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return R.ok("登录成功", response);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public R<String> refreshToken(String refreshToken) {
        String newToken = authService.refreshToken(refreshToken);
        return R.ok("刷新成功", newToken);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            authService.logout(userId);
        }
        return R.okMsg("登出成功");
    }
}
