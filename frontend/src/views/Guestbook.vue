<template>
  <div class="guestbook-page">
    <h1 class="page-title">留言板</h1>
    
    <div class="guestbook-container">
      <!-- 留言输入区 -->
      <cyber-card class="message-input-card" :glow="false" :hoverable="false">
        <h2 class="section-title">
          <el-icon><Edit /></el-icon>
          写下你的留言
        </h2>
        
        <!-- 需要登录提示 -->
        <el-alert
          v-if="loginRequired && !isLoggedIn"
          title="需要登录"
          type="warning"
          description="当前留言功能需要登录后才能使用，请先登录。"
          :closable="false"
          show-icon
          class="login-alert"
        />
        
        <el-form v-else :model="messageForm" ref="messageFormRef" @submit.prevent="submitMessage">
          <el-form-item>
            <el-input
              v-model="messageForm.content"
              type="textarea"
              :rows="6"
              placeholder="说点什么吧..."
              maxlength="500"
              show-word-limit
              @keydown.enter.ctrl="submitMessage"
            />
          </el-form-item>
          
          <div class="form-footer">
            <!-- 只有在未登录且允许匿名留言时显示游客信息输入 -->
            <div v-if="!isLoggedIn" class="guest-info">
              <el-input
                v-model="messageForm.nickname"
                placeholder="昵称 *"
                style="width: 180px; margin-right: 12px"
              />
              <el-input
                v-model="messageForm.email"
                placeholder="邮箱（可选）"
                style="width: 220px"
              />
            </div>
            <div v-else class="logged-user-info">
              <span class="info-text">以 {{ userInfo?.nickname }} 的身份留言</span>
            </div>
            <cyber-button type="cyan" :disabled="submitting" @click="submitMessage">
              <el-icon><Promotion /></el-icon>
              {{ submitting ? '发送中...' : '发送留言' }}
            </cyber-button>
          </div>
        </el-form>
      </cyber-card>

      <!-- 留言列表 -->
      <div v-loading="loading" class="messages-list">
        <cyber-card
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :glow="false"
          :hoverable="false"
        >
          <div class="message-header">
            <div class="user-info">
              <img
                :src="message.avatar || defaultAvatar"
                alt="avatar"
                class="user-avatar"
              />
              <div class="user-details">
                <span class="user-name">{{ message.nickname }}</span>
                <span class="message-time">{{ message.createTime }}</span>
              </div>
            </div>
            <div v-if="message.address" class="message-location">
              <el-icon><Location /></el-icon>
              {{ message.address }}
            </div>
          </div>
          
          <div class="message-content">
            {{ message.content }}
          </div>
        </cyber-card>

        <!-- 空状态 -->
        <div v-if="!loading && messages.length === 0" class="empty-state">
          <el-empty description="还没有留言，快来抢沙发吧！" />
        </div>

        <!-- 分页 -->
        <el-config-provider :locale="locale">
          <el-pagination
            v-if="messages.length > 0"
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 30]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            class="pagination"
          />
        </el-config-provider>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElConfigProvider } from 'element-plus'
import { Edit, Promotion, Location } from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { getMessagePage, saveMessage } from '@/api/message'
import { isMessageLoginRequired } from '@/api/config'
import { useUserStore } from '@/store/user'
import type { Message } from '@/types'
import CyberCard from '@/components/CyberCard.vue'
import CyberButton from '@/components/CyberButton.vue'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const messages = ref<Message[]>([])
const messageFormRef = ref()
const loginRequired = ref(false)
const locale = zhCn

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 判断是否已登录
const isLoggedIn = computed(() => !!userStore.token)
const userInfo = computed(() => userStore.userInfo)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 留言表单
const messageForm = reactive({
  content: '',
  nickname: '',
  email: ''
})

// 检查留言是否需要登录
const checkLoginRequired = async () => {
  try {
    const res = await isMessageLoginRequired()
    loginRequired.value = res.data
  } catch (error) {
    console.error('检查留言登录配置失败:', error)
    loginRequired.value = false
  }
}

// 获取留言列表
const getMessages = async () => {
  loading.value = true
  
  try {
    const res = await getMessagePage({
      page: pagination.page,
      size: pagination.size,
      status: 1 // 只查询已通过的留言
    })
    // 确保按时间降序排列（最新的在最前面）
    messages.value = res.data.records.sort((a: any, b: any) => {
      return new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
    })
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取留言列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 分页切换处理
const handlePageChange = async (page: number) => {
  pagination.page = page
  await getMessages()
  // 滚动到顶部以查看新页面内容
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = async (size: number) => {
  pagination.size = size
  pagination.page = 1 // 改变每页数量时回到第一页
  await getMessages()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 提交留言
const submitMessage = async () => {
  // 防止重复提交
  if (submitting.value) {
    return
  }
  
  if (!messageForm.content.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  // 如果未登录且需要登录
  if (loginRequired.value && !isLoggedIn.value) {
    ElMessage.warning('请先登录后再留言')
    return
  }
  
  // 如果未登录且允许匿名留言，检查昵称
  if (!isLoggedIn.value && !messageForm.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }

  submitting.value = true
  try {
    await saveMessage(messageForm)
    ElMessage.success('留言成功！')
    
    // 重置表单
    messageForm.content = ''
    if (!isLoggedIn.value) {
      messageForm.nickname = ''
      messageForm.email = ''
    }
    
    // 刷新留言列表，回到第一页以查看新留言
    pagination.page = 1
    await getMessages()
    // 滚动到留言列表顶部
    window.scrollTo({ top: 300, behavior: 'smooth' })
  } catch (error: any) {
    console.error('留言失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(() => {
  checkLoginRequired()
  getMessages()
})
</script>

<style lang="scss" scoped>
.guestbook-page {
  min-height: 100vh;
  background: transparent;
  position: relative;
}

.page-title {
  position: relative;
  font-size: 56px;
  font-weight: 700;
  color: $neon-cyan;
  text-shadow: 0 0 10px rgba($neon-cyan, 0.5), 0 0 20px rgba($neon-cyan, 0.3);
  text-align: center;
  margin-bottom: 60px;
  text-transform: uppercase;
  letter-spacing: 8px;
  animation: neon-glow 2s ease-in-out infinite alternate;
  
  // 添加装饰线
  &::after {
    content: '';
    position: absolute;
    bottom: -20px;
    left: 50%;
    transform: translateX(-50%);
    width: 200px;
    height: 2px;
    background: linear-gradient(90deg, transparent, $neon-cyan, transparent);
    box-shadow: 0 0 8px rgba($neon-cyan, 0.6);
  }
}

@keyframes neon-glow {
  from {
    text-shadow: 0 0 8px rgba($neon-cyan, 0.4), 0 0 15px rgba($neon-cyan, 0.3);
  }
  to {
    text-shadow: 0 0 12px rgba($neon-cyan, 0.6), 0 0 20px rgba($neon-cyan, 0.4), 0 0 25px rgba($neon-cyan, 0.2);
  }
}

.guestbook-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 1;
}

.message-input-card {
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
  
  // 增强毛玻璃效果
  :deep(.cyber-card-wrapper) {
    background: rgba($bg-card, 0.75) !important;
    backdrop-filter: blur(20px) saturate(150%) !important;
    -webkit-backdrop-filter: blur(20px) saturate(150%) !important;
    border: 1px solid rgba($neon-cyan, 0.2);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.08);
  }
  
  // 添加边角装饰
  &::before,
  &::after {
    content: '';
    position: absolute;
    width: 20px;
    height: 20px;
    border: 2px solid $neon-magenta;
    z-index: 1;
    pointer-events: none;
  }
  
  &::before {
    top: 10px;
    left: 10px;
    border-right: none;
    border-bottom: none;
    box-shadow: -2px -2px 10px rgba($neon-magenta, 0.5);
  }
  
  &::after {
    bottom: 10px;
    right: 10px;
    border-left: none;
    border-top: none;
    box-shadow: 2px 2px 10px rgba($neon-magenta, 0.5);
  }

  .section-title {
    font-size: 24px;
    font-weight: 600;
    color: $neon-cyan;
    margin-bottom: 28px;
    display: flex;
    align-items: center;
    gap: 12px;
    text-transform: uppercase;
    letter-spacing: 2px;
    
    .el-icon {
      font-size: 28px;
      animation: pulse 2s ease-in-out infinite;
    }
  }

  .login-alert {
    margin-bottom: 20px;
    border-left: 3px solid $neon-yellow;
    background: rgba($neon-yellow, 0.05) !important;
  }

  .form-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;

    .guest-info {
      display: flex;
      align-items: center;
    }

    .logged-user-info {
      flex: 1;
      
      .info-text {
        display: inline-block;
        padding: 8px 16px;
        background: rgba($neon-cyan, 0.1);
        border: 1px solid rgba($neon-cyan, 0.3);
        border-radius: 20px;
        color: $neon-cyan;
        font-size: 14px;
        font-weight: 500;
        text-shadow: 0 0 10px rgba($neon-cyan, 0.5);
        animation: badge-glow 2s ease-in-out infinite;
      }
    }
  }
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

@keyframes badge-glow {
  0%, 100% {
    box-shadow: 0 0 5px rgba($neon-cyan, 0.3);
  }
  50% {
    box-shadow: 0 0 15px rgba($neon-cyan, 0.6), inset 0 0 10px rgba($neon-cyan, 0.2);
  }
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-height: 400px;
}

.message-item {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  
  // 增强毛玻璃效果
  :deep(.cyber-card-wrapper) {
    background: rgba($bg-card, 0.7) !important;
    backdrop-filter: blur(20px) saturate(150%) !important;
    -webkit-backdrop-filter: blur(20px) saturate(150%) !important;
    border: 1px solid rgba($neon-cyan, 0.15);
    box-shadow: 
      0 4px 16px rgba(0, 0, 0, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.05);
  }
  
  // 添加环绕渐变边框
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 12px;
    padding: 2px;
    background: linear-gradient(90deg, $neon-cyan, $neon-magenta, $neon-purple, $neon-cyan, $neon-magenta);
    background-size: 200% 100%;
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    mask-composite: exclude;
    opacity: 0;
    transition: opacity 0.4s;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 
      0 8px 24px rgba($neon-cyan, 0.3),
      0 0 30px rgba($neon-magenta, 0.2);
    
    :deep(.cyber-card-wrapper) {
      backdrop-filter: blur(25px) saturate(180%) !important;
      -webkit-backdrop-filter: blur(25px) saturate(180%) !important;
      border-color: rgba($neon-cyan, 0.3);
    }
    
    &::before {
      opacity: 1;
      animation: border-flow 3s linear infinite;
    }
  }
}

@keyframes border-flow {
  0% {
    background-position: 0% 0%;
  }
  100% {
    background-position: 200% 0%;
  }
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba($neon-cyan, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: 2px solid rgba($neon-cyan, 0.5);
  box-shadow: 0 0 15px rgba($neon-cyan, 0.3);
  transition: all 0.3s;
  
  &:hover {
    border-color: $neon-cyan;
    box-shadow: 0 0 20px rgba($neon-cyan, 0.6);
    transform: scale(1.05);
  }
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.user-name {
  color: $neon-cyan;
  font-size: 17px;
  font-weight: 600;
  text-shadow: 0 0 8px rgba($neon-cyan, 0.4);
  letter-spacing: 0.5px;
}

.message-time {
  color: $text-secondary;
  font-size: 12px;
  font-family: $font-family-code;
  opacity: 0.8;
}

.message-location {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: rgba($neon-purple, 0.1);
  border: 1px solid rgba($neon-purple, 0.3);
  border-radius: 12px;
  color: $neon-purple;
  font-size: 13px;
  font-weight: 500;

  .el-icon {
    font-size: 14px;
  }
}

.message-content {
  color: $text-primary;
  line-height: 1.9;
  font-size: 15px;
  word-break: break-all;
  white-space: pre-wrap;
  background: linear-gradient(135deg, rgba($bg-secondary, 0.6), rgba($bg-secondary, 0.4));
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 10px;
  border-left: 4px solid $neon-cyan;
  box-shadow: 
    inset 0 0 20px rgba($neon-cyan, 0.08),
    inset 1px 1px 0 rgba(255, 255, 255, 0.03);
  position: relative;
  overflow: hidden;
  
  // 添加装饰性渐变效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 120px;
    height: 120px;
    background: radial-gradient(circle, rgba($neon-cyan, 0.12), transparent 70%);
    pointer-events: none;
  }
  
  // 添加底部微光
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba($neon-cyan, 0.3), transparent);
    opacity: 0.5;
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  
  :deep(.el-empty) {
    .el-empty__description {
      color: $text-secondary;
      font-size: 16px;
    }
  }
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  
  // 美化分页组件
  :deep(.el-pagination) {
    .btn-prev,
    .btn-next,
    .el-pager li {
      background: rgba($bg-secondary, 0.6);
      border: 1px solid rgba($neon-cyan, 0.2);
      color: $text-primary;
      transition: all 0.3s;
      
      &:hover {
        background: rgba($neon-cyan, 0.1);
        border-color: $neon-cyan;
        color: $neon-cyan;
        box-shadow: 0 0 10px rgba($neon-cyan, 0.3);
      }
      
      &.is-active {
        background: rgba($neon-cyan, 0.2);
        border-color: $neon-cyan;
        color: $neon-cyan;
        box-shadow: 0 0 15px rgba($neon-cyan, 0.4);
      }
    }
    
    .el-pagination__total,
    .el-pagination__jump {
      color: $text-secondary;
    }
    
    .el-select {
      .el-input__wrapper {
        background: rgba($bg-secondary, 0.6);
        border: 1px solid rgba($neon-cyan, 0.2);
        box-shadow: none;
        
        &:hover {
          border-color: $neon-cyan;
          box-shadow: 0 0 10px rgba($neon-cyan, 0.2);
        }
      }
      
      .el-input__inner {
        color: $text-primary;
      }
    }
  }
}

// Element Plus 组件样式定制
:deep(.el-form) {
  .el-textarea__inner {
    background: rgba($bg-secondary, 0.8);
    border: 2px solid rgba($neon-cyan, 0.3);
    border-radius: 12px;
    color: $text-primary;
    font-size: 15px;
    line-height: 1.8;
    padding: 16px;
    transition: all 0.3s;
    box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.3);
    
    &:hover {
      border-color: rgba($neon-cyan, 0.5);
      background: rgba($bg-secondary, 0.9);
    }
    
    &:focus {
      border-color: $neon-cyan;
      background: rgba($bg-secondary, 1);
      box-shadow: 
        0 0 20px rgba($neon-cyan, 0.3),
        inset 0 0 30px rgba($neon-cyan, 0.05);
      outline: none;
    }
    
    &::placeholder {
      color: $text-disabled;
      font-style: italic;
    }
  }
  
  .el-textarea__count {
    background: transparent !important;
    color: $text-secondary !important;
    font-family: $font-family-code;
    font-size: 12px;
  }
  
  .el-input__count {
    background: transparent !important;
    
    .el-input__count-inner {
      background: rgba($bg-secondary, 0.8) !important;
      color: $text-secondary !important;
      border: 1px solid rgba($neon-cyan, 0.2);
      border-radius: 4px;
      padding: 2px 8px;
      font-family: $font-family-code;
    }
  }
  
  .el-input__wrapper {
    background: rgba($bg-secondary, 0.8);
    border: 2px solid rgba($neon-cyan, 0.3);
    border-radius: 8px;
    padding: 8px 14px;
    box-shadow: inset 0 0 15px rgba(0, 0, 0, 0.3);
    transition: all 0.3s;
    
    &:hover {
      border-color: rgba($neon-cyan, 0.5);
      background: rgba($bg-secondary, 0.9);
    }
    
    &.is-focus {
      border-color: $neon-cyan;
      background: rgba($bg-secondary, 1);
      box-shadow: 
        0 0 15px rgba($neon-cyan, 0.3),
        inset 0 0 20px rgba($neon-cyan, 0.05);
    }
  }
  
  .el-input__inner {
    color: $text-primary;
    font-size: 14px;
    
    &::placeholder {
      color: $text-disabled;
      font-style: italic;
    }
  }
  
  .el-input__suffix {
    .el-input__count-inner {
      background: transparent;
      color: $text-secondary;
    }
  }
}

// Alert 组件样式定制
:deep(.el-alert) {
  border-radius: 10px;
  backdrop-filter: blur(10px);
  
  &.el-alert--warning {
    background: rgba($neon-yellow, 0.1) !important;
    border: 1px solid rgba($neon-yellow, 0.4);
    
    .el-alert__title {
      color: $neon-yellow;
      font-weight: 600;
      text-shadow: 0 0 10px rgba($neon-yellow, 0.3);
    }
    
    .el-alert__description {
      color: $text-primary;
    }
    
    .el-alert__icon {
      color: $neon-yellow;
      filter: drop-shadow(0 0 5px rgba($neon-yellow, 0.5));
    }
  }
}

// Loading 样式定制
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

// CyberButton 增强样式
:deep(.cyber-button) {
  backdrop-filter: blur(5px);
  font-weight: 600;
  
  &:hover {
    transform: translateY(-2px);
  }
  
  &:active {
    transform: translateY(0);
  }
}


@media (max-width: 768px) {
  .page-title {
    font-size: 36px;
    letter-spacing: 4px;
    margin-bottom: 40px;
  }

  .message-input-card {
    &::before,
    &::after {
      width: 15px;
      height: 15px;
    }
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .form-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;

    .guest-info {
      flex-direction: column;
      
      :deep(.el-input) {
        width: 100% !important;
        margin-right: 0 !important;
        margin-bottom: 8px;
      }
    }
  }
  
  .message-item {
    &:hover {
      transform: translateY(-2px);
    }
  }
}
</style>

<style lang="scss">
// 全局样式 - 影响 Element Plus 弹出组件
.el-select-dropdown,
.el-popper {
  background: rgba($bg-secondary, 0.95) !important;
  border: 1px solid rgba($neon-cyan, 0.3) !important;
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba($neon-cyan, 0.2) !important;
  
  .el-select-dropdown__item {
    color: $text-primary !important;
    transition: all 0.3s;
    
    &:hover {
      background: rgba($neon-cyan, 0.15) !important;
      color: $neon-cyan !important;
    }
    
    &.is-selected {
      background: rgba($neon-cyan, 0.2) !important;
      color: $neon-cyan !important;
      font-weight: 600;
    }
  }
  
  .el-popper__arrow::before {
    background: rgba($bg-secondary, 0.95) !important;
    border: 1px solid rgba($neon-cyan, 0.3) !important;
  }
}

// 分页输入框样式
.el-pagination__editor {
  .el-input__wrapper {
    background: rgba($bg-secondary, 0.6) !important;
    border: 1px solid rgba($neon-cyan, 0.2) !important;
    box-shadow: none !important;
    
    &:hover {
      border-color: $neon-cyan !important;
    }
  }
  
  .el-input__inner {
    color: $text-primary !important;
  }
}

// 修复 textarea 字数统计背景
.guestbook-page {
  .el-textarea__count,
  .el-input__count {
    background: transparent !important;
  }
  
  .el-input__count-inner {
    background: rgba($bg-secondary, 0.9) !important;
    color: $text-secondary !important;
    border: 1px solid rgba($neon-cyan, 0.2) !important;
    border-radius: 4px;
    padding: 2px 8px;
    font-family: $font-family-code;
  }
}
</style>
