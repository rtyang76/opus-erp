import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import type { LoginRequest } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshTokenValue = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<{
    id: number
    username: string
    nickname: string
    avatar: string
    roles: string[]
    permissions: string[]
  } | null>(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || '')
  const roles = computed(() => userInfo.value?.roles || [])
  const permissions = computed(() => userInfo.value?.permissions || [])

  // 登录
  async function login(loginRequest: LoginRequest) {
    const { data } = await loginApi(loginRequest)

    // 保存 Token
    token.value = data.accessToken
    refreshTokenValue.value = data.refreshToken
    userInfo.value = data.userInfo

    // 持久化到 localStorage
    localStorage.setItem('token', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除状态
      token.value = ''
      refreshTokenValue.value = ''
      userInfo.value = null

      // 清除 localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
    }
  }

  // 检查权限
  function hasPermission(perm: string): boolean {
    if (roles.value.includes('ADMIN')) {
      return true
    }
    return permissions.value.includes(perm)
  }

  // 检查角色
  function hasRole(role: string): boolean {
    return roles.value.includes(role)
  }

  return {
    token,
    refreshToken: refreshTokenValue,
    userInfo,
    isLoggedIn,
    username,
    nickname,
    roles,
    permissions,
    login,
    logout,
    hasPermission,
    hasRole,
  }
})
