<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <!-- 左侧图片区域 -->
      <div class="register-left">
        <div class="welcome-text">
          <h1>加入我们</h1>
          <p>创建您的账户，开启ES管理系统之旅</p>
        </div>
        <div class="illustration">
          <div class="icon-user">
            <el-icon size="80" color="#E6A23C">
              <UserFilled />
            </el-icon>
          </div>
        </div>
        <div class="features">
          <div class="feature-item">
            <el-icon color="#67C23A"><CircleCheck /></el-icon>
            <span>安全可靠的数据保护</span>
          </div>
          <div class="feature-item">
            <el-icon color="#67C23A"><CircleCheck /></el-icon>
            <span>简单易用的操作界面</span>
          </div>
          <div class="feature-item">
            <el-icon color="#67C23A"><CircleCheck /></el-icon>
            <span>强大的系统功能</span>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="register-right">
        <div class="register-header">
          <h2>用户注册</h2>
          <p>请填写您的注册信息</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名（3-20位字符）"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（6-20位字符）"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>


          <el-form-item>
            <el-checkbox v-model="agreeTerms">
              我已阅读并同意
              <el-link type="primary" :underline="false">《用户协议》</el-link>
              和
              <el-link type="primary" :underline="false">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
              :loading="loading"
              :disabled="!agreeTerms"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <p>已有账户？
            <el-link type="primary" :underline="false" @click="goToLogin">
              立即登录
            </el-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Lock, Message, CircleCheck } from '@element-plus/icons-vue'
import { api } from '../../api/index.js'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)
const agreeTerms = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',

})

// 密码强度检查
const checkPasswordStrength = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value.length > 20) {
    callback(new Error('密码长度不能超过20位'))
  } else {
    callback()
  }
}

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}



// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: checkPasswordStrength, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],

}


// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return


  try {
    // 验证表单
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    // 检查是否同意协议
    if (!agreeTerms.value) {
      ElMessage.warning('请先阅读并同意用户协议和隐私政策')
      return
    }

    loading.value = true

    // 调用注册API
    const response = await api.user.register({
      username: registerForm.username,
      password: registerForm.password
    })

    if (response.success) {
      ElMessage.success('注册成功！请登录')

      // 跳转到登录页面
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      ElMessage.error(response.message+"1" || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
   // ElMessage.error(error.message+"注册失误" || '注册失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
}

.register-card {
  display: flex;
  width: 90%;
  max-width: 1000px;
  min-height: 600px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 1;
  margin: 20px;
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.welcome-text h1 {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 15px;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.6;
}

.illustration {
  text-align: center;
  margin: 30px 0;
}

.icon-user {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  backdrop-filter: blur(10px);
}

.features {
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.feature-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
}

.register-right {
  flex: 1.2;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.register-header p {
  color: #909399;
  font-size: 14px;
}

.register-form {
  width: 100%;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
}

.el-form-item:last-child {
  margin-bottom: 0;
}

.el-form-item:has(.register-btn) {
  margin-top: 30px;
}

.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
  border: none;
  transition: all 0.3s ease;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(230, 162, 60, 0.3);
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-footer {
  text-align: center;
  margin-top: 30px;
  color: #909399;
  font-size: 14px;
}

.register-footer p {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .register-card {
    width: 95%;
    max-width: 900px;
    min-height: 550px;
  }

  .register-left {
    padding: 40px 30px;
  }

  .register-right {
    padding: 40px 30px;
  }
}

@media (max-width: 768px) {
  .register-card {
    flex-direction: column;
    width: 95%;
    min-height: auto;
    margin: 10px;
  }

  .register-left {
    padding: 30px 20px;
    min-height: 250px;
  }

  .register-right {
    padding: 30px 20px;
  }

  .welcome-text h1 {
    font-size: 24px;
  }

  .register-header h2 {
    font-size: 24px;
  }

  .icon-user {
    width: 80px;
    height: 80px;
  }

  .el-icon {
    font-size: 40px !important;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: 10px;
  }

  .register-card {
    width: 100%;
    margin: 5px;
    border-radius: 15px;
  }

  .register-left {
    padding: 20px 15px;
    min-height: 200px;
  }

  .register-right {
    padding: 20px 15px;
  }

  .welcome-text h1 {
    font-size: 20px;
  }

  .register-header h2 {
    font-size: 20px;
  }
}
</style>