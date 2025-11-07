<template>
  <div class="main-layout">
    <!-- 导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/home')">
          <span class="logo-text glitch" data-text="SAURY">SAURY</span>
          <span class="logo-sub">BLOG</span>
        </div>
        
        <nav class="nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
          >
            {{ item.name }}
          </router-link>
        </nav>

        <div class="header-actions">
          <CyberButton
            v-if="!userStore.token"
            type="cyan"
            @click="router.push('/login')"
          >
            登录
          </CyberButton>
          <el-dropdown v-else trigger="click">
            <div class="user-avatar">
              <img :src="userStore.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="avatar" />
              <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/user/center')">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" @click="router.push('/admin')">
                  <el-icon><Setting /></el-icon>
                  后台管理
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-info">
          <p>© 2025 Saury Blog. All Rights Reserved.</p>
          <p class="footer-links">
            <a href="https://github.com/IceSaury" target="_blank">GitHub</a>
          </p>
        </div>
      </div>
    </footer>

    <!-- Live2D角色 -->
    <Live2DWidget
      :width="300"
      :height="400"
      position="right"
      :show-controls="true"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import CyberButton from '@/components/CyberButton.vue'
import Live2DWidget from '@/components/Live2DWidget.vue'

const router = useRouter()
const userStore = useUserStore()

// 判断是否是管理员 (role: 0-管理员, 1-普通用户)
const isAdmin = computed(() => userStore.userInfo?.role === 0)

const navItems = [
  { name: '首页', path: '/home' },
  { name: '关于', path: '/about' },
  { name: '博客', path: '/blog' },
  { name: '项目', path: '/projects' },
  { name: '技能', path: '/skills' },
  { name: 'AI Miku', path: '/ai-chat' },
  { name: '留言', path: '/guestbook' },
]

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 初始化用户信息
onMounted(() => {
  if (userStore.token && !userStore.userInfo) {
    userStore.initUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: $bg-primary;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: $header-height;
  background: $bg-overlay;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid $border-secondary;
  z-index: 1000;

  &-content {
    max-width: $container-max-width;
    height: 100%;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.logo {
  display: flex;
  align-items: baseline;
  gap: 8px;
  cursor: pointer;

  &-text {
    font-size: 28px;
    font-weight: 700;
    color: $neon-cyan;
    text-shadow: $shadow-neon-cyan;
    position: relative;

    &.glitch {
      &::before,
      &::after {
        content: attr(data-text);
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
      }

      &:hover::before {
        left: 2px;
        text-shadow: -2px 0 $neon-magenta;
        animation: glitch-anim 0.3s;
      }

      &:hover::after {
        left: -2px;
        text-shadow: 2px 0 $neon-purple;
        animation: glitch-anim 0.3s reverse;
      }
    }
  }

  &-sub {
    font-size: 14px;
    color: $text-secondary;
    font-weight: 600;
  }
}

.nav {
  display: flex;
  gap: 32px;

  &-item {
    color: $text-secondary;
    font-size: 16px;
    font-weight: 500;
    text-decoration: none;
    transition: $transition-base;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 0;
      width: 0;
      height: 2px;
      background: $gradient-cyber;
      transition: width 0.3s ease;
    }

    &:hover,
    &.router-link-active {
      color: $neon-cyan;

      &::after {
        width: 100%;
      }
    }
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba($neon-cyan, 0.05);
  border: 1px solid rgba($neon-cyan, 0.2);

  img {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    border: 2px solid $neon-cyan;
    box-shadow: 0 0 8px rgba($neon-cyan, 0.4);
    transition: all 0.3s;
    object-fit: cover;
  }
  
  .user-name {
    color: $neon-cyan;
    font-size: 14px;
    font-weight: 500;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &:hover {
    background: rgba($neon-cyan, 0.1);
    border-color: rgba($neon-cyan, 0.4);
    box-shadow: 0 0 10px rgba($neon-cyan, 0.2);
    
    img {
      box-shadow: 0 0 12px rgba($neon-cyan, 0.6);
      transform: scale(1.05);
    }
  }
}

.main-content {
  flex: 1;
  margin-top: $header-height;
  padding: 40px 24px;
  max-width: $container-max-width;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  position: relative;
  z-index: 2;
}

.footer {
  background: $bg-secondary;
  border-top: 1px solid $border-secondary;
  padding: 24px 0;
  position: relative;
  z-index: 2;

  &-content {
    max-width: $container-max-width;
    margin: 0 auto;
    padding: 0 24px;
  }

  &-info {
    text-align: center;
    color: $text-secondary;
    font-size: 14px;

    p {
      margin: 8px 0;
    }
  }

  &-links {
    a {
      color: $neon-cyan;
      text-decoration: none;
      transition: $transition-fast;

      &:hover {
        text-shadow: $shadow-neon-cyan;
      }
    }

    .divider {
      margin: 0 12px;
    }
  }
}

@keyframes glitch-anim {
  0% { clip-path: inset(40% 0 61% 0); }
  20% { clip-path: inset(92% 0 1% 0); }
  40% { clip-path: inset(43% 0 1% 0); }
  60% { clip-path: inset(25% 0 58% 0); }
  80% { clip-path: inset(54% 0 7% 0); }
  100% { clip-path: inset(58% 0 43% 0); }
}
</style>

