<template>
  <div class="user-center-page">
    <div class="user-center-container">
      <h1 class="page-title">个人中心</h1>
      
      <el-card class="user-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span class="header-title">
              <el-icon><User /></el-icon>
              个人信息
            </span>
            <el-button type="primary" @click="handleSave" :loading="saving">
              保存修改
            </el-button>
          </div>
        </template>

        <el-form :model="userForm" label-width="100px" class="user-form">
          <el-form-item label="用户名">
            <el-input v-model="userForm.username" disabled />
            <div class="form-item-tip">
              <el-icon><Lock /></el-icon>
              用户名不可修改
            </div>
          </el-form-item>

          <el-form-item label="昵称">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
          </el-form-item>

          <el-form-item label="邮箱">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>

          <el-form-item label="头像">
            <div class="avatar-upload">
              <div class="avatar-preview" v-if="userForm.avatar">
                <img :src="userForm.avatar" alt="avatar" />
              </div>
              <el-upload
                class="avatar-uploader"
                :action="''"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <el-button type="primary" :loading="uploading">
                  <el-icon><Upload /></el-icon>
                  {{ uploading ? '上传中...' : '上传头像' }}
                </el-button>
              </el-upload>
              <div class="avatar-tip">支持 JPG、PNG 格式，大小不超过 5MB</div>
            </div>
          </el-form-item>

          <el-form-item label="GitHub">
            <el-input v-model="userForm.github" placeholder="请输入GitHub地址" />
          </el-form-item>

          <el-form-item label="CSDN">
            <el-input v-model="userForm.csdn" placeholder="请输入CSDN地址" />
          </el-form-item>

          <el-form-item label="个人简介">
            <el-input
              v-model="userForm.intro"
              type="textarea"
              :rows="4"
              placeholder="请输入个人简介"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="password-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">
              <el-icon><Lock /></el-icon>
              修改密码
            </span>
          </div>
        </template>

        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" class="password-form">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="actions">
        <el-button @click="router.push('/home')">返回首页</el-button>
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Lock, Upload } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { uploadImage } from '@/api/upload'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const changingPassword = ref(false)
const uploading = ref(false)
const passwordFormRef = ref<FormInstance>()

const userForm = reactive({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  github: '',
  csdn: '',
  intro: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证确认密码
const validateConfirmPassword = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  loading.value = true
  try {
    const res: any = await getUserInfo()
    if (res.code === 200) {
      Object.assign(userForm, res.data)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  } finally {
    loading.value = false
  }
}

// 保存用户信息
const handleSave = async () => {
  saving.value = true
  try {
    // 只发送UserDTO中定义的字段
    const updateData = {
      nickname: userForm.nickname,
      avatar: userForm.avatar,
      email: userForm.email,
      github: userForm.github,
      csdn: userForm.csdn,
      intro: userForm.intro
    }
    console.log('保存数据:', updateData)
    const res: any = await updateUserInfo(updateData)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      // 更新store中的用户信息
      if (userStore.userInfo) {
        userStore.userInfo.nickname = userForm.nickname
        userStore.userInfo.avatar = userForm.avatar
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      }
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error: any) {
    console.error('保存失败:', error)
    console.error('错误详情:', error.response)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('保存失败，请稍后重试')
    }
  } finally {
    saving.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        const res: any = await updatePassword(passwordForm)
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          // 清空密码表单
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          // 退出登录
          setTimeout(() => {
            userStore.logout()
            router.push('/login')
          }, 1500)
        } else {
          ElMessage.error(res.message || '修改密码失败')
        }
      } catch (error: any) {
        console.error('修改密码失败:', error)
        if (error.response?.data?.message) {
          ElMessage.error(error.response.data.message)
        } else {
          ElMessage.error('修改密码失败，请稍后重试')
        }
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// 上传头像前的校验
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 自定义上传头像
const handleAvatarUpload = async (options: UploadRequestOptions) => {
  uploading.value = true
  try {
    const res: any = await uploadImage(options.file as File)
    console.log('上传返回结果:', res)
    if (res.code === 200) {
      // 后端直接返回URL字符串
      userForm.avatar = res.data
      ElMessage.success('头像上传成功')
      console.log('头像URL已更新:', userForm.avatar)
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error: any) {
    console.error('上传失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('上传失败，请稍后重试')
    }
  } finally {
    uploading.value = false
  }
}

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

// 初始化
onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
@use "sass:color";

.user-center-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: transparent;
}

.user-center-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 48px;
  font-weight: 700;
  color: $neon-cyan;
  text-shadow: 0 0 10px rgba($neon-cyan, 0.5), 0 0 20px rgba($neon-cyan, 0.3);
  text-align: center;
  margin-bottom: 48px;
  text-transform: uppercase;
  letter-spacing: 6px;
  animation: neon-glow 2s ease-in-out infinite alternate;
}

@keyframes neon-glow {
  from {
    text-shadow: 0 0 8px rgba($neon-cyan, 0.4), 0 0 15px rgba($neon-cyan, 0.3);
  }
  to {
    text-shadow: 0 0 12px rgba($neon-cyan, 0.6), 0 0 20px rgba($neon-cyan, 0.4), 0 0 25px rgba($neon-cyan, 0.2);
  }
}

.user-card,
.password-card {
  margin-bottom: 24px;
  background: rgba($bg-card, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba($neon-cyan, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  
  :deep(.el-card__header) {
    background: rgba($bg-secondary, 0.6);
    border-bottom: 1px solid rgba($neon-cyan, 0.2);
    padding: 18px 24px;
  }
  
  :deep(.el-card__body) {
    padding: 32px 24px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: $neon-cyan;
    text-shadow: 0 0 8px rgba($neon-cyan, 0.4);
    
    .el-icon {
      font-size: 20px;
    }
  }
}

.user-form,
.password-form {
  .el-form-item {
    margin-bottom: 24px;
    
    :deep(.el-form-item__label) {
      color: $text-primary;
      font-weight: 500;
    }
  }
  
  .form-item-tip {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 8px;
    padding: 6px 12px;
    background: rgba($neon-yellow, 0.08);
    border: 1px solid rgba($neon-yellow, 0.2);
    border-radius: 6px;
    color: $neon-yellow;
    font-size: 13px;
    
    .el-icon {
      font-size: 14px;
    }
  }
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
  
  .avatar-preview {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 2px solid $neon-cyan;
    overflow: hidden;
    box-shadow: 0 0 15px rgba($neon-cyan, 0.3);
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .avatar-uploader {
    flex: 1;
  }
  
  .avatar-tip {
    color: $text-disabled;
    font-size: 12px;
    margin-top: 8px;
  }
}

.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}

// Element Plus 组件样式定制
:deep(.el-input) {
  .el-input__wrapper {
    background: rgba($bg-secondary, 0.8);
    border: 1px solid rgba($neon-cyan, 0.2);
    box-shadow: inset 0 0 15px rgba(0, 0, 0, 0.3);
    transition: all 0.3s;
    
    &:hover {
      border-color: rgba($neon-cyan, 0.4);
    }
    
    &.is-focus {
      border-color: $neon-cyan;
      box-shadow: 0 0 15px rgba($neon-cyan, 0.3), inset 0 0 20px rgba($neon-cyan, 0.05);
    }
  }
  
  .el-input__inner {
    color: $text-primary;
    
    &::placeholder {
      color: $text-disabled;
    }
    
    &:disabled {
      color: $text-disabled;
      -webkit-text-fill-color: $text-disabled;
    }
  }
}

:deep(.el-textarea) {
  .el-textarea__inner {
    background: rgba($bg-secondary, 0.8);
    border: 1px solid rgba($neon-cyan, 0.2);
    color: $text-primary;
    box-shadow: inset 0 0 15px rgba(0, 0, 0, 0.3);
    transition: all 0.3s;
    
    &:hover {
      border-color: rgba($neon-cyan, 0.4);
    }
    
    &:focus {
      border-color: $neon-cyan;
      box-shadow: 0 0 15px rgba($neon-cyan, 0.3), inset 0 0 20px rgba($neon-cyan, 0.05);
    }
    
    &::placeholder {
      color: $text-disabled;
    }
  }
  
  // 修复字数统计背景
  .el-input__count {
    background: transparent !important;
    color: $text-secondary !important;
  }
}

:deep(.el-button) {
  &.el-button--primary {
    background: linear-gradient(135deg, $neon-cyan, rgba($neon-cyan, 0.8));
    border-color: $neon-cyan;
    color: $bg-primary;
    font-weight: 600;
    box-shadow: 0 0 15px rgba($neon-cyan, 0.3);
    transition: all 0.3s;
    
    &:hover {
      background: linear-gradient(135deg, color.adjust($neon-cyan, $lightness: 10%), $neon-cyan);
      box-shadow: 0 0 20px rgba($neon-cyan, 0.5);
      transform: translateY(-2px);
    }
  }
  
  &.el-button--danger {
    background: linear-gradient(135deg, $neon-magenta, rgba($neon-magenta, 0.8));
    border-color: $neon-magenta;
    color: $bg-primary;
    font-weight: 600;
    box-shadow: 0 0 15px rgba($neon-magenta, 0.3);
    
    &:hover {
      background: linear-gradient(135deg, color.adjust($neon-magenta, $lightness: 10%), $neon-magenta);
      box-shadow: 0 0 20px rgba($neon-magenta, 0.5);
      transform: translateY(-2px);
    }
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
    letter-spacing: 4px;
    margin-bottom: 32px;
  }
  
  .user-form,
  .password-form {
    :deep(.el-form-item__label) {
      width: 100% !important;
      text-align: left;
      margin-bottom: 8px;
    }
    
    :deep(.el-form-item__content) {
      margin-left: 0 !important;
    }
  }
  
  .avatar-upload {
    flex-direction: column;
    align-items: flex-start;
    
    .avatar-preview {
      width: 80px;
      height: 80px;
    }
  }
  
  .actions {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>

