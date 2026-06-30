package com.opus.erp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opus.erp.system.dto.MenuDTO;
import com.opus.erp.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService extends IService<SysMenu> {

    /**
     * 查询菜单树
     * @return 菜单树
     */
    List<SysMenu> getMenuTree();

    /**
     * 查询当前用户的菜单树
     * @param userId 用户ID
     * @return 菜单树
     */
    List<SysMenu> getUserMenuTree(Long userId);

    /**
     * 查询所有菜单（平铺）
     * @return 菜单列表
     */
    List<SysMenu> listAllMenus();

    /**
     * 查询菜单详情
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    SysMenu getMenuDetail(Long menuId);

    /**
     * 创建菜单
     * @param dto 菜单DTO
     * @return 创建的菜单
     */
    SysMenu createMenu(MenuDTO dto);

    /**
     * 更新菜单
     * @param id 菜单ID
     * @param dto 菜单DTO
     * @return 更新的菜单
     */
    SysMenu updateMenu(Long id, MenuDTO dto);

    /**
     * 删除菜单（逻辑删除）
     * @param menuId 菜单ID
     */
    void deleteMenu(Long menuId);

    /**
     * 查询用户的权限标识列表
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> selectPermsByUserId(Long userId);
}
