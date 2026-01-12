import request from './request'

// 测试模式配置
const isTestMode = import.meta.env.VITE_APP_TEST_MODE === 'true'

// 测试模式下的模拟用户数据
let mockUsers = [
  {
    id: 1,
    username: 'admin',
    nickname: '系统管理员',
    email: 'admin@example.com',
    role: 'ADMIN',
    status: 'ACTIVE',
    createdAt: '2024-01-01 00:00:00',
    lastLoginAt: '2024-06-18 10:30:00'
  },
  {
    id: 2,
    username: 'user1',
    nickname: '张三',
    email: 'zhangsan@example.com',
    role: 'USER',
    status: 'ACTIVE',
    createdAt: '2024-02-01 00:00:00',
    lastLoginAt: '2024-06-17 15:20:00'
  },
  {
    id: 3,
    username: 'operator1',
    nickname: '李四',
    email: 'lisi@example.com',
    role: 'OPERATOR',
    status: 'ACTIVE',
    createdAt: '2024-03-01 00:00:00',
    lastLoginAt: '2024-06-16 09:15:00'
  }
]

// 生成唯一ID
let nextUserId = 4

// 测试模式下的模拟请求函数
const mockUserRequest = (config) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let response = { code: 200, msg: 'success', data: null }
      
      if (config.url === '/users' && config.method === 'get') {
        // 处理搜索条件
        let filteredUsers = [...mockUsers]
        const { username, role, status } = config.params || {}
        
        if (username) {
          filteredUsers = filteredUsers.filter(user => 
            user.username.toLowerCase().includes(username.toLowerCase())
          )
        }
        if (role) {
          filteredUsers = filteredUsers.filter(user => user.role === role)
        }
        if (status) {
          filteredUsers = filteredUsers.filter(user => user.status === status)
        }
        
        response.data = {
          list: filteredUsers,
          total: filteredUsers.length,
          page: 1,
          size: 10
        }
      } else if (config.url === '/users' && config.method === 'post') {
        const newUser = { 
          ...config.data, 
          id: nextUserId++,
          createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
          lastLoginAt: null
        }
        mockUsers.push(newUser)
        response.data = newUser
        console.log('新增用户:', newUser)
        console.log('当前用户列表:', mockUsers)
      } else if (config.url.startsWith('/users/') && config.method === 'put') {
        const id = parseInt(config.url.split('/').pop())
        const index = mockUsers.findIndex(user => user.id === id)
        if (index !== -1) {
          mockUsers[index] = { 
            ...mockUsers[index], 
            ...config.data,
            updatedAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
          }
          response.data = mockUsers[index]
          console.log('更新用户:', mockUsers[index])
        }
      } else if (config.url.startsWith('/users/') && config.method === 'delete') {
        const id = parseInt(config.url.split('/').pop())
        const index = mockUsers.findIndex(user => user.id === id)
        if (index !== -1) {
          const deletedUser = mockUsers.splice(index, 1)[0]
          console.log('删除用户:', deletedUser)
          console.log('当前用户列表:', mockUsers)
        }
      } else if (config.url.startsWith('/users/') && config.url.includes('/status') && config.method === 'put') {
        const id = parseInt(config.url.split('/')[2])
        const index = mockUsers.findIndex(user => user.id === id)
        if (index !== -1) {
          mockUsers[index].status = mockUsers[index].status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
          response.data = mockUsers[index]
          console.log('切换用户状态:', mockUsers[index])
        }
      }
      
      resolve(response)
    }, 300)
  })
}

// 获取用户列表
export function getUserList(params) {
  if (isTestMode) {
    return mockUserRequest({
      url: '/users',
      method: 'get',
      params
    })
  }
  
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data) {
  if (isTestMode) {
    return mockUserRequest({
      url: '/users',
      method: 'post',
      data
    })
  }
  
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  if (isTestMode) {
    return mockUserRequest({
      url: `/users/${id}`,
      method: 'put',
      data
    })
  }
  
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  if (isTestMode) {
    return mockUserRequest({
      url: `/users/${id}`,
      method: 'delete'
    })
  }
  
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 切换用户状态
export function toggleUserStatus(id) {
  if (isTestMode) {
    return mockUserRequest({
      url: `/users/${id}/status`,
      method: 'put'
    })
  }
  
  return request({
    url: `/users/${id}/status`,
    method: 'put'
  })
}

// 获取用户详情
export function getUserDetail(id) {
  if (isTestMode) {
    return mockUserRequest({
      url: `/users/${id}`,
      method: 'get'
    })
  }
  
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
} 