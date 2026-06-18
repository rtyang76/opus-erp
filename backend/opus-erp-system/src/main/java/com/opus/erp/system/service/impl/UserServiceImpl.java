package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.system.entity.SysUser;
import com.opus.erp.system.entity.SysUserRole;
import com.opus.erp.system.mapper.SysUserMapper;
import com.opus.erp.system.mapper.SysUserRoleMapper;
import com.opus.erp.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SysUser> listUsers(int pageNum, int pageSize, String username, Integer status) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }

        wrapper.orderByDesc(SysUser::getCreatedAt);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUser(SysUser user) {
        // 检查用户名是否已存在
        SysUser existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.DUPLICATE, "用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 保存用户
        userMapper.insert(user);

        // 保存用户角色关联
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            for (Long roleId : user.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        log.info("创建用户成功: username={}", user.getUsername());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUser(SysUser user) {
        // 检查用户是否存在
        SysUser existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 更新用户信息（不更新密码）
        user.setPassword(null);
        userMapper.updateById(user);

        // 更新用户角色关联
        if (user.getRoleIds() != null) {
            // 删除原有关联
            userRoleMapper.deleteByUserId(user.getId());
            // 保存新关联
            for (Long roleId : user.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        log.info("更新用户成功: userId={}", user.getId());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 逻辑删除用户
        userMapper.deleteById(userId);

        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);

        log.info("删除用户成功: userId={}", userId);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(updateUser);

        log.info("重置用户密码成功: userId={}", userId);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 更新状态
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        userMapper.updateById(updateUser);

        log.info("更新用户状态成功: userId={}, status={}", userId, status);
    }

    @Override
    public SysUser getUserDetail(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 查询用户角色
        List<Long> roleIds = userMapper.selectRoleIdsByUserId(userId);
        user.setRoleIds(roleIds);

        return user;
    }

    @Override
    public SysUser selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
