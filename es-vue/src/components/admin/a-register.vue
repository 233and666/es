<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <el-icon size="48" color="#8b5cf6" icon="UserPlus" />
        <h2>管理员注册</h2>
        <p>只有超级管理员才能注册新管理员</p>
      </div>

      <el-form :model="registerForm" label-width="100px" class="register-form">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }]"
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            :rules="[{ required: true, message: '请确认密码', trigger: 'blur' }]"
          />
        </el-form-item>

        <el-form-item label="管理员类型">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="0">普通管理员</el-radio>
            <el-radio label="1">超级管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="register-btn" @click="handleRegister" :loading="loading">
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <el-link type="info" :underline="false" @click="goToLogin">
          返回首页
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/index.js'

const router = useRouter()

const loading = ref(false)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: '0'
})

const handleRegister = async () => {
  // 表单验证
  if (!registerForm.username.trim()) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!registerForm.password.trim()) {
    ElMessage.error('请输入密码')
    return
  }
  if (!registerForm.confirmPassword.trim()) {
    ElMessage.error('请确认密码')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  loading.value = true

  try {
    const response = await api.admin.register({
      username: registerForm.username,
      password: registerForm.password,
      role: registerForm.role === '1'
    })

    if (response.code === 200) {
      ElMessage.success('注册成功')
      setTimeout(() => {
        router.push('/adminHome')
      }, 1500)
    } else {
      ElMessage.error(response.msg || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/adminHome')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 35px;
}

.register-header h2 {
  margin: 15px 0 8px;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.register-header p {
  color: #999;
  font-size: 14px;
}

.register-form {
  margin-bottom: 20px;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.register-footer {
  text-align: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.register-footer a {
  font-size: 14px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 30px;
}

:deep(.el-radio) {
  font-size: 14px;
}
</style>