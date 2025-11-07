<template>
  <div class="image-upload-container">
    <el-upload
      class="image-uploader"
      :action="uploadUrl"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :http-request="handleUpload"
      :disabled="uploading"
    >
      <div v-if="imageUrl" class="image-preview">
        <img :src="imageUrl" class="uploaded-image" />
        <div class="image-overlay">
          <el-icon class="overlay-icon" @click.stop="handleRemove">
            <Delete />
          </el-icon>
          <el-icon class="overlay-icon" @click.stop>
            <ZoomIn />
          </el-icon>
        </div>
      </div>
      <div v-else class="upload-placeholder">
        <el-icon v-if="!uploading" class="upload-icon"><Plus /></el-icon>
        <el-progress
          v-else
          type="circle"
          :percentage="uploadProgress"
          :width="60"
          class="upload-progress"
        />
        <div class="upload-text">
          {{ uploading ? '上传中...' : '点击上传图片' }}
        </div>
        <div class="upload-hint">{{ hint }}</div>
      </div>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, ZoomIn } from '@element-plus/icons-vue'
import { uploadImage } from '@/api/upload'
import type { ApiResponse } from '@/types'

interface Props {
  modelValue?: string
  hint?: string
  maxSize?: number // MB
  accept?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  hint: '支持 jpg、png、gif 格式，大小不超过 5MB',
  maxSize: 5,
  accept: 'image/jpeg,image/png,image/gif'
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const imageUrl = ref(props.modelValue)
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadUrl = ref('') // 占位，实际使用自定义上传

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  imageUrl.value = newVal
})

// 上传前检查
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < props.maxSize

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB!`)
    return false
  }
  return true
}

// 自定义上传
const handleUpload = async (options: any) => {
  const { file } = options
  
  uploading.value = true
  uploadProgress.value = 0
  
  // 模拟上传进度
  const progressTimer = setInterval(() => {
    if (uploadProgress.value < 90) {
      uploadProgress.value += 10
    }
  }, 200)

  try {
    const res = await uploadImage(file) as unknown as ApiResponse<string>
    
    clearInterval(progressTimer)
    uploadProgress.value = 100
    
    if (res.code === 200) {
      imageUrl.value = res.data
      emit('update:modelValue', res.data)
      ElMessage.success('上传成功')
    } else {
      throw new Error(res.message || '上传失败')
    }
  } catch (error: any) {
    clearInterval(progressTimer)
    console.error('上传失败:', error)
    ElMessage.error(error.message || '上传失败，请重试')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

// 删除图片
const handleRemove = () => {
  imageUrl.value = ''
  emit('update:modelValue', '')
}
</script>

<style lang="scss" scoped>
.image-upload-container {
  .image-uploader {
    :deep(.el-upload) {
      border: 1px dashed rgba($neon-cyan, 0.3);
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;
      background: rgba($bg-primary, 0.5);

      &:hover {
        border-color: $neon-cyan;
        box-shadow: 0 0 10px rgba($neon-cyan, 0.3);
      }
    }
  }

  .image-preview {
    position: relative;
    width: 180px;
    height: 180px;

    .uploaded-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 20px;
      opacity: 0;
      transition: opacity 0.3s;

      &:hover {
        opacity: 1;
      }

      .overlay-icon {
        font-size: 24px;
        color: $neon-cyan;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          transform: scale(1.2);
          color: $neon-purple;
        }
      }
    }
  }

  .upload-placeholder {
    width: 180px;
    height: 180px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: $text-secondary;

    .upload-icon {
      font-size: 48px;
      color: rgba($neon-cyan, 0.6);
      margin-bottom: 12px;
    }

    .upload-progress {
      margin-bottom: 12px;
    }

    .upload-text {
      font-size: 14px;
      color: $text-primary;
      margin-bottom: 8px;
    }

    .upload-hint {
      font-size: 12px;
      color: $text-secondary;
      text-align: center;
      padding: 0 10px;
    }
  }
}
</style>

