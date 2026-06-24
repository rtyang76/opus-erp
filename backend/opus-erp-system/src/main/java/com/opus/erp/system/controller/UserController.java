package com.opus.erp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opus.erp.common.result.R;
import com.opus.erp.system.dto.UserDTO;
import com.opus.erp.system.entity.SysUser;
import com.opus.erp.system.service.UserService;
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

import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public R<Page<SysUser>> listUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        Page<SysUser> page = userService.listUsers(pageNum, pageSize, username, status);
        return R.ok(page);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    public R<SysUser> getUser(@PathVariable Long id) {
        SysUser user = userService.getUserDetail(id);
        return R.ok(user);
    }

    /**
     * 创建用户
     * TODO: DTO-to-Entity 转换应移入 Service 层
     */
    @PostMapping
    public R<SysUser> createUser(@Valid @RequestBody UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        SysUser createdUser = userService.createUser(user);
        return R.ok("创建成功", createdUser);
    }

    /**
     * 更新用户
     * TODO: DTO-to-Entity 转换应移入 Service 层
     */
    @PutMapping("/{id}")
    public R<SysUser> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setId(id);
        SysUser updatedUser = userService.updateUser(user);
        return R.ok("更新成功", updatedUser);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.okMsg("删除成功");
    }

    /**
     * 重置用户密码
     * 注意：密码通过请求体传递，避免被日志记录
     */
    @PutMapping("/{id}/password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) {
            return R.fail(1001, "新密码不能为空");
        }
        userService.resetPassword(id, newPassword);
        return R.okMsg("密码重置成功");
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return R.okMsg("状态更新成功");
    }
}
