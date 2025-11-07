<template>
  <div class="site-config-page">
    <h2 class="page-title">网站配置</h2>
    
    <el-card class="config-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><Setting /></el-icon>
            系统设置
          </span>
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存设置
          </el-button>
        </div>
      </template>

      <el-form :model="configForm" label-width="180px" class="config-form">
        <!-- 留言设置 -->
        <el-divider content-position="left">
          <el-icon><ChatDotRound /></el-icon>
          留言板设置
        </el-divider>
        
        <el-form-item label="留言是否需要登录">
          <el-switch
            v-model="configForm.messageLoginRequired"
            active-text="需要登录"
            inactive-text="允许匿名留言"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="(value) => handleSwitchChange(value as boolean, 'message')"
          />
          <div class="form-item-tip">
            <el-icon><InfoFilled /></el-icon>
            {{ configForm.messageLoginRequired 
              ? '开启后，只有登录用户才能留言' 
              : '关闭后，所有访客都可以匿名留言' }}
          </div>
        </el-form-item>

        <!-- 评论设置 -->
        <el-divider content-position="left">
          <el-icon><ChatDotRound /></el-icon>
          文章评论设置
        </el-divider>
        
        <el-form-item label="评论是否需要登录">
          <el-switch
            v-model="configForm.commentLoginRequired"
            active-text="需要登录"
            inactive-text="允许匿名评论"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="(value) => handleSwitchChange(value as boolean, 'comment')"
          />
          <div class="form-item-tip">
            <el-icon><InfoFilled /></el-icon>
            {{ configForm.commentLoginRequired 
              ? '开启后，只有登录用户才能发表评论' 
              : '关闭后，所有访客都可以匿名评论' }}
          </div>
        </el-form-item>

        <!-- 可以继续添加其他配置项 -->
        <el-divider content-position="left">
          <el-icon><Tools /></el-icon>
          更多设置
        </el-divider>

        <el-alert
          title="提示"
          type="info"
          description="更多配置项正在开发中，敬请期待..."
          :closable="false"
          show-icon
          class="more-config-tip"
        />
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, ChatDotRound, InfoFilled, Tools } from '@element-plus/icons-vue'
import { isMessageLoginRequired, isCommentLoginRequired } from '@/api/config'
import { saveOrUpdateConfig } from '@/api/config'

const loading = ref(false)
const saving = ref(false)

// 配置表单
const configForm = reactive({
  messageLoginRequired: false,
  commentLoginRequired: false
})

// 原始配置（用于检测是否有修改）
const originalConfig = reactive({
  messageLoginRequired: false,
  commentLoginRequired: false
})

// 加载配置
const loadConfig = async () => {
  loading.value = true
  try {
    // 获取留言登录配置
    const messageRes = await isMessageLoginRequired()
    configForm.messageLoginRequired = messageRes.data
    originalConfig.messageLoginRequired = messageRes.data
    
    // 获取评论登录配置
    const commentRes = await isCommentLoginRequired()
    configForm.commentLoginRequired = commentRes.data
    originalConfig.commentLoginRequired = commentRes.data
  } catch (error) {
    console.error('加载配置失败:', error)
    ElMessage.error('加载配置失败')
  } finally {
    loading.value = false
  }
}

// 保存配置
const handleSave = async () => {
  saving.value = true
  try {
    // 保存留言登录配置
    await saveOrUpdateConfig(
      'message_login_required',
      configForm.messageLoginRequired ? 'true' : 'false'
    )
    
    // 保存评论登录配置
    await saveOrUpdateConfig(
      'comment_login_required',
      configForm.commentLoginRequired ? 'true' : 'false'
    )
    
    // 更新原始配置
    originalConfig.messageLoginRequired = configForm.messageLoginRequired
    originalConfig.commentLoginRequired = configForm.commentLoginRequired
    
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存配置失败')
  } finally {
    saving.value = false
  }
}

// 开关变化处理
const handleSwitchChange = (value: boolean, type: 'message' | 'comment') => {
  const action = value ? '开启' : '关闭'
  const typeName = type === 'message' ? '留言' : '评论'
  ElMessage.info(`已${action}${typeName}登录要求，请点击"保存设置"按钮保存更改`)
}

// 初始化
onMounted(() => {
  loadConfig()
})
</script>

<style lang="scss" scoped>
@use "sass:color";

.site-config-page {
  max-width: 1200px;
}

.page-title {
  font-size: 28px;
  color: $neon-cyan;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  text-shadow: 0 0 10px rgba($neon-cyan, 0.5);
  
  .el-icon {
    font-size: 32px;
  }
}

.config-card {
  background: rgba($bg-card, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba($neon-cyan, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  
  :deep(.el-card__header) {
    background: rgba($bg-secondary, 0.6);
    border-bottom: 1px solid rgba($neon-cyan, 0.2);
    padding: 18px 24px;
  }
  
  :deep(.el-card__body) {
    padding: 32px 24px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: $neon-cyan;
    text-shadow: 0 0 8px rgba($neon-cyan, 0.4);
    
    .el-icon {
      font-size: 20px;
    }
  }
}

.config-form {
  .el-divider {
    margin: 32px 0 24px 0;
    
    :deep(.el-divider__text) {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: $neon-magenta;
      background: rgba($bg-card, 0.8);
      padding: 0 16px;
      
      .el-icon {
        font-size: 18px;
      }
    }
  }
  
  .el-form-item {
    margin-bottom: 32px;
    
    :deep(.el-form-item__label) {
      color: $text-primary;
      font-weight: 500;
      font-size: 15px;
    }
    
    :deep(.el-form-item__content) {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }
  }
  
  .form-item-tip {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: rgba($neon-cyan, 0.08);
    border: 1px solid rgba($neon-cyan, 0.2);
    border-radius: 8px;
    color: $text-secondary;
    font-size: 14px;
    line-height: 1.6;
    max-width: 500px;
    
    .el-icon {
      color: $neon-cyan;
      font-size: 16px;
      flex-shrink: 0;
    }
  }
  
  .more-config-tip {
    margin-top: 16px;
    border-radius: 8px;
    background: rgba($neon-cyan, 0.05);
    border: 1px solid rgba($neon-cyan, 0.15);
    
    :deep(.el-alert__title) {
      color: $neon-cyan;
      font-weight: 600;
    }
    
    :deep(.el-alert__description) {
      color: $text-secondary;
    }
    
    :deep(.el-alert__icon) {
      color: $neon-cyan;
    }
  }
}

// Element Plus 组件样式定制
:deep(.el-switch) {
  --el-switch-on-color: #{$neon-cyan};
  --el-switch-off-color: #{$neon-magenta};
  height: 28px;
  line-height: 28px;
  
  .el-switch__core {
    height: 28px;
    border: 2px solid currentColor;
    background: rgba(0, 0, 0, 0.3);
    box-shadow: 0 0 10px rgba($neon-magenta, 0.3);
    transition: all 0.3s;
    
    &::after {
      width: 22px;
      height: 22px;
      box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
    }
  }
  
  &.is-checked .el-switch__core {
    box-shadow: 0 0 15px rgba($neon-cyan, 0.5);
  }
  
  .el-switch__label {
    color: $text-primary;
    font-weight: 500;
    font-size: 14px;
    
    &.is-active {
      color: currentColor;
      font-weight: 600;
    }
  }
}

:deep(.el-button) {
  &.el-button--primary {
    background: linear-gradient(135deg, $neon-cyan, rgba($neon-cyan, 0.8));
    border-color: $neon-cyan;
    color: $bg-primary;
    font-weight: 600;
    box-shadow: 0 0 15px rgba($neon-cyan, 0.3);
    transition: all 0.3s;
    
    &:hover {
      background: linear-gradient(135deg, color.adjust($neon-cyan, $lightness: 10%), $neon-cyan);
      box-shadow: 0 0 20px rgba($neon-cyan, 0.5);
      transform: translateY(-2px);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

:deep(.el-loading-mask) {
  background: rgba($bg-primary, 0.9);
  backdrop-filter: blur(10px);
  
  .el-loading-spinner {
    .circular {
      stroke: $neon-cyan;
      filter: drop-shadow(0 0 10px $neon-cyan);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .config-form {
    .el-form-item {
      :deep(.el-form-item__label) {
        width: 100% !important;
        text-align: left;
        margin-bottom: 8px;
      }
      
      :deep(.el-form-item__content) {
        margin-left: 0 !important;
      }
    }
    
    .form-item-tip {
      max-width: 100%;
    }
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>

