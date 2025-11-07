<template>
  <div class="blog-detail-page">
    <div v-loading="loading" class="page-container">
      <!-- 文章内容 -->
      <cyber-card v-if="article" class="article-container">
        <!-- 文章头部 -->
        <div class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          
          <div class="article-meta">
            <div class="meta-left">
              <span><el-icon><User /></el-icon> {{ article.authorNickname }}</span>
              <span><el-icon><Calendar /></el-icon> {{ article.createTime }}</span>
              <span><el-icon><Folder /></el-icon> {{ article.categoryName }}</span>
            </div>
            <div class="meta-right">
              <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
            </div>
          </div>

          <div v-if="article.tags && article.tags.length" class="article-tags">
            <cyber-tag
              v-for="tag in article.tags"
              :key="tag.id"
              :color="tag.color as any"
            >
              {{ tag.name }}
            </cyber-tag>
          </div>
        </div>

        <!-- 文章内容 -->
        <div class="article-content markdown-body" v-html="renderedContent"></div>
      </cyber-card>

      <!-- 评论区 -->
      <cyber-card class="comment-section">
        <h2 class="section-title">
          <el-icon><ChatDotRound /></el-icon>
          评论 ({{ comments.length }})
        </h2>

        <!-- 评论输入框 -->
        <div class="comment-input-section">
          <!-- 需要登录提示 -->
          <el-alert
            v-if="loginRequired && !isLoggedIn"
            title="需要登录"
            type="warning"
            description="当前评论功能需要登录后才能使用，请先登录。"
            :closable="false"
            show-icon
            class="login-alert"
          />
          
          <el-form v-else :model="commentForm" ref="commentFormRef" @submit.prevent="submitComment">
            <el-form-item>
              <el-input
                v-model="commentForm.content"
                type="textarea"
                :rows="4"
                placeholder="写下你的评论..."
                maxlength="500"
                show-word-limit
                @keydown.enter.ctrl="submitComment"
              />
            </el-form-item>
            
            <div class="comment-footer">
              <!-- 游客信息输入（未登录时显示） -->
              <div v-if="!isLoggedIn" class="guest-info">
                <el-input
                  v-model="commentForm.nickname"
                  placeholder="昵称 *"
                  style="width: 150px; margin-right: 12px"
                />
                <el-input
                  v-model="commentForm.email"
                  placeholder="邮箱（可选）"
                  style="width: 200px"
                />
              </div>
              <!-- 登录用户信息显示 -->
              <div v-else class="logged-user-info">
                <el-icon class="user-icon"><User /></el-icon>
                <span class="info-text">以 <strong>{{ userInfo?.nickname }}</strong> 的身份评论</span>
              </div>
              <cyber-button type="cyan" :disabled="submitting" @click="submitComment">
                <el-icon><ChatDotRound /></el-icon>
                {{ submitting ? '发送中...' : '发表评论' }}
              </cyber-button>
            </div>
          </el-form>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <div
            v-for="comment in comments"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-avatar">
              <img :src="comment.avatar || defaultAvatar" alt="avatar" />
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.nickname }}</span>
                <span class="comment-time">{{ comment.createTime }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              
              <!-- 评论操作按钮 -->
              <div class="comment-actions">
                <button class="action-btn like-btn" :class="{ liked: comment.isLiked }" @click="handleLikeComment(comment)">
                  <el-icon><StarFilled v-if="comment.isLiked" /><Star v-else /></el-icon>
                  <span>{{ comment.likeCount || 0 }}</span>
                </button>
                <button class="action-btn reply-btn" @click="handleReply(comment)">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>回复</span>
                </button>
                <button v-if="isAdmin" class="action-btn delete-btn" @click="handleDeleteComment(comment.id)">
                  <el-icon><Delete /></el-icon>
                  <span>删除</span>
                </button>
              </div>

              <!-- 回复输入框 -->
              <div v-if="replyingTo === comment.id" class="reply-input-box">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="3"
                  :placeholder="`回复 @${comment.nickname}`"
                  maxlength="500"
                  show-word-limit
                />
                <div class="reply-actions">
                  <cyber-button size="small" @click="cancelReply">取消</cyber-button>
                  <cyber-button type="cyan" size="small" @click="submitReply(comment)">发送</cyber-button>
                </div>
              </div>
              
              <!-- 子评论 -->
              <div v-if="comment.children && comment.children.length" class="comment-children">
                <template
                  v-for="child in comment.children"
                  :key="child.id"
                >
                  <div class="comment-item-child">
                    <div class="comment-avatar">
                      <img :src="child.avatar || defaultAvatar" alt="avatar" />
                    </div>
                    <div class="comment-content">
                      <div class="comment-header">
                        <span class="comment-author">{{ child.nickname }}</span>
                        <span v-if="child.replyUserNickname" class="reply-to">
                          回复 @{{ child.replyUserNickname }}
                        </span>
                        <span class="comment-time">{{ child.createTime }}</span>
                      </div>
                      <div class="comment-text">{{ child.content }}</div>
                      
                      <!-- 子评论操作按钮 -->
                      <div class="comment-actions">
                        <button class="action-btn like-btn" :class="{ liked: child.isLiked }" @click="handleLikeComment(child)">
                          <el-icon><StarFilled v-if="child.isLiked" /><Star v-else /></el-icon>
                          <span>{{ child.likeCount || 0 }}</span>
                        </button>
                        <button class="action-btn reply-btn" @click="handleReplyChild(comment, child)">
                          <el-icon><ChatDotRound /></el-icon>
                          <span>回复</span>
                        </button>
                        <button v-if="isAdmin" class="action-btn delete-btn" @click="handleDeleteComment(child.id)">
                          <el-icon><Delete /></el-icon>
                          <span>删除</span>
                        </button>
                      </div>
                      
                      <!-- 子评论回复输入框 -->
                      <div v-if="replyingTo === `${comment.id}-${child.id}`" class="reply-input-box">
                        <el-input
                          v-model="replyContent"
                          type="textarea"
                          :rows="3"
                          :placeholder="`回复 @${replyToUser}`"
                          maxlength="500"
                          show-word-limit
                        />
                        <div class="reply-actions">
                          <cyber-button size="small" @click="cancelReply">取消</cyber-button>
                          <cyber-button type="cyan" size="small" @click="submitReply(comment, child)">发送</cyber-button>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论，快来抢沙发吧！" />
          </div>
        </div>
      </cyber-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Calendar, Folder, View, ChatDotRound, Star, StarFilled, Delete } from '@element-plus/icons-vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'
import { getArticleDetail } from '@/api/article'
import { getCommentsByArticleId, saveComment, likeComment, deleteComment } from '@/api/comment'
import { isCommentLoginRequired } from '@/api/config'
import { useUserStore } from '@/store/user'
import type { Article, Comment } from '@/types'
import CyberCard from '@/components/CyberCard.vue'
import CyberTag from '@/components/CyberTag.vue'
import CyberButton from '@/components/CyberButton.vue'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const article = ref<Article | null>(null)
const comments = ref<Comment[]>([])
const commentFormRef = ref()
const loginRequired = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 判断用户是否已登录
const isLoggedIn = computed(() => !!userStore.token)
const userInfo = computed(() => userStore.userInfo)
// 判断是否是管理员
const isAdmin = computed(() => userStore.userInfo?.role === 0)

// 评论表单
const commentForm = reactive({
  articleId: 0,
  content: '',
  nickname: '',
  email: '',
  parentId: undefined as number | undefined
})

// 回复相关
const replyingTo = ref<number | string | null>(null)
const replyContent = ref('')
const replyToUser = ref('')

// 配置marked
marked.setOptions({
  breaks: true,
  gfm: true
})

// 配置代码高亮
marked.use({
  renderer: {
    code(code, lang) {
      const language = hljs.getLanguage(lang || '') ? lang : 'plaintext'
      const highlighted = hljs.highlight(code, { language: language || 'plaintext' }).value
      return `<pre><code class="hljs language-${lang}">${highlighted}</code></pre>`
    }
  }
})

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return marked(article.value.content)
})

// 获取文章详情
const getArticle = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await getArticleDetail(id)
    article.value = res.data
    commentForm.articleId = id
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('文章不存在或已被删除')
  } finally {
    loading.value = false
  }
}

// 检查评论是否需要登录
const checkLoginRequired = async () => {
  try {
    const res = await isCommentLoginRequired()
    loginRequired.value = res.data
  } catch (error) {
    console.error('检查评论登录配置失败:', error)
    loginRequired.value = false
  }
}

// 获取评论列表
const getComments = async () => {
  try {
    const id = Number(route.params.id)
    const res = await getCommentsByArticleId(id)
    comments.value = res.data
  } catch (error) {
    console.error('获取评论列表失败:', error)
  }
}

// 提交评论
const submitComment = async () => {
  // 防止重复提交
  if (submitting.value) {
    return
  }
  
  if (!commentForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  // 如果未登录，需要填写昵称
  if (!isLoggedIn.value && !commentForm.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }

  submitting.value = true
  try {
    await saveComment(commentForm)
    ElMessage.success('评论成功')
    
    // 重置表单
    commentForm.content = ''
    commentForm.parentId = undefined
    // 如果是游客，不清空昵称和邮箱（方便连续评论）
    if (isLoggedIn.value) {
      commentForm.nickname = ''
      commentForm.email = ''
    }
    
    // 刷新评论列表
    await getComments()
    
    // 更新文章评论数（不刷新整个页面）
    if (article.value) {
      article.value.commentCount = comments.value.length
    }
  } catch (error) {
    console.error('评论失败:', error)
  } finally {
    submitting.value = false
  }
}

// 点赞评论
const handleLikeComment = async (comment: Comment) => {
  try {
    const res = await likeComment(comment.id!)
    // 更新点赞状态
    comment.isLiked = res.data
    // 更新点赞数
    if (res.data) {
      comment.likeCount = (comment.likeCount || 0) + 1
    } else {
      comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1)
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  }
}

// 回复评论（父评论）
const handleReply = (comment: Comment) => {
  replyingTo.value = comment.id!
  replyContent.value = ''
  replyToUser.value = comment.nickname || ''
}

// 回复评论（子评论）
const handleReplyChild = (parentComment: Comment, childComment: Comment) => {
  replyingTo.value = `${parentComment.id}-${childComment.id}`
  replyContent.value = ''
  replyToUser.value = childComment.nickname || ''
  commentForm.parentId = parentComment.id
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
  replyToUser.value = ''
}

// 提交回复
const submitReply = async (parentComment: Comment, childComment?: Comment) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  // 如果未登录，需要填写昵称
  if (!isLoggedIn.value && !commentForm.nickname.trim()) {
    ElMessage.warning('请先在上方填写昵称')
    return
  }

  try {
    const replyData = {
      articleId: commentForm.articleId,
      content: replyContent.value,
      nickname: isLoggedIn.value ? '' : commentForm.nickname,
      email: isLoggedIn.value ? '' : commentForm.email,
      parentId: parentComment.id,
      replyUserId: childComment ? childComment.userId : parentComment.userId
    }
    
    await saveComment(replyData)
    ElMessage.success('回复成功')
    
    // 清空回复框
    cancelReply()
    
    // 刷新评论列表
    await getComments()
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  }
}

// 删除评论
const handleDeleteComment = async (commentId: number | undefined) => {
  if (!commentId) return
  
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await deleteComment(commentId)
    ElMessage.success('删除成功')
    
    // 刷新评论列表
    await getComments()
    
    // 更新文章评论数
    if (article.value) {
      article.value.commentCount = comments.value.length
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 初始化
onMounted(async () => {
  // 确保页面滚动到顶部 - 多重保险
  window.scrollTo(0, 0)
  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
  
  await nextTick()
  
  // 再次确保滚动到顶部
  window.scrollTo(0, 0)
  
  checkLoginRequired()
  getArticle()
  getComments()
})
</script>

<style lang="scss" scoped>
.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.article-container {
  margin-bottom: 32px;
}

.article-header {
  padding-bottom: 24px;
  border-bottom: 1px solid rgba($neon-cyan, 0.2);
  margin-bottom: 32px;
}

.article-title {
  font-size: 36px;
  color: $neon-cyan;
  margin-bottom: 20px;
  line-height: 1.4;
  text-shadow: 0 0 10px rgba($neon-cyan, 0.5);
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  color: $text-secondary;
  font-size: 14px;

  .meta-left,
  .meta-right {
    display: flex;
    gap: 20px;
  }

  span {
    display: flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      color: $neon-cyan;
    }
  }
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-content {
  line-height: 1.8;
  color: $text-primary;
  font-size: 16px;
  
  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    color: $neon-cyan;
    margin: 24px 0 16px;
  }
  
  :deep(p) {
    margin: 16px 0;
  }
  
  :deep(a) {
    color: $neon-magenta;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  :deep(code) {
    background: rgba($bg-secondary, 0.8);
    padding: 2px 6px;
    border-radius: 4px;
    color: $neon-yellow;
    font-family: 'Courier New', monospace;
  }
  
  :deep(pre) {
    background: rgba($bg-secondary, 0.9);
    padding: 16px;
    border-radius: 8px;
    border: 1px solid rgba($neon-cyan, 0.3);
    overflow-x: auto;
    margin: 16px 0;
    
    code {
      background: none;
      padding: 0;
      color: inherit;
    }
  }
  
  :deep(blockquote) {
    border-left: 4px solid $neon-cyan;
    padding-left: 16px;
    margin: 16px 0;
    color: $text-secondary;
    background: rgba($bg-secondary, 0.3);
    padding: 12px 16px;
    border-radius: 0 4px 4px 0;
  }
  
  :deep(ul), :deep(ol) {
    padding-left: 24px;
    margin: 16px 0;
  }
  
  :deep(li) {
    margin: 8px 0;
  }
  
  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
    margin: 16px 0;
    border: 1px solid rgba($neon-cyan, 0.3);
  }
  
  :deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 16px 0;
    
    th, td {
      border: 1px solid rgba($neon-cyan, 0.3);
      padding: 8px 12px;
    }
    
    th {
      background: rgba($bg-secondary, 0.8);
      color: $neon-cyan;
    }
  }
}

.comment-section {
  .section-title {
    font-size: 24px;
    color: $neon-cyan;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.comment-input-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba($neon-cyan, 0.2);
  
  .login-alert {
    border-radius: 8px;
    background: rgba($neon-yellow, 0.05);
    border: 1px solid rgba($neon-yellow, 0.3);
    
    :deep(.el-alert__title) {
      color: $neon-yellow;
      font-weight: 600;
    }
    
    :deep(.el-alert__description) {
      color: $text-secondary;
    }
    
    :deep(.el-alert__icon) {
      color: $neon-yellow;
    }
  }
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  
  .guest-info {
    display: flex;
    align-items: center;
  }

  .logged-user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: $text-primary;
    font-size: 14px;
    
    .user-icon {
      color: $neon-cyan;
      font-size: 18px;
    }
    
    .info-text {
      color: $text-secondary;
      
      strong {
        color: $neon-cyan;
        font-weight: 600;
        text-shadow: 0 0 5px rgba($neon-cyan, 0.5);
      }
    }
  }
}

.comment-list {
  .comment-item {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .comment-avatar {
    flex-shrink: 0;
    
    img {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      border: 2px solid rgba($neon-cyan, 0.3);
    }
  }
  
  .comment-content {
    flex: 1;
  }
  
  .comment-header {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .comment-author {
    color: $neon-cyan;
    font-weight: 500;
  }
  
  .reply-to {
    color: $text-secondary;
    font-size: 14px;
  }
  
  .comment-time {
    color: $text-secondary;
    font-size: 12px;
  }
  
  .comment-text {
    color: $text-primary;
    line-height: 1.6;
    word-break: break-all;
    margin-bottom: 12px;
  }
  
  .comment-actions {
    display: flex;
    gap: 16px;
    margin-top: 8px;
    
    .action-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 4px 12px;
      background: rgba($bg-secondary, 0.5);
      border: 1px solid rgba($neon-cyan, 0.3);
      border-radius: 16px;
      color: $text-secondary;
      font-size: 13px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: $neon-cyan;
        color: $neon-cyan;
        background: rgba($neon-cyan, 0.1);
        transform: translateY(-2px);
      }
      
      .el-icon {
        font-size: 14px;
      }
      
      &.like-btn.liked {
        border-color: $neon-yellow;
        color: $neon-yellow;
        background: rgba($neon-yellow, 0.1);
        
        .el-icon {
          animation: likeAnimation 0.3s ease;
        }
      }
      
      &.delete-btn {
        &:hover {
          border-color: #ff4444;
          color: #ff4444;
          background: rgba(#ff4444, 0.1);
        }
      }
    }
  }
  
  .reply-input-box {
    margin-top: 12px;
    padding: 12px;
    background: rgba($bg-secondary, 0.3);
    border: 1px solid rgba($neon-cyan, 0.2);
    border-radius: 8px;
    
    .reply-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      margin-top: 8px;
    }
  }
  
  .comment-children {
    margin-top: 16px;
    padding-left: 24px;
    border-left: 2px solid rgba($neon-cyan, 0.2);
    
    .comment-item-child {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .comment-avatar img {
        width: 32px;
        height: 32px;
      }
    }
  }
}

.empty-comments {
  padding: 40px 0;
  text-align: center;
}

@keyframes likeAnimation {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
}

@media (max-width: 768px) {
  .article-title {
    font-size: 24px;
  }

  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .comment-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;

    .guest-info {
      flex-direction: column;
      
      .el-input {
        width: 100% !important;
        margin-right: 0 !important;
        margin-bottom: 8px;
      }
    }
  }
}
</style>
