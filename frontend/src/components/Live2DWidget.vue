<template>
  <div class="live2d-widget" :data-position="position" ref="widgetContainer">
    <canvas ref="canvas" class="live2d-canvas"></canvas>
    <div v-if="showControls" class="live2d-controls">
      <button @click="playRandomMotion('Tap')" class="control-btn" title="点击互动">👋</button>
      <button @click="playRandomMotion('Idle')" class="control-btn" title="待机动作">💤</button>
      <button @click="playRandomMotion('Flick')" class="control-btn" title="滑动动作">💫</button>
      <button @click="playRandomMotion('FlickUp')" class="control-btn" title="向上滑动">⬆️</button>
      <button @click="toggleSize" class="control-btn" title="切换大小">{{ isLarge ? '📐' : '📏' }}</button>
      <button @click="toggleVisibility" class="control-btn" title="隐藏/显示">{{ isVisible ? '👁️' : '🙈' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

let PIXI: any = null
let Live2DModel: any = null

const props = defineProps({
  modelPath: {
    type: String,
    default: '/live2d/miku/miku_sample_t04.model3.json'
  },
  width: {
    type: Number,
    default: 300
  },
  height: {
    type: Number,
    default: 400
  },
  position: {
    type: String,
    default: 'right', // 'left' | 'right'
    validator: (value: string) => ['left', 'right'].includes(value)
  },
  showControls: {
    type: Boolean,
    default: true
  }
})

const widgetContainer = ref<HTMLElement>()
const canvas = ref<HTMLCanvasElement>()
const isVisible = ref(true)
const isLarge = ref(false)

let app: any = null
let model: any = null
let originalModelSize = { width: 0, height: 0 } // 保存模型原始尺寸
let mouseX = 0
let mouseY = 0
let targetX = 0
let targetY = 0
let animationFrameId: number | null = null

onMounted(async () => {
  try {
    // 等待 Live2D 运行时加载完成
    if (typeof window !== 'undefined') {
      // 等待最多 5 秒让运行时加载
      let attempts = 0
      const maxAttempts = 50
      
      while (attempts < maxAttempts) {
        const hasLive2D = (window as any).Live2D
        const hasCubismCore = (window as any).Live2DCubismCore
        
        if (hasLive2D && hasCubismCore) {
          console.log('Live2D 运行时加载完成')
          break
        }
        
        await new Promise(resolve => setTimeout(resolve, 100))
        attempts++
      }
      
      if (!(window as any).Live2D || !(window as any).Live2DCubismCore) {
        console.error('Live2D 运行时未完全加载', {
          hasLive2D: !!(window as any).Live2D,
          hasCubismCore: !!(window as any).Live2DCubismCore
        })
        return
      }
    }
    
    // 动态导入pixi.js和pixi-live2d-display
    console.log('开始导入 PIXI 和 Live2D 模块...')
    const pixiModule = await import('pixi.js')
    const live2dModule = await import('pixi-live2d-display')
    
    console.log('模块导入完成', { pixiModule, live2dModule })
    
    // pixi.js导出的是命名空间
    PIXI = pixiModule
    // pixi-live2d-display导出Live2DModel类
    Live2DModel = live2dModule.Live2DModel
    
    console.log('Live2DModel:', Live2DModel)
    
    // 配置全局变量（pixi-live2d-display需要）
    if (typeof window !== 'undefined') {
      (window as any).PIXI = PIXI
      console.log('全局 PIXI 配置完成')
    }
    
    console.log('准备初始化 Live2D...')
    await initLive2D()
    console.log('Live2D 初始化完成')
  } catch (error) {
    console.error('Live2D初始化失败:', error)
    console.error('错误堆栈:', (error as Error).stack)
  }
})

onBeforeUnmount(() => {
  cleanup()
})

const initLive2D = async () => {
  console.log('initLive2D 开始执行...')
  console.log('检查依赖:', { 
    hasCanvas: !!canvas.value, 
    hasPIXI: !!PIXI, 
    hasLive2DModel: !!Live2DModel 
  })
  
  if (!canvas.value || !PIXI || !Live2DModel) {
    console.error('Live2D依赖未加载完成', {
      canvas: !!canvas.value,
      PIXI: !!PIXI,
      Live2DModel: !!Live2DModel
    })
    return
  }

  // 创建PIXI应用
  console.log('创建 PIXI Application...')
  app = new PIXI.Application({
    view: canvas.value,
    width: props.width,
    height: props.height,
    backgroundAlpha: 0,
    antialias: true,
    resolution: window.devicePixelRatio || 1,
    autoDensity: true
  })
  console.log('PIXI Application 创建成功:', app)

    // 加载Live2D模型
  try {
    console.log('开始加载模型:', props.modelPath)
    model = await Live2DModel.from(props.modelPath, {
      // 禁用自动交互，避免与 PixiJS v6 的事件系统冲突
      autoInteract: false,
      autoHitTest: false
    })
    console.log('模型加载成功:', model)

    if (app && model) {
      console.log('开始配置模型...')
      
      // 将模型添加到舞台
      app.stage.addChild(model)
      console.log('模型已添加到舞台')

      // 保存模型原始尺寸（未缩放前）
      originalModelSize.width = model.width
      originalModelSize.height = model.height
      console.log('保存原始模型尺寸:', originalModelSize)

      // 调整模型大小以适应画布
      const scale = Math.min(
        props.width / originalModelSize.width,
        props.height / originalModelSize.height
      ) * 0.8

      console.log('计算缩放:', { scale, modelWidth: model.width, modelHeight: model.height })
      
      model.scale.set(scale)
      model.x = props.width / 2
      model.y = props.height / 2
      model.anchor.set(0.5, 0.5)
      
      console.log('模型位置和缩放设置完成:', { x: model.x, y: model.y, scale })

      // 禁用模型的交互模式，避免事件系统错误
      model.eventMode = 'none'
      model.interactiveChildren = false
      
      // 启用自动眨眼和自动呼吸
      if (model.internalModel) {
        model.internalModel.motionManager.autoUpdate = true
        console.log('自动动画已启用')
      }

      // 播放待机动画
      console.log('播放待机动画...')
      playRandomMotion('Idle')
      
      console.log('✅ Live2D 模型完全初始化成功！')
      
      // 手动添加点击交互（使用 canvas 的原生事件）
      if (canvas.value) {
        canvas.value.addEventListener('click', () => {
          console.log('画布被点击')
          playRandomMotion('Tap')
        })
        console.log('点击事件已绑定到 canvas')
      }

      // 添加鼠标追踪（平滑版本）
      if (app.view) {
        const view = app.view as HTMLCanvasElement
        
        // 鼠标移动事件：只更新目标位置
        view.addEventListener('mousemove', (event: MouseEvent) => {
          if (model && model.internalModel) {
            const rect = view.getBoundingClientRect()
            targetX = ((event.clientX - rect.left) / rect.width) * 2 - 1
            targetY = ((event.clientY - rect.top) / rect.height) * 2 - 1
          }
        })
        
        // 使用动画帧平滑更新模型参数
        const updateModelLook = () => {
          if (model && model.internalModel && model.internalModel.coreModel) {
            // 平滑插值（缓动）
            const lerpFactor = 0.15 // 缓动系数，值越小越平滑
            mouseX += (targetX - mouseX) * lerpFactor
            mouseY += (targetY - mouseY) * lerpFactor
            
            // 设置模型的注视点
            model.internalModel.coreModel.addParameterValueById('ParamAngleX', mouseX * 30)
            model.internalModel.coreModel.addParameterValueById('ParamAngleY', -mouseY * 30)
            model.internalModel.coreModel.addParameterValueById('ParamBodyAngleX', mouseX * 10)
          }
          
          animationFrameId = requestAnimationFrame(updateModelLook)
        }
        
        // 启动动画循环
        updateModelLook()
      }
    }
  } catch (error) {
    console.error('加载Live2D模型失败:', error)
    throw error
  }
}

const playRandomMotion = (group: string = 'Idle') => {
  if (model && model.internalModel && model.internalModel.motionManager) {
    try {
      const motionManager = model.internalModel.motionManager
      const motions = motionManager.definitions?.[group]
      
      if (motions && motions.length > 0) {
        const randomIndex = Math.floor(Math.random() * motions.length)
        motionManager.startMotion(group, randomIndex)
      }
    } catch (error) {
      console.error('播放动作失败:', error)
    }
  }
}

const toggleSize = () => {
  isLarge.value = !isLarge.value
  if (app && model && originalModelSize.width > 0) {
    const newWidth = isLarge.value ? props.width * 1.5 : props.width
    const newHeight = isLarge.value ? props.height * 1.5 : props.height
    
    console.log('切换大小:', { isLarge: isLarge.value, newWidth, newHeight })
    
    app.renderer.resize(newWidth, newHeight)
    
    // 使用原始模型尺寸计算缩放，避免越来越小
    const scale = Math.min(
      newWidth / originalModelSize.width,
      newHeight / originalModelSize.height
    ) * 0.8
    
    console.log('重新计算缩放:', { scale, originalWidth: originalModelSize.width, originalHeight: originalModelSize.height })
    
    model.scale.set(scale)
    model.x = newWidth / 2
    model.y = newHeight / 2
  }
}

const toggleVisibility = () => {
  isVisible.value = !isVisible.value
  if (model) {
    model.visible = isVisible.value
  }
}

const cleanup = () => {
  // 停止动画循环
  if (animationFrameId !== null) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }
  
  if (model) {
    model.destroy()
    model = null
  }
  if (app) {
    app.destroy(true, { children: true, texture: true, baseTexture: true })
    app = null
  }
}

// 暴露方法给父组件
defineExpose({
  playRandomMotion,
  toggleSize,
  toggleVisibility
})
</script>

<style lang="scss" scoped>
.live2d-widget {
  position: fixed;
  bottom: 0;
  z-index: 9999;
  pointer-events: auto;
  
  &[data-position="left"] {
    left: 0;
  }
  
  &[data-position="right"] {
    right: 0;
  }
}

.live2d-canvas {
  display: block;
  cursor: pointer;
  filter: drop-shadow(0 0 20px rgba(0, 255, 255, 0.3));
  transition: filter 0.3s ease;
  
  &:hover {
    filter: drop-shadow(0 0 30px rgba(0, 255, 255, 0.5));
  }
}

.live2d-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
  
  .live2d-widget:hover & {
    opacity: 1;
  }
}

.control-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #00ffff;
  background: rgba(0, 20, 40, 0.8);
  color: #00ffff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.3);
  
  &:hover {
    background: rgba(0, 255, 255, 0.2);
    box-shadow: 0 0 20px rgba(0, 255, 255, 0.6);
    transform: scale(1.1);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

@media (max-width: 768px) {
  .live2d-widget {
    transform: scale(0.7);
    transform-origin: bottom right;
  }
  
  .live2d-controls {
    opacity: 1;
  }
}
</style>

