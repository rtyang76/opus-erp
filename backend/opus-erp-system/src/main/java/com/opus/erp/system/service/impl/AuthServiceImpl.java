package com.opus.erp.system.service.impl;

import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.common.utils.JwtUtils;
import com.opus.erp.system.dto.LoginRequest;
import com.opus.erp.system.dto.LoginResponse;
import com.opus.erp.system.entity.SysRole;
import com.opus.erp.system.entity.SysUser;
import com.opus.erp.system.service.AuthService;
import com.opus.erp.system.service.MenuService;
import com.opus.erp.system.service.RoleService;
import com.opus.erp.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_TOKEN_KEY = "user:token:";
    private static final String USER_PERMS_KEY = "user:perms:";

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户
        SysUser user = userService.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 4. 查询用户角色
        List<SysRole> roles = roleService.selectRolesByUserId(user.getId());
        List<String> roleCodes = roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());

        // 5. 查询用户权限
        List<String> perms = menuService.selectPermsByUserId(user.getId());

        // 6. 生成 Token
        String accessToken = jwtUtils.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        // 7. 将 Token 存入 Redis（2小时过期）
        redisTemplate.opsForValue().set(
                USER_TOKEN_KEY + user.getId(),
                accessToken,
                2,
                TimeUnit.HOURS
        );

        // 8. 将权限存入 Redis（2小时过期）
        redisTemplate.opsForValue().set(
                USER_PERMS_KEY + user.getId(),
                perms,
                2,
                TimeUnit.HOURS
        );

        // 9. 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(7200L);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roleCodes);
        userInfo.setPermissions(perms);
        response.setUserInfo(userInfo);

        log.info("用户登录成功: username={}", user.getUsername());
        return response;
    }

    @Override
    public String refreshToken(String refreshToken) {
        // 1. 验证 Refresh Token
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 2. 检查是否是 Refresh Token
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);

        // 3. 生成新的 Access Token
        return jwtUtils.generateToken(userId, username);
    }

    @Override
    public void logout(Long userId) {
        // 从 Redis 中删除 Token 和权限
        redisTemplate.delete(USER_TOKEN_KEY + userId);
        redisTemplate.delete(USER_PERMS_KEY + userId);
        log.info("用户登出成功: userId={}", userId);
    }
}
