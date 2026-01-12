<template>
  <div class="home-container">
    <!-- 测试模式提示 -->
    <el-alert
      v-if="isTestMode"
      title="测试模式"
      description="当前运行在测试模式下，使用模拟数据。用户名：admin，密码：admin123"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <!-- 欢迎信息 -->
    <el-card style="margin-bottom: 20px;">
      <div class="welcome-section">
        <div class="welcome-content">
          <h2>欢迎回来，{{ userStore.userInfo.nickname || userStore.userInfo.username }}！</h2>
          <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        <div class="user-avatar">
          <el-avatar :size="64" :src="userStore.userInfo.avatar">
            {{ userStore.userInfo.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card primary">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ totalLinks }}</div>
              <div class="stats-label">链路总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card success">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ primaryLinks }}</div>
              <div class="stats-label">一级链路</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card warning">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><StarFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ secondaryLinks }}</div>
              <div class="stats-label">二级链路</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card info">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ totalPrice }}</div>
              <div class="stats-label">总价值(元)</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>链路类型分布</span>
              <el-button type="primary" link @click="refreshData">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <div class="chart-content">
            <div v-if="loading" class="loading-container">
              <el-skeleton :rows="5" animated />
            </div>
            <div v-else ref="chartRef" style="width:100%;height:300px;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近添加的链路</span>
            </div>
          </template>
          <div class="recent-links">
            <div v-if="loading" class="loading-container">
              <el-skeleton :rows="3" animated />
            </div>
            <div v-else-if="recentLinks.length === 0" class="empty-container">
              <el-empty description="暂无数据" />
            </div>
            <div v-else class="link-list">
              <div 
                v-for="link in recentLinks" 
                :key="link.id" 
                class="link-item"
              >
                <div class="link-info">
                  <div class="link-name">
                    {{ link.vpsProvider }} - {{ link.linkType === 'PRIMARY' ? '一级链路' : '二级链路' }}
                  </div>
                  <div class="link-meta">
                    <span>采购者: {{ link.purchaser }}</span>
                    <span>价格: ¥{{ link.price }}</span>
                  </div>
                </div>
                <div class="link-status">
                  <el-tag :type="link.linkType === 'PRIMARY' ? 'success' : 'warning'" size="small">
                    {{ link.linkType === 'PRIMARY' ? '一级' : '二级' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useUserStore } from '../stores/user'
import { getLinkStats, getLinkList } from '../api/link'
import { Connection, Star, StarFilled, Money, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const userStore = useUserStore()

const loading = ref(false)
const totalLinks = ref(0)
const primaryLinks = ref(0)
const secondaryLinks = ref(0)
const totalPrice = ref(0)
const recentLinks = ref([])
const chartRef = ref(null)
let chartInstance = null
const isTestMode = import.meta.env.VITE_APP_TEST_MODE === 'true'

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const renderChart = () => {
  if (!chartRef.value) return
  // 销毁旧实例，防止重复初始化
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  chartInstance = echarts.init(chartRef.value)
  const option = {
    title: { text: '链路类型分布', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 10, left: 'center' },
    series: [
      {
        name: '链路类型',
        type: 'pie',
        radius: '50%',
        data: [
          { value: primaryLinks.value, name: '一级链路' },
          { value: secondaryLinks.value, name: '二级链路' }
        ]
      }
    ]
  }
  chartInstance.setOption(option)
}

const fetchData = async () => {
  try {
    loading.value = true
    // Fetch stats
    const statsRes = await getLinkStats()
    const statsData = statsRes.data
    totalLinks.value = statsData.totalLinks
    primaryLinks.value = statsData.primaryLinks
    secondaryLinks.value = statsData.secondaryLinks
    totalPrice.value = (statsData.totalPrice || 0).toFixed(2)

    // Fetch recent links
    const recentRes = await getLinkList({ page: 1, size: 5, sortField: 'purchase_time', sortOrder: 'desc' })
    recentLinks.value = recentRes.data.content

  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  } finally {
    loading.value = false
    await nextTick()
    renderChart()
  }
}

const refreshData = () => {
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}

.welcome-content h2 {
  margin: 0 0 8px 0;
  color: #409eff;
  font-size: 24px;
  font-weight: 600;
}

.welcome-content p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.user-avatar {
  display: flex;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.stats-card.primary .stats-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-card.success .stats-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stats-card.warning .stats-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-card.danger .stats-icon {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.stats-label {
  font-size: 14px;
  color: #666;
}

.recent-links-card {
  margin-bottom: 20px;
}

.recent-links-card .el-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.recent-links-table {
  margin-top: 10px;
}

.recent-links-table .el-table {
  font-size: 14px;
}

.recent-links-table .el-table th {
  background-color: #f8f9fa;
  color: #333;
  font-weight: 600;
}

.recent-links-table .el-table td {
  padding: 12px 0;
}

.link-type-tag {
  font-size: 12px;
}

.expired {
  color: #f56c6c;
  font-weight: 500;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.no-data .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-data p {
  margin: 0;
  font-size: 16px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #ebeef5;
  padding: 15px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.chart-content {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-container {
  width: 100%;
  padding: 20px;
}

.chart-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.recent-links {
  height: 300px;
  overflow-y: auto;
}

.empty-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.link-list {
  padding: 0;
}

.link-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.link-item:hover {
  background-color: #f5f7fa;
}

.link-item:last-child {
  border-bottom: none;
}

.link-info {
  flex: 1;
}

.link-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.link-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 16px;
}

.link-status {
  margin-left: 16px;
}
</style> 