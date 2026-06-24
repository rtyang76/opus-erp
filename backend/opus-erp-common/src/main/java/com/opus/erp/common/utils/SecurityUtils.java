package com.opus.erp.common.utils;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 * 注意：这是一个简化的实现，实际项目中应该从 SecurityContext 中获取
 * 所有方法均为静态方法，不需要 @Component 注解
 */
public class SecurityUtils {

    /**
     * 使用 ThreadLocal 存储当前用户ID
     */
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    /**
     * 使用 ThreadLocal 存储当前用户名
     */
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    /**
     * 使用 ThreadLocal 存储当前租户ID
     */
    private static final ThreadLocal<Long> CURRENT_TENANT_ID = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, String username) {
        CURRENT_USER_ID.set(userId);
        CURRENT_USERNAME.set(username);
    }

    /**
     * 设置当前用户信息（包含租户ID）
     */
    public static void setCurrentUser(Long userId, String username, Long tenantId) {
        CURRENT_USER_ID.set(userId);
        CURRENT_USERNAME.set(username);
        CURRENT_TENANT_ID.set(tenantId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return CURRENT_USERNAME.get();
    }

    /**
     * 获取当前租户ID
     */
    public static Long getCurrentTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    /**
     * 清除当前用户信息（请求结束时调用）
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
        CURRENT_TENANT_ID.remove();
    }

    /**
     * 判断是否有登录用户
     */
    public static boolean isAuthenticated() {
        return CURRENT_USER_ID.get() != null;
    }
}
