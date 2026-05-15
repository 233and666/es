<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="28" color="#fff"><Menu /></el-icon>
          <span class="logo-text">ES管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info" @click="toggleUserMenu">
          <el-avatar :src="userAvatar" class="user-avatar"></el-avatar>
          <span class="username">{{ userName }}</span>
          <el-icon size="16" color="#fff"><ArrowDown /></el-icon>
        </div>
        <!-- 用户下拉菜单 -->
        <div class="user-dropdown" v-show="showUserMenu">
          <div class="dropdown-item" @click="goToProfile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </div>
          <div class="dropdown-item" @click="goToSetting">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </div>
          <div class="dropdown-item logout" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="home-main">
      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <div class="welcome-content">
          <h1>欢迎回来，{{ userName }}！</h1>
          <p>今天是{{ todayDate }}，祝您工作愉快</p>
        </div>
        <div class="stats-card">
          <div class="stat-item">
            <span class="stat-value">0</span>
            <span class="stat-label">待处理订单</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">0</span>
            <span class="stat-label">商品总数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">0</span>
            <span class="stat-label">今日访问</span>
          </div>
        </div>
      </section>

      <!-- 功能入口区域 -->
      <section class="features-section">
        <h2 class="section-title">功能中心</h2>
        <div class="features-grid">
          <!-- 商品管理 -->
          <div class="feature-card" @click="goToGoodsManage">
            <div class="card-icon goods-icon">
              <el-icon size="36"><ShoppingCart /></el-icon>
            </div>
            <div class="card-content">
              <h3>商品管理</h3>
              <p>管理商品信息、库存、分类和价格</p>
            </div>
            <div class="card-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 订单管理 -->
          <div class="feature-card" @click="goToOrderManage">
            <div class="card-icon order-icon">
              <el-icon size="36"><Ticket /></el-icon>
            </div>
            <div class="card-content">
              <h3>订单管理</h3>
              <p>查看、处理和跟踪所有订单状态</p>
            </div>
            <div class="card-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 个人信息 -->
          <div class="feature-card" @click="goToProfile">
            <div class="card-icon profile-icon">
              <el-icon size="36"><UserFilled /></el-icon>
            </div>
            <div class="card-content">
              <h3>个人信息</h3>
              <p>修改个人资料、密码和联系方式</p>
            </div>
            <div class="card-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 数据统计 -->
          <div class="feature-card" @click="goToDataAnalysis">
            <div class="card-icon data-icon">
              <el-icon size="36"><DataAnalysis /></el-icon>
            </div>
            <div class="card-content">
              <h3>数据统计</h3>
              <p>查看销售数据、用户分析和趋势报表</p>
            </div>
            <div class="card-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 系统设置 -->
          <div class="feature-card" @click="goToSetting">
            <div class="card-icon setting-icon">
              <el-icon size="36"><Tools /></el-icon>
            </div>
            <div class="card-content">
              <h3>系统设置</h3>
              <p>配置系统参数、权限和通知设置</p>
            </div>
            <div class="card-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>

          <!-- 帮助中心 -->
          <div class="feature-card" @click="goToHelpCenter">
            <div class="card-icon help-icon">
              <el-icon size="36"><QuestionFilled /></el-icon>
            </div>
            <div class="card-content">
              <h3>帮助中心</h3>
              <p>查看使用教程、常见问题和联系客服</p>
            </div>
            <div class="card-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="home-footer">
      <p>© 2025 ES管理系统 - 版权所有</p>
      <div class="footer-links">
        <a href="javascript:;">使用条款</a>
        <a href="javascript:;">隐私政策</a>
        <a href="javascript:;">联系我们</a>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Menu, ArrowDown, User, Setting, SwitchButton, ShoppingCart,
  Ticket, UserFilled, DataAnalysis, Tools, QuestionFilled, ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()

// 用户信息
const userName = ref('管理员')
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')
const showUserMenu = ref(false)

// 日期格式化
const todayDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 切换用户菜单显示
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

// 点击空白处关闭菜单
onMounted(() => {
  document.addEventListener('click', (e) => {
    const userInfo = document.querySelector('.user-info')
    if (userInfo && !userInfo.contains(e.target)) {
      showUserMenu.value = false
    }
  })
})

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('rememberedUsername')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 功能跳转（预留入口，可根据实际路由修改）
const goToGoodsManage = () => {
  ElMessage.info('即将进入商品管理页面（功能预留）')
  // router.push('/goods-manage')
}

const goToOrderManage = () => {
  ElMessage.info('即将进入订单管理页面（功能预留）')
  // router.push('/order-manage')
}

const goToProfile = () => {
  ElMessage.info('即将进入个人信息页面（功能预留）')
  // router.push('/profile')
}

const goToDataAnalysis = () => {
  ElMessage.info('即将进入数据统计页面（功能预留）')
  // router.push('/data-analysis')
}

const goToSetting = () => {
  ElMessage.info('即将进入系统设置页面（功能预留）')
  // router.push('/system-setting')
}

const goToHelpCenter = () => {
  ElMessage.info('即将进入帮助中心页面（功能预留）')
  // router.push('/help-center')
}
</script>

<style scoped>
/* 全局重置 */
:global(*) {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:global(html),
:global(body),
:global(#app) {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  font-family: 'Segoe UI', 'PingFang SC', Roboto, 'Helvetica Neue', sans-serif;
}

/* 主容器 - 渐变背景 */
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #6b8dd6 100%);
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 70px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #fff;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.header-right {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  cursor: pointer;
  padding: 8px 15px;
  border-radius: 30px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.username {
  font-weight: 500;
}

/* 用户下拉菜单 */
.user-dropdown {
  position: absolute;
  top: 60px;
  right: 0;
  width: 200px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 200;
  animation: dropdownFade 0.3s ease forwards;
}

@keyframes dropdownFade {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background: #f0f7ff;
  color: #409eff;
  padding-left: 25px;
}

.dropdown-item.logout {
  color: #f56c6c;
}

.dropdown-item.logout:hover {
  background: #fff2f2;
  color: #f56c6c;
}

/* 主内容区域 */
.home-main {
  flex: 1;
  padding: 40px 30px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 50px;
  color: #fff;
}

.welcome-content h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.welcome-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 20px;
}

/* 数据统计卡片 */
.stats-card {
  display: flex;
  gap: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px 30px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  position: relative;
}

.stat-item:not(:last-child)::after {
  content: '';
  position: absolute;
  right: 0;
  top: 15%;
  height: 70%;
  width: 1px;
  background: rgba(255, 255, 255, 0.2);
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 5px;
  background: linear-gradient(90deg, #fff, #e0e7ff);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

/* 功能区域 */
.features-section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 28px;
  color: #fff;
  margin-bottom: 30px;
  position: relative;
  padding-left: 15px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 28px;
  background: linear-gradient(180deg, #409eff, #67c23a);
  border-radius: 3px;
}

/* 功能卡片网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

/* 功能卡片样式 */
.feature-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px 25px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

/* 卡片hover效果 */
.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

/* 卡片背景动效 */
.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.05), transparent);
  transition: all 0.8s ease;
}

.feature-card:hover::before {
  left: 100%;
}

/* 卡片图标区域 */
.card-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.goods-icon {
  background: linear-gradient(135deg, #e8f4f8, #f0f8fb);
  color: #409eff;
}

.order-icon {
  background: linear-gradient(135deg, #fdf2f8, #fef7fb);
  color: #e6a23c;
}

.profile-icon {
  background: linear-gradient(135deg, #f5fafe, #eaf6fa);
  color: #67c23a;
}

.data-icon {
  background: linear-gradient(135deg, #f8f8f8, #fafafa);
  color: #909399;
}

.setting-icon {
  background: linear-gradient(135deg, #faf0f5, #fdf2f8);
  color: #f56c6c;
}

.help-icon {
  background: linear-gradient(135deg, #eaf6fa, #e8f4f8);
  color: #722ed1;
}

/* 卡片内容 */
.card-content {
  flex: 1;
}

.card-content h3 {
  font-size: 20px;
  color: #1f2f3f;
  margin-bottom: 8px;
  font-weight: 600;
}

.card-content p {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.5;
}

/* 卡片箭头 */
.card-arrow {
  color: #409eff;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.feature-card:hover .card-arrow {
  opacity: 1;
  transform: translateX(5px);
}

/* 页脚 */
.home-footer {
  padding: 25px 30px;
  background: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.footer-links {
  display: flex;
  gap: 20px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.2s ease;
}

.footer-links a:hover {
  color: #fff;
  text-decoration: underline;
}

/* 响应式设计 - 平板 */
@media (max-width: 992px) {
  .home-main {
    padding: 30px 20px;
  }

  .stats-card {
    flex-wrap: wrap;
    gap: 15px;
  }

  .stat-item {
    flex: 1 1 calc(33.33% - 10px);
  }

  .stat-item:not(:last-child)::after {
    display: none;
  }

  .features-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

/* 响应式设计 - 手机 */
@media (max-width: 768px) {
  .home-header {
    padding: 0 15px;
    height: 60px;
  }

  .logo-text {
    font-size: 18px;
  }

  .username {
    display: none;
  }

  .welcome-content h1 {
    font-size: 28px;
  }

  .stats-card {
    padding: 15px;
  }

  .stat-item {
    flex: 1 1 100%;
    padding: 15px 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .stat-item:last-child {
    border-bottom: none;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .feature-card {
    padding: 25px 20px;
  }

  .home-footer {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}

/* 小屏手机适配 */
@media (max-width: 480px) {
  .home-main {
    padding: 20px 15px;
  }

  .welcome-section {
    margin-bottom: 30px;
  }

  .section-title {
    font-size: 24px;
  }

  .feature-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
}
</style>