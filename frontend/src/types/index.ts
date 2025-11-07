// 用户相关类型
export interface UserInfo {
  userId: number
  username: string
  nickname: string
  avatar: string
  role: number
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  nickname: string
  email?: string
}

// 文章相关类型
export interface Article {
  id?: number
  userId?: number
  authorNickname?: string
  authorAvatar?: string
  categoryId?: number
  categoryName?: string
  title: string
  summary?: string
  cover?: string
  content: string
  tags?: Tag[]
  tagIds?: number[]
  viewCount?: number
  likeCount?: number
  commentCount?: number
  collectCount?: number
  isTop?: number
  isFeatured?: number
  status: number
  createTime?: string
  updateTime?: string
}

// 分类类型
export interface Category {
  id?: number
  name: string
  description?: string
  icon?: string
  sort?: number
  articleCount?: number
  createTime?: string
  updateTime?: string
}

// 标签类型
export interface Tag {
  id?: number
  name: string
  color?: string
  articleCount?: number
  createTime?: string
  updateTime?: string
}

// 项目类型
export interface Project {
  id?: number
  name: string
  description: string
  cover?: string
  images?: string[]
  techStack?: string[]
  githubUrl?: string
  demoUrl?: string
  highlights?: string[]
  type?: number
  sort?: number
  viewCount?: number
  status?: number
  createTime?: string
  updateTime?: string
}

// 评论类型
export interface Comment {
  id?: number
  articleId: number
  userId?: number
  parentId?: number
  replyUserId?: number
  replyUserNickname?: string
  content: string
  nickname?: string
  email?: string
  avatar?: string
  address?: string
  likeCount?: number
  isLiked?: boolean  // 当前用户是否已点赞
  children?: Comment[]
  createTime?: string
}

// 留言类型
export interface Message {
  id?: number
  userId?: number
  content: string
  nickname?: string
  email?: string
  avatar?: string
  address?: string
  status?: number
  createTime?: string
}

// 网站配置类型
export interface SiteConfig {
  id?: number
  configKey: string
  configValue: string
  description?: string
  createTime?: string
  updateTime?: string
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页响应类型
export interface PageResponse<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

