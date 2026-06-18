package com.opus.erp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param roleName 角色名称（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    Page<SysRole> listRoles(int pageNum, int pageSize, String roleName, Integer status);

    /**
     * 查询所有角色（不分页）
     * @return 角色列表
     */
    List<SysRole> listAllRoles();

    /**
     * 查询角色详情（包含菜单ID列表）
     * @param roleId 角色ID
     * @return 角色详情
     */
    SysRole getRoleDetail(Long roleId);

    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建的角色
     */
    SysRole createRole(SysRole role);

    /**
     * 更新角色
     * @param role 角色信息
     * @return 更新的角色
     */
    SysRole updateRole(SysRole role);

    /**
     * 删除角色（逻辑删除）
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 查询用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);
}
