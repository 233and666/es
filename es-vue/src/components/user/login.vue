<template>
  <div class="login-container">
    <!-- 背景装饰（增强版） -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
      <div class="circle circle-5"></div>
      <div class="blur-bg"></div>
      <!-- 新增网格背景增强层次感 -->
      <div class="grid-bg"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 左侧图片区域 -->
      <div class="login-left">
        <div class="welcome-text">
          <h1>欢迎回来</h1>
          <p>登录您的账户，开始使用ES管理系统</p>
        </div>
        <div class="illustration">
          <div class="icon-user">
            <el-icon size="80" color="#fff">
              <User />
            </el-icon>
          </div>
        </div>
        <!-- 新增装饰元素 -->
        <div class="left-decoration">
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="login-right">
        <div class="login-header">
          <h2>用户登录</h2>
          <p>请输入您的账户信息</p>
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
            <el-link type="primary" class="forgot-password" :underline="false">
              忘记密码？
            </el-link>
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
          <p>还没有账户？
            <el-link type="primary" :underline="false" @click="goToRegister" class="register-link">
              立即注册
            </el-link>
          </p>
          <p class="admin-link-wrapper">
            <el-link type="info" :underline="false" @click="goToAdminLogin" class="admin-link">
              管理员登录
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
import { User, Lock } from '@element-plus/icons-vue'
import { api } from '../../api/index.js'

const router = useRouter()

const loading = ref(false)
const rememberMe = ref(false)
// 新增输入框聚焦状态
const inputState = ref({
  username: false,
  password: false
})

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})
// 表单校验错误提示
const usernameError = ref('')
const passwordError = ref('')

// 新增输入框聚焦/失焦处理
const inputFocus = (type) => {
  inputState.value[type] = true
}
const inputBlur = (type) => {
  inputState.value[type] = false
}

// 表单校验：用户名非空
const checkUsername = () => {
  if (!loginForm.username.trim()) {
    usernameError.value = '用户名不能为空'
  } else {
    usernameError.value = ''
  }
}
// 表单校验：密码非空
const checkPassword = () => {
  if (!loginForm.password.trim()) {
    passwordError.value = '密码不能为空'
  } else {
    passwordError.value = ''
  }
}
// 处理登录
const handleLogin = async () => {
  checkUsername()
  checkPassword()
  if (usernameError.value || passwordError.value) {
    return // 有错误，不提交
  }
  try {
    loading.value = true
    const response = await api.user.login(loginForm)
    if (response.success) {
      localStorage.setItem('token', response.data.token)
      if (rememberMe.value) {
        localStorage.setItem('rememberedUsername', loginForm.username)
      }
      ElMessage.success('登录成功！')
      await router.push('/home')
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

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}

// 跳转到管理员登录页面
const goToAdminLogin = () => {
  router.push('/adminlogin')
}

// 页面加载时检查是否有记住的用户名
const checkRememberedUser = () => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    rememberMe.value = true
  }
}
// 初始化
checkRememberedUser()
</script>

<style scoped>
/* 确保页面完全铺满屏幕 */
:global(html),
:global(body) {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow: hidden; /* 防止滚动条出现 */
}

.login-container {
  width: 100vw; /* 视口宽度100% */
  height: 100vh; /* 视口高度100% */
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', 'PingFang SC', Roboto, 'Helvetica Neue', sans-serif;
}

/* 背景装饰层 - 增强版 */
.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

/* 新增网格背景 */
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

/* 优化圆形装饰动效和样式 */
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
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.25), rgba(255, 255, 255, 0.05));
  animation-delay: 0s;
  box-shadow: 0 0 80px rgba(255,255,255,0.1);
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -150px;
  left: -100px;
  background: linear-gradient(225deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.02));
  animation-delay: -2s;
  box-shadow: 0 0 60px rgba(255,255,255,0.08);
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 40%;
  left: 15%;
  background: linear-gradient(45deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0));
  animation-delay: -5s;
}

.circle-4 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  right: 10%;
  background: rgba(255, 255, 255, 0.12);
  animation-delay: -8s;
}

.circle-5 {
  width: 220px;
  height: 220px;
  top: 10%;
  right: 20%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2), transparent);
  animation-delay: -3s;
}

/* 优化浮动动画 */
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

/* 登录卡片 - 增强质感 */
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

/* 左侧区域 - 优化渐变和装饰 */
.login-left {
  flex: 1;
  background: linear-gradient(145deg, #409effdd, #67c23add);
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

/* 左侧新增装饰点 */
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

.icon-user {
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

.icon-user::after {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.2);
}

.icon-user:hover {
  transform: scale(1.08) rotate(5deg);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

/* 右侧区域 */
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
  background: linear-gradient(90deg, #409eff, #67c23a);
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
  background: linear-gradient(120deg, #409eff, #67c23a);
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

/* 表单项样式优化 */
.login-form {
  width: 100%;
}

/* 输入框样式增强 */
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
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.15);
  background-color: #fff;
  transform: translateY(-1px);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.25);
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
  color: #409eff;
  font-size: 18px;
}

.el-form-item {
  margin-bottom: 28px;
}

.el-form-item:last-child {
  margin-bottom: 0;
}

/* 复选框样式优化 */
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
  border-color: #409eff;
}

.forgot-password {
  float: right;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.forgot-password:hover {
  color: #67c23a !important;
  transform: translateX(2px);
}

/* 登录按钮 - 增强视觉效果 */
.login-btn {
  width: 100%;
  height: 56px;
  border-radius: 16px;
  font-size: 17px;
  font-weight: 600;
  background: linear-gradient(105deg, #409eff 0%, #67c23a 100%);
  border: none;
  transition: all 0.4s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  letter-spacing: 1.2px;
  box-shadow: 0 10px 25px rgba(64, 158, 255, 0.3);
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
  box-shadow: 0 15px 30px rgba(64, 158, 255, 0.45);
  background: linear-gradient(105deg, #66b1ff, #85ce4b);
}

.login-btn:active {
  transform: translateY(1px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
}

/* 登录页脚 */
.login-footer {
  text-align: center;
  margin-top: 35px;
  color: #7e8c8d;
  font-size: 15px;
}

.login-footer p {
  margin: 0;
}

.register-link {
  font-weight: 600;
  position: relative;
}

.register-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #67c23a;
  transition: width 0.3s ease;
}

.register-link:hover::after {
  width: 100%;
}

.admin-link-wrapper {
  margin-top: 12px;
}

.admin-link {
  font-weight: 500;
  font-size: 14px;
  position: relative;
}

.admin-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #409eff;
  transition: width 0.3s ease;
}

.admin-link:hover::after {
  width: 100%;
}

/* 响应式设计 - 确保移动端也铺满屏幕 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 95%;
    height: 95vh; /* 移动端占95%视口高度 */
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

  .icon-user {
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

/* 适配更小屏幕 */
@media (max-width: 480px) {
  .login-container {
    padding: 0 10px;
  }

  .login-card {
    width: 100%;
    height: 100vh; /* 小屏完全铺满 */
    border-radius: 0;
  }

  .login-left, .login-right {
    padding: 40px 20px;
  }
}
</style>