import request from './request'
import type { R, PageResult } from './types'

// ========== 角色类型定义 ==========

export interface SysRole {
  id: number
  roleCode: string
  roleName: string
  description: string
  sortOrder: number
  status: number
  menuIds: number[]
  createdAt: string
}

export interface RoleQuery {
  pageNum: number
  pageSize: number
  roleName?: string
  status?: number
}

// ========== 角色 API ==========

export function getRoles(params: RoleQuery): Promise<R<PageResult<SysRole>>> {
  return request.get('/system/roles', { params })
}

export function getAllRoles(): Promise<R<SysRole[]>> {
  return request.get('/system/roles/all')
}

export function getRole(id: number): Promise<R<SysRole>> {
  return request.get(`/system/roles/${id}`)
}

export function createRole(data: Partial<SysRole>): Promise<R<SysRole>> {
  return request.post('/system/roles', data)
}

export function updateRole(id: number, data: Partial<SysRole>): Promise<R<SysRole>> {
  return request.put(`/system/roles/${id}`, data)
}

export function deleteRole(id: number): Promise<R<void>> {
  return request.delete(`/system/roles/${id}`)
}
