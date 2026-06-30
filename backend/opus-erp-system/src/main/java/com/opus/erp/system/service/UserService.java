package com.opus.erp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.system.dto.UserDTO;
import com.opus.erp.system.entity.SysUser;

/**
 * 用户服务接口
 */
public interface UserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param username 用户名（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    Page<SysUser> listUsers(int pageNum, int pageSize, String username, Integer status);

    /**
     * 创建用户
     * @param dto 用户DTO
     * @return 创建的用户
     */
    SysUser createUser(UserDTO dto);

    /**
     * 更新用户
     * @param id 用户ID
     * @param dto 用户DTO
     * @return 更新的用户
     */
    SysUser updateUser(Long id, UserDTO dto);

    /**
     * 删除用户（逻辑删除）
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 查询用户详情（包含角色信息）
     * @param userId 用户ID
     * @return 用户详情
     */
    SysUser getUserDetail(Long userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(String username);
}
