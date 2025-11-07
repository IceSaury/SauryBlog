import request from '@/utils/request'

export interface CategoryStats {
  name: string
  count: number
}

export interface TagStats {
  name: string
  count: number
}

export interface ArticleViewStats {
  title: string
  viewCount: number
}

export interface DailyStats {
  date: string
  count: number
}

export interface DashboardStats {
  articleCount: number
  projectCount: number
  totalViews: number
  commentCount: number
  userCount: number
  messageCount: number
  articleTrend: number
  projectTrend: number
  viewTrend: number
  commentTrend: number
  categoryStats: CategoryStats[]
  tagStats: TagStats[]
  topViewedArticles: ArticleViewStats[]
  dailyViewStats: DailyStats[]
  articleStatusStats: Record<string, number>
  commentStatusStats: Record<string, number>
}

/**
 * 获取仪表盘统计数据
 */
export const getDashboardStats = () => {
  return request<DashboardStats>({
    url: '/dashboard/stats',
    method: 'get'
  })
}

