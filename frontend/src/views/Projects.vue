<template>
  <div class="projects-page">
    <h1 class="page-title">项目展示</h1>
    
    <!-- 类型筛选 -->
    <div class="filter-section">
      <cyber-tag
        :class="{ active: selectedType === null }"
        @click="selectType(null)"
      >
        全部
      </cyber-tag>
      <cyber-tag
        :class="{ active: selectedType === 0 }"
        @click="selectType(0)"
      >
        企业项目
      </cyber-tag>
      <cyber-tag
        :class="{ active: selectedType === 1 }"
        @click="selectType(1)"
      >
        个人项目
      </cyber-tag>
      <cyber-tag
        :class="{ active: selectedType === 2 }"
        @click="selectType(2)"
      >
        开源项目
      </cyber-tag>
    </div>
    
    <div v-loading="loading" class="projects-grid">
      <cyber-card
        v-for="project in projects"
        :key="project.id"
        class="project-card"
      >
        <div v-if="project.cover" class="project-cover">
          <img :src="project.cover" :alt="project.name" />
          <div class="project-overlay">
            <cyber-button @click="viewProject(project.id!)">
              查看详情
            </cyber-button>
          </div>
        </div>
        
        <div class="project-info">
          <div class="project-header">
            <h3 class="project-name">{{ project.name }}</h3>
            <el-tag v-if="project.type === 0" type="primary" size="small">企业</el-tag>
            <el-tag v-else-if="project.type === 1" type="success" size="small">个人</el-tag>
            <el-tag v-else type="info" size="small">开源</el-tag>
          </div>
          
          <p class="project-desc">{{ project.description }}</p>
          
          <div v-if="project.techStack && project.techStack.length" class="project-tech">
            <cyber-tag
              v-for="(tech, index) in project.techStack.slice(0, 5)"
              :key="index"
              size="small"
            >
              {{ tech }}
            </cyber-tag>
            <span v-if="project.techStack.length > 5" class="more-tech">
              +{{ project.techStack.length - 5 }}
            </span>
          </div>

          <div v-if="project.highlights && project.highlights.length" class="project-highlights">
            <h4 class="highlights-title">项目亮点</h4>
            <ul>
              <li v-for="(highlight, index) in project.highlights.slice(0, 3)" :key="index">
                {{ highlight }}
              </li>
            </ul>
          </div>
          
          <div class="project-links">
            <a
              v-if="project.githubUrl"
              :href="project.githubUrl"
              target="_blank"
              class="project-link"
            >
              <el-icon><Link /></el-icon>
              GitHub
            </a>
            <a
              v-if="project.demoUrl"
              :href="project.demoUrl"
              target="_blank"
              class="project-link"
            >
              <el-icon><Monitor /></el-icon>
              在线演示
            </a>
          </div>

          <div class="project-meta">
            <span><el-icon><View /></el-icon> {{ project.viewCount }}</span>
          </div>
        </div>
      </cyber-card>

      <!-- 空状态 -->
      <div v-if="!loading && projects.length === 0" class="empty-state">
        <el-empty description="暂无项目" />
      </div>
    </div>

    <!-- 项目详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentProject?.name"
      width="900px"
      class="project-detail-dialog"
      @close="closeDetailDialog"
    >
      <div v-loading="detailLoading" class="project-detail">
        <template v-if="currentProject">
          <!-- 项目头部信息 -->
          <div class="detail-header">
            <el-tag :type="getProjectTypeTagType(currentProject.type)" size="large">
              {{ getProjectTypeLabel(currentProject.type) }}
            </el-tag>
          </div>

          <!-- 项目封面 -->
          <div v-if="currentProject.cover" class="detail-cover">
            <img :src="currentProject.cover" :alt="currentProject.name" />
          </div>

          <!-- 项目描述 -->
          <div class="detail-section">
            <h3 class="section-title">项目简介</h3>
            <p class="section-content">{{ currentProject.description }}</p>
          </div>

          <!-- 技术栈 -->
          <div v-if="currentProject.techStack && currentProject.techStack.length" class="detail-section">
            <h3 class="section-title">技术栈</h3>
            <div class="tech-tags">
              <cyber-tag
                v-for="(tech, index) in currentProject.techStack"
                :key="index"
                size="small"
              >
                {{ tech }}
              </cyber-tag>
            </div>
          </div>

          <!-- 项目亮点 -->
          <div v-if="currentProject.highlights && currentProject.highlights.length" class="detail-section">
            <h3 class="section-title">项目亮点</h3>
            <ul class="highlights-list">
              <li v-for="(highlight, index) in currentProject.highlights" :key="index">
                {{ highlight }}
              </li>
            </ul>
          </div>

          <!-- 项目图片 -->
          <div v-if="currentProject.images && currentProject.images.length" class="detail-section">
            <h3 class="section-title">项目截图</h3>
            <div class="project-images">
              <el-image
                v-for="(image, index) in currentProject.images"
                :key="index"
                :src="image"
                :preview-src-list="currentProject.images"
                :initial-index="index"
                fit="cover"
                class="project-image"
              />
            </div>
          </div>

          <!-- 项目链接 -->
          <div class="detail-section">
            <h3 class="section-title">相关链接</h3>
            <div class="detail-links">
              <a
                v-if="currentProject.githubUrl"
                :href="currentProject.githubUrl"
                target="_blank"
                class="detail-link"
              >
                <el-icon><Link /></el-icon>
                <span>GitHub 仓库</span>
              </a>
              <a
                v-if="currentProject.demoUrl"
                :href="currentProject.demoUrl"
                target="_blank"
                class="detail-link"
              >
                <el-icon><Monitor /></el-icon>
                <span>在线演示</span>
              </a>
              <div v-if="!currentProject.githubUrl && !currentProject.demoUrl" class="no-links">
                暂无相关链接
              </div>
            </div>
          </div>

          <!-- 项目统计 -->
          <div class="detail-footer">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span>浏览量：{{ currentProject.viewCount || 0 }}</span>
            </div>
            <div v-if="currentProject.createTime" class="stat-item">
              <span>创建时间：{{ currentProject.createTime }}</span>
            </div>
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { View, Link, Monitor } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAllProjects, getProjectDetail } from '@/api/project'
import type { Project } from '@/types'
import CyberCard from '@/components/CyberCard.vue'
import CyberTag from '@/components/CyberTag.vue'
import CyberButton from '@/components/CyberButton.vue'

const loading = ref(false)
const selectedType = ref<number | null>(null)
const allProjects = ref<Project[]>([])
const projects = ref<Project[]>([])

// 详情对话框
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const currentProject = ref<Project | null>(null)

// 获取项目列表
const getProjects = async () => {
  loading.value = true
  try {
    const res = await getAllProjects()
    allProjects.value = res.data
    filterProjects()
  } catch (error) {
    console.error('获取项目列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 筛选项目
const filterProjects = () => {
  if (selectedType.value === null) {
    projects.value = allProjects.value
  } else {
    projects.value = allProjects.value.filter(p => p.type === selectedType.value)
  }
}

// 选择类型
const selectType = (type: number | null) => {
  selectedType.value = type
  filterProjects()
}

// 查看项目详情
const viewProject = async (id: number) => {
  detailLoading.value = true
  detailDialogVisible.value = true
  try {
    const res = await getProjectDetail(id)
    currentProject.value = res.data
    
    // 使用后端返回的最新浏览量更新列表
    updateProjectViewCount(id, res.data.viewCount)
  } catch (error) {
    console.error('获取项目详情失败:', error)
    ElMessage.error('获取项目详情失败')
    detailDialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

// 更新项目浏览量
const updateProjectViewCount = (id: number, newViewCount?: number) => {
  // 更新所有项目列表中的浏览量
  const allProject = allProjects.value.find(p => p.id === id)
  if (allProject && newViewCount !== undefined) {
    allProject.viewCount = newViewCount
  }
  
  // 更新筛选后的项目列表中的浏览量
  const project = projects.value.find(p => p.id === id)
  if (project && newViewCount !== undefined) {
    project.viewCount = newViewCount
  }
}

// 关闭详情对话框
const closeDetailDialog = () => {
  detailDialogVisible.value = false
  currentProject.value = null
}

// 获取项目类型标签
const getProjectTypeLabel = (type?: number) => {
  switch (type) {
    case 0: return '企业项目'
    case 1: return '个人项目'
    case 2: return '开源项目'
    default: return '未知'
  }
}

// 获取项目类型标签类型
const getProjectTypeTagType = (type?: number) => {
  switch (type) {
    case 0: return 'primary'
    case 1: return 'success'
    case 2: return 'info'
    default: return 'info'
  }
}

// 初始化
onMounted(() => {
  getProjects()
})
</script>

<style lang="scss" scoped>
.page-title {
  font-size: 48px;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  text-align: center;
  margin-bottom: 32px;
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

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 32px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  min-height: 400px;
}

.project-card {
  transition: all 0.3s;
  overflow: hidden;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 32px rgba($neon-cyan, 0.5);

    .project-overlay {
      opacity: 1;
    }

    .project-cover img {
      transform: scale(1.1);
    }
  }
}

.project-cover {
  position: relative;
  width: 100%;
  height: 220px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }

  .project-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba($bg-primary, 0.85);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
  }
}

.project-info {
  padding: 4px 0;
}

.project-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.project-name {
  font-size: 22px;
  color: $neon-cyan;
  margin: 0;
  flex: 1;
}

.project-desc {
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;

  .more-tech {
    color: $text-secondary;
    font-size: 12px;
    display: flex;
    align-items: center;
  }
}

.project-highlights {
  margin-bottom: 16px;
  padding: 12px;
  background: rgba($bg-secondary, 0.3);
  border-radius: 6px;
  border-left: 3px solid $neon-cyan;

  .highlights-title {
    font-size: 14px;
    color: $neon-cyan;
    margin: 0 0 8px 0;
  }

  ul {
    margin: 0;
    padding-left: 20px;
    color: $text-secondary;
    font-size: 14px;

    li {
      margin-bottom: 4px;
    }
  }
}

.project-links {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;

  .project-link {
    display: flex;
    align-items: center;
    gap: 4px;
    color: $neon-magenta;
    text-decoration: none;
    font-size: 14px;
    transition: all 0.3s;

    &:hover {
      color: $neon-cyan;
      text-shadow: 0 0 10px $neon-cyan;
    }

    .el-icon {
      font-size: 16px;
    }
  }
}

.project-meta {
  display: flex;
  gap: 16px;
  color: $text-secondary;
  font-size: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba($neon-cyan, 0.2);

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
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }

  .projects-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

// 项目详情对话框样式
:deep(.project-detail-dialog) {
  .el-dialog__header {
    background: linear-gradient(135deg, rgba($neon-cyan, 0.1) 0%, rgba($neon-purple, 0.1) 100%);
    border-bottom: 1px solid rgba($neon-cyan, 0.3);
    
    .el-dialog__title {
      color: $neon-cyan;
      font-size: 24px;
      font-weight: bold;
      text-shadow: 0 0 10px rgba($neon-cyan, 0.5);
    }
  }

  .el-dialog__body {
    padding: 24px;
    background: $bg-primary;
  }
}

.project-detail {
  min-height: 200px;
  color: $text-primary;
}

.detail-header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-cover {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
  border: 1px solid rgba($neon-cyan, 0.2);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.detail-section {
  margin-bottom: 24px;

  .section-title {
    font-size: 18px;
    color: $neon-cyan;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 2px solid rgba($neon-cyan, 0.3);
    display: flex;
    align-items: center;
    gap: 8px;

    &::before {
      content: '';
      width: 4px;
      height: 18px;
      background: linear-gradient(180deg, $neon-cyan 0%, $neon-purple 100%);
      border-radius: 2px;
    }
  }

  .section-content {
    color: $text-secondary;
    line-height: 1.8;
    font-size: 15px;
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.highlights-list {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    position: relative;
    padding-left: 24px;
    margin-bottom: 12px;
    color: $text-secondary;
    line-height: 1.6;

    &::before {
      content: '▸';
      position: absolute;
      left: 0;
      color: $neon-cyan;
      font-weight: bold;
    }
  }
}

.project-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;

  .project-image {
    width: 100%;
    height: 150px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid rgba($neon-cyan, 0.2);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 16px rgba($neon-cyan, 0.3);
      border-color: $neon-cyan;
    }
  }
}

.detail-links {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;

  .detail-link {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: linear-gradient(135deg, rgba($neon-cyan, 0.1) 0%, rgba($neon-purple, 0.1) 100%);
    border: 1px solid rgba($neon-cyan, 0.3);
    border-radius: 6px;
    color: $neon-cyan;
    text-decoration: none;
    font-size: 15px;
    transition: all 0.3s;

    &:hover {
      background: linear-gradient(135deg, rgba($neon-cyan, 0.2) 0%, rgba($neon-purple, 0.2) 100%);
      border-color: $neon-cyan;
      box-shadow: 0 0 20px rgba($neon-cyan, 0.4);
      transform: translateY(-2px);
    }

    .el-icon {
      font-size: 18px;
    }
  }

  .no-links {
    color: $text-secondary;
    font-style: italic;
  }
}

.detail-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid rgba($neon-cyan, 0.2);
  display: flex;
  gap: 24px;
  flex-wrap: wrap;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: $text-secondary;
    font-size: 14px;

    .el-icon {
      color: $neon-cyan;
      font-size: 16px;
    }
  }
}

@media (max-width: 768px) {
  :deep(.project-detail-dialog) {
    width: 95% !important;
  }

  .detail-cover {
    height: 250px;
  }

  .project-images {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    
    .project-image {
      height: 120px;
    }
  }

  .detail-links {
    .detail-link {
      flex: 1 1 100%;
      justify-content: center;
    }
  }
}
</style>
