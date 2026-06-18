package com.opus.erp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.system.dto.RoleDTO;
import com.opus.erp.system.entity.SysRole;
import com.opus.erp.system.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 查询角色列表（分页）
     */
    @GetMapping
    public R<Page<SysRole>> listRoles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        Page<SysRole> page = roleService.listRoles(pageNum, pageSize, roleName, status);
        return R.ok(page);
    }

    /**
     * 查询所有角色（不分页）
     */
    @GetMapping("/all")
    public R<List<SysRole>> listAllRoles() {
        List<SysRole> roles = roleService.listAllRoles();
        return R.ok(roles);
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/{id}")
    public R<SysRole> getRole(@PathVariable Long id) {
        SysRole role = roleService.getRoleDetail(id);
        return R.ok(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public R<SysRole> createRole(@Valid @RequestBody RoleDTO dto) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        SysRole createdRole = roleService.createRole(role);
        return R.ok("创建成功", createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public R<SysRole> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setId(id);
        SysRole updatedRole = roleService.updateRole(role);
        return R.ok("更新成功", updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.okMsg("删除成功");
    }
}
