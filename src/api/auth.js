import request from './request'

// 测试模式配置
const isTestMode = import.meta.env.VITE_APP_TEST_MODE === 'true'

// 测试模式下的模拟登录
const mockLogin = (username, password) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (username === 'admin' && password === 'admin123') {
        resolve({
          code: 200,
          msg: '登录成功',
          data: {
            token: 'test-token-123456',
            userInfo: {
              id: 1,
              username: 'admin',
              nickname: '系统管理员',
              email: 'admin@example.com',
              role: 'ADMIN',
              status: 'ACTIVE',
              createdAt: '2024-01-01 00:00:00',
              lastLoginAt: '2024-06-18 10:30:00'
            }
          }
        })
      } else if (username === 'user1' && password === 'user123') {
        resolve({
          code: 200,
          msg: '登录成功',
          data: {
            token: 'test-token-user-789',
            userInfo: {
              id: 2,
              username: 'user1',
              nickname: '张三',
              email: 'zhangsan@example.com',
              role: 'USER',
              status: 'ACTIVE',
              createdAt: '2024-02-01 00:00:00',
              lastLoginAt: '2024-06-17 15:20:00'
            }
          }
        })
      } else {
        reject(new Error('用户名或密码错误'))
      }
    }, 500)
  })
}

export function login(username, password) {
  if (isTestMode) {
    return mockLogin(username, password)
  }
  
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
} 