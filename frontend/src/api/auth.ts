import request from './request'
import type { R } from './system'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  userInfo: {
    id: number
    username: string
    nickname: string
    avatar: string
    roles: string[]
    permissions: string[]
  }
}

/**
 * 用户登录
 */
export function login(data: LoginRequest): Promise<R<LoginResponse>> {
  return request.post('/auth/login', data)
}

/**
 * 刷新 Token
 */
export function refreshToken(refreshToken: string): Promise<R<string>> {
  return request.post('/auth/refresh', { refreshToken })
}

/**
 * 用户登出
 */
export function logout(): Promise<R<void>> {
  return request.post('/auth/logout')
}
