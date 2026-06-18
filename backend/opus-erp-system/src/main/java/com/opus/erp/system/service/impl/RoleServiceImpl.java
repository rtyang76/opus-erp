package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.system.entity.SysRole;
import com.opus.erp.system.mapper.SysRoleMapper;
import com.opus.erp.system.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {

    @Override
    public Page<SysRole> listRoles(int pageNum, int pageSize, String roleName, Integer status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(SysRole::getStatus, status);
        }

        wrapper.orderByAsc(SysRole::getSortOrder);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysRole> listAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.orderByAsc(SysRole::getSortOrder);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysRole getRoleDetail(Long roleId) {
        SysRole role = baseMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        // 查询角色的菜单ID列表
        List<Long> menuIds = baseMapper.selectMenuIdsByRoleId(roleId);
        role.setMenuIds(menuIds);

        return role;
    }

    @Override
    public SysRole createRole(SysRole role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "角色编码已存在");
        }

        baseMapper.insert(role);
        log.info("创建角色成功: roleCode={}", role.getRoleCode());
        return role;
    }

    @Override
    public SysRole updateRole(SysRole role) {
        // 检查角色是否存在
        SysRole existingRole = baseMapper.selectById(role.getId());
        if (existingRole == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        // 检查编码是否重复（排除自身）
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode())
               .ne(SysRole::getId, role.getId());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE, "角色编码已存在");
        }

        baseMapper.updateById(role);
        log.info("更新角色成功: roleId={}", role.getId());
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        SysRole role = baseMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "角色不存在");
        }

        baseMapper.deleteById(roleId);
        log.info("删除角色成功: roleId={}", roleId);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }
}
