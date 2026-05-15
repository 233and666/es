<template>
  <div class="login-container">
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
      <div class="circle circle-5"></div>
      <div class="blur-bg"></div>
      <div class="grid-bg"></div>
    </div>

    <div class="login-card">
      <div class="login-left">
        <div class="welcome-text">
          <h1>管理中心</h1>
          <p>登录您的管理员账户，管理系统数据</p>
        </div>
        <div class="illustration">
          <div class="icon-admin">
            <el-icon size="80" color="#fff">
            </el-icon>
          </div>
        </div>
        <div class="left-decoration">
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
        </div>
      </div>

      <div class="login-right">
        <div class="login-header">
          <h2>管理员登录</h2>
          <p>请输入管理员账户信息</p>
        </div>

        <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            size="large"
        >
          <el-form-item prop="username">
            <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                clearable
                @focus="inputFocus('username')"
                @blur="inputBlur('username')"
                class="login-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                clearable
                @keyup.enter="handleLogin"
                @focus="inputFocus('password')"
                @blur="inputBlur('password')"
                class="login-input"
            />
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="rememberMe" class="remember-checkbox">记住我</el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
                type="primary"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <el-link type="primary" :underline="false" @click="goToUserLogin" class="back-link">
            返回用户登录
          </el-link>
        </div>
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
const rememberMe = ref(false)

const inputState = ref({
  username: false,
  password: false
})

const loginForm = reactive({
  username: '',
  password: ''
})

const usernameError = ref('')
const passwordError = ref('')

const inputFocus = (type) => {
  inputState.value[type] = true
}
const inputBlur = (type) => {
  inputState.value[type] = false
}

const checkUsername = () => {
  if (!loginForm.username.trim()) {
    usernameError.value = '用户名不能为空'
  } else {
    usernameError.value = ''
  }
}

const checkPassword = () => {
  if (!loginForm.password.trim()) {
    passwordError.value = '密码不能为空'
  } else {
    passwordError.value = ''
  }
}

const handleLogin = async () => {
  checkUsername()
  checkPassword()
  if (usernameError.value || passwordError.value) {
    return
  }
  try {
    loading.value = true
    const response = await api.admin.login(loginForm)
    if (response.success) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('role', 'admin')
      if (rememberMe.value) {
        localStorage.setItem('rememberedAdmin', loginForm.username)
      }
      ElMessage.success('管理员登录成功！')
      await router.push('/adminHome')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error(error.message || '登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const goToUserLogin = () => {
  router.push('/login')
}

const checkRememberedAdmin = () => {
  const rememberedAdmin = localStorage.getItem('rememberedAdmin')
  if (rememberedAdmin) {
    loginForm.username = rememberedAdmin
    rememberMe.value = true
  }
}

checkRememberedAdmin()
</script>

<style scoped>
:global(html),
:global(body) {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3a8a 0%, #312e81 50%, #4c1d95 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', 'PingFang SC', Roboto, 'Helvetica Neue', sans-serif;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.grid-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
  background-size: 40px 40px;
  z-index: 1;
}

.blur-bg {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 40%, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0) 70%);
  animation: rotateBg 40s linear infinite;
  z-index: 0;
}

@keyframes rotateBg {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  animation: float 15s ease-in-out infinite;
  z-index: 0;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -200px;
  right: -120px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.3), rgba(99, 102, 241, 0.1));
  animation-delay: 0s;
  box-shadow: 0 0 80px rgba(139,92,246,0.2);
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -150px;
  left: -100px;
  background: linear-gradient(225deg, rgba(59, 130, 246, 0.25), rgba(34, 211, 238, 0.1));
  animation-delay: -2s;
  box-shadow: 0 0 60px rgba(59,130,246,0.15);
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 40%;
  left: 15%;
  background: linear-gradient(45deg, rgba(168, 85, 247, 0.2), rgba(255, 255, 255, 0));
  animation-delay: -5s;
}

.circle-4 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  right: 10%;
  background: rgba(99, 102, 241, 0.15);
  animation-delay: -8s;
}

.circle-5 {
  width: 220px;
  height: 220px;
  top: 10%;
  right: 20%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.25), transparent);
  animation-delay: -3s;
}

@keyframes float {
  0% {
    transform: translateY(0px) translateX(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) translateX(15px) rotate(8deg);
  }
  100% {
    transform: translateY(0px) translateX(0px) rotate(0deg);
  }
}

.login-card {
  display: flex;
  width: 980px;
  max-width: 95%;
  height: 580px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 36px;
  box-shadow:
      0 30px 60px rgba(0, 0, 0, 0.25),
      0 0 0 1px rgba(255, 255, 255, 0.3) inset,
      0 0 40px rgba(255,255,255,0.1) inset;
  overflow: hidden;
  z-index: 2;
  backdrop-filter: blur(8px);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
}

.login-card:hover {
  transform: translateY(-6px);
  box-shadow:
      0 40px 80px rgba(0, 0, 0, 0.3),
      0 0 0 1px rgba(255, 255, 255, 0.4) inset,
      0 0 60px rgba(255,255,255,0.15) inset;
}

.login-left {
  flex: 1;
  background: linear-gradient(145deg, #6366f1dd, #8b5cf6dd);
  backdrop-filter: blur(10px);
  color: white;
  padding: 70px 50px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}

.login-left::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.35), transparent 70%),
      linear-gradient(45deg, rgba(0,0,0,0.05) 0%, transparent 100%);
  pointer-events: none;
}

.left-decoration {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.left-decoration .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255,255,255,0.4);
  animation: pulse 3s infinite alternate;
}

.left-decoration .dot:nth-child(2) {
  animation-delay: 0.5s;
}

.left-decoration .dot:nth-child(3) {
  animation-delay: 1s;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.6; }
  100% { transform: scale(1.3); opacity: 1; }
}

.welcome-text h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: 1.2px;
  text-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  position: relative;
}

.welcome-text h1::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 60px;
  height: 4px;
  background: rgba(255,255,255,0.8);
  border-radius: 2px;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.95;
  line-height: 1.6;
  font-weight: 400;
  max-width: 300px;
}

.illustration {
  text-align: center;
  margin-top: 40px;
}

.icon-admin {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 140px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  backdrop-filter: blur(15px);
  box-shadow:
      0 12px 30px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255,255,255,0.3) inset;
  transition: all 0.4s ease;
  position: relative;
}

.icon-admin::after {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.2);
}

.icon-admin:hover {
  transform: scale(1.08) rotate(5deg);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.login-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.99);
  position: relative;
}

.login-right::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  border-radius: 3px 3px 0 0;
}

.login-header {
  text-align: center;
  margin-bottom: 45px;
}

.login-header h2 {
  font-size: 36px;
  font-weight: 700;
  color: #1f2f3f;
  margin-bottom: 10px;
  background: linear-gradient(120deg, #6366f1, #8b5cf6);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  letter-spacing: 1px;
}

.login-header p {
  color: #6c757d;
  font-size: 15px;
  letter-spacing: 0.8px;
}

.login-form {
  width: 100%;
}

.login-input {
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper) {
  border-radius: 16px;
  background-color: #f8f9fc;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid transparent;
  padding: 8px 16px;
  height: 56px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 5px 15px rgba(99, 102, 241, 0.15);
  background-color: #fff;
  transform: translateY(-1px);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #6366f1;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.25);
  background-color: #fff;
  transform: translateY(-2px);
}

:deep(.el-input__inner) {
  font-weight: 500;
  font-size: 15px;
  color: #2d3748;
  border: none !important;
}

:deep(.el-input__prefix) {
  color: #6366f1;
  font-size: 18px;
}

.el-form-item {
  margin-bottom: 28px;
}

.el-form-item:last-child {
  margin-bottom: 0;
}

.remember-checkbox {
  --el-checkbox-text-color: #4a5568;
  font-weight: 500;
  font-size: 14px;
}

:deep(.el-checkbox__inner) {
  border-radius: 6px;
  width: 18px;
  height: 18px;
}

:deep(.el-checkbox__inner:hover) {
  border-color: #6366f1;
}

.login-btn {
  width: 100%;
  height: 56px;
  border-radius: 16px;
  font-size: 17px;
  font-weight: 600;
  background: linear-gradient(105deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  transition: all 0.4s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  letter-spacing: 1.2px;
  box-shadow: 0 10px 25px rgba(99, 102, 241, 0.3);
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: all 0.6s ease;
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 30px rgba(99, 102, 241, 0.45);
  background: linear-gradient(105deg, #818cf8, #a78bfa);
}

.login-btn:active {
  transform: translateY(1px);
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 35px;
  color: #7e8c8d;
  font-size: 15px;
}

.back-link {
  font-weight: 600;
  position: relative;
}

.back-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #8b5cf6;
  transition: width 0.3s ease;
}

.back-link:hover::after {
  width: 100%;
}

@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 95%;
    height: 95vh;
    max-height: none;
    overflow-y: auto;
    border-radius: 24px;
  }

  .login-left {
    padding: 50px 30px;
    text-align: center;
    min-height: 300px;
  }

  .login-right {
    padding: 50px 30px;
  }

  .welcome-text h1 {
    font-size: 32px;
  }

  .welcome-text p {
    max-width: 100%;
  }

  .login-header h2 {
    font-size: 30px;
  }

  .icon-admin {
    width: 120px;
    height: 120px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 14px;
    height: 52px;
  }

  .login-btn {
    height: 52px;
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 0 10px;
  }

  .login-card {
    width: 100%;
    height: 100vh;
    border-radius: 0;
  }

  .login-left, .login-right {
    padding: 40px 20px;
  }
}
</style>