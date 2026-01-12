<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleEdit" :icon="Edit">
                {{ isEditing ? '保存' : '编辑' }}
              </el-button>
            </div>
          </template>
          
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            :disabled="!isEditing"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input v-model="form.username" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="form.nickname" placeholder="请输入昵称" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="角色">
                  <el-input v-model="roleLabel" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" v-if="isEditing">
              <el-col :span="12">
                <el-form-item label="新密码" prop="password">
                  <el-input 
                    v-model="form.password" 
                    type="password" 
                    placeholder="留空则不修改密码"
                    show-password
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input 
                    v-model="form.confirmPassword" 
                    type="password" 
                    placeholder="请确认密码"
                    show-password
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item v-if="isEditing">
              <el-button type="primary" @click="handleSave" :loading="saveLoading">
                {{ saveLoading ? '保存中...' : '保存' }}
              </el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 统计信息卡片 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>账户信息</span>
          </template>
          
          <div class="account-info">
            <div class="info-item">
              <div class="info-label">注册时间</div>
              <div class="info-value">{{ formatDateTime(form.createdAt) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">最后登录</div>
              <div class="info-value">{{ formatDateTime(form.lastLoginAt) }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">账户状态</div>
              <div class="info-value">
                <el-tag :type="form.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
                  {{ form.status === 'ACTIVE' ? '正常' : '禁用' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 操作日志卡片 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>最近操作</span>
          </template>
          
          <div class="recent-actions">
            <div v-if="recentActions.length === 0" class="no-data">
              暂无操作记录
            </div>
            <div v-else class="action-list">
              <div 
                v-for="action in recentActions" 
                :key="action.id" 
                class="action-item"
              >
                <div class="action-content">{{ action.content }}</div>
                <div class="action-time">{{ formatDateTime(action.createdAt) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { updateUser, getUserDetail } from '../api/user'

const userStore = useUserStore()
const formRef = ref(null)
const isEditing = ref(false)
const saveLoading = ref(false)

// 表单数据
const form = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: '',
  status: '',
  createdAt: '',
  lastLoginAt: ''
})

// 表单验证规则
const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { 
      validator: (rule, value, callback) => {
        if (form.password && value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 角色标签
const roleLabel = computed(() => {
  const roleMap = {
    'ADMIN': '管理员',
    'USER': '普通用户',
    'OPERATOR': '操作员'
  }
  return roleMap[form.role] || form.role
})

// 模拟最近操作数据
const recentActions = ref([
  {
    id: 1,
    content: '登录系统',
    createdAt: '2024-06-18 10:30:00'
  },
  {
    id: 2,
    content: '查看链路列表',
    createdAt: '2024-06-18 10:25:00'
  },
  {
    id: 3,
    content: '编辑链路信息',
    createdAt: '2024-06-18 10:20:00'
  }
])

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 从用户store获取基本信息
    const userInfo = userStore.userInfo
    Object.assign(form, {
      id: userInfo.id,
      username: userInfo.username,
      nickname: userInfo.nickname || userInfo.username,
      email: userInfo.email || '',
      role: userInfo.role,
      status: userInfo.status || 'ACTIVE',
      createdAt: userInfo.createdAt || '',
      lastLoginAt: userInfo.lastLoginAt || ''
    })
    
    // 如果有用户ID，获取详细信息
    if (userInfo.id) {
      const res = await getUserDetail(userInfo.id)
      if (res.data) {
        Object.assign(form, res.data)
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// 开始编辑
const handleEdit = () => {
  if (isEditing.value) {
    handleSave()
  } else {
    isEditing.value = true
  }
}

// 保存信息
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    saveLoading.value = true
    
    // 格式化提交数据
    const submitData = {
      nickname: form.nickname,
      email: form.email,
      password: form.password || undefined
    }
    
    console.log('保存用户信息:', submitData)
    
    await updateUser(form.id, submitData)
    ElMessage.success('保存成功')
    
    // 更新用户store中的信息
    userStore.userInfo = {
      ...userStore.userInfo,
      nickname: form.nickname,
      email: form.email
    }
    
    // 重置编辑状态
    isEditing.value = false
    form.password = ''
    form.confirmPassword = ''
  } catch (error) {
    console.error('保存失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('保存失败')
    }
  } finally {
    saveLoading.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  form.password = ''
  form.confirmPassword = ''
  loadUserInfo() // 重新加载原始数据
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.account-info .info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.account-info .info-item:last-child {
  border-bottom: none;
}

.account-info .info-item .info-label {
  color: #666;
  font-size: 14px;
}

.account-info .info-item .info-value {
  color: #333;
  font-weight: 500;
}

.recent-actions .no-data {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.recent-actions .action-list .action-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.recent-actions .action-list .action-item:last-child {
  border-bottom: none;
}

.recent-actions .action-list .action-item .action-content {
  color: #333;
  font-size: 14px;
  margin-bottom: 4px;
}

.recent-actions .action-list .action-item .action-time {
  color: #999;
  font-size: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #f5f7fa;
}
</style> 