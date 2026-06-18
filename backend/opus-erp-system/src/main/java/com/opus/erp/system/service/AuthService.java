package com.opus.erp.system.service;

import com.opus.erp.system.dto.LoginRequest;
import com.opus.erp.system.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（包含 Token）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新 Token
     * @param refreshToken 刷新 Token
     * @return 新的访问 Token
     */
    String refreshToken(String refreshToken);

    /**
     * 用户登出
     * @param userId 用户ID
     */
    void logout(Long userId);
}
