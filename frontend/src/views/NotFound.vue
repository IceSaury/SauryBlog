<template>
  <div class="not-found-page">
    <h1 class="error-code glitch" data-text="404">404</h1>
    <p class="error-message">页面不存在</p>
    <CyberButton type="cyan" @click="router.push('/home')">
      返回首页
    </CyberButton>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import CyberButton from '@/components/CyberButton.vue'

const router = useRouter()
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: 900;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  margin-bottom: 24px;
  position: relative;

  &.glitch::before,
  &.glitch::after {
    content: attr(data-text);
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  &.glitch::before {
    left: 2px;
    text-shadow: -2px 0 $neon-magenta;
    animation: glitch-anim 0.3s infinite;
  }

  &.glitch::after {
    left: -2px;
    text-shadow: 2px 0 $neon-purple;
    animation: glitch-anim 0.3s infinite reverse;
  }
}

.error-message {
  font-size: 24px;
  color: $text-secondary;
  margin-bottom: 32px;
}

@keyframes glitch-anim {
  0%, 100% {
    clip-path: inset(0 0 0 0);
  }
  25% {
    clip-path: inset(40% 0 61% 0);
  }
  50% {
    clip-path: inset(92% 0 1% 0);
  }
  75% {
    clip-path: inset(43% 0 1% 0);
  }
}
</style>

