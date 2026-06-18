import request from './request'
import type { R } from './types'

// ========== 菜单类型定义 ==========

export interface SysMenu {
  id: number
  parentId: number
  menuName: string
  menuType: string
  path: string
  component: string
  perms: string
  icon: string
  sortOrder: number
  visible: number
  status: number
  remark: string
  children: SysMenu[]
  createdAt: string
}

// ========== 菜单 API ==========

export function getMenuTree(): Promise<R<SysMenu[]>> {
  return request.get('/system/menus/tree')
}

export function getUserMenus(): Promise<R<SysMenu[]>> {
  return request.get('/system/menus/user')
}

export function getAllMenus(): Promise<R<SysMenu[]>> {
  return request.get('/system/menus')
}

export function getMenu(id: number): Promise<R<SysMenu>> {
  return request.get(`/system/menus/${id}`)
}

export function createMenu(data: Partial<SysMenu>): Promise<R<SysMenu>> {
  return request.post('/system/menus', data)
}

export function updateMenu(id: number, data: Partial<SysMenu>): Promise<R<SysMenu>> {
  return request.put(`/system/menus/${id}`, data)
}

export function deleteMenu(id: number): Promise<R<void>> {
  return request.delete(`/system/menus/${id}`)
}
