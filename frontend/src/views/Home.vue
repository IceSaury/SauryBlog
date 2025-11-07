<template>
  <div class="home-page">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="avatar-container">
          <div class="avatar-glow"></div>
          <!-- 头像图片 https://saury-blog.oss-cn-shenzhen.aliyuncs.com/%E5%A4%B4%E5%83%8F.png-->
          <!-- https://saury-blog.oss-cn-shenzhen.aliyuncs.com/%E9%87%91%E5%85%8B%E6%96%AF -->
          <img src="https://saury-blog.oss-cn-shenzhen.aliyuncs.com/%E5%A4%B4%E5%83%8F.png" alt="Saury" class="avatar" />
        </div>
        <h1 class="hero-title glitch" data-text="SAURY">SAURY</h1>
        <div class="typing-text">
          <span ref="typingRef" class="typing-content"></span>
          <span class="cursor">|</span>
        </div>
        <div class="hero-tags">
          <CyberTag color="cyan">Java</CyberTag>
          <CyberTag color="magenta">Spring Boot</CyberTag>
          <CyberTag color="purple">Vue.js</CyberTag>
          <CyberTag color="yellow">TypeScript</CyberTag>
          <CyberTag color="green">微服务</CyberTag>
        </div>
        <div class="hero-actions">
          <CyberButton type="cyan" @click="router.push('/blog')">
            查看博客
          </CyberButton>
          <CyberButton type="magenta" @click="router.push('/projects')">
            项目展示
          </CyberButton>
        </div>
      </div>
    </section>

    <!-- 数据统计 -->
    <section class="stats-section">
      <div class="stats-grid">
        <CyberCard v-for="stat in stats" :key="stat.label" class="stat-card">
          <div class="stat-icon">{{ stat.icon }}</div>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </CyberCard>
      </div>
    </section>

    <!-- 推荐文章 -->
    <section class="featured-section">
      <h2 class="section-title">
        <span class="title-text">推荐文章</span>
        <div class="neon-line"></div>
      </h2>
      <div class="articles-grid">
        <CyberCard
          v-for="article in featuredArticles"
          :key="article.id"
          class="article-card"
          @click="router.push(`/blog/${article.id}`)"
        >
          <div class="article-cover">
            <img :src="article.cover" :alt="article.title" />
          </div>
          <div class="article-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary }}</p>
            <div class="article-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ article.viewCount }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(article.createTime) }}
              </span>
            </div>
          </div>
        </CyberCard>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { View, Clock } from '@element-plus/icons-vue'
import CyberButton from '@/components/CyberButton.vue'
import CyberCard from '@/components/CyberCard.vue'
import CyberTag from '@/components/CyberTag.vue'
import { getDashboardStats } from '@/api/dashboard'
import { getArticlePage } from '@/api/article'

const router = useRouter()
const typingRef = ref<HTMLElement>()
let typingInterval: number | null = null

// 统计数据
const stats = ref([
  { icon: '📝', value: '0', label: '文章数' },
  { icon: '🚀', value: '0', label: '项目数' },
  { icon: '👁️', value: '0', label: '访问量' },
  { icon: '💬', value: '0', label: '评论数' },
])

// 推荐文章
const featuredArticles = ref<any[]>([])

// 格式化数字（大数字转为 K 格式）
const formatNumber = (num: number): string => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.value = [
        { icon: '📝', value: formatNumber(res.data.articleCount), label: '文章数' },
        { icon: '🚀', value: formatNumber(res.data.projectCount), label: '项目数' },
        { icon: '👁️', value: formatNumber(res.data.totalViews), label: '访问量' },
        { icon: '💬', value: formatNumber(res.data.commentCount), label: '评论数' },
      ]
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载推荐文章（按访问量排序，取前3篇）
const loadFeaturedArticles = async () => {
  try {
    const res = await getArticlePage({
      pageNum: 1,
      pageSize: 3,
      status: 1, // 只查询已发布的文章
      sortBy: 'view_count', // 按访问量排序
      sortOrder: 'desc'
    })
    if (res.data && res.data.records) {
      featuredArticles.value = res.data.records
    }
  } catch (error) {
    console.error('加载推荐文章失败:', error)
  }
}

onMounted(() => {
  // 加载统计数据
  loadStats()
  
  // 加载推荐文章
  loadFeaturedArticles()
  
  // 打字机效果
  const text = 'Java开发工程师 | 全栈开发者 | 技术分享者'
  let index = 0
  
  typingInterval = window.setInterval(() => {
    if (typingRef.value && index < text.length) {
      typingRef.value.textContent += text[index]
      index++
    } else {
      // 打字完成后清除定时器
      if (typingInterval !== null) {
        clearInterval(typingInterval)
        typingInterval = null
      }
    }
  }, 100)
})

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  if (typingInterval !== null) {
    clearInterval(typingInterval)
    typingInterval = null
  }
})

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN')
}
</script>

<style lang="scss" scoped>
.home-page {
  padding: 0;
  min-height: 100vh;
  background: transparent;
}

.hero-section {
  min-height: calc(100vh - $header-height - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  position: relative;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.avatar-container {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 40px;
}

.avatar-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: $gradient-cyber;
  opacity: 0.3;
  filter: blur(20px);
  animation: pulse 3s infinite;
}

.avatar {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 3px solid $neon-cyan;
  box-shadow: $shadow-neon-cyan;
  object-fit: cover;
}

.hero-title {
  font-size: 72px;
  font-weight: 900;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  margin-bottom: 24px;
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

.typing-text {
  font-size: 24px;
  color: $text-primary;
  margin-bottom: 32px;
  min-height: 40px;

  .cursor {
    color: $neon-cyan;
    animation: blink 1s infinite;
  }
}

.hero-tags {
  margin-bottom: 40px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.stats-section {
  padding: 60px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.stat-card {
  text-align: center;
}

.stat-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: $neon-cyan;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: $text-secondary;
}

.featured-section {
  padding: 60px 0;
}

.section-title {
  text-align: center;
  margin-bottom: 48px;

  .title-text {
    font-size: 36px;
    font-weight: 700;
    color: $neon-cyan;
    text-shadow: $shadow-neon-cyan;
  }
}

.neon-line {
  width: 200px;
  height: 3px;
  margin: 16px auto 0;
  background: $gradient-cyber;
  box-shadow: $shadow-neon-cyan;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.article-card {
  cursor: pointer;
}

.article-cover {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .article-card:hover & img {
    transform: scale(1.1);
  }
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  font-size: 14px;
  color: $text-secondary;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: $text-disabled;
  font-size: 13px;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 0.5;
  }
}

@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
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
</style>

