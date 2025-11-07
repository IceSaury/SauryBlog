<template>
  <div class="project-manage-page">
    <div class="page-header">
      <h2 class="page-title">项目管理</h2>
      <div class="header-actions">
        <cyber-button @click="handleShowVisible" style="margin-right: 12px">
          <el-icon><View /></el-icon>
          已显示
        </cyber-button>
        <cyber-button @click="handleShowHidden" style="margin-right: 12px">
          <el-icon><Hide /></el-icon>
          已隐藏
        </cyber-button>
        <cyber-button type="cyan" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增项目
        </cyber-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索项目名称..."
        style="width: 300px"
        clearable
        @clear="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select
        v-model="searchForm.type"
        placeholder="项目类型"
        clearable
        style="width: 150px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option label="企业项目" :value="0" />
        <el-option label="个人项目" :value="1" />
        <el-option label="开源项目" :value="2" />
      </el-select>
      
      <el-select
        v-model="searchForm.status"
        placeholder="状态"
        clearable
        style="width: 120px; margin-left: 12px"
        @change="handleSearch"
      >
        <el-option label="隐藏" :value="0" />
        <el-option label="显示" :value="1" />
      </el-select>
      
      <cyber-button @click="handleSearch" style="margin-left: 12px">
        搜索
      </cyber-button>
    </div>

    <!-- 项目列表 -->
    <div class="project-list">
      <el-table
        :data="projectList"
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
        <el-table-column prop="name" label="项目名称" width="180" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.type === 0" type="primary">企业项目</el-tag>
            <el-tag v-else-if="row.type === 1" type="success">个人项目</el-tag>
            <el-tag v-else type="info">开源项目</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="技术栈" min-width="300">
          <template #default="{ row }">
            <div class="tech-stack-wrapper">
              <cyber-tag
                v-for="(tech, index) in row.techStack"
                :key="index"
                size="small"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ tech }}
              </cyber-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">隐藏</el-tag>
            <el-tag v-else type="success">显示</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <cyber-button size="small" @click="handleEdit(row)">
                编辑
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
        @size-change="getList"
        @current-change="getList"
        class="pagination"
      />
    </div>

    <!-- 项目编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      class="project-dialog"
    >
      <el-form
        ref="formRef"
        :model="projectForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input
            v-model="projectForm.name"
            placeholder="请输入项目名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="项目类型" prop="type">
          <el-radio-group v-model="projectForm.type">
            <el-radio :label="0">企业项目</el-radio>
            <el-radio :label="1">个人项目</el-radio>
            <el-radio :label="2">开源项目</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="项目封面">
          <image-upload v-model="projectForm.cover" class="cover-upload" />
        </el-form-item>
        
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="8"
            placeholder="请输入项目描述"
            maxlength="1000"
            show-word-limit
            class="description-textarea"
          />
        </el-form-item>
        
        <el-form-item label="技术栈">
          <el-tag
            v-for="(tech, index) in projectForm.techStack"
            :key="index"
            closable
            @close="removeTech(index)"
            style="margin-right: 8px; margin-bottom: 8px"
          >
            {{ tech }}
          </el-tag>
          <el-input
            v-if="techInputVisible"
            ref="techInputRef"
            v-model="techInputValue"
            size="small"
            style="width: 120px"
            @keyup.enter="handleTechInputConfirm"
            @blur="handleTechInputConfirm"
          />
          <cyber-button
            v-else
            size="small"
            @click="showTechInput"
          >
            + 添加技术
          </cyber-button>
        </el-form-item>
        
        <el-form-item label="项目亮点">
          <el-tag
            v-for="(highlight, index) in projectForm.highlights"
            :key="index"
            closable
            @close="removeHighlight(index)"
            style="margin-right: 8px; margin-bottom: 8px"
          >
            {{ highlight }}
          </el-tag>
          <el-input
            v-if="highlightInputVisible"
            ref="highlightInputRef"
            v-model="highlightInputValue"
            size="small"
            style="width: 200px"
            @keyup.enter="handleHighlightInputConfirm"
            @blur="handleHighlightInputConfirm"
          />
          <cyber-button
            v-else
            size="small"
            @click="showHighlightInput"
          >
            + 添加亮点
          </cyber-button>
        </el-form-item>
        
        <el-form-item label="GitHub">
          <el-input
            v-model="projectForm.githubUrl"
            placeholder="请输入GitHub地址"
          />
        </el-form-item>
        
        <el-form-item label="演示地址">
          <el-input
            v-model="projectForm.demoUrl"
            placeholder="请输入演示地址"
          />
        </el-form-item>
        
        <el-form-item label="排序">
          <el-input-number
            v-model="projectForm.sort"
            :min="0"
            controls-position="right"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-radio-group v-model="projectForm.status">
            <el-radio :label="0">隐藏</el-radio>
            <el-radio :label="1">显示</el-radio>
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, View, Hide } from '@element-plus/icons-vue'
import { getProjectPage, saveProject, deleteProject } from '@/api/project'
import type { Project } from '@/types'
import CyberButton from '@/components/CyberButton.vue'
import CyberTag from '@/components/CyberTag.vue'
import ImageUpload from '@/components/ImageUpload.vue'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: undefined as number | undefined,
  status: undefined as number | undefined
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 列表数据
const projectList = ref<Project[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增项目')
const formRef = ref()

// 项目表单
const projectForm = reactive<Project>({
  name: '',
  description: '',
  type: 1,
  cover: '',
  techStack: [],
  highlights: [],
  githubUrl: '',
  demoUrl: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入项目描述', trigger: 'blur' }]
}

// 技术栈输入
const techInputVisible = ref(false)
const techInputValue = ref('')
const techInputRef = ref()

// 亮点输入
const highlightInputVisible = ref(false)
const highlightInputValue = ref('')
const highlightInputRef = ref()

// 获取项目列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getProjectPage({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    projectList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取项目列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getList()
}

// 查看已显示
const handleShowVisible = () => {
  searchForm.status = 1
  searchForm.keyword = ''
  searchForm.type = undefined
  pagination.page = 1
  getList()
}

// 查看已隐藏
const handleShowHidden = () => {
  searchForm.status = 0
  searchForm.keyword = ''
  searchForm.type = undefined
  pagination.page = 1
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增项目'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Project) => {
  dialogTitle.value = '编辑项目'
  Object.assign(projectForm, {
    id: row.id,
    name: row.name,
    description: row.description,
    type: row.type || 1,
    cover: row.cover || '',
    techStack: row.techStack || [],
    highlights: row.highlights || [],
    githubUrl: row.githubUrl || '',
    demoUrl: row.demoUrl || '',
    sort: row.sort || 0,
    status: row.status
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个项目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteProject(id!)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    await saveProject(projectForm)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(projectForm, {
    id: undefined,
    name: '',
    description: '',
    type: 1,
    cover: '',
    techStack: [],
    highlights: [],
    githubUrl: '',
    demoUrl: '',
    sort: 0,
    status: 1
  })
  formRef.value?.clearValidate()
}

// 技术栈相关
const showTechInput = () => {
  techInputVisible.value = true
  nextTick(() => {
    techInputRef.value?.focus()
  })
}

const handleTechInputConfirm = () => {
  if (techInputValue.value) {
    projectForm.techStack = projectForm.techStack || []
    projectForm.techStack.push(techInputValue.value)
  }
  techInputVisible.value = false
  techInputValue.value = ''
}

const removeTech = (index: number) => {
  projectForm.techStack?.splice(index, 1)
}

// 亮点相关
const showHighlightInput = () => {
  highlightInputVisible.value = true
  nextTick(() => {
    highlightInputRef.value?.focus()
  })
}

const handleHighlightInputConfirm = () => {
  if (highlightInputValue.value) {
    projectForm.highlights = projectForm.highlights || []
    projectForm.highlights.push(highlightInputValue.value)
  }
  highlightInputVisible.value = false
  highlightInputValue.value = ''
}

const removeHighlight = (index: number) => {
  projectForm.highlights?.splice(index, 1)
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.project-manage-page {
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

.project-list {
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

.tech-stack-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
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

:deep(.project-dialog) {
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
    resize: vertical;

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

  // 修复文本域右下角白色背景
  .el-textarea {
    .el-input__count {
      background: transparent;
      color: $text-secondary;
      right: 10px;
      bottom: 5px;
    }
  }

  // 项目封面上传组件样式
  .cover-upload {
    :deep(.image-preview) {
      width: 280px !important;
      height: 160px !important;
      
      .uploaded-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    :deep(.upload-placeholder) {
      width: 280px !important;
      height: 160px !important;
    }
  }

  // 数字输入框样式
  .el-input-number {
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

  // 标签样式
  .el-tag {
    border-radius: 6px;
    border: 1px solid rgba($neon-cyan, 0.3);
    background: rgba($bg-secondary, 0.6);
    color: $text-primary;

    .el-tag__close {
      color: $neon-cyan;
      
      &:hover {
        background: rgba($neon-purple, 0.3);
        color: $neon-purple;
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
    
    &.el-tag--primary {
      background: rgba($neon-cyan, 0.2);
      border-color: $neon-cyan;
      color: $neon-cyan;
      box-shadow: 0 0 8px rgba($neon-cyan, 0.3);
    }
  }
}
</style>
