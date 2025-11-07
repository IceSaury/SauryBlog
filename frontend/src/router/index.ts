import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' },
      },
      {
        path: '/about',
        name: 'About',
        component: () => import('@/views/About.vue'),
        meta: { title: '关于我' },
      },
      {
        path: '/blog',
        name: 'Blog',
        component: () => import('@/views/Blog.vue'),
        meta: { title: '技术博客' },
      },
      {
        path: '/blog/:id',
        name: 'BlogDetail',
        component: () => import('@/views/BlogDetail.vue'),
        meta: { title: '文章详情' },
      },
      {
        path: '/projects',
        name: 'Projects',
        component: () => import('@/views/Projects.vue'),
        meta: { title: '项目展示' },
      },
      {
        path: '/skills',
        name: 'Skills',
        component: () => import('@/views/Skills.vue'),
        meta: { title: '技能树' },
      },
      {
        path: '/ai-chat',
        name: 'AIChat',
        component: () => import('@/views/AIChat.vue'),
        meta: { title: 'AI助手', requiresAuth: true },
      },
      {
        path: '/guestbook',
        name: 'Guestbook',
        component: () => import('@/views/Guestbook.vue'),
        meta: { title: '留言板' },
      },
      {
        path: '/user/center',
        name: 'UserCenter',
        component: () => import('@/views/UserCenter.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/admin/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true },
      },
      {
        path: '/admin/articles',
        name: 'ArticleManage',
        component: () => import('@/views/admin/ArticleManage.vue'),
        meta: { title: '文章管理', requiresAuth: true },
      },
      {
        path: '/admin/projects',
        name: 'ProjectManage',
        component: () => import('@/views/admin/ProjectManage.vue'),
        meta: { title: '项目管理', requiresAuth: true },
      },
      {
        path: '/admin/profile',
        name: 'UserProfile',
        component: () => import('@/views/admin/UserProfile.vue'),
        meta: { title: '个人信息', requiresAuth: true },
      },
      {
        path: '/admin/config',
        name: 'SiteConfig',
        component: () => import('@/views/admin/SiteConfig.vue'),
        meta: { title: '网站配置', requiresAuth: true },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的位置(浏览器前进后退),恢复到该位置
    if (savedPosition) {
      return savedPosition
    }
    // 如果有锚点,滚动到锚点
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth',
      }
    }
    // 始终滚动到顶部
    return { top: 0, left: 0 }
  },
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - Saury Blog` : 'Saury Blog'

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    if (!userStore.token) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
    
    // 检查是否需要管理员权限（/admin路径下的页面）
    if (to.path.startsWith('/admin')) {
      // 初始化用户信息（如果还没有）
      if (!userStore.userInfo) {
        userStore.initUserInfo()
      }
      
      // 检查是否是管理员 (role: 0-管理员, 1-普通用户)
      if (userStore.userInfo?.role !== 0) {
        ElMessage.error('您没有权限访问该页面')
        next({ name: 'Home' })
        return
      }
    }
    
    next()
  } else {
    next()
  }
})

// 路由后置守卫 - 确保滚动到顶部
router.afterEach(() => {
  // 立即滚动到顶部，不使用平滑滚动
  window.scrollTo(0, 0)
  // 也滚动 document.documentElement
  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
})

export default router

