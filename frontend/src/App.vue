<template>
  <div id="app" class="cyberpunk-app">
    <!-- z-index 层级说明:
        0: 背景图片 & 粒子效果
        1: 扫描线效果
        2: 樱花飘落效果 (Sakura.js)
        5: 卡片内容 (CyberCard)
        9999: Live2D角色
    -->
    <!-- 背景图片层 -->
    <div class="background-image"></div>
    <!-- 粒子背景 -->
    <div id="particles-js" class="particles-bg"></div>
    
    <!-- 扫描线效果 -->
    <div class="scanline"></div>
    
    <!-- 樱花效果通过 Sakura.js 动态创建，z-index: 2 -->
    
    <!-- 主内容 -->
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  // 初始化用户信息
  userStore.initUserInfo()
  
  // 初始化粒子效果
  initParticles()
})

const initParticles = () => {
  // 动态加载 particles.js
  if (typeof (window as any).particlesJS !== 'undefined') {
    (window as any).particlesJS('particles-js', {
      particles: {
        number: { value: 80, density: { enable: true, value_area: 800 } },
        color: { value: '#00F0FF' },
        shape: { type: 'circle' },
        opacity: { value: 0.5, random: true },
        size: { value: 3, random: true },
        line_linked: {
          enable: true,
          distance: 150,
          color: '#00F0FF',
          opacity: 0.4,
          width: 1,
        },
        move: {
          enable: true,
          speed: 2,
          direction: 'none',
          random: true,
          out_mode: 'out',
        },
      },
      interactivity: {
        detect_on: 'canvas',
        events: {
          onhover: { enable: true, mode: 'grab' },
          onclick: { enable: true, mode: 'push' },
        },
      },
      retina_detect: true,
    })
  }
}
</script>

<style lang="scss">
.cyberpunk-app {
  position: relative;
  min-height: 100vh;
  background: $bg-primary;
  color: $text-primary;
}

.particles-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.scanline {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to bottom,
    transparent 50%,
    rgba(0, 240, 255, 0.03) 50%
  );
  background-size: 100% 4px;
  z-index: 1;
  pointer-events: none;
  animation: scanline 8s linear infinite;
}

@keyframes scanline {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(4px);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 背景图片层 */
.background-image {
  position: fixed;
  width: 100vw;
  height: 100vh;
  top: 0;
  left: 0;
  background-image: url('@/assets/images/miku-bg.png'); /* 修改为你的图片路径 */
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  background-repeat: no-repeat;
  opacity: 0.3; /* 调整透明度，0.1-0.5 之间较合适 */
  z-index: 0;
  pointer-events: none;
}
</style>

