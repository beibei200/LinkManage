import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 测试模式配置
const isTestMode = import.meta.env.VITE_APP_TEST_MODE === 'true'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // console.log('发送请求:', config.method?.toUpperCase(), config.url, config.data)
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // console.log('收到响应:', response.status, response.data)

    // 如果响应是文件流，则直接返回，不进行JSON解析
    if (response.data instanceof Blob && response.request.responseType === 'blob') {
      return response.data;
    }

    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    console.error('请求错误:', error.response?.status, error.message)
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

// 测试模式下的模拟数据 - 使用全局变量确保数据持久化
let mockLinks = [
  {
    id: 1,
    linkType: 'PRIMARY',
    purchaseTime: '2024-01-15 10:30:00',
    expireTime: '2025-01-15 10:30:00',
    price: 299.99,
    vpsProvider: '阿里云',
    purchaser: '张三',
    jumpCount: 1
  },
  {
    id: 2,
    linkType: 'SECONDARY',
    purchaseTime: '2024-02-20 14:20:00',
    expireTime: '2025-02-20 14:20:00',
    price: 199.99,
    vpsProvider: '腾讯云',
    purchaser: '李四',
    jumpCount: 2
  },
  {
    id: 3,
    linkType: 'PRIMARY',
    purchaseTime: '2024-03-10 09:15:00',
    expireTime: '2025-03-10 09:15:00',
    price: 399.99,
    vpsProvider: '华为云',
    purchaser: '王五',
    jumpCount: 1
  }
]

// 生成唯一ID
let nextId = 4

// 测试模式下的模拟请求函数
const mockRequest = (config) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let response = { code: 200, msg: 'success', data: null }
      
      if (config.url === '/links' && config.method === 'get') {
        response.data = {
          list: [...mockLinks], // 返回副本
          total: mockLinks.length,
          page: 1,
          size: 10
        }
      } else if (config.url === '/links' && config.method === 'post') {
        const newLink = { 
          ...config.data, 
          id: nextId++,
          created_at: new Date().toISOString().slice(0, 19).replace('T', ' '),
          updated_at: new Date().toISOString().slice(0, 19).replace('T', ' '),
          deleted: 0
        }
        mockLinks.push(newLink)
        response.data = newLink
        console.log('新增链路:', newLink)
        console.log('当前链路列表:', mockLinks)
      } else if (config.url.startsWith('/links/') && config.method === 'put') {
        const id = parseInt(config.url.split('/').pop())
        const index = mockLinks.findIndex(link => link.id === id)
        if (index !== -1) {
          mockLinks[index] = { 
            ...mockLinks[index], 
            ...config.data,
            updated_at: new Date().toISOString().slice(0, 19).replace('T', ' ')
          }
          response.data = mockLinks[index]
          console.log('更新链路:', mockLinks[index])
        }
      } else if (config.url.startsWith('/links/') && config.method === 'delete') {
        const id = parseInt(config.url.split('/').pop())
        const index = mockLinks.findIndex(link => link.id === id)
        if (index !== -1) {
          const deletedLink = mockLinks.splice(index, 1)[0]
          console.log('删除链路:', deletedLink)
          console.log('当前链路列表:', mockLinks)
        }
      }
      
      resolve(response)
    }, 300) // 减少延迟到300ms
  })
}

// 导出请求函数
export default isTestMode ? mockRequest : request 