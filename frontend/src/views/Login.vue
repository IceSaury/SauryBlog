<template>
  <div class="login-container">
    <!-- 背景图片层 -->
    <div class="background-image"></div>
    
    <!-- 动态渐变背景 -->
    <div class="gradient-bg"></div>
    
    <!-- 光束效果 -->
    <div class="light-beams">
      <div class="beam" v-for="i in 5" :key="'beam-' + i" :style="getBeamStyle(i)"></div>
    </div>
    
    <!-- 粒子效果 -->
    <div class="particles">
      <div class="particle" v-for="i in 100" :key="i" :style="getParticleStyle(i)"></div>
    </div>

    <!-- 扫描线效果 -->
    <!-- <div class="scanline"></div> -->
    
    <!-- 六边形装饰 -->
    <div class="hexagons">
      <div class="hexagon" v-for="i in 8" :key="'hex-' + i" :style="getHexagonStyle(i)"></div>
    </div>

    <!-- 登录容器 -->
    <div class="login-wrapper">
      <!-- 登录卡片 -->
      <div class="login-card">
      <!-- 标题 -->
      <div class="login-header">
        <h1 class="glitch" data-text="LOGIN">LOGIN</h1>
      </div>

      <!-- 表单 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label class="form-label">
            <span class="label-text">&gt; 用户名</span>
          </label>
          <div class="input-wrapper">
            <input
              type="text"
              v-model="username"
              placeholder="请输入用户名..."
              class="cyber-input"
              required
            />
            <div class="input-glow"></div>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">
            <span class="label-text">&gt; 密码</span>
          </label>
          <div class="input-wrapper">
            <input
              type="password"
              v-model="password"
              placeholder="请输入密码..."
              class="cyber-input"
              required
            />
            <div class="input-glow"></div>
          </div>
        </div>

        <div class="form-options">
          <label class="checkbox-wrapper">
            <input type="checkbox" v-model="rememberMe" />
            <span class="checkbox-label">记住我</span>
            <span class="checkmark"></span>
          </label>
        </div>

        <div class="button-group">
          <button type="submit" class="cyber-button login-button" :disabled="loading">
            <span class="button-text">
              {{ loading ? '登录中...' : '立即登录' }}
            </span>
            <div class="button-glow"></div>
          </button>

          <button type="button" class="cyber-button home-button" @click="goHome">
            <span class="button-text">返回首页</span>
            <div class="button-glow"></div>
          </button>
        </div>

        <div class="divider">
          <span>或</span>
        </div>

        <div class="register-link">
          <span>还没有账号？</span>
          <a @click="goRegister" class="link-glitch">立即注册</a>
        </div>
      </form>

      <!-- 装饰元素 -->
      <div class="corner-decoration tl"></div>
      <div class="corner-decoration tr"></div>
      <div class="corner-decoration bl"></div>
      <div class="corner-decoration br"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const loading = ref(false)

const getParticleStyle = (index) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 5}s`,
    animationDuration: `${5 + Math.random() * 10}s`
  }
}

const getBeamStyle = (index) => {
  return {
    left: `${index * 20}%`,
    animationDelay: `${index * 0.5}s`,
    animationDuration: `${8 + Math.random() * 4}s`,
    opacity: 0.1 + Math.random() * 0.2
  }
}

const getHexagonStyle = (index) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${index * 0.8}s`,
    animationDuration: `${10 + Math.random() * 5}s`,
    width: `${50 + Math.random() * 100}px`,
    height: `${50 + Math.random() * 100}px`
  }
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const success = await userStore.login({
      username: username.value,
      password: password.value
    })
    
    if (success) {
      ElMessage.success('登录成功！')
      // 如果有重定向地址，则跳转到重定向地址，否则跳转到首页
      const redirect = route.query.redirect || '/home'
      router.push(redirect)
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } catch (error) {
    console.error('登录失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/home')
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 20px;
  padding-left: 80px;
  background: #0a0e27;
  overflow: hidden;
  font-family: 'Courier New', monospace;
}

/* 背景图片层 */
.background-image {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background-image: url('@/assets/images/Miku.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.3;
  z-index: 0;
}

/* 动态渐变背景 */
.gradient-bg {
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  background: radial-gradient(circle at 20% 50%, rgba(0, 255, 255, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 80% 80%, rgba(255, 182, 217, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 40% 20%, rgba(201, 160, 220, 0.06) 0%, transparent 50%);
  animation: gradientShift 15s ease-in-out infinite;
  z-index: 1;
}

@keyframes gradientShift {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-5%, -5%) rotate(120deg);
  }
  66% {
    transform: translate(5%, 5%) rotate(240deg);
  }
}

/* 光束效果 */
.light-beams {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.beam {
  position: absolute;
  width: 2px;
  height: 100%;
  background: linear-gradient(to bottom,
    transparent,
    rgba(0, 255, 255, 0.3) 50%,
    transparent);
  animation: beamMove linear infinite;
  filter: blur(1px);
}

@keyframes beamMove {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100%);
  }
}

/* 粒子效果 */
.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.particle {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(0, 255, 255, 0.6);
  border-radius: 50%;
  animation: float linear infinite;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.8),
              0 0 20px rgba(0, 255, 255, 0.4);
}

@keyframes float {
  0% {
    transform: translateY(0) translateX(0) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
    transform: translateY(-10vh) translateX(10px) scale(1);
  }
  50% {
    opacity: 0.8;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) translateX(50px) scale(0);
    opacity: 0;
  }
}

/* 扫描线 */
.scanline {
  position: absolute;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(0, 255, 255, 0.8), 
    transparent);
  animation: scan 6s linear infinite;
  z-index: 1;
  box-shadow: 0 0 30px rgba(0, 255, 255, 0.8),
              0 0 60px rgba(0, 255, 255, 0.4);
  filter: blur(0.5px);
}

@keyframes scan {
  0% {
    top: -10%;
  }
  100% {
    top: 110%;
  }
}

/* 六边形装饰 */
.hexagons {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.hexagon {
  position: absolute;
  border: 1px solid rgba(255, 182, 217, 0.2);
  animation: hexagonFloat linear infinite;
  clip-path: polygon(30% 0%, 70% 0%, 100% 50%, 70% 100%, 30% 100%, 0% 50%);
  box-shadow: 0 0 20px rgba(255, 182, 217, 0.3),
              inset 0 0 20px rgba(255, 182, 217, 0.1);
}

@keyframes hexagonFloat {
  0% {
    transform: translateY(100vh) rotate(0deg) scale(0.5);
    opacity: 0;
  }
  10% {
    opacity: 0.6;
  }
  90% {
    opacity: 0.6;
  }
  100% {
    transform: translateY(-100vh) rotate(720deg) scale(1.2);
    opacity: 0;
  }
}

.login-wrapper {
  width: 100%;
  max-width: 400px;
  position: relative;
  z-index: 5;
}

/* 登录卡片 */
.login-card {
  position: relative;
  z-index: 5;
  width: 100%;
  padding: 40px 30px;
  background: rgba(10, 14, 39, 0.3);
  border: 2px solid rgba(0, 255, 255, 0.3);
  border-radius: 10px;
  box-shadow: 
    0 0 50px rgba(0, 255, 255, 0.3),
    inset 0 0 50px rgba(0, 255, 255, 0.05);
  backdrop-filter: blur(30px);
}

/* 角落装饰 */
.corner-decoration {
  position: absolute;
  width: 20px;
  height: 20px;
  border: 2px solid #ffb6d9;
  box-shadow: 0 0 10px rgba(255, 182, 217, 0.8);
}

.corner-decoration.tl {
  top: -2px;
  left: -2px;
  border-right: none;
  border-bottom: none;
}

.corner-decoration.tr {
  top: -2px;
  right: -2px;
  border-left: none;
  border-bottom: none;
}

.corner-decoration.bl {
  bottom: -2px;
  left: -2px;
  border-right: none;
  border-top: none;
}

.corner-decoration.br {
  bottom: -2px;
  right: -2px;
  border-left: none;
  border-top: none;
}

/* 标题 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.glitch {
  font-size: 56px;
  font-weight: 900;
  color: #00ffff;
  text-transform: uppercase;
  position: relative;
  text-shadow: 
    0 0 10px rgba(0, 255, 255, 0.8),
    0 0 20px rgba(0, 255, 255, 0.6),
    0 0 30px rgba(0, 255, 255, 0.4);
  animation: glitch 3s infinite;
  letter-spacing: 6px;
  margin: 0;
}

@keyframes glitch {
  0%, 90%, 100% {
    transform: translate(0);
  }
  92% {
    transform: translate(-2px, 2px);
  }
  94% {
    transform: translate(2px, -2px);
  }
  96% {
    transform: translate(-2px, -2px);
  }
  98% {
    transform: translate(2px, 2px);
  }
}

.glitch::before {
  content: attr(data-text);
  position: absolute;
  left: 2px;
  top: 0;
  width: 100%;
  text-shadow: -2px 0 #ffb6d9;
  overflow: hidden;
  clip: rect(0, 900px, 0, 0);
  animation: glitch-1 2s infinite linear alternate-reverse;
}

.glitch::after {
  content: attr(data-text);
  position: absolute;
  left: -2px;
  top: 0;
  width: 100%;
  text-shadow: 2px 0 #c9a0dc;
  overflow: hidden;
  clip: rect(0, 900px, 0, 0);
  animation: glitch-2 3s infinite linear alternate-reverse;
}

@keyframes glitch-1 {
  0% {
    clip: rect(42px, 9999px, 44px, 0);
  }
  25% {
    clip: rect(12px, 9999px, 59px, 0);
  }
  50% {
    clip: rect(82px, 9999px, 20px, 0);
  }
  75% {
    clip: rect(35px, 9999px, 71px, 0);
  }
  100% {
    clip: rect(63px, 9999px, 15px, 0);
  }
}

@keyframes glitch-2 {
  0% {
    clip: rect(65px, 9999px, 12px, 0);
  }
  25% {
    clip: rect(45px, 9999px, 78px, 0);
  }
  50% {
    clip: rect(23px, 9999px, 34px, 0);
  }
  75% {
    clip: rect(89px, 9999px, 56px, 0);
  }
  100% {
    clip: rect(12px, 9999px, 67px, 0);
  }
}

.subtitle {
  color: #ffb6d9;
  font-size: 12px;
  margin-top: 10px;
  letter-spacing: 2px;
  opacity: 0.8;
}

/* 表单 */
.login-form {
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
}

.label-text {
  color: #00ffff;
  font-size: 16px;
  letter-spacing: 2px;
  font-weight: bold;
  font-family: 'Microsoft YaHei', sans-serif;
}

.input-wrapper {
  position: relative;
}

.cyber-input {
  width: 100%;
  padding: 12px 15px;
  background: rgba(0, 255, 255, 0.05);
  border: 1px solid rgba(0, 255, 255, 0.3);
  border-radius: 5px;
  color: #fff;
  font-size: 14px;
  font-family: 'Microsoft YaHei', sans-serif;
  transition: all 0.3s ease;
  outline: none;
}

.cyber-input:focus {
  border-color: #00ffff;
  background: rgba(0, 255, 255, 0.1);
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.3);
}

.cyber-input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.input-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.3), transparent);
  pointer-events: none;
  transition: width 0.3s ease;
}

.cyber-input:focus + .input-glow {
  width: 100%;
}

/* 选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 12px;
}

.checkbox-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.checkbox-wrapper input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.checkbox-label {
  color: rgba(255, 255, 255, 0.7);
  margin-left: 25px;
  font-size: 14px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.checkmark {
  position: absolute;
  left: 0;
  height: 16px;
  width: 16px;
  border: 2px solid rgba(0, 255, 255, 0.5);
  border-radius: 3px;
  transition: all 0.3s ease;
}

.checkbox-wrapper:hover .checkmark {
  border-color: #00ffff;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.checkbox-wrapper input:checked ~ .checkmark {
  background: #00ffff;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.8);
}

.checkmark::after {
  content: "";
  position: absolute;
  display: none;
  left: 4px;
  top: 1px;
  width: 4px;
  height: 8px;
  border: solid #0a0e27;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-wrapper input:checked ~ .checkmark::after {
  display: block;
}

.forgot-link {
  color: #ffb6d9;
  text-decoration: none;
  transition: all 0.3s ease;
  font-family: 'Microsoft YaHei', sans-serif;
}

.forgot-link:hover {
  color: #ffb6d9;
  text-shadow: 0 0 10px rgba(255, 182, 217, 0.8);
}

/* 按钮组 */
.button-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 25px;
}

/* 按钮 */
.cyber-button {
  position: relative;
  padding: 12px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
  font-family: 'Microsoft YaHei', sans-serif;
  letter-spacing: 2px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  text-transform: uppercase;
  border: 2px solid;
}

.cyber-button.login-button {
  background: linear-gradient(45deg, rgba(0, 255, 255, 0.2), rgba(0, 255, 255, 0.1));
  border-color: #00ffff;
  color: #00ffff;
}

.cyber-button.login-button:hover:not(:disabled) {
  background: linear-gradient(45deg, rgba(0, 255, 255, 0.3), rgba(0, 255, 255, 0.2));
  box-shadow: 
    0 0 30px rgba(0, 255, 255, 0.6),
    inset 0 0 20px rgba(0, 255, 255, 0.2);
  transform: translateY(-2px);
}

.cyber-button.home-button {
  background: linear-gradient(45deg, rgba(255, 182, 217, 0.2), rgba(255, 182, 217, 0.1));
  border-color: #ffb6d9;
  color: #ffb6d9;
}

.cyber-button.home-button:hover {
  background: linear-gradient(45deg, rgba(255, 182, 217, 0.3), rgba(255, 182, 217, 0.2));
  box-shadow: 
    0 0 30px rgba(255, 182, 217, 0.6),
    inset 0 0 20px rgba(255, 182, 217, 0.2);
  transform: translateY(-2px);
}

.cyber-button:active:not(:disabled) {
  transform: translateY(0);
}

.cyber-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.button-text {
  position: relative;
  font-size: 16px;
  font-weight: 900;
  z-index: 2;
  font-family: 'Microsoft YaHei', sans-serif;
}

.button-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  transform: translate(-50%, -50%);
  transition: all 0.5s ease;
  pointer-events: none;
}

.cyber-button.login-button:hover:not(:disabled) .button-glow {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(0, 255, 255, 0.6), transparent);
}

.cyber-button.home-button:hover .button-glow {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 182, 217, 0.6), transparent);
}

/* 分割线 */
.divider {
  position: relative;
  text-align: center;
  margin: 20px 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(0, 255, 255, 0.5), 
    transparent);
}

.divider span {
  position: relative;
  padding: 0 15px;
  background: rgba(10, 14, 39, 0.3);
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  letter-spacing: 2px;
}

/* 注册链接 */
.register-link {
  text-align: center;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

.register-link span {
  margin-right: 10px;
}

.link-glitch {
  color: #ffb6d9;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
  letter-spacing: 1px;
  cursor: pointer;
}

.link-glitch:hover {
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.8);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    justify-content: center;
    padding-left: 20px;
  }

  .login-card {
    width: 90%;
    padding: 40px 30px;
  }

  .glitch {
    font-size: 48px;
  }

  .button-group {
    grid-template-columns: 1fr;
  }
}
</style>

