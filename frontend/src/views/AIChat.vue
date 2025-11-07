<template>
  <div class="ai-chat-page">
    <h1 class="page-title">AI 初音未来</h1>
    
    <div class="chat-layout">
      <!-- 历史会话侧边栏 -->
      <div :class="['session-sidebar', { 'sidebar-open': sidebarOpen }]">
        <div class="sidebar-header">
          <h3>历史对话</h3>
          <button @click="toggleSidebar" class="btn-close-sidebar">✕</button>
        </div>
        <div class="session-list">
          <div 
            v-for="session in sessionList" 
            :key="session.id"
            :class="['session-item', { 'active': session.id === currentSessionId }]"
            @click="loadSession(session.id)"
          >
            <div class="session-item-icon">💬</div>
            <div class="session-item-content">
              <div class="session-item-title">{{ session.title }}</div>
              <div class="session-item-meta">
                <span>{{ session.messageCount }} 条消息</span>
                <span>{{ formatSessionTime(session.updateTime) }}</span>
              </div>
            </div>
          </div>
          <div v-if="sessionList.length === 0" class="empty-sessions">
            <div class="empty-icon">📝</div>
            <p>暂无历史对话</p>
          </div>
        </div>
      </div>

      <!-- 主聊天区域 -->
      <div class="chat-container">
        <!-- 工具栏 -->
        <div class="chat-toolbar">
          <div class="session-info">
            <button @click="toggleSidebar" class="btn-toggle-sidebar">
              <span class="icon">☰</span>
            </button>
            <span class="session-icon">💬</span>
            <div class="session-details">
              <span class="session-title">{{ getSessionTitle() }}</span>
              <span v-if="messages.length > 0" class="session-meta">{{ messages.length }} 条消息</span>
            </div>
          </div>
          <div class="toolbar-actions">
            <button @click="handleNewSession" class="btn-new-session">
              <span class="icon">✨</span> 新会话
            </button>
            <button 
              v-if="messages.length > 0" 
              @click="handleClearHistory" 
              class="btn-clear"
            >
              <span class="icon">🗑️</span> 清空
            </button>
          </div>
        </div>

      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="messages.length === 0" class="empty-state">
          <div class="avatar-container">
            <div class="avatar-glow"></div>
            <img src="https://saury-blog.oss-cn-shenzhen.aliyuncs.com/AI/head3.jpg" alt="Miku" class="avatar" />
          </div>
          <h3>你好! 我是 Miku! 很高兴认识你!</h3> 
          <div class="typing-text">
            <span class="typing-content">我可以帮你解答问题、提供建议，随时为你服务！</span>
            <span class="cursor">|</span>
          </div>
        </div>

        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['message', `message-${message.role}`]"
        >
          <div class="message-avatar" :class="message.role">
            <img 
              v-if="message.role === 'user'" 
              :src="userStore.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
              alt="用户头像"
              class="avatar-img"
              loading="lazy"
            />
            <img 
              v-else 
              src="https://saury-blog.oss-cn-shenzhen.aliyuncs.com/AI/head4.jpg" 
              alt="AI助手"
              class="avatar-img"
              loading="lazy"
            />
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(message.content)"></div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="message message-assistant">
          <div class="message-avatar assistant">
            <img 
              src="https://saury-blog.oss-cn-shenzhen.aliyuncs.com/AI/head4.jpg" 
              alt="AI助手"
              class="avatar-img"
              loading="lazy"
            />
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-container">
        <textarea
          v-model="inputMessage"
          @keydown.enter.exact.prevent="handleSend"
          @keydown.enter.shift.exact="inputMessage += '\n'"
          placeholder="输入消息... (Enter发送，Shift+Enter换行)"
          class="message-input"
          rows="1"
          :disabled="isLoading"
        ></textarea>
        <button 
          @click="handleSend" 
          :disabled="!inputMessage.trim() || isLoading"
          class="btn-send"
        >
          <span v-if="!isLoading">发送 ➤</span>
          <span v-else>发送中...</span>
        </button>
      </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { sendMessageStream, getChatHistory, clearChatHistory, type ChatMessage } from '@/api/chat'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { marked } from 'marked'
import hljs from 'highlight.js'

const userStore = useUserStore()
const router = useRouter()

const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const currentSessionId = ref<string>('')
const messagesContainer = ref<HTMLElement>()
const streamingMessage = ref('') // 正在流式接收的消息
const sidebarOpen = ref(false) // 侧边栏是否打开
const sessionList = ref<SessionInfo[]>([]) // 会话列表

// 会话信息接口
interface SessionInfo {
  id: string
  title: string
  messageCount: number
  updateTime: number
}

// 配置 marked 渲染器
const renderer = new marked.Renderer()
renderer.code = function(code: string, language: string | undefined) {
  if (language && hljs.getLanguage(language)) {
    try {
      const highlighted = hljs.highlight(code, { language }).value
      return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`
    } catch (err) {
      console.error('代码高亮失败', err)
    }
  }
  const highlighted = hljs.highlightAuto(code).value
  return `<pre><code class="hljs">${highlighted}</code></pre>`
}

marked.setOptions({
  renderer,
  breaks: true, // 支持换行
  gfm: true // 支持GitHub风格的Markdown
})

// 获取会话标题
function getSessionTitle(): string {
  if (!currentSessionId.value) {
    return '新会话'
  }
  
  if (messages.value.length === 0) {
    return '会话中'
  }
  
  // 如果有消息，基于第一条消息生成标题
  const firstMessage = messages.value.find(m => m.role === 'user')
  if (firstMessage) {
    // 取第一条用户消息的前15个字符作为标题
    const content = firstMessage.content.trim()
    if (content.length > 15) {
      return content.substring(0, 15) + '...'
    }
    return content
  }
  
  return '会话中'
}

// 切换侧边栏
function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}

// 格式化会话时间
function formatSessionTime(timestamp: number): string {
  const now = Date.now()
  const diff = now - timestamp
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    const date = new Date(timestamp)
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

// 加载所有会话列表
function loadAllSessions() {
  const sessions: SessionInfo[] = []
  
  // 遍历 localStorage，找出所有会话
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i)
    if (key && key.startsWith('chat_messages_')) {
      const sessionId = key.replace('chat_messages_', '')
      const messagesStr = localStorage.getItem(key)
      
      if (messagesStr) {
        try {
          const msgs: ChatMessage[] = JSON.parse(messagesStr)
          if (msgs.length > 0) {
            const firstUserMsg = msgs.find(m => m.role === 'user')
            let title = '新会话'
            
            if (firstUserMsg) {
              const content = firstUserMsg.content.trim()
              title = content.length > 20 ? content.substring(0, 20) + '...' : content
            }
            
            // 获取最后更新时间
            const lastMsg = msgs[msgs.length - 1]
            const updateTime = lastMsg?.timestamp || Date.now()
            
            sessions.push({
              id: sessionId,
              title,
              messageCount: msgs.length,
              updateTime
            })
          }
        } catch (error) {
          console.error('解析会话失败', error)
        }
      }
    }
  }
  
  // 按更新时间倒序排序
  sessions.sort((a, b) => b.updateTime - a.updateTime)
  sessionList.value = sessions
}

// 加载指定会话
async function loadSession(sessionId: string) {
  if (sessionId === currentSessionId.value) {
    // 如果是当前会话，只关闭侧边栏
    sidebarOpen.value = false
    return
  }
  
  // 先保存当前会话
  if (currentSessionId.value && messages.value.length > 0) {
    saveSessionToLocal()
  }
  
  // 加载新会话
  currentSessionId.value = sessionId
  localStorage.setItem('lastChatSessionId', sessionId)
  
  // 从本地加载消息
  const localMessages = localStorage.getItem(`chat_messages_${sessionId}`)
  if (localMessages) {
    try {
      messages.value = JSON.parse(localMessages)
      console.log('加载会话:', sessionId, messages.value.length, '条消息')
      scrollToBottom()
      sidebarOpen.value = false
    } catch (error) {
      console.error('加载会话失败', error)
      ElMessage.error('加载会话失败')
    }
  }
}

// 检查登录状态
onMounted(() => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 加载会话列表
  loadAllSessions()
  
  // 加载上次的会话历史
  loadLastSession()
})

// 加载最后一次会话
async function loadLastSession() {
  // 从localStorage获取最后的sessionId
  const lastSessionId = localStorage.getItem('lastChatSessionId')
  if (!lastSessionId) return
  
  currentSessionId.value = lastSessionId
  
  // 优先从本地加载消息（更快）
  const localMessages = localStorage.getItem(`chat_messages_${lastSessionId}`)
  if (localMessages) {
    try {
      messages.value = JSON.parse(localMessages)
      console.log('从本地加载了', messages.value.length, '条消息')
      scrollToBottom()
      return
    } catch (error) {
      console.error('解析本地消息失败', error)
    }
  }
  
  // 如果本地没有，尝试从服务器加载
  try {
    const res = await getChatHistory(lastSessionId)
    messages.value = res.data.messages
    console.log('从服务器加载了', messages.value.length, '条消息')
    // 保存到本地
    saveSessionToLocal()
    scrollToBottom()
  } catch (error) {
    console.error('加载会话历史失败', error)
  }
}

// 生成UUID（简单版本）
function generateSessionId(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// 保存会话到本地
function saveSessionToLocal() {
  if (currentSessionId.value) {
    localStorage.setItem('lastChatSessionId', currentSessionId.value)
    localStorage.setItem(`chat_messages_${currentSessionId.value}`, JSON.stringify(messages.value))
    
    // 刷新会话列表
    loadAllSessions()
  }
}

// 发送消息 - 使用流式响应
async function handleSend() {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 如果是新会话，生成sessionId
  if (!currentSessionId.value) {
    currentSessionId.value = generateSessionId()
    console.log('创建新会话:', currentSessionId.value)
  }
  
  // 添加用户消息到列表
  const timestamp = Date.now()
  messages.value.push({
    role: 'user',
    content: userMessage,
    timestamp
  })
  
  // 保存到本地
  saveSessionToLocal()
  
  scrollToBottom()
  isLoading.value = true
  streamingMessage.value = ''
  
  let aiMessageIndex = -1 // 用于跟踪AI消息的索引
  
  try {
    await sendMessageStream(
      {
        message: userMessage,
        sessionId: currentSessionId.value || undefined
      },
      // onMessage - 接收流式内容
      (content: string) => {
        streamingMessage.value += content
        
        // 如果是第一次接收内容，才添加AI消息到列表
        if (aiMessageIndex === -1) {
          aiMessageIndex = messages.value.length
          messages.value.push({
            role: 'assistant',
            content: streamingMessage.value,
            timestamp: Date.now()
          })
          // 收到第一个内容后，关闭加载状态（隐藏加载动画）
          isLoading.value = false
        } else {
          // 更新已有消息的内容
          messages.value[aiMessageIndex].content = streamingMessage.value
        }
        
        scrollToBottom()
      },
      // onDone - 完成
      () => {
        if (aiMessageIndex !== -1) {
          messages.value[aiMessageIndex].timestamp = Date.now()
        }
        isLoading.value = false
        streamingMessage.value = ''
        
        // 保存完整对话到本地
        saveSessionToLocal()
      },
      // onError - 错误处理
      (error: string) => {
        ElMessage.error(error || '发送消息失败')
        // 只移除用户消息（AI消息可能还未添加）
        if (aiMessageIndex === -1) {
          // AI消息还未创建，只移除用户消息
          messages.value.pop()
        } else {
          // AI消息已创建，移除用户消息和AI消息
          messages.value.pop()
          messages.value.pop()
        }
        isLoading.value = false
        streamingMessage.value = ''
      }
    )
  } catch (error: any) {
    ElMessage.error(error.message || '发送消息失败')
    // 只移除用户消息（AI消息可能还未添加）
    if (aiMessageIndex === -1) {
      messages.value.pop()
    } else {
      messages.value.pop()
      messages.value.pop()
    }
    isLoading.value = false
    streamingMessage.value = ''
  }
}

// 新建会话
function handleNewSession() {
  if (messages.value.length > 0) {
    ElMessageBox.confirm('确定要开始新会话吗？当前会话将被保存。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 先保存当前会话
      saveSessionToLocal()
      
      // 清空当前状态
      currentSessionId.value = ''
      messages.value = []
      localStorage.removeItem('lastChatSessionId')
      ElMessage.success('已开始新会话')
    }).catch(() => {})
  }
}

// 清空历史
function handleClearHistory() {
  ElMessageBox.confirm('确定要清空当前会话的所有消息吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    if (currentSessionId.value) {
      try {
        // 清空服务器端的历史
        await clearChatHistory(currentSessionId.value)
        
        // 清空本地存储
        localStorage.removeItem(`chat_messages_${currentSessionId.value}`)
        localStorage.removeItem('lastChatSessionId')
        
        // 清空当前状态
        messages.value = []
        currentSessionId.value = ''
        
        // 刷新会话列表
        loadAllSessions()
        
        ElMessage.success('已清空会话')
      } catch (error: any) {
        ElMessage.error(error.message || '清空失败')
      }
    } else {
      messages.value = []
      ElMessage.success('已清空会话')
    }
  }).catch(() => {})
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 格式化消息内容（使用 Markdown 渲染）
function formatMessage(content: string) {
  try {
    // 使用 marked 渲染 Markdown
    return marked.parse(content) as string
  } catch (error) {
    console.error('Markdown 渲染失败', error)
    // 降级处理：简单的HTML转义和换行处理
    return content
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\n/g, '<br>')
  }
}

// 格式化时间
function formatTime(timestamp: number) {
  const date = new Date(timestamp)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}
</script>

<style lang="scss">
// 导入代码高亮样式（全局样式，用于代码块）
@import 'highlight.js/styles/atom-one-dark.css';
</style>

<style lang="scss" scoped>
@use "sass:color";
@import '@/styles/variables.scss';

.ai-chat-page {
  min-height: calc(100vh - 80px);
  padding: 40px 20px;
}

.page-title {
  font-size: 48px;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  text-align: center;
  margin-bottom: 40px;
  animation: glow 2s ease-in-out infinite alternate;
}

.chat-layout {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  height: calc(100vh - 220px);
  min-height: 500px;
}

// 历史会话侧边栏
.session-sidebar {
  width: 280px;
  background: rgba(0, 20, 40, 0.6);
  border: 2px solid $neon-cyan;
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 255, 255, 0.3);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  
  @media (max-width: 768px) {
    position: fixed;
    left: -300px;
    top: 120px;
    z-index: 1000;
    height: calc(100vh - 140px);
    
    &.sidebar-open {
      left: 20px;
    }
  }
}

.sidebar-header {
  padding: 20px;
  background: rgba(0, 30, 60, 0.8);
  border-bottom: 1px solid $neon-cyan;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  h3 {
    color: $neon-cyan;
    font-size: 18px;
    margin: 0;
    text-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
  }
}

.btn-close-sidebar {
  background: none;
  border: none;
  color: $neon-cyan;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: none;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  
  &:hover {
    color: $neon-magenta;
    transform: rotate(90deg);
  }
  
  @media (max-width: 768px) {
    display: flex;
  }
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(0, 20, 40, 0.3);
  }
  
  &::-webkit-scrollbar-thumb {
    background: $neon-cyan;
    border-radius: 3px;
    
    &:hover {
      background: color.adjust($neon-cyan, $lightness: 10%);
    }
  }
}

.session-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  margin-bottom: 8px;
  background: rgba(0, 30, 60, 0.4);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: rgba(0, 255, 255, 0.1);
    border-color: $neon-cyan;
    box-shadow: 0 0 15px rgba(0, 255, 255, 0.3);
  }
  
  &.active {
    background: rgba(0, 255, 255, 0.15);
    border-color: $neon-cyan;
    box-shadow: 0 0 20px rgba(0, 255, 255, 0.4);
  }
}

.session-item-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.session-item-content {
  flex: 1;
  overflow: hidden;
}

.session-item-title {
  color: $text-primary;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-item-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: $text-secondary;
  font-family: 'Courier New', monospace;
  
  span {
    &:last-child {
      color: rgba(0, 255, 255, 0.6);
    }
  }
}

.empty-sessions {
  text-align: center;
  padding: 40px 20px;
  color: $text-secondary;
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: 12px;
    opacity: 0.5;
  }
  
  p {
    font-size: 14px;
  }
}

.chat-container {
  flex: 1;
  background: rgba(0, 20, 40, 0.6);
  border: 2px solid $neon-cyan;
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 255, 255, 0.3);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chat-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: rgba(0, 30, 60, 0.8);
  border-bottom: 1px solid $neon-cyan;
}

.session-info {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .session-icon {
    font-size: 24px;
    filter: drop-shadow(0 0 8px rgba(0, 255, 255, 0.6));
  }
  
  .session-details {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  
  .session-title {
    color: $neon-cyan;
    font-size: 16px;
    font-weight: 500;
    text-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
  }
  
  .session-meta {
    color: $text-secondary;
    font-size: 12px;
    font-family: 'Courier New', monospace;
  }
}

.btn-toggle-sidebar {
  background: none;
  border: 1px solid $neon-cyan;
  color: $neon-cyan;
  border-radius: 6px;
  cursor: pointer;
  font-size: 20px;
  padding: 4px 8px;
  transition: all 0.3s;
  display: none;
  
  &:hover {
    background: rgba(0, 255, 255, 0.2);
    box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
  }
  
  @media (max-width: 768px) {
    display: block;
  }
}

.toolbar-actions {
  display: flex;
  gap: 12px;
}

.btn-new-session,
.btn-clear {
  padding: 8px 16px;
  background: rgba(0, 255, 255, 0.1);
  border: 1px solid $neon-cyan;
  color: $neon-cyan;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  
  &:hover {
    background: rgba(0, 255, 255, 0.2);
    box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
  }
  
  .icon {
    margin-right: 4px;
  }
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(0, 20, 40, 0.3);
  }
  
  &::-webkit-scrollbar-thumb {
    background: $neon-cyan;
    border-radius: 4px;
    
    &:hover {
      background: color.adjust($neon-cyan, $lightness: 10%);
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: $text-secondary;
  
  .avatar-container {
    position: relative;
    width: 150px;
    height: 150px;
    margin: 0 auto 30px;
  }
  
  .avatar-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 170px;
    height: 170px;
    border-radius: 50%;
    background: linear-gradient(135deg, #00ffff, #ff00ff);
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
    box-shadow: 0 0 20px rgba(0, 255, 255, 0.6);
    object-fit: cover;
  }
  
  h3 {
    color: $neon-cyan;
    font-size: 24px;
    margin-bottom: 16px;
    text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
  }
  
  .typing-text {
    font-size: 16px;
    color: $text-primary;
    min-height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 4px;
    
    .cursor {
      color: $neon-cyan;
      animation: blink 1s infinite;
    }
  }
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease-in;
  
  &-user {
    flex-direction: row-reverse;
    
    .message-content {
      background: rgba(0, 255, 255, 0.1);
      border-color: $neon-cyan;
    }
  }
  
  &-assistant {
    .message-content {
      background: rgba(0, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border-color: $neon-cyan;
    }
  }
}

.message-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  border: 2px solid currentColor;
  overflow: hidden;
  position: relative;
  
  // 根据角色设置不同的背景色（更友好的渐变色）
  &.user {
    background: linear-gradient(135deg, rgba(0, 240, 255, 0.2), rgba(170, 0, 255, 0.2));
  }
  
  &.assistant {
    background: linear-gradient(135deg, rgba(255, 0, 170, 0.2), rgba(255, 200, 0, 0.2));
  }
  
  // 默认背景（兼容旧代码）
  &:not(.user):not(.assistant) {
    background: linear-gradient(135deg, rgba(0, 240, 255, 0.15), rgba(170, 0, 255, 0.15));
  }
  
  // 加载动画效果
  &::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.1),
      transparent
    );
    animation: shimmer 2s infinite;
    z-index: 1;
  }
  
  .avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    position: relative;
    z-index: 2;
    
    // 图片加载时的过渡效果
    opacity: 0;
    animation: fadeInAvatar 0.3s ease-in 0.1s forwards;
  }
}

// 加载闪烁动画
@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

// 图片淡入动画
@keyframes fadeInAvatar {
  to {
    opacity: 1;
  }
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid;
  
  :deep(pre) {
    background: rgba(0, 0, 0, 0.5);
    padding: 12px;
    border-radius: 6px;
    overflow-x: auto;
    margin: 8px 0;
  }
  
  :deep(code) {
    background: rgba(0, 0, 0, 0.3);
    padding: 2px 6px;
    border-radius: 3px;
    font-family: 'Courier New', monospace;
  }
}

.message-text {
  color: $text-primary;
  line-height: 1.6;
  
  // Markdown 渲染后的样式
  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    color: $neon-cyan;
    margin: 16px 0 8px 0;
    font-weight: 600;
  }
  
  :deep(h1) { font-size: 1.8em; }
  :deep(h2) { font-size: 1.5em; }
  :deep(h3) { font-size: 1.3em; }
  
  :deep(p) {
    margin: 8px 0;
  }
  
  :deep(strong) {
    color: $neon-cyan;
    font-weight: 600;
    text-shadow: 0 0 5px rgba(0, 255, 255, 0.3);
  }
  
  :deep(em) {
    color: $neon-magenta;
    font-style: italic;
  }
  
  :deep(ul), :deep(ol) {
    margin: 8px 0;
    padding-left: 24px;
  }
  
  :deep(li) {
    margin: 4px 0;
    line-height: 1.6;
  }
  
  :deep(blockquote) {
    border-left: 3px solid $neon-cyan;
    padding-left: 12px;
    margin: 12px 0;
    color: $text-secondary;
    font-style: italic;
  }
  
  :deep(a) {
    color: $neon-cyan;
    text-decoration: none;
    border-bottom: 1px solid $neon-cyan;
    
    &:hover {
      text-shadow: 0 0 8px rgba(0, 255, 255, 0.6);
    }
  }
  
  :deep(hr) {
    border: none;
    border-top: 1px solid rgba(0, 255, 255, 0.3);
    margin: 16px 0;
  }
  
  :deep(table) {
    border-collapse: collapse;
    width: 100%;
    margin: 12px 0;
    
    th, td {
      border: 1px solid rgba(0, 255, 255, 0.3);
      padding: 8px 12px;
      text-align: left;
    }
    
    th {
      background: rgba(0, 255, 255, 0.1);
      color: $neon-cyan;
      font-weight: 600;
    }
  }
  word-wrap: break-word;
}

.message-time {
  font-size: 12px;
  color: $text-secondary;
  margin-top: 6px;
  text-align: right;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
  
  span {
    width: 8px;
    height: 8px;
    background: $neon-magenta;
    border-radius: 50%;
    animation: typing 1.4s infinite;
    
    &:nth-child(2) {
      animation-delay: 0.2s;
    }
    
    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

.input-container {
  display: flex;
  gap: 12px;
  padding: 20px;
  background: rgba(0, 30, 60, 0.8);
  border-top: 1px solid $neon-cyan;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  background: rgba(0, 20, 40, 0.4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $neon-cyan;
  border-radius: 8px;
  color: $text-primary;
  font-size: 15px;
  resize: none;
  max-height: 120px;
  font-family: inherit;
  
  &:focus {
    outline: none;
    box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
    background: rgba(0, 20, 40, 0.5);
  }
  
  &::placeholder {
    color: $text-secondary;
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.btn-send {
  padding: 12px 24px;
  background: linear-gradient(135deg, $neon-cyan, $neon-magenta);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  
  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 0 20px rgba(0, 255, 255, 0.6);
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

@keyframes glow {
  from {
    text-shadow: 0 0 10px rgba(0, 255, 255, 0.5),
                 0 0 20px rgba(0, 255, 255, 0.3);
  }
  to {
    text-shadow: 0 0 20px rgba(0, 255, 255, 0.8),
                 0 0 30px rgba(0, 255, 255, 0.5);
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

@media (max-width: 768px) {
  .chat-container {
    height: calc(100vh - 180px);
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .page-title {
    font-size: 36px;
  }
}
</style>
