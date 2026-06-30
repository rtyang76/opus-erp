package com.opus.erp.system.controller;

import com.opus.erp.common.result.R;
import com.opus.erp.common.utils.SecurityUtils;
import com.opus.erp.system.dto.MenuDTO;
import com.opus.erp.system.entity.SysMenu;
import com.opus.erp.system.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 查询菜单树
     */
    @GetMapping("/tree")
    public R<List<SysMenu>> getMenuTree() {
        List<SysMenu> tree = menuService.getMenuTree();
        return R.ok(tree);
    }

    /**
     * 查询当前用户的菜单树
     */
    @GetMapping("/user")
    public R<List<SysMenu>> getUserMenus() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<SysMenu> tree = menuService.getUserMenuTree(userId);
        return R.ok(tree);
    }

    /**
     * 查询所有菜单（平铺）
     */
    @GetMapping
    public R<List<SysMenu>> listMenus() {
        List<SysMenu> menus = menuService.listAllMenus();
        return R.ok(menus);
    }

    /**
     * 查询菜单详情
     */
    @GetMapping("/{id}")
    public R<SysMenu> getMenu(@PathVariable Long id) {
        SysMenu menu = menuService.getMenuDetail(id);
        return R.ok(menu);
    }

    /**
     * 创建菜单
     */
    @PostMapping
    public R<SysMenu> createMenu(@Valid @RequestBody MenuDTO dto) {
        SysMenu createdMenu = menuService.createMenu(dto);
        return R.ok("创建成功", createdMenu);
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    public R<SysMenu> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuDTO dto) {
        SysMenu updatedMenu = menuService.updateMenu(id, dto);
        return R.ok("更新成功", updatedMenu);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.okMsg("删除成功");
    }
}
