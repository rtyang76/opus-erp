package com.opus.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opus.erp.common.exception.BusinessException;
import com.opus.erp.common.result.ErrorCode;
import com.opus.erp.system.entity.SysMenu;
import com.opus.erp.system.mapper.SysMenuMapper;
import com.opus.erp.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements MenuService {

    @Override
    public List<SysMenu> getMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> menus = baseMapper.selectList(wrapper);

        return buildTree(menus, 0L);
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        List<SysMenu> menus = baseMapper.selectMenusByUserId(userId);
        return buildTree(menus, 0L);
    }

    @Override
    public List<SysMenu> listAllMenus() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSortOrder);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysMenu getMenuDetail(Long menuId) {
        SysMenu menu = baseMapper.selectById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "菜单不存在");
        }
        return menu;
    }

    @Override
    public SysMenu createMenu(SysMenu menu) {
        baseMapper.insert(menu);
        log.info("创建菜单成功: menuName={}", menu.getMenuName());
        return menu;
    }

    @Override
    public SysMenu updateMenu(SysMenu menu) {
        SysMenu existingMenu = baseMapper.selectById(menu.getId());
        if (existingMenu == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "菜单不存在");
        }

        baseMapper.updateById(menu);
        log.info("更新菜单成功: menuId={}", menu.getId());
        return menu;
    }

    @Override
    public void deleteMenu(Long menuId) {
        SysMenu menu = baseMapper.selectById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "菜单不存在");
        }

        baseMapper.deleteById(menuId);
        log.info("删除菜单成功: menuId={}", menuId);
    }

    @Override
    public List<String> selectPermsByUserId(Long userId) {
        return baseMapper.selectPermsByUserId(userId);
    }

    /**
     * 构建树形结构
     */
    private List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        Map<Long, List<SysMenu>> grouped = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        return buildChildren(grouped, parentId);
    }

    /**
     * 递归构建子节点
     */
    private List<SysMenu> buildChildren(Map<Long, List<SysMenu>> grouped, Long parentId) {
        List<SysMenu> children = grouped.getOrDefault(parentId, new ArrayList<>());
        for (SysMenu child : children) {
            child.setChildren(buildChildren(grouped, child.getId()));
        }
        return children;
    }
}
