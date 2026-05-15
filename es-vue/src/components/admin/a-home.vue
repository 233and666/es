<template>
  <div class="admin-container">
    <!-- 顶部导航栏 -->
    <header class="admin-header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="28" color="#fff" icon="Settings" />
          <span class="logo-text">管理中心</span>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info" @click="toggleUserMenu">
          <el-avatar :src="userAvatar" class="user-avatar"></el-avatar>
          <span class="username">{{ userName }}</span>
          <el-icon size="16" color="#fff" icon="ArrowDown" />
        </div>
        <div class="user-dropdown" v-show="showUserMenu">
          <div class="dropdown-item" @click="goToProfile">
            <el-icon icon="User" />
            <span>个人信息</span>
          </div>
          <div class="dropdown-item" @click="goToSetting">
            <el-icon icon="Setting" />
            <span>系统设置</span>
          </div>
          <div class="dropdown-item logout" @click="handleLogout">
            <el-icon icon="SwitchButton" />
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="admin-main">
      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <div class="welcome-content">
          <h1>欢迎回来，{{ userName }}！</h1>
          <p>今天是{{ todayDate }}，祝您工作愉快</p>
        </div>
        <div class="stats-card">
          <div class="stat-item">
            <el-icon size="24" color="#8b5cf6" icon="Users" />
            <div class="stat-info">
              <span class="stat-value">{{ userCount }}</span>
              <span class="stat-label">用户总数</span>
            </div>
          </div>
          <div class="stat-item">
            <el-icon size="24" color="#6366f1" icon="ShoppingCart" />
            <div class="stat-info">
              <span class="stat-value">{{ productCount }}</span>
              <span class="stat-label">商品总数</span>
            </div>
          </div>
          <div class="stat-item">
            <el-icon size="24" color="#a78bfa" icon="Ticket" />
            <div class="stat-info">
              <span class="stat-value">{{ orderCount }}</span>
              <span class="stat-label">订单总数</span>
            </div>
          </div>
          <div class="stat-item">
            <el-icon size="24" color="#c4b5fd" icon="TrendingUp" />
            <div class="stat-info">
              <span class="stat-value">{{ todayOrders }}</span>
              <span class="stat-label">今日订单</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 功能入口区域 -->
      <section class="features-section">
        <h2 class="section-title">功能管理</h2>
        <div class="features-grid">
          <div class="feature-card user-card" @click="goToUserManage">
            <div class="card-icon">
              <el-icon size="40" icon="UserFilled" />
            </div>
            <div class="card-content">
              <h3>用户管理</h3>
              <p>管理系统用户、权限和角色</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>

          <div class="feature-card product-card" @click="goToProductManage">
            <div class="card-icon">
              <el-icon size="40" icon="Package" />
            </div>
            <div class="card-content">
              <h3>商品管理</h3>
              <p>发布、编辑和管理商品信息</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>

          <div class="feature-card order-card" @click="goToOrderManage">
            <div class="card-icon">
              <el-icon size="40" icon="Ticket" />
            </div>
            <div class="card-content">
              <h3>订单管理</h3>
              <p>查看订单状态、处理订单</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>

          <div class="feature-card data-card" @click="goToDataAnalysis">
            <div class="card-icon">
              <el-icon size="40" icon="DataAnalysis" />
            </div>
            <div class="card-content">
              <h3>数据统计</h3>
              <p>查看系统数据和统计报表</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>

          <div class="feature-card admin-card" @click="goToAdminManage">
            <div class="card-icon">
              <el-icon size="40" icon="Shield" />
            </div>
            <div class="card-content">
              <h3>管理员管理</h3>
              <p>管理管理员账户和权限</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>

          <div class="feature-card register-card" @click="goRegister">
            <div class="card-icon">
              <el-icon size="40" icon="UserPlus" />
            </div>
            <div class="card-content">
              <h3>用户注册</h3>
              <p>快速注册新用户账户</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>

          <div class="feature-card setting-card" @click="goToSystemSetting">
            <div class="card-icon">
              <el-icon size="40" icon="Settings" />
            </div>
            <div class="card-content">
              <h3>系统设置</h3>
              <p>配置系统参数和选项</p>
            </div>
            <div class="card-arrow">
              <el-icon icon="ArrowRight" />
            </div>
          </div>
        </div>
      </section>

      <!-- 最近动态 -->
      <section class="activity-section">
        <h2 class="section-title">最近动态</h2>
        <div class="activity-list">
          <div class="activity-item" v-for="(item, index) in recentActivities" :key="index">
            <div class="activity-icon" :class="item.type">
              <el-icon :size="20" :icon="item.icon" />
            </div>
            <div class="activity-content">
              <p>{{ item.description }}</p>
              <span class="activity-time">{{ item.time }}</span>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="admin-footer">
      <p>© 2026 ES管理系统 - 管理中心</p>
      <div class="footer-links">
        <a href="javascript:;">使用帮助</a>
        <a href="javascript:;">系统日志</a>
        <a href="javascript:;">关于我们</a>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const userName = ref('管理员')
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')
const showUserMenu = ref(false)

const userCount = ref(128)
const productCount = ref(256)
const orderCount = ref(892)
const todayOrders = ref(32)

const todayDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const recentActivities = ref([
  { type: 'user', icon: 'User', description: '用户 "admin" 登录系统', time: '2分钟前' },
  { type: 'order', icon: 'Ticket', description: '新订单 #20260509001 已创建', time: '15分钟前' },
  { type: 'product', icon: 'Package', description: '商品 "iPhone 15 Pro" 已上架', time: '1小时前' },
  { type: 'user', icon: 'Users', description: '新用户 "testuser" 注册', time: '2小时前' },
  { type: 'order', icon: 'TrendingUp', description: '订单 #20260508999 已完成', time: '3小时前' }
])

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

onMounted(() => {
  document.addEventListener('click', (e) => {
    const userInfo = document.querySelector('.user-info')
    if (userInfo && !userInfo.contains(e.target)) {
      showUserMenu.value = false
    }
  })
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('rememberedAdmin')
  ElMessage.success('退出登录成功')
  router.push('/admin/login')
}

const goToUserManage = () => {
<<<<<<< HEAD
  router.push('/adminUser')
=======
  ElMessage.info('即将进入用户管理页面')
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40
}

const goToProductManage = () => {
  router.push('/adminProduct')
}

const goToOrderManage = () => {
  router.push('/adminOrder')
}

const goToDataAnalysis = () => {
  ElMessage.info('即将进入数据统计页面')
}

const goToAdminManage = () => {
  ElMessage.info('即将进入管理员管理页面')
}

const goToSystemSetting = () => {
  ElMessage.info('即将进入系统设置页面')
}

const goToProfile = () => {
  ElMessage.info('即将进入个人信息页面')
}

const goRegister = () => {

  router.push('/adminRegister')
}

const goToSetting = () => {
  ElMessage.info('即将进入系统设置页面')
}



</script>

<style scoped>
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

.admin-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3a8a 0%, #312e81 50%, #4c1d95 100%);
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
}

.admin-header {
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
  background: #f3e8ff;
  color: #6366f1;
  padding-left: 25px;
}

.dropdown-item.logout {
  color: #f56c6c;
}

.dropdown-item.logout:hover {
  background: #fff2f2;
  color: #f56c6c;
}

.admin-main {
  flex: 1;
  padding: 40px 30px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

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

.stats-card {
  display: flex;
  gap: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 0;
  position: relative;
}

.stat-item:not(:last-child)::after {
  content: '';
  position: absolute;
  right: 0;
  top: 20%;
  height: 60%;
  width: 1px;
  background: rgba(255, 255, 255, 0.2);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(90deg, #fff, #c4b5fd);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.features-section {
  margin-bottom: 50px;
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
  background: linear-gradient(180deg, #6366f1, #8b5cf6);
  border-radius: 3px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 28px 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  gap: 18px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(139, 92, 246, 0.08), transparent);
  transition: all 0.8s ease;
}

.feature-card:hover::before {
  left: 100%;
}

.card-icon {
  width: 65px;
  height: 65px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-card .card-icon {
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #6366f1;
}

.product-card .card-icon {
  background: linear-gradient(135deg, #ede9fe, #ddd6fe);
  color: #8b5cf6;
}

.order-card .card-icon {
  background: linear-gradient(135deg, #f0e7ff, #e9d5ff);
  color: #a78bfa;
}

.data-card .card-icon {
  background: linear-gradient(135deg, #f5f3ff, #ede9fe);
  color: #9333ea;
}

.admin-card .card-icon {
  background: linear-gradient(135deg, #faf5ff, #f3e8ff);
  color: #7c3aed;
}

.setting-card .card-icon {
  background: linear-gradient(135deg, #f3e8ff, #e9d5ff);
  color: #6d28d9;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  font-size: 19px;
  color: #1f2f3f;
  margin-bottom: 6px;
  font-weight: 600;
}

.card-content p {
  font-size: 13px;
  color: #6c757d;
  line-height: 1.5;
}

.card-arrow {
  color: #8b5cf6;
  opacity: 0.6;
  transition: all 0.3s ease;
}

.feature-card:hover .card-arrow {
  opacity: 1;
  transform: translateX(5px);
}

.activity-section {
  margin-bottom: 60px;
}

.activity-list {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background: #faf5ff;
  padding-left: 10px;
  margin-left: -10px;
  margin-right: -10px;
  padding-right: 10px;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.activity-icon.user {
  background: #e0e7ff;
  color: #6366f1;
}

.activity-icon.order {
  background: #ede9fe;
  color: #8b5cf6;
}

.activity-icon.product {
  background: #f0e7ff;
  color: #a78bfa;
}

.activity-content {
  flex: 1;
}

.activity-content p {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.admin-footer {
  padding: 25px 30px;
  background: rgba(0, 0, 0, 0.15);
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

@media (max-width: 992px) {
  .admin-main {
    padding: 30px 20px;
  }

  .stats-card {
    flex-wrap: wrap;
    gap: 15px;
  }

  .stat-item {
    flex: 1 1 calc(50% - 7px);
  }

  .stat-item:not(:last-child)::after {
    display: none;
  }

  .features-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-header {
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
    padding: 20px;
  }

  .stat-item {
    flex: 1 1 100%;
    padding: 12px 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .stat-item:last-child {
    border-bottom: none;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .feature-card {
    padding: 22px 18px;
  }

  .admin-footer {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .admin-main {
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
    gap: 12px;
  }

  .activity-item {
    padding: 12px 0;
  }
}
</style>