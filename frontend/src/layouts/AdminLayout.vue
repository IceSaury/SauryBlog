<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="250px" class="admin-sidebar">
        <div class="sidebar-header">
          <h2 class="sidebar-title">管理后台</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/articles">
            <el-icon><Document /></el-icon>
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/projects">
            <el-icon><Folder /></el-icon>
            <span>项目管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/profile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/admin/config">
            <el-icon><Setting /></el-icon>
            <span>网站配置</span>
          </el-menu-item>
          <el-menu-item @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
          <el-menu-item @click="router.push('/home')">
            <el-icon><House /></el-icon>
            <span>返回首页</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容 -->
      <el-container>
        <el-header class="admin-header">
          <div class="header-title">Saury Blog 管理系统</div>
          <div class="header-user">
            <span>{{ userStore.userInfo?.nickname }}</span>
          </div>
        </el-header>
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Odometer, Document, Folder, User, SwitchButton, House, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  background: $bg-primary;
  position: relative;
  overflow: hidden;
  
  // 渐变光晕背景层 - 蓝色主题
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(ellipse at 15% 25%, rgba(#00B4FF, 0.08) 0%, transparent 45%),
      radial-gradient(ellipse at 85% 75%, rgba(#4169E1, 0.07) 0%, transparent 45%),
      radial-gradient(ellipse at 50% 50%, rgba(#0080FF, 0.05) 0%, transparent 50%),
      linear-gradient(135deg, transparent 0%, rgba(#00D4FF, 0.03) 50%, transparent 100%);
    pointer-events: none;
    z-index: 0;
    animation: backgroundPulse 20s ease-in-out infinite;
  }
  
  // 动态粒子层 - 蓝色主题
  &::after {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(1px 1px at 15% 20%, rgba(#00D4FF, 0.35), transparent),
      radial-gradient(1px 1px at 45% 35%, rgba(#5B7EFF, 0.3), transparent),
      radial-gradient(2px 2px at 75% 60%, rgba(#0080FF, 0.4), transparent),
      radial-gradient(1px 1px at 25% 70%, rgba(#00B4FF, 0.3), transparent),
      radial-gradient(1px 1px at 85% 25%, rgba(#00E5FF, 0.35), transparent),
      radial-gradient(2px 2px at 60% 85%, rgba(#4169E1, 0.3), transparent),
      radial-gradient(1px 1px at 90% 50%, rgba(#00D4FF, 0.35), transparent),
      radial-gradient(1px 1px at 35% 90%, rgba(#6B5EFF, 0.25), transparent);
    background-size: 300% 300%;
    background-position: 0% 0%;
    opacity: 0.5;
    animation: particleDrift 30s ease-in-out infinite;
    pointer-events: none;
    z-index: 0;
  }
}

.admin-sidebar {
  background: rgba($bg-secondary, 0.95);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba($neon-cyan, 0.2);
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.3);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 1px;
    height: 100%;
    background: linear-gradient(180deg, 
      transparent, 
      rgba($neon-cyan, 0.5) 30%, 
      rgba($neon-cyan, 0.5) 70%, 
      transparent
    );
    box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
    animation: borderGlow 3s ease-in-out infinite;
  }
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba($neon-cyan, 0.2);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, transparent, $neon-cyan, transparent);
    box-shadow: 0 0 10px rgba($neon-cyan, 0.8);
  }
}

.sidebar-title {
  color: $neon-cyan;
  font-size: 22px;
  font-weight: 900;
  text-shadow: 
    0 0 10px rgba($neon-cyan, 0.8),
    0 0 20px rgba($neon-cyan, 0.4);
  letter-spacing: 3px;
  text-transform: uppercase;
  animation: titlePulse 2s ease-in-out infinite;
}

.admin-menu {
  border: none;
  background: transparent;
  padding: 12px 0;
  
  :deep(.el-menu-item) {
    margin: 4px 12px;
    border-radius: 8px;
    border-left: 3px solid transparent;
    transition: all 0.3s;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba($neon-cyan, 0.1), transparent);
      transition: left 0.5s;
    }
    
    &:hover {
      background: rgba($neon-cyan, 0.08);
      border-left-color: $neon-cyan;
      transform: translateX(4px);
      
      &::before {
        left: 100%;
      }
      
      span {
        color: #FFFFFF;
      }
      
      .el-icon {
        color: $neon-cyan;
        transform: scale(1.2);
        filter: drop-shadow(0 0 8px $neon-cyan);
      }
    }
    
    &.is-active {
      background: rgba($neon-cyan, 0.15);
      border-left-color: $neon-cyan;
      box-shadow: 
        0 0 15px rgba($neon-cyan, 0.3),
        inset 0 0 20px rgba($neon-cyan, 0.1);
      
      span {
        color: $neon-cyan;
        font-weight: 600;
        text-shadow: 0 0 8px rgba($neon-cyan, 0.5);
      }
      
      .el-icon {
        color: $neon-cyan;
        filter: drop-shadow(0 0 10px $neon-cyan);
      }
    }
    
    .el-icon {
      transition: all 0.3s;
      font-size: 20px;
      color: rgba($neon-cyan, 0.7);
    }
    
    span {
      font-size: 15px;
      letter-spacing: 0.5px;
      color: #F0F0F0;
      font-weight: 500;
    }
  }
}

.admin-header {
  background: rgba($bg-secondary, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba($neon-cyan, 0.2);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, 
      transparent, 
      rgba($neon-cyan, 0.5) 20%, 
      rgba($neon-magenta, 0.5) 50%,
      rgba($neon-cyan, 0.5) 80%,
      transparent
    );
    box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
  }
}

.header-title {
  color: $neon-cyan;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 0 10px rgba($neon-cyan, 0.5);
  letter-spacing: 2px;
  text-transform: uppercase;
}

.header-user {
  color: $text-primary;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 20px;
  background: rgba($neon-cyan, 0.08);
  border: 1px solid rgba($neon-cyan, 0.2);
  border-radius: 20px;
  transition: all 0.3s;
  
  &:hover {
    background: rgba($neon-cyan, 0.15);
    border-color: rgba($neon-cyan, 0.4);
    box-shadow: 0 0 15px rgba($neon-cyan, 0.3);
  }
}

.admin-main {
  background: transparent;
  padding: 0;
  position: relative;
  z-index: 1;
}

// 动画
@keyframes borderGlow {
  0%, 100% {
    opacity: 0.5;
    filter: brightness(1);
  }
  50% {
    opacity: 1;
    filter: brightness(1.5);
  }
}

@keyframes titlePulse {
  0%, 100% {
    text-shadow: 
      0 0 10px rgba($neon-cyan, 0.8),
      0 0 20px rgba($neon-cyan, 0.4);
  }
  50% {
    text-shadow: 
      0 0 15px rgba($neon-cyan, 1),
      0 0 30px rgba($neon-cyan, 0.6),
      0 0 40px rgba($neon-cyan, 0.3);
  }
}

@keyframes backgroundPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.85;
    transform: scale(1.05);
  }
}

@keyframes particleDrift {
  0%, 100% {
    background-position: 0% 0%;
    opacity: 0.5;
  }
  33% {
    background-position: 100% 50%;
    opacity: 0.7;
  }
  66% {
    background-position: 50% 100%;
    opacity: 0.4;
  }
}
</style>

