<template>
  <div class="user-profile-page">
    <h2 class="page-title">个人信息</h2>

    <div class="profile-container">
      <!-- 用户信息 -->
      <cyber-card class="profile-card">
        <h3 class="section-title">基本信息</h3>
        
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="100px"
        >
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled />
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input
              v-model="profileForm.nickname"
              placeholder="请输入昵称"
              maxlength="50"
            />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="profileForm.email"
              placeholder="请输入邮箱"
              maxlength="100"
            />
          </el-form-item>
          
          <el-form-item label="头像">
            <div class="avatar-upload-container">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
              <div class="upload-tips">
                <p>点击上传头像</p>
                <p class="tip-text">支持jpg、png、gif格式，大小不超过5MB</p>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item label="GitHub">
            <el-input
              v-model="profileForm.github"
              placeholder="请输入GitHub地址"
            />
          </el-form-item>
          
          <el-form-item label="CSDN">
            <el-input
              v-model="profileForm.csdn"
              placeholder="请输入CSDN地址"
            />
          </el-form-item>
          
          <el-form-item label="个人简介">
            <el-input
              v-model="profileForm.intro"
              type="textarea"
              :rows="4"
              placeholder="请输入个人简介"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item>
            <cyber-button type="primary" @click="handleUpdateProfile">
              保存修改
            </cyber-button>
            <cyber-button @click="handleReset" style="margin-left: 12px">
              重置
            </cyber-button>
          </el-form-item>
        </el-form>
      </cyber-card>

      <!-- 修改密码 -->
      <cyber-card class="password-card">
        <h3 class="section-title">修改密码</h3>
        
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
        >
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入旧密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位）"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <cyber-button type="primary" @click="handleUpdatePassword">
              修改密码
            </cyber-button>
          </el-form-item>
        </el-form>
      </cyber-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { uploadImage } from '@/api/upload'
import CyberCard from '@/components/CyberCard.vue'
import CyberButton from '@/components/CyberButton.vue'

// 用户信息
const userInfo = ref<any>({})
const uploading = ref(false)

// 个人信息表单
const profileFormRef = ref()
const profileForm = reactive({
  nickname: '',
  email: '',
  avatar: '',
  github: '',
  csdn: '',
  intro: ''
})

// 个人信息验证规则
const profileRules = {
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

// 密码表单
const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data
    
    // 填充表单
    Object.assign(profileForm, {
      nickname: res.data.nickname || '',
      email: res.data.email || '',
      avatar: res.data.avatar || '',
      github: res.data.github || '',
      csdn: res.data.csdn || '',
      intro: res.data.intro || ''
    })
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 保存个人信息
const handleUpdateProfile = async () => {
  try {
    await profileFormRef.value.validate()
    
    await updateUserInfo(profileForm)
    ElMessage.success('保存成功')
    loadUserInfo()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 重置个人信息
const handleReset = () => {
  loadUserInfo()
  profileFormRef.value?.clearValidate()
}

// 修改密码
const handleUpdatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清空表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value?.clearValidate()
    
    // 延迟后跳转到登录页
    setTimeout(() => {
      // 清除token
      localStorage.removeItem('token')
      window.location.href = '/login'
    }, 1500)
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}

// 上传前验证
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = async (options: any) => {
  const { file } = options
  
  uploading.value = true
  try {
    const res = await uploadImage(file)
    profileForm.avatar = res.data.url
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  } finally {
    uploading.value = false
  }
}

// 初始化
onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.user-profile-page {
  padding: 20px;
}

.page-title {
  font-size: 28px;
  color: $neon-cyan;
  margin: 0 0 24px 0;
  text-shadow: 0 0 10px $neon-cyan;
}

.profile-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  max-width: 1400px;
}

.profile-card,
.password-card {
  .section-title {
    font-size: 20px;
    color: $neon-cyan;
    margin: 0 0 24px 0;
    padding-bottom: 12px;
    border-bottom: 1px solid rgba($neon-cyan, 0.3);
  }
}

.avatar-upload-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 2px dashed rgba($neon-cyan, 0.3);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;
    background: rgba($bg-secondary, 0.3);

    &:hover {
      border-color: $neon-cyan;
      box-shadow: 0 0 10px rgba($neon-cyan, 0.5);
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: $neon-cyan;
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .avatar {
    width: 120px;
    height: 120px;
    display: block;
    object-fit: cover;
  }
}

.upload-tips {
  flex: 1;

  p {
    margin: 0 0 8px 0;
    color: $text-primary;
  }

  .tip-text {
    font-size: 12px;
    color: $text-secondary;
  }
}

:deep(.el-form-item__label) {
  color: $text-primary;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  background: rgba($bg-secondary, 0.5);
  border-color: rgba($neon-cyan, 0.3);
  color: $text-primary;

  &:focus {
    border-color: $neon-cyan;
  }

  &:disabled {
    background: rgba($bg-secondary, 0.3);
    color: $text-secondary;
  }
}

@media (max-width: 1024px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
}
</style>

