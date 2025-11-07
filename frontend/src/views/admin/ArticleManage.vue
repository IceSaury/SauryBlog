<template>
  <div class="article-manage-page">
    <div class="page-header">
      <h2 class="page-title">文章管理</h2>
      <div class="header-actions">
        <cyber-button @click="handleShowPublished" style="margin-right: 12px">
          <el-icon><Check /></el-icon>
          已发布
        </cyber-button>
        <cyber-button @click="handleShowDrafts" style="margin-right: 12px">
          <el-icon><Document /></el-icon>
          草稿
        </cyber-button>
        <cyber-button @click="handleShowUnlisted" style="margin-right: 12px">
          <el-icon><Warning /></el-icon>
          已下架
        </cyber-button>
        <cyber-button type="cyan" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增文章
        </cyber-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索标题..."
        style="width: 300px"
        clearable
        @clear="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select
        v-model="searchForm.categoryId"
        placeholder="分类"
        clearable
        style="width: 150px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option
          v-for="cat in categories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
      
      <el-select
        v-model="searchForm.status"
        placeholder="状态"
        clearable
        style="width: 120px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option label="草稿" :value="0" />
        <el-option label="已发布" :value="1" />
        <el-option label="已下架" :value="2" />
      </el-select>
      
      <cyber-button @click="handleSearch" style="margin-left: 12px">
        搜索
      </cyber-button>
    </div>

    <!-- 文章列表 -->
    <div class="article-list">
      <el-table
        :data="articleList"
        v-loading="loading"
        style="width: 100%"
        class="cyber-table"
      >
        <el-table-column label="封面" width="120">
          <template #default="{ row }">
            <img v-if="row.cover" :src="row.cover" class="cover-img" />
            <div v-else class="no-cover">无封面</div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <cyber-tag
              v-for="tag in row.tags"
              :key="tag.id"
              :color="tag.color"
              size="small"
              style="margin-right: 4px"
            >
              {{ tag.name }}
            </cyber-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已发布</el-tag>
            <el-tag v-else type="warning">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <cyber-button size="small" @click="handleEdit(row)">
                编辑
              </cyber-button>
              <cyber-button
                v-if="row.status === 1"
                size="small"
                type="yellow"
                @click="handleUnlist(row.id)"
              >
                下架
              </cyber-button>
              <cyber-button
                size="small"
                type="magenta"
                @click="handleDelete(row.id)"
              >
                删除
              </cyber-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
        class="pagination"
      />
    </div>

    <!-- 文章编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="90%"
      :close-on-click-modal="false"
      class="article-dialog"
    >
      <el-form
        ref="formRef"
        :model="articleForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input
            v-model="articleForm.title"
            placeholder="请输入文章标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="文章分类" prop="categoryId">
          <el-select
            v-model="articleForm.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="文章标签">
          <el-select
            v-model="articleForm.tagIds"
            placeholder="请选择标签"
            multiple
            style="width: 100%"
          >
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="文章封面">
          <image-upload v-model="articleForm.cover" />
        </el-form-item>
        
        <el-form-item label="文章摘要">
          <el-input
            v-model="articleForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="文章内容" prop="content">
          <div class="markdown-editor">
            <el-input
              v-model="articleForm.content"
              type="textarea"
              :rows="20"
              placeholder="请输入Markdown格式的文章内容..."
            />
          </div>
        </el-form-item>
        
        <el-form-item label="设置">
          <el-checkbox v-model="articleForm.isTop" :true-label="1" :false-label="0">
            置顶
          </el-checkbox>
          <el-checkbox
            v-model="articleForm.isFeatured"
            :true-label="1"
            :false-label="0"
            style="margin-left: 20px"
          >
            推荐
          </el-checkbox>
        </el-form-item>
        
        <el-form-item label="发布状态" prop="status">
          <el-radio-group v-model="articleForm.status">
            <el-radio :label="0">保存为草稿</el-radio>
            <el-radio :label="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <cyber-button @click="dialogVisible = false">取消</cyber-button>
        <cyber-button type="cyan" @click="handleSubmit">保存</cyber-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Document, Check, Warning } from '@element-plus/icons-vue'
import { getArticlePage, saveArticle, deleteArticle, unlistArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'
import type { Article, Category, Tag } from '@/types'
import CyberButton from '@/components/CyberButton.vue'
import CyberTag from '@/components/CyberTag.vue'
import ImageUpload from '@/components/ImageUpload.vue'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  status: undefined as number | undefined
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 列表数据
const articleList = ref<Article[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增文章')
const formRef = ref()

// 文章表单
const articleForm = reactive<Article>({
  title: '',
  categoryId: undefined,
  tagIds: [],
  cover: '',
  summary: '',
  content: '',
  isTop: 0,
  isFeatured: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }],
  status: [{ required: true, message: '请选择发布状态', trigger: 'change' }]
}

// 获取文章列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getArticlePage({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    articleList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取标签列表
const getTags = async () => {
  try {
    const res = await getTagList()
    tags.value = res.data
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getList()
}

// 查看已发布
const handleShowPublished = () => {
  searchForm.status = 1
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  pagination.page = 1
  getList()
}

// 查看草稿
const handleShowDrafts = () => {
  searchForm.status = 0
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  pagination.page = 1
  getList()
}

// 查看已下架
const handleShowUnlisted = () => {
  searchForm.status = 2
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  pagination.page = 1
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增文章'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Article) => {
  dialogTitle.value = '编辑文章'
  Object.assign(articleForm, {
    id: row.id,
    title: row.title,
    categoryId: row.categoryId,
    tagIds: row.tags?.map(t => t.id!) || [],
    cover: row.cover || '',
    summary: row.summary || '',
    content: row.content,
    isTop: row.isTop || 0,
    isFeatured: row.isFeatured || 0,
    status: row.status
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteArticle(id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 下架
const handleUnlist = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要下架这篇文章吗？下架后文章将不在前台展示。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await unlistArticle(id)
    ElMessage.success('文章已下架')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    const isPublishing = articleForm.status === 1 // 是否正在发布
    
    await saveArticle(articleForm)
    
    // 根据状态显示不同的提示
    if (articleForm.status === 0) {
      ElMessage.success('草稿已保存！可在列表中选择"草稿"状态查看和编辑')
    } else if (articleForm.status === 1) {
      ElMessage.success('文章发布成功！')
      
      // 如果是发布操作，自动切换到"已发布"状态筛选
      if (isPublishing) {
        searchForm.status = 1
      }
    } else {
      ElMessage.success('保存成功')
    }
    
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(articleForm, {
    id: undefined,
    title: '',
    categoryId: undefined,
    tagIds: [],
    cover: '',
    summary: '',
    content: '',
    isTop: 0,
    isFeatured: 0,
    status: 1
  })
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  getList()
  getCategories()
  getTags()
})
</script>

<style lang="scss" scoped>
.article-manage-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .header-actions {
    display: flex;
    gap: 0;
    align-items: center;
  }
}

.page-title {
  font-size: 28px;
  color: $neon-cyan;
  margin: 0;
  text-shadow: 0 0 10px $neon-cyan;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: rgba($bg-secondary, 0.6);
  border: 1px solid rgba($neon-cyan, 0.3);
  border-radius: 8px;
}

.article-list {
  background: rgba($bg-secondary, 0.4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba($neon-cyan, 0.3);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.cover-img {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  border: 2px solid rgba($neon-cyan, 0.4);
  box-shadow: 0 0 8px rgba($neon-cyan, 0.3);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    border-color: $neon-cyan;
    box-shadow: 0 0 15px rgba($neon-cyan, 0.6),
                0 0 25px rgba($neon-purple, 0.3);
    transform: scale(1.05);
  }
}

.no-cover {
  width: 80px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba($bg-primary, 0.8), rgba($bg-secondary, 0.6));
  border: 2px dashed rgba($neon-cyan, 0.4);
  border-radius: 6px;
  color: $text-secondary;
  font-size: 12px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(45deg, transparent, rgba($neon-cyan, 0.1), transparent);
    animation: shimmer 2s infinite;
  }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
  flex-wrap: nowrap;
  align-items: center;

  :deep(.cyber-btn) {
    padding: 4px 12px !important;
    font-size: 13px !important;
    min-width: 60px;
    height: 28px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    writing-mode: horizontal-tb;
    text-orientation: mixed;
    letter-spacing: 1px !important;

    .cyber-btn-content {
      writing-mode: horizontal-tb;
      display: inline-block;
    }
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.markdown-editor {
  width: 100%;
  
  :deep(.el-textarea__inner) {
    font-family: 'Courier New', monospace;
    background: rgba($bg-primary, 0.5);
    border-color: rgba($neon-cyan, 0.3);
    color: $text-primary;
  }
}

:deep(.article-dialog) {
  .el-dialog {
    border-radius: 12px;
    border: 2px solid rgba($neon-cyan, 0.5);
    box-shadow: 0 0 30px rgba($neon-cyan, 0.3), 
                0 0 60px rgba($neon-purple, 0.2);
    overflow: hidden;
  }

  .el-dialog__header {
    background: linear-gradient(135deg, rgba($bg-secondary, 0.95), rgba($bg-primary, 0.95));
    border-bottom: 2px solid rgba($neon-cyan, 0.5);
    padding: 20px 30px;
    border-radius: 12px 12px 0 0;
  }
  
  .el-dialog__title {
    color: $neon-cyan;
    font-size: 22px;
    font-weight: 600;
    text-shadow: 0 0 10px rgba($neon-cyan, 0.8);
  }

  .el-dialog__headerbtn {
    .el-dialog__close {
      color: $neon-cyan;
      font-size: 20px;
      
      &:hover {
        color: $neon-purple;
        text-shadow: 0 0 10px $neon-purple;
      }
    }
  }
  
  .el-dialog__body {
    background: linear-gradient(180deg, rgba($bg-primary, 0.98), rgba($bg-secondary, 0.95));
    padding: 30px;
  }
  
  .el-form-item__label {
    color: $text-primary;
    font-weight: 500;
    font-size: 14px;
    text-shadow: 0 0 5px rgba($neon-cyan, 0.3);
  }

  // 输入框样式
  .el-input__wrapper {
    background: rgba($bg-secondary, 0.6);
    border-radius: 8px;
    border: 1px solid rgba($neon-cyan, 0.3);
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba($neon-cyan, 0.6);
      box-shadow: 0 0 10px rgba($neon-cyan, 0.2),
                  inset 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    &.is-focus {
      border-color: $neon-cyan;
      box-shadow: 0 0 15px rgba($neon-cyan, 0.4),
                  inset 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    .el-input__inner {
      color: $text-primary;
      
      &::placeholder {
        color: $text-secondary;
      }
    }
  }

  // 文本域样式
  .el-textarea__inner {
    background: rgba($bg-secondary, 0.6);
    border-radius: 8px;
    border: 1px solid rgba($neon-cyan, 0.3);
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.3);
    color: $text-primary;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba($neon-cyan, 0.6);
      box-shadow: 0 0 10px rgba($neon-cyan, 0.2),
                  inset 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    &:focus {
      border-color: $neon-cyan;
      box-shadow: 0 0 15px rgba($neon-cyan, 0.4),
                  inset 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    &::placeholder {
      color: $text-secondary;
    }
  }

  // 选择框样式
  .el-select {
    .el-input__wrapper {
      border-radius: 8px;
    }
  }

  // 单选框样式
  .el-radio {
    margin-right: 24px;

    .el-radio__label {
      color: $text-primary;
    }

    .el-radio__input.is-checked {
      .el-radio__inner {
        background: $neon-cyan;
        border-color: $neon-cyan;
        box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
      }
    }

    &:hover {
      .el-radio__inner {
        border-color: $neon-cyan;
      }
    }
  }

  // 复选框样式
  .el-checkbox {
    .el-checkbox__label {
      color: $text-primary;
    }

    .el-checkbox__input.is-checked {
      .el-checkbox__inner {
        background: $neon-cyan;
        border-color: $neon-cyan;
        box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
      }
    }

    &:hover {
      .el-checkbox__inner {
        border-color: $neon-cyan;
      }
    }
  }

  // 对话框底部
  .el-dialog__footer {
    padding: 20px 30px;
    background: linear-gradient(180deg, rgba($bg-secondary, 0.95), rgba($bg-primary, 0.98));
    border-top: 1px solid rgba($neon-cyan, 0.3);
    border-radius: 0 0 12px 12px;
  }
}

:deep(.cyber-table) {
  background: transparent !important;
  border-radius: 8px;
  overflow: hidden;
  
  .el-table__header {
    th {
      background: rgba($bg-secondary, 0.8) !important;
      color: $neon-cyan !important;
      border-color: rgba($neon-cyan, 0.3) !important;
      font-weight: 600;
      font-size: 14px;
      text-shadow: 0 0 8px rgba($neon-cyan, 0.5);
      padding: 16px 0 !important;
    }
  }
  
  .el-table__body {
    tr {
      background: rgba($bg-primary, 0.3);
      border-left: 3px solid transparent;
      transition: all 0.3s ease;
      
      &:hover {
        background: linear-gradient(90deg, 
          rgba($neon-cyan, 0.1), 
          rgba($bg-secondary, 0.5)
        ) !important;
        border-left-color: $neon-cyan;
        box-shadow: inset 3px 0 8px rgba($neon-cyan, 0.3);
        transform: translateX(2px);
        
        td {
          color: $neon-cyan !important;
        }
      }
      
      td {
        border-color: rgba($neon-cyan, 0.15) !important;
        color: $text-primary;
        padding: 14px 0 !important;
        transition: color 0.3s ease;
      }
    }
  }

  // 状态标签样式增强
  .el-tag {
    border-radius: 4px;
    padding: 4px 12px;
    font-weight: 500;
    border-width: 1px;
    
    &.el-tag--info {
      background: rgba($text-secondary, 0.2);
      border-color: $text-secondary;
      color: $text-secondary;
    }
    
    &.el-tag--success {
      background: rgba($neon-green, 0.2);
      border-color: $neon-green;
      color: $neon-green;
      box-shadow: 0 0 8px rgba($neon-green, 0.3);
    }
    
    &.el-tag--warning {
      background: rgba($neon-yellow, 0.2);
      border-color: $neon-yellow;
      color: $neon-yellow;
      box-shadow: 0 0 8px rgba($neon-yellow, 0.3);
    }
  }
}
</style>
