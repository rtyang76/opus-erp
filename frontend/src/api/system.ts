/**
 * 系统管理 API - 重新导出
 * 此文件保持向后兼容，实际实现已拆分为独立模块
 */

// 重新导出所有类型和函数
export type { R, PageResult } from './types'
export type { SysUser, UserQuery } from './system-user'
export type { SysRole, RoleQuery } from './system-role'
export type { SysMenu } from './system-menu'
export type { SysDictType, SysDictData } from './system-dict'

export {
  getUsers,
  getUser,
  createUser,
  updateUser,
  deleteUser,
  resetPassword,
  updateStatus
} from './system-user'

export {
  getRoles,
  getAllRoles,
  getRole,
  createRole,
  updateRole,
  deleteRole
} from './system-role'

export {
  getMenuTree,
  getUserMenus,
  getAllMenus,
  getMenu,
  createMenu,
  updateMenu,
  deleteMenu
} from './system-menu'

export {
  getDictTypes,
  getDictType,
  createDictType,
  updateDictType,
  deleteDictType,
  getDictDataByType,
  getDictData,
  createDictData,
  updateDictData,
  deleteDictData
} from './system-dict'
