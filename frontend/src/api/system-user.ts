import request from './request'
import type { R, PageResult } from './types'

// ========== 用户类型定义 ==========

export interface SysUser {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  status: number
  deptId: number
  remark: string
  roleIds: number[]
  createdAt: string
}

export interface UserQuery {
  pageNum: number
  pageSize: number
  username?: string
  status?: number
}

// ========== 用户 API ==========

export function getUsers(params: UserQuery): Promise<R<PageResult<SysUser>>> {
  return request.get('/system/users', { params })
}

export function getUser(id: number): Promise<R<SysUser>> {
  return request.get(`/system/users/${id}`)
}

export function createUser(data: Partial<SysUser>): Promise<R<SysUser>> {
  return request.post('/system/users', data)
}

export function updateUser(id: number, data: Partial<SysUser>): Promise<R<SysUser>> {
  return request.put(`/system/users/${id}`, data)
}

export function deleteUser(id: number): Promise<R<void>> {
  return request.delete(`/system/users/${id}`)
}

export function resetPassword(id: number, newPassword: string): Promise<R<void>> {
  return request.put(`/system/users/${id}/password`, null, { params: { newPassword } })
}

export function updateStatus(id: number, status: number): Promise<R<void>> {
  return request.put(`/system/users/${id}/status`, null, { params: { status } })
}
