<template>
  <div class="blog-page">
    <h1 class="page-title">技术博客</h1>
    
    <div class="blog-content">
      <!-- 搜索栏 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章..."
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <cyber-button @click="handleSearch" style="margin-left: 12px">
          搜索
        </cyber-button>
      </div>

      <!-- 分类筛选 -->
      <div class="filter-section">
        <cyber-tag
          :color="selectedCategory === null ? '#00F0FF' : undefined"
          :class="{ active: selectedCategory === null }"
          @click="selectCategory(null)"
        >
          全部
        </cyber-tag>
        <cyber-tag
          v-for="cat in categories"
          :key="cat.id"
          :class="{ active: selectedCategory === cat.id }"
          @click="selectCategory(cat.id!)"
        >
          {{ cat.icon }} {{ cat.name }} ({{ cat.articleCount }})
        </cyber-tag>
      </div>

      <!-- 文章列表 -->
      <div v-loading="loading" class="articles-list">
        <cyber-card
          v-for="article in articles"
          :key="article.id"
          class="article-item"
          @click="router.push(`/blog/${article.id}`)"
        >
          <div class="article-header">
            <div v-if="article.cover" class="article-cover">
              <img :src="article.cover" :alt="article.title" />
            </div>
            <div class="article-info">
              <div class="article-badges">
                <el-tag v-if="article.isTop === 1" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="article.isFeatured === 1" type="warning" size="small">推荐</el-tag>
              </div>
              <h2 class="article-title">{{ article.title }}</h2>
              <p class="article-summary">{{ article.summary }}</p>
              <div class="article-tags">
                <cyber-tag
                  v-for="tag in article.tags"
                  :key="tag.id"
                  :color="tag.color"
                  size="small"
                >
                  {{ tag.name }}
                </cyber-tag>
              </div>
              <div class="article-meta">
                <span><el-icon><User /></el-icon> {{ article.authorNickname }}</span>
                <span><el-icon><Calendar /></el-icon> {{ article.createTime }}</span>
                <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
                <span><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
              </div>
            </div>
          </div>
        </cyber-card>

        <!-- 空状态 -->
        <div v-if="!loading && articles.length === 0" class="empty-state">
          <el-empty description="暂无文章" />
        </div>

        <!-- 分页 -->
        <el-pagination
          v-if="articles.length > 0"
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getArticles"
          @current-change="getArticles"
          class="pagination"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, User, Calendar, View, ChatDotRound } from '@element-plus/icons-vue'
import { getArticlePage } from '@/api/article'
import { getCategoryList } from '@/api/category'
import type { Article, Category } from '@/types'
import CyberCard from '@/components/CyberCard.vue'
import CyberTag from '@/components/CyberTag.vue'
import CyberButton from '@/components/CyberButton.vue'

const router = useRouter()

const searchKeyword = ref('')
const selectedCategory = ref<number | null>(null)
const categories = ref<Category[]>([])
const articles = ref<Article[]>([])
const loading = ref(false)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取文章列表
const getArticles = async () => {
  loading.value = true
  try {
    const res = await getArticlePage({
      page: pagination.page,
      size: pagination.size,
      categoryId: selectedCategory.value,
      keyword: searchKeyword.value,
      status: 1 // 只查询已发布的文章
    })
    articles.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择分类
const selectCategory = (id: number | null) => {
  selectedCategory.value = id
  pagination.page = 1
  getArticles()
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getArticles()
}

// 初始化
onMounted(() => {
  getCategories()
  getArticles()
})
</script>

<style lang="scss" scoped>
.page-title {
  font-size: 48px;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  text-align: center;
  margin-bottom: 48px;
  animation: neon-glow 1.5s ease-in-out infinite alternate;
}

@keyframes neon-glow {
  from {
    text-shadow: 0 0 10px $neon-cyan, 0 0 20px $neon-cyan;
  }
  to {
    text-shadow: 0 0 20px $neon-cyan, 0 0 30px $neon-cyan, 0 0 40px $neon-cyan;
  }
}

.blog-content {
  max-width: 1200px;
  margin: 0 auto;
}

.search-section {
  margin-bottom: 32px;
  display: flex;
  justify-content: center;
  align-items: center;

  .el-input {
    max-width: 500px;
  }
}

.filter-section {
  margin-bottom: 32px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;

  :deep(.cyber-tag) {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba($neon-cyan, 0.4);
    }

    &.active {
      background: linear-gradient(135deg, $neon-cyan 0%, $neon-purple 100%);
      box-shadow: 0 0 20px rgba($neon-cyan, 0.6);
    }
  }
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-height: 400px;
}

.article-item {
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba($neon-cyan, 0.4);
  }
}

.article-header {
  display: flex;
  gap: 20px;
}

.article-cover {
  flex-shrink: 0;
  width: 200px;
  height: 150px;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid rgba($neon-cyan, 0.3);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;

    &:hover {
      transform: scale(1.1);
    }
  }
}

.article-info {
  flex: 1;
}

.article-badges {
  margin-bottom: 8px;
  
  .el-tag {
    margin-right: 8px;
  }
}

.article-title {
  font-size: 24px;
  color: $neon-cyan;
  margin-bottom: 12px;
  transition: color 0.3s;

  &:hover {
    color: $neon-magenta;
  }
}

.article-summary {
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-tags {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: $text-secondary;
  font-size: 14px;

  span {
    display: flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      color: $neon-cyan;
    }
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .article-header {
    flex-direction: column;
  }

  .article-cover {
    width: 100%;
    height: 200px;
  }

  .article-meta {
    flex-wrap: wrap;
    gap: 12px;
  }
}
</style>
