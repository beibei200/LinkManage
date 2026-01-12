import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: 'links',
        name: 'Links',
        component: () => import('../views/Links.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 测试模式
router.beforeEach((to, from, next) => {
  // console.log('路由跳转:', to.path)
  
  // 测试模式：直接允许访问所有页面，如果访问登录页则跳转到主页
  const isTestMode = import.meta.env.VITE_APP_TEST_MODE === 'true' // 设置为true启用测试模式
  
  if (isTestMode) {
    if (to.path === '/login') {
      console.log('测试模式：从登录页跳转到主页')
      next('/')
    } else {
      console.log('测试模式：允许访问', to.path)
      next()
    }
    return
  }
  
  // 正常模式：检查认证
  const token = localStorage.getItem('token')
  // console.log('Token:', token)
  
  if (to.meta.requiresAuth && !token) {
    console.log('需要认证，跳转到登录页')
    next('/login')
  } else {
    // console.log('允许访问:', to.path)
    next()
  }
})

export default router 