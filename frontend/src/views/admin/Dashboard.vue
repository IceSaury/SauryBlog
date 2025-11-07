<template>
  <div class="dashboard-page">
    <!-- 赛博朋克背景装饰 -->
    <div class="cyber-bg">
      <div class="glow-orb glow-orb-1"></div>
      <div class="glow-orb glow-orb-2"></div>
      <div class="glow-orb glow-orb-3"></div>
    </div>

    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-decoration">
        <span class="deco-line"></span>
        <span class="deco-dot"></span>
        <span class="deco-line"></span>
      </div>
      <h1 class="page-title glitch" data-text="数据统计展示大屏">数据统计展示大屏</h1>
      <div class="header-subtitle">
        <span class="subtitle-bracket">[</span>
        <span class="subtitle-text">REAL-TIME DATA VISUALIZATION DASHBOARD</span>
        <span class="subtitle-bracket">]</span>
      </div>
      <div class="header-line"></div>
      <div class="header-stats">
        <div class="header-stat-item">
          <span class="stat-label">SYSTEM</span>
          <span class="stat-value stat-blink">ONLINE</span>
        </div>
        <div class="header-stat-item">
          <span class="stat-label">STATUS</span>
          <span class="stat-value">ACTIVE</span>
        </div>
        <div class="header-stat-item">
          <span class="stat-label">VERSION</span>
          <span class="stat-value">2.0.1</span>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading">
        <Loading />
      </el-icon>
      <span>数据加载中...</span>
    </div>

    <template v-else>
      <!-- 核心统计卡片 -->
      <div class="stats-grid">
        <div
          v-for="(stat, index) in statsCards"
          :key="stat.title"
          class="stat-card"
          :style="{ 
            animationDelay: `${index * 0.1}s`,
            '--card-color': stat.color 
          }"
        >
          <div class="card-content">
            <div class="card-header">
              <el-icon class="card-icon" :style="{ color: stat.color }">
                <component :is="stat.icon" />
              </el-icon>
              <div class="card-trend" v-if="stat.trend !== undefined">
                <el-icon :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
                  <CaretTop v-if="stat.trend > 0" />
                  <CaretBottom v-else />
                </el-icon>
                <span>{{ Math.abs(stat.trend).toFixed(1) }}%</span>
              </div>
            </div>
            
            <div class="card-body">
              <h3 class="card-title">{{ stat.title }}</h3>
              <div class="card-value" :style="{ color: stat.color }">
                <span class="value-number">{{ stat.value }}</span>
                <span class="value-label" v-if="stat.label">{{ stat.label }}</span>
              </div>
            </div>

            <div class="card-footer">
              <span class="footer-text">{{ stat.description }}</span>
            </div>
          </div>

          <div class="card-bg" :style="{ background: `radial-gradient(circle at 50% 50%, ${stat.color}15, transparent 70%)` }"></div>
          <div class="card-glow" :style="{ background: stat.color }"></div>
        </div>
      </div>

      <!-- 快速操作区 -->
      <div class="quick-actions-section">
        <div class="section-header">
          <div class="section-title">
            <el-icon class="title-icon"><Lightning /></el-icon>
            <span>快速操作</span>
            <div class="title-line"></div>
          </div>
        </div>
        <div class="actions-grid">
          <div
            v-for="action in quickActions"
            :key="action.title"
            class="action-card"
            :style="{ '--action-color': action.color }"
            @click="handleAction(action.route)"
          >
            <div class="action-glow"></div>
            <el-icon class="action-icon">
              <component :is="action.icon" />
            </el-icon>
            <div class="action-content">
              <h4 class="action-title">{{ action.title }}</h4>
              <p class="action-desc">{{ action.description }}</p>
            </div>
            <el-icon class="action-arrow">
              <ArrowRight />
            </el-icon>
            <div class="action-border"></div>
          </div>
        </div>
      </div>

      <!-- 数据可视化图表区 -->
      <div class="charts-section">
        <!-- 第一行图表 -->
        <div class="charts-row">
          <!-- 访问量趋势图 -->
          <div class="chart-card large">
            <div class="chart-header">
              <h3 class="chart-title">
                <el-icon><TrendCharts /></el-icon>
                访问量趋势
              </h3>
              <span class="chart-subtitle">最近7天</span>
            </div>
            <v-chart ref="viewTrendChart" class="chart" :option="viewTrendOption" autoresize />
          </div>

          <!-- 分类统计 -->
          <div class="chart-card medium">
            <div class="chart-header">
              <h3 class="chart-title">
                <el-icon><PieChart /></el-icon>
                分类统计
              </h3>
              <span class="chart-subtitle">文章分布</span>
            </div>
            <v-chart ref="categoryChart" class="chart" :option="categoryOption" autoresize />
          </div>
        </div>

        <!-- 第二行图表 -->
        <div class="charts-row">
          <!-- 标签云 -->
          <div class="chart-card medium">
            <div class="chart-header">
              <h3 class="chart-title">
                <el-icon><Grid /></el-icon>
                热门标签
              </h3>
              <span class="chart-subtitle">Top 10</span>
            </div>
            <v-chart ref="tagChart" class="chart" :option="tagOption" autoresize />
          </div>

          <!-- 文章浏览量排行 -->
          <div class="chart-card large">
            <div class="chart-header">
              <h3 class="chart-title">
                <el-icon><DataLine /></el-icon>
                文章浏览排行
              </h3>
              <span class="chart-subtitle">Top 10</span>
            </div>
            <v-chart ref="articleRankChart" class="chart" :option="articleRankOption" autoresize />
          </div>
        </div>

        <!-- 第三行图表 -->
        <div class="charts-row">
          <!-- 文章状态统计 -->
          <div class="chart-card small">
            <div class="chart-header">
              <h3 class="chart-title">
                <el-icon><Document /></el-icon>
                文章状态
              </h3>
            </div>
            <v-chart ref="articleStatusChart" class="chart" :option="articleStatusOption" autoresize />
          </div>

          <!-- 评论状态统计 -->
          <div class="chart-card small">
            <div class="chart-header">
              <h3 class="chart-title">
                <el-icon><ChatDotRound /></el-icon>
                评论状态
              </h3>
            </div>
            <v-chart ref="commentStatusChart" class="chart" :option="commentStatusOption" autoresize />
          </div>

        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Document, 
  Folder, 
  ChatDotRound, 
  View,
  CaretTop,
  CaretBottom,
  Lightning,
  Setting,
  Upload,
  EditPen,
  ArrowRight,
  Loading,
  TrendCharts,
  PieChart,
  Grid,
  DataLine,
  User
} from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { 
  LineChart, 
  PieChart as EPieChart, 
  BarChart,
  RadarChart
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent
} from 'echarts/components'
import { getDashboardStats, type DashboardStats } from '@/api/dashboard'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  EPieChart,
  BarChart,
  RadarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent
])

const router = useRouter()
const loading = ref(true)
const dashboardData = ref<DashboardStats | null>(null)

// 统计卡片数据
const statsCards = computed(() => {
  if (!dashboardData.value) return []
  
  const data = dashboardData.value
  return [
    {
      title: '文章总数',
      value: data.articleCount,
      label: '篇',
      description: '已发布文章',
      icon: Document,
      color: '#00F0FF',
      trend: data.articleTrend
    },
    {
      title: '项目总数',
      value: data.projectCount,
      label: '个',
      description: '在线项目',
      icon: Folder,
      color: '#FFB7D4',
      trend: data.projectTrend
    },
    {
      title: '访问量',
      value: formatNumber(data.totalViews),
      label: '',
      description: '总访问次数',
      icon: View,
      color: '#A200FF',
      trend: data.viewTrend
    },
    {
      title: '评论数',
      value: data.commentCount,
      label: '条',
      description: '用户互动',
      icon: ChatDotRound,
      color: '#FFED00',
      trend: data.commentTrend
    },
    {
      title: '用户总数',
      value: data.userCount,
      label: '人',
      description: '注册用户',
      icon: User,
      color: '#00FF88',
      trend: undefined
    },
    {
      title: '留言总数',
      value: data.messageCount,
      label: '条',
      description: '访客留言',
      icon: ChatDotRound,
      color: '#FF00AA',
      trend: undefined
    }
  ]
})

// 快速操作
const quickActions = ref([
  {
    title: '发布文章',
    description: 'CREATE NEW ARTICLE',
    icon: EditPen,
    color: '#00F0FF',
    route: '/admin/articles'
  },
  {
    title: '上传项目',
    description: 'UPLOAD PROJECT',
    icon: Upload,
    color: '#FFB7D4',
    route: '/admin/projects'
  },
  {
    title: '网站配置',
    description: 'SITE SETTINGS',
    icon: Setting,
    color: '#A200FF',
    route: '/admin/config'
  }
])

// 访问量趋势图配置
const viewTrendOption = computed(() => {
  if (!dashboardData.value) return {}
  
  const dates = dashboardData.value.dailyViewStats.map(item => item.date)
  const counts = dashboardData.value.dailyViewStats.map(item => item.count)
  
  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 15, 30, 0.9)',
      borderColor: '#00F0FF',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#00F0FF33' } },
      axisLabel: { color: '#00F0FF' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { lineStyle: { color: '#00F0FF33' } },
      axisLabel: { color: '#00F0FF' },
      splitLine: { lineStyle: { color: '#00F0FF11' } }
    },
    series: [{
      data: counts,
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#00F0FF',
        width: 3
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 240, 255, 0.4)' },
            { offset: 1, color: 'rgba(0, 240, 255, 0.05)' }
          ]
        }
      },
      itemStyle: {
        color: '#00F0FF',
        borderColor: '#fff',
        borderWidth: 2
      }
    }]
  }
})

// 分类统计图配置
const categoryOption = computed(() => {
  if (!dashboardData.value) return {}
  
  const data = dashboardData.value.categoryStats.map(item => ({
    value: item.count,
    name: item.name
  }))
  
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 15, 30, 0.9)',
      borderColor: '#FFB7D4',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    legend: {
      bottom: '5%',
      left: 'center',
      textStyle: { color: '#fff' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#000',
        borderWidth: 2
      },
      label: {
        show: true,
        color: '#fff',
        formatter: '{b}: {c}'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      data: data,
      color: ['#00F0FF', '#FFB7D4', '#A200FF', '#FFED00', '#00FF88', '#FF00AA']
    }]
  }
})

// 标签统计图配置
const tagOption = computed(() => {
  if (!dashboardData.value) return {}
  
  const data = dashboardData.value.tagStats.map(item => ({
    name: item.name,
    value: item.count
  }))
  
  return {
    tooltip: {
      backgroundColor: 'rgba(0, 15, 30, 0.9)',
      borderColor: '#A200FF',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    series: [{
      type: 'pie',
      radius: '70%',
      roseType: 'area',
      itemStyle: {
        borderRadius: 8
      },
      label: {
        color: '#fff'
      },
      data: data,
      color: ['#00F0FF', '#FFB7D4', '#A200FF', '#FFED00', '#00FF88', '#FF00AA', '#FF6B6B', '#4ECDC4', '#FFA07A', '#98D8C8']
    }]
  }
})

// 文章浏览量排行图配置
const articleRankOption = computed(() => {
  if (!dashboardData.value) return {}
  
  // 后端返回的是降序（大到小），横向柱状图需要反转才能让大数显示在上方
  const titles = dashboardData.value.topViewedArticles.map(item => 
    item.title.length > 20 ? item.title.substring(0, 20) + '...' : item.title
  ).reverse()
  const views = dashboardData.value.topViewedArticles.map(item => item.viewCount).reverse()
  
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(0, 15, 30, 0.9)',
      borderColor: '#00F0FF',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#00F0FF33' } },
      axisLabel: { color: '#00F0FF' },
      splitLine: { lineStyle: { color: '#00F0FF11' } }
    },
    yAxis: {
      type: 'category',
      data: titles,
      axisLine: { lineStyle: { color: '#00F0FF33' } },
      axisLabel: { color: '#00F0FF' }
    },
    series: [{
      type: 'bar',
      data: views,
      itemStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 1,
          y2: 0,
          colorStops: [
            { offset: 0, color: 'rgba(0, 240, 255, 0.5)' },
            { offset: 1, color: 'rgba(0, 240, 255, 1)' }
          ]
        },
        borderRadius: [0, 5, 5, 0]
      }
    }]
  }
})

// 文章状态统计图配置
const articleStatusOption = computed(() => {
  if (!dashboardData.value) return {}
  
  const data = Object.entries(dashboardData.value.articleStatusStats).map(([name, value]) => ({
    name,
    value
  }))
  
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 15, 30, 0.9)',
      borderColor: '#00F0FF',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    legend: {
      bottom: '5%',
      textStyle: { color: '#fff' }
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: data,
      color: ['#FFB7D4', '#00FF88'],
      label: {
        color: '#fff',
        formatter: '{b}: {c}'
      }
    }]
  }
})

// 评论状态统计图配置
const commentStatusOption = computed(() => {
  if (!dashboardData.value) return {}
  
  const data = Object.entries(dashboardData.value.commentStatusStats).map(([name, value]) => ({
    name,
    value
  }))
  
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 15, 30, 0.9)',
      borderColor: '#A200FF',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    legend: {
      bottom: '5%',
      textStyle: { color: '#fff' }
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: data,
      color: ['#00FF88', '#FF6B6B'],
      label: {
        color: '#fff',
        formatter: '{b}: {c}'
      }
    }]
  }
})

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 处理快速操作点击
const handleAction = (route: string) => {
  router.push(route)
}

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    loading.value = true
    const res = await getDashboardStats()
    dashboardData.value = res.data
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: 100vh;
  padding: 24px;
  position: relative;
  background: #0a0e27;
  overflow-x: hidden;
  
  > * {
    position: relative;
    z-index: 10;
  }
}

// 赛博朋克背景
.cyber-bg {
  position: fixed;
  top: 0;
  left: 250px;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
  
  // 渐变背景层
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: 
      radial-gradient(ellipse at 20% 30%, rgba(#00B4FF, 0.1) 0%, transparent 50%),
      radial-gradient(ellipse at 80% 70%, rgba(#0066FF, 0.08) 0%, transparent 50%),
      radial-gradient(ellipse at 50% 50%, rgba(#4169E1, 0.06) 0%, transparent 60%);
    animation: gradientPulse 15s ease-in-out infinite;
  }
  
  // 动态粒子层
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background-image: 
      radial-gradient(2px 2px at 20% 30%, rgba(#00D4FF, 0.5), transparent),
      radial-gradient(2px 2px at 60% 70%, rgba(#5B7EFF, 0.4), transparent),
      radial-gradient(1px 1px at 50% 50%, rgba(#0080FF, 0.45), transparent),
      radial-gradient(1px 1px at 80% 10%, rgba(#00E5FF, 0.4), transparent),
      radial-gradient(2px 2px at 90% 60%, rgba(#4169E1, 0.35), transparent),
      radial-gradient(1px 1px at 33% 80%, rgba(#00B4FF, 0.4), transparent),
      radial-gradient(2px 2px at 15% 90%, rgba(#6B5EFF, 0.35), transparent);
    background-size: 200% 200%;
    background-position: 0% 0%;
    opacity: 0.6;
    animation: particleFloat 20s ease-in-out infinite;
  }
}

// 发光球体 - 蓝色系增强版
.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.18;
  animation: float 12s ease-in-out infinite;
  
  &.glow-orb-1 {
    width: 450px;
    height: 450px;
    background: radial-gradient(circle, #00D4FF, #0080FF, transparent);
    top: 15%;
    left: 5%;
    animation-delay: 0s;
    box-shadow: 
      0 0 80px rgba(#00D4FF, 0.3),
      0 0 150px rgba(#00D4FF, 0.15);
  }
  
  &.glow-orb-2 {
    width: 550px;
    height: 550px;
    background: radial-gradient(circle, #4169E1, #0066FF, transparent);
    top: 45%;
    right: 5%;
    animation-delay: 3s;
    box-shadow: 
      0 0 90px rgba(#4169E1, 0.3),
      0 0 160px rgba(#4169E1, 0.15);
  }
  
  &.glow-orb-3 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, #5B7EFF, #00B4FF, transparent);
    bottom: 15%;
    left: 45%;
    animation-delay: 6s;
    box-shadow: 
      0 0 80px rgba(#5B7EFF, 0.3),
      0 0 150px rgba(#5B7EFF, 0.15);
  }
}

.page-header {
  margin-bottom: 40px;
  animation: slideInDown 0.6s ease-out;
  text-align: center;
  position: relative;
  padding: 40px 24px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 92%;
    height: 100%;
    background: rgba($bg-card, 0.4);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border-radius: 24px;
    border: 1px solid rgba($neon-cyan, 0.25);
    z-index: -1;
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.3),
      inset 0 1px 1px rgba(255, 255, 255, 0.1),
      0 0 60px rgba($neon-cyan, 0.1);
  }
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 70%;
    height: 2px;
    background: linear-gradient(90deg, transparent, $neon-cyan, $neon-purple, transparent);
    box-shadow: 0 0 15px rgba($neon-cyan, 0.5);
  }
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
  
  .deco-line {
    width: 80px;
    height: 2px;
    background: linear-gradient(90deg, transparent, $neon-cyan, transparent);
    box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
  }
  
  .deco-dot {
    width: 10px;
    height: 10px;
    background: $neon-cyan;
    border-radius: 50%;
    box-shadow: 
      0 0 15px $neon-cyan,
      0 0 25px $neon-cyan;
    animation: softPulse 3s ease-in-out infinite;
  }
}

.page-title {
  font-size: 64px;
  font-weight: 900;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  margin: 0 0 24px 0;
  letter-spacing: 8px;
  position: relative;
  
  &.glitch::before,
  &.glitch::after {
    content: attr(data-text);
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  &.glitch::before {
    left: 2px;
    text-shadow: -2px 0 $neon-magenta;
    animation: glitch-anim-1 2s infinite linear alternate-reverse;
  }

  &.glitch::after {
    left: -2px;
    text-shadow: 2px 0 $neon-purple;
    animation: glitch-anim-2 3s infinite linear alternate-reverse;
  }
}

.header-subtitle {
  font-size: 13px;
  color: $text-secondary;
  letter-spacing: 3px;
  margin-bottom: 16px;
  font-family: $font-family-code;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  .subtitle-bracket {
    color: $neon-cyan;
    font-size: 16px;
    font-weight: bold;
  }
  
  .subtitle-text {
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, $neon-cyan, transparent);
    }
  }
}

.header-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-top: 20px;
}

.header-stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: $font-family-code;
  font-size: 12px;
  
  .stat-label {
    color: $text-disabled;
    letter-spacing: 2px;
  }
  
  .stat-value {
    color: $neon-cyan;
    font-weight: 700;
    letter-spacing: 1px;
    padding: 4px 12px;
    background: rgba($neon-cyan, 0.15);
    backdrop-filter: blur(10px);
    border: 1px solid rgba($neon-cyan, 0.4);
    border-radius: 6px;
    box-shadow: 
      0 0 10px rgba($neon-cyan, 0.3),
      inset 0 1px 1px rgba(255, 255, 255, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba($neon-cyan, 0.25);
      box-shadow: 
        0 0 20px rgba($neon-cyan, 0.5),
        inset 0 1px 1px rgba(255, 255, 255, 0.2);
    }
    
    &.stat-blink {
      animation: softPulse 2.5s ease-in-out infinite;
    }
  }
}

.header-line {
  height: 3px;
  width: 300px;
  margin: 0 auto;
  background: linear-gradient(90deg, transparent, $neon-cyan, $neon-purple, transparent);
  box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
  animation: lineGrow 1s ease-out;
}

// 加载状态
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 100px;
  font-size: 24px;
  color: $neon-cyan;
  
  .el-icon {
    font-size: 48px;
  }
}

// 统计卡片网格
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  position: relative;
  background: rgba($bg-card, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 28px;
  overflow: hidden;
  border: 2px solid rgba($neon-cyan, 0.2);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out backwards;
  cursor: pointer;
  
  // 闪光效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(#fff, 0.08), transparent);
    transition: left 0.6s;
  }
  
  &:hover {
    transform: translateY(-5px) scale(1.02);
    border-color: var(--card-color);
    
    &::before {
      left: 100%;
    }
    
    .card-bg {
      opacity: 0.6;
      transform: scale(1.2);
    }
    
    .card-glow {
      opacity: 0.25;
      transform: scale(1.4);
    }
    
    .card-icon {
      transform: scale(1.15) rotate(10deg);
      filter: drop-shadow(0 0 15px currentColor) drop-shadow(0 0 25px currentColor);
    }
    
    .card-value .value-number {
      text-shadow: 
        0 0 20px currentColor,
        0 0 35px currentColor;
    }
  }
}

.card-content {
  position: relative;
  z-index: 2;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 48px;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  filter: drop-shadow(0 0 10px currentColor) drop-shadow(0 0 20px currentColor);
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba($bg-secondary, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  font-family: $font-family-code;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  
  .trend-up {
    color: #00FF88;
    filter: drop-shadow(0 0 6px #00FF88);
  }
  
  .trend-down {
    color: #FF4444;
    filter: drop-shadow(0 0 6px #FF4444);
  }
}

.card-body {
  margin-bottom: 12px;
}

.card-title {
  font-size: 14px;
  color: $text-secondary;
  margin: 0 0 10px 0;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.card-value {
  display: flex;
  align-items: baseline;
  gap: 8px;
  
  .value-number {
    font-size: 42px;
    font-weight: 900;
    line-height: 1;
    text-shadow: 
      0 0 15px currentColor,
      0 0 30px currentColor;
    font-family: $font-family-code;
  }
  
  .value-label {
    font-size: 18px;
    font-weight: 700;
    opacity: 0.9;
    text-shadow: 0 0 8px currentColor;
  }
}

.card-footer {
  padding-top: 12px;
  border-top: 1px solid rgba($neon-cyan, 0.1);
}

.footer-text {
  font-size: 12px;
  color: $text-disabled;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.4;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  pointer-events: none;
  z-index: 0;
}

.card-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  opacity: 0.12;
  filter: blur(50px);
  transition: all 0.4s;
  pointer-events: none;
  transform: translate(-50%, -50%);
  z-index: 0;
}

// 图表区域
.charts-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 20px;
}

.chart-card {
  background: rgba($bg-card, 0.7);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba($neon-cyan, 0.15);
  overflow: hidden;
  position: relative;
  animation: fadeInUp 0.6s ease-out backwards;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, transparent, $neon-cyan, transparent);
    opacity: 0;
    transition: opacity 0.4s;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 10px;
    right: 10px;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: $neon-cyan;
    box-shadow: 0 0 10px $neon-cyan;
    animation: pulse 2s ease-in-out infinite;
  }
  
  &:hover {
    border-color: rgba($neon-cyan, 0.4);
    box-shadow: 0 5px 30px rgba($neon-cyan, 0.15);
    
    &::before {
      opacity: 1;
    }
  }
  
  &.large {
    grid-column: span 7;
  }
  
  &.medium {
    grid-column: span 5;
  }
  
  &.small {
    grid-column: span 6;
  }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba($neon-cyan, 0.1);
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: $text-primary;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
  
  .el-icon {
    font-size: 20px;
    color: $neon-cyan;
  }
}

.chart-subtitle {
  font-size: 12px;
  color: $text-secondary;
  font-family: $font-family-code;
  letter-spacing: 1px;
}

.chart {
  width: 100%;
  height: 320px;
}

// 快速操作区域
.quick-actions-section {
  margin-bottom: 32px;
  animation: fadeInUp 0.6s ease-out 0.2s backwards;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 900;
  color: $neon-cyan;
  text-transform: uppercase;
  letter-spacing: 3px;
  position: relative;
  
  .title-icon {
    font-size: 28px;
    animation: pulse 2s ease-in-out infinite;
    filter: drop-shadow(0 0 10px currentColor);
  }
  
  .title-line {
    flex: 1;
    height: 2px;
    background: linear-gradient(90deg, $neon-cyan, transparent);
    box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
    margin-left: 12px;
  }
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.action-card {
  --action-color: #00F0FF;
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: rgba($bg-card, 0.7);
  backdrop-filter: blur(20px);
  border: 2px solid rgba($neon-cyan, 0.2);
  border-radius: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(#fff, 0.05), transparent);
    transition: left 0.6s;
  }
  
  &:hover {
    transform: translateY(-5px) scale(1.02);
    border-color: var(--action-color);
    
    &::before {
      left: 100%;
    }
    
    .action-glow {
      opacity: 0.3;
      transform: scale(1.5);
    }
    
    .action-icon {
      transform: scale(1.2) rotate(10deg);
    }
    
    .action-arrow {
      transform: translateX(10px);
      opacity: 1;
    }
    
    .action-border {
      opacity: 1;
    }
  }
}

.action-glow {
  position: absolute;
  top: 50%;
  left: 60px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--action-color);
  opacity: 0.1;
  filter: blur(40px);
  transition: all 0.4s;
  pointer-events: none;
  transform: translateY(-50%);
}

.action-icon {
  font-size: 48px;
  color: var(--action-color);
  transition: all 0.4s;
  filter: drop-shadow(0 0 15px var(--action-color));
  z-index: 2;
}

.action-content {
  flex: 1;
  z-index: 2;
}

.action-title {
  font-size: 18px;
  font-weight: 700;
  color: $text-primary;
  margin: 0 0 4px 0;
  letter-spacing: 1px;
}

.action-desc {
  font-size: 11px;
  color: $text-secondary;
  margin: 0;
  font-family: $font-family-code;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.action-arrow {
  font-size: 24px;
  color: var(--action-color);
  opacity: 0.5;
  transition: all 0.4s;
  z-index: 2;
}

.action-border {
  position: absolute;
  inset: 0;
  border-radius: 16px;
  padding: 2px;
  background: linear-gradient(135deg, var(--action-color), transparent);
  -webkit-mask: 
    linear-gradient(#fff 0 0) content-box, 
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask: 
    linear-gradient(#fff 0 0) content-box, 
    linear-gradient(#fff 0 0);
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s;
  pointer-events: none;
}


// 动画
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes lineGrow {
  from {
    width: 0;
  }
  to {
    width: 200px;
  }
}

@keyframes glitch-anim-1 {
  0% { clip-path: inset(40% 0 61% 0); }
  20% { clip-path: inset(92% 0 1% 0); }
  40% { clip-path: inset(43% 0 1% 0); }
  60% { clip-path: inset(25% 0 58% 0); }
  80% { clip-path: inset(54% 0 7% 0); }
  100% { clip-path: inset(58% 0 43% 0); }
}

@keyframes glitch-anim-2 {
  0% { clip-path: inset(25% 0 58% 0); }
  20% { clip-path: inset(54% 0 7% 0); }
  40% { clip-path: inset(58% 0 43% 0); }
  60% { clip-path: inset(40% 0 61% 0); }
  80% { clip-path: inset(92% 0 1% 0); }
  100% { clip-path: inset(43% 0 1% 0); }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes progressGrow {
  from {
    width: 0;
  }
}

@keyframes gradientPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

@keyframes particleFloat {
  0%, 100% {
    background-position: 0% 0%;
    opacity: 0.6;
  }
  25% {
    background-position: 100% 100%;
    opacity: 0.8;
  }
  50% {
    background-position: 100% 0%;
    opacity: 0.5;
  }
  75% {
    background-position: 0% 100%;
    opacity: 0.7;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(40px, -40px) scale(1.05);
  }
  50% {
    transform: translate(-30px, 30px) scale(0.95);
  }
  75% {
    transform: translate(30px, 40px) scale(1.02);
  }
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

// 新增呼吸灯和毛玻璃效果动画
@keyframes cardGlow {
  0%, 100% {
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.3),
      inset 0 1px 1px rgba(255, 255, 255, 0.1);
  }
  50% {
    box-shadow: 
      0 12px 40px rgba(0, 0, 0, 0.4),
      inset 0 1px 2px rgba(255, 255, 255, 0.15);
  }
}

@keyframes iconGlow {
  0%, 100% {
    filter: drop-shadow(0 0 15px currentColor) drop-shadow(0 0 30px currentColor);
  }
  50% {
    filter: drop-shadow(0 0 20px currentColor) drop-shadow(0 0 40px currentColor);
  }
}

@keyframes iconBreath {
  0%, 100% {
    filter: drop-shadow(0 0 15px currentColor) drop-shadow(0 0 30px currentColor);
  }
  50% {
    filter: drop-shadow(0 0 25px currentColor) drop-shadow(0 0 50px currentColor);
  }
}

@keyframes numberGlow {
  0%, 100% {
    text-shadow: 
      0 0 20px currentColor,
      0 0 40px currentColor,
      0 0 60px currentColor;
  }
  50% {
    text-shadow: 
      0 0 30px currentColor,
      0 0 60px currentColor,
      0 0 90px currentColor;
  }
}

@keyframes bgPulse {
  0%, 100% {
    opacity: 0.4;
  }
  50% {
    opacity: 0.6;
  }
}

@keyframes borderFlow {
  0% {
    background-position: 0% 0%;
  }
  100% {
    background-position: 200% 200%;
  }
}

@keyframes borderPulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

@keyframes rotateGlow {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes shimmerMove {
  0% {
    left: -100%;
  }
  50% {
    left: 100%;
  }
  100% {
    left: 100%;
  }
}

@keyframes trendPulse {
  0%, 100% {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  50% {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  }
}

@keyframes trendBounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-2px);
  }
}

@keyframes orbPulse {
  0%, 100% {
    opacity: 0.2;
    filter: blur(100px);
  }
  50% {
    opacity: 0.3;
    filter: blur(120px);
  }
}

@keyframes headerBreath {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.01);
  }
}

@keyframes lineFlow {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 200% 50%;
  }
}

@keyframes softPulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

// 响应式
@media (max-width: 1400px) {
  .chart-card {
    &.large,
    &.medium,
    &.small {
      grid-column: span 12;
    }
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .page-title {
    font-size: 42px;
    letter-spacing: 4px;
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 16px;
  }
  
  .cyber-bg {
    left: 0;
  }
  
  .page-header {
    padding: 20px 16px;
    
    &::before {
      width: 100%;
    }
  }
  
  .page-title {
    font-size: 28px;
    letter-spacing: 2px;
  }
  
  .header-subtitle {
    font-size: 10px;
    letter-spacing: 1px;
    flex-direction: column;
    gap: 4px;
  }
  
  .header-stats {
    flex-direction: column;
    gap: 12px;
    align-items: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .card-value .value-number {
    font-size: 28px;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .action-card {
    padding: 20px;
  }
  
  .action-icon {
    font-size: 36px;
  }
  
  .chart {
    height: 250px;
  }
  
  .chart-card {
    padding: 16px;
  }
  
  .chart-title {
    font-size: 16px;
  }
}
</style>
