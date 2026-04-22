<template>
  <div class="profile-page">
    <!-- 顶部导航 -->
    <header class="profile-header">
      <div class="header-wrap">
        <div class="logo">ES 用户中心</div>
        <div class="user-info" @click.stop="toggleUserMenu">
          <el-avatar :src="userInfo.avatar" class="avatar" />
          <span class="name">{{ userInfo.username }}</span>
        </div>
      </div>

      <!-- 用户下拉菜单 -->
      <div class="user-menu" v-show="showMenu">
        <div class="menu-item active">
          <el-icon name="User" /> 个人资料
        </div>
        <div class="menu-item" @click="goPage('order')">
          <el-icon name="ShoppingCart" /> 我的订单
        </div>
        <div class="menu-item divider"></div>
        <div class="menu-item logout" @click="handleLogout">
          <el-icon name="SwitchButton" /> 退出登录
        </div>
      </div>
    </header>

    <!-- 用户信息卡片 -->
    <section class="user-card">
      <div class="card-wrap">
        <div class="avatar-section">
          <el-avatar :src="userInfo.avatar" class="big-avatar" />
          <div class="user-detail">
            <h2 class="username">{{ userInfo.username }}</h2>
            <p class="user-id">用户ID：{{ userInfo.id }}</p>
          </div>
        </div>
        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-value">{{ buyCount }}</span>
            <span class="stat-label">已购买</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ sellCount }}</span>
            <span class="stat-label">已卖出</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ favoriteCount }}</span>
            <span class="stat-label">收藏</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能菜单 -->
    <section class="func-section">
      <div class="func-list">
        <div class="func-card" @click="showChangePassword = true">
          <div class="func-icon password-icon">
            <el-icon size="24" name="Lock" />
          </div>
          <div class="func-content">
            <h3>修改密码</h3>
            <p>保护账户安全</p>
          </div>
          <el-icon class="arrow-icon" name="ChevronRight" />
        </div>

        <div class="func-card" @click="goPage('myProduct')">
          <div class="func-icon product-icon">
            <el-icon size="24" name="Package" />
          </div>
          <div class="func-content">
            <h3>我的商品</h3>
            <p>管理已发布商品</p>
          </div>
          <el-icon class="arrow-icon" name="ChevronRight" />
        </div>

        <div class="func-card" @click="goPage('order')">
          <div class="func-icon order-icon">
            <el-icon size="24" name="ShoppingBag" />
          </div>
          <div class="func-content">
            <h3>我的订单</h3>
            <p>查看订单详情</p>
          </div>
          <el-icon class="arrow-icon" name="ChevronRight" />
        </div>

        <div class="func-card" @click="goPage('favoriteList')">
          <div class="func-icon favorite-icon">
            <el-icon size="24" name="Star" />
          </div>
          <div class="func-content">
            <h3>我的收藏</h3>
            <p>收藏的商品</p>
          </div>
          <el-icon class="arrow-icon" name="ChevronRight" />
        </div>

        <div class="func-card" @click="goPage('service')">
          <div class="func-icon service-icon">
            <el-icon size="24" name="MessageCircle" />
          </div>
          <div class="func-content">
            <h3>客服帮助</h3>
            <p>在线咨询</p>
          </div>
          <el-icon class="arrow-icon" name="ChevronRight" />
        </div>
      </div>
    </section>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="showChangePassword" width="400px">
      <form @submit.prevent="changePassword">
        <el-form :model="passwordForm" label-width="80px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
          </el-form-item>
        </el-form>
      </form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 页脚 -->
    <footer class="profile-footer">
      <p>© 2026 ES 用户中心 版权所有</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/index.js'


const router = useRouter()
const showMenu = ref(false)
const showChangePassword = ref(false)

const userInfo = ref({
  id: '',
  username: '尊敬的用户',
  avatar: 'https://cube.elemecdn.com/9/c2/f0e286d9074109dd6fdfc6ed9665dpng.png'
})

const buyCount = ref(0)
const sellCount = ref(0)
const favoriteCount = ref(0)

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const toggleUserMenu = () => showMenu.value = !showMenu.value

const goPage = (type) => {
  const pageMap = {
    profile: '/',
    order: '/myOrder',
    myProduct: '/myProduct',
    favoriteList: '/favoriteList',
    service: '/service'
  }
  if (pageMap[type]) {
    router.push(pageMap[type])
  }
  showMenu.value = false
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('rememberedUsername')
  ElMessage.success('已安全退出')
  api.user.logout().then(() => {
    router.push('/login')
  })
}

const fetchUserInfo = async () => {
  try {
    const res = await api.user.getInfo()
    if (res.data) {
      userInfo.value = {
        id: res.data.id || '',
        username: res.data.username || '尊敬的用户',
        avatar: res.data.avatar || 'https://cube.elemecdn.com/9/c2/f0e286d9074109dd6fdfc6ed9665dpng.png'
      }
    }
  } catch (error) {
    console.error('获取用户信息失败')
  }
}

const fetchStats = async () => {
  try {
    const [buyRes, sellRes, favRes] = await Promise.all([
      api.order.myBuy(),
      api.order.mySell(),
      api.favorite.list()
    ])
    buyCount.value = buyRes.data ? buyRes.data.length : 0
    sellCount.value = sellRes.data ? sellRes.data.length : 0
    favoriteCount.value = favRes.data ? favRes.data.length : 0
  } catch (error) {
    console.error('获取统计数据失败')
  }
}

const changePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    ElMessage.error('请输入原密码')
    return
  }
  if (!passwordForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    await api.user.updatePassword(passwordForm.value)
    ElMessage.success('密码修改成功')
    showChangePassword.value = false
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}

onMounted(() => {
  document.addEventListener('click', (e) => {
    const userInfo = document.querySelector('.user-info')
    if (userInfo && !userInfo.contains(e.target)) {
      showMenu.value = false
    }
  })
  fetchUserInfo()
  fetchStats()
})
</script>

<style scoped>
:global(*) { margin: 0; padding: 0; box-sizing: border-box; }
:global(html),:global(body),:global(#app) {
  width: 100%; height: 100%; overflow-x: hidden;
  font-family: 'PingFang SC','Microsoft YaHei',sans-serif;
}

.profile-page {
  width: 100vw;
  min-height: 100vh;
  background: #eef2f7;
  padding-bottom: 60px;
}

/* 顶部导航 */
.profile-header {
  position: sticky;
  top: 0;
  z-index: 999;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
}
.header-wrap {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  font-size: 19px;
  font-weight: 500;
  color: #334155;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 30px;
  transition: 0.2s;
}
.user-info:hover { background: rgba(0,0,0,0.05); }
.avatar { width: 38px; height: 38px; border: 2px solid #fff; }
.name { color: #334155; font-weight: 500; }

/* 下拉菜单 */
.user-menu {
  position: absolute;
  top: 100%;
  right: 25px;
  width: 160px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
  overflow: hidden;
  animation: fade 0.2s ease;
  z-index: 9999;
  margin-top: 6px;
}
@keyframes fade {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}
.menu-item {
  padding: 12px 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #475569;
  cursor: pointer;
  transition: 0.2s;
}
.menu-item:hover { background: #f8fafc; color: #3b82f6; }
.menu-item.active { background: #eff6ff; color: #3b82f6; }
.divider {
  height: 1px;
  background: #f1f5f9;
  margin: 4px 0;
  padding: 0;
}
.logout { color: #ef4444; }
.logout:hover { background: #fef2f2; color: #dc2626; }

/* 用户信息卡片 */
.user-card {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 25px;
}
.card-wrap {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 20px;
  padding: 35px;
  color: #ffffff;
}
.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}
.big-avatar {
  width: 90px;
  height: 90px;
  border: 4px solid rgba(255,255,255,0.4);
}
.user-detail h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 6px;
}
.user-detail p {
  font-size: 14px;
  opacity: 0.8;
}
.stats-row {
  display: flex;
  justify-content: center;
  gap: 60px;
  padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.2);
}
.stat-item {
  text-align: center;
}
.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
}
.stat-label {
  font-size: 14px;
  opacity: 0.8;
}
.stat-divider {
  width: 1px;
  background: rgba(255,255,255,0.3);
}

/* 功能菜单 */
.func-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 25px;
}
.func-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.func-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #ffffff;
  border-radius: 16px;
  padding: 20px 24px;
  cursor: pointer;
  transition: 0.25s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.func-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}
.func-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.password-icon { background: #fef2f2; color: #ef4444; }
.product-icon { background: #eff6ff; color: #3b82f6; }
.order-icon { background: #f0fdf4; color: #16a34a; }
.favorite-icon { background: #fffbeb; color: #f59e0b; }
.service-icon { background: #fce7f3; color: #ec4899; }
.func-content h3 {
  flex: 1;
  font-size: 16px;
  color: #1e293b;
  font-weight: 500;
  margin-bottom: 4px;
}
.func-content p {
  font-size: 13px;
  color: #94a3b8;
}
.arrow-icon {
  color: #cbd5e1;
  font-size: 18px;
}

/* 页脚 */
.profile-footer {
  text-align: center;
  padding: 40px 0;
  color: #94a3b8;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .stats-row {
    gap: 30px;
  }
  .stat-value {
    font-size: 22px;
  }
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
}
</style>