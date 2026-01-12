import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  // 测试模式：自动设置测试用户
  const initTestUser = () => {
    const isTestMode = import.meta.env.VITE_APP_TEST_MODE === 'true'
    if (isTestMode && !token.value) {
      token.value = 'test-token-123456'
      userInfo.value = {
        id: 1,
        username: 'admin',
        nickname: '系统管理员',
        email: 'admin@example.com',
        role: 'ADMIN',
        status: 'ACTIVE',
        createdAt: '2024-01-01 00:00:00',
        lastLoginAt: '2024-06-18 10:30:00'
      }
      localStorage.setItem('token', token.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  // 初始化测试用户
  initTestUser()

  async function loginAction(username, password) {
    try {
      const res = await login(username, password)
      token.value = res.data.token
      userInfo.value = res.data.userInfo
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      return Promise.resolve()
    } catch (error) {
      return Promise.reject(error)
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 更新用户信息
  function updateUserInfo(newUserInfo) {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    token,
    userInfo,
    loginAction,
    logout,
    updateUserInfo
  }
}) 