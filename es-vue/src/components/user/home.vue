<template>
  <div class="user-home">
    <!-- 顶部导航 -->
    <header class="user-header">
      <div class="header-wrap">
        <div class="logo">ES 用户中心</div>
        <!-- 点击头像正常触发菜单 -->
        <div class="user-info" @click.stop="toggleUserMenu">
          <el-avatar :src="avatar" class="avatar" />
          <span class="name">{{ username }}</span>
        </div>
      </div>

      <!-- 用户下拉菜单 修复定位+层级+冒泡 -->
      <div class="user-menu" v-show="showMenu">
        <div class="menu-item" @click="goPage('profile')">
          <el-icon><User /></el-icon> 个人资料
        </div>
        <div class="menu-item" @click="goPage('order')">
          <el-icon><ShoppingCart /></el-icon> 我的订单
        </div>
        <div class="menu-item divider"></div>
        <div class="menu-item logout" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出登录
        </div>
      </div>
    </header>

    <!-- 欢迎区域 和你截图完全一致 -->
    <section class="welcome-banner">
      <div class="banner-content">
        <h1>欢迎回来，{{ username }}</h1>
        <p>美好体验，从这里开始</p>
        <el-button type="primary" class="banner-btn" @click="goPage('goods')">
          浏览商品
        </el-button>
      </div>
    </section>

    <!-- 我的服务 卡片区域 -->
    <section class="func-area">
      <h2 class="area-title">我的服务</h2>
      <div class="func-list">
        <div class="func-item" @click="goPage('publish')">
          <div class="func-icon publish-icon">
            <el-icon size="28"><Ticket /></el-icon>
          </div>
          <p class="func-name">发布商品</p>
          <p class="func-desc">发布商品</p>
        </div>

        <div class="func-item" @click="goPage('myProduct')">
          <div class="func-icon myProduct-icon">
            <el-icon size="28"><Ticket /></el-icon>
          </div>
          <p class="func-name">我的商品</p>
          <p class="func-desc">查看已发布商品列表</p>
        </div>


        <div class="func-item" @click="goPage('order')">
          <div class="func-icon order-icon">
            <el-icon size="28"><Ticket /></el-icon>
          </div>
          <p class="func-name">我的订单</p>
          <p class="func-desc">查看全部订单状态</p>
        </div>

        <div class="func-item" @click="goPage('profile')">
          <div class="func-icon user-icon">
            <el-icon size="28"><UserFilled /></el-icon>
          </div>
          <p class="func-name">个人中心</p>
          <p class="func-desc">管理个人信息</p>
        </div>

        <div class="func-item" @click="goPage('collect')">
          <div class="func-icon collect-icon">
            <el-icon size="28"><Star /></el-icon>
          </div>
          <p class="func-name">我的收藏</p>
          <p class="func-desc">查看收藏商品</p>
        </div>

        <div class="func-item" @click="goPage('service')">
          <div class="func-icon help-icon">
            <el-icon size="28"><ChatDotRound /></el-icon>
          </div>
          <p class="func-name">客服帮助</p>
          <p class="func-desc">在线咨询与售后</p>
        </div>
      </div>
    </section>

    <!-- 专属服务区域 -->
    <section class="recommend-area">
      <div class="recommend-card">
        <div class="rec-text">
          <h3>专属服务</h3>
          <p>为你提供更贴心的使用体验</p>
        </div>
        <el-button plain @click="goPage('service')">立即查看</el-button>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="user-footer">
      <p>© 2026 ES 用户中心 版权所有</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/index.js'
// ✅ 补全所有缺失图标，之前少了ShoppingCart、SwitchButton
import {
  User, ShoppingCart, SwitchButton,
  Ticket, UserFilled, Star, ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const username = ref('尊敬的用户')
const avatar = ref('https://cube.elemecdn.com/9/c2/f0e286d9074109dd6fdfc6ed9665dpng.png')
const showMenu = ref(false)

// 切换菜单，加.stop阻止事件冒泡
const toggleUserMenu = () => showMenu.value = !showMenu.value

// 页面跳转
const goPage = (type) => {
  const map = {
    profile: '个人资料', order: '我的订单', collect: '我的收藏',
    service: '客服帮助', goods: '商品浏览', publish: '发布商品',
    myProduct:'我的商品'
  }

  if (type === 'publish') {
    // 跳转到发布商品页面
    router.push('/publishGoods')
  }
  if (type === 'myProduct') {
    // 跳转到我的商品页面
    router.push('/myProduct')
  }
  if (type === 'goods') {
    // 跳转到商品浏览页面
    router.push('/searchProduct')
  }
  if (type === 'collect') {
    // 跳转到我的收藏页面
    router.push('/favoriteList')
  }
  if (type === 'order') {
    // 跳转到我的订单页面
    router.push('/myOrder')
  }
  if (type === 'profile') {
    // 跳转到个人中心页面
    router.push('/profile')
  }
  if (type === 'service') {
    // 跳转到客服帮助页面
    router.push('/service')
  }

  
  showMenu.value = false // 跳转自动关闭菜单
}

// 退出登录
const handleLogout = () => {
  api.user.logout().then(()=>{
    router.push('/login')
  })
  // localStorage.removeItem('token')
  // localStorage.removeItem('rememberedUsername')
  ElMessage.success('已安全退出')
}

// ✅ 修复点击空白关闭菜单，不会误触头像
onMounted(() => {
  document.addEventListener('click', (e) => {
    // 只有点击不是头像区域，才关闭菜单
    const userInfo = document.querySelector('.user-info')
    if(userInfo && !userInfo.contains(e.target)){
      showMenu.value = false
    }
  })
})
</script>

<style scoped>
/* 全局重置 彻底解决页面左右空白 */
:global(*) { margin: 0; padding: 0; box-sizing: border-box; }
:global(html),:global(body),:global(#app) {
  width: 100%; height: 100%; overflow-x: hidden;
  font-family: 'PingFang SC','Microsoft YaHei',sans-serif;
}

/* 主容器 匹配你截图浅灰白渐变 */
.user-home {
  width: 100vw;
  min-height: 100vh;
  background: #eef2f7;
  position: relative;
  padding-bottom: 50px;
}

/* ✅ 关键修复：导航栏加相对定位，菜单才能正常跟随头像 */
.user-header {
  position: sticky;
  top: 0;
  z-index: 999; /* 拉高层级，菜单不会被遮挡 */
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  overflow: visible;
}
.header-wrap {
  width: 100%;
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

/* ✅ 修复下拉菜单样式 位置正常显示 */
.user-menu {
  position: absolute;
  /* 紧贴导航底部，永远不会上下错位断层 */
  top: 100%;
  /* 跟随右上角头像对齐 */
  right: 25px;
  /* 向左微调，完美贴合头像下方 */
  transform: translateX(0);
  width: 160px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
  overflow: hidden;
  animation: fade 0.2s ease;
  z-index: 9999;
  /* 消除动画断层 */
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

.divider {
  height: 1px;
  background: #f1f5f9;
  /* 取消上下多余间距，杜绝断层留白 */
  margin: 4px 0;
  padding: 0;
}

.logout { color: #ef4444; }
.logout:hover { background: #fef2f2; color: #dc2626; }

/* 欢迎横幅 1:1还原你截图样式 */
.welcome-banner {
  max-width: 1200px;
  margin: 40px auto 40px;
  padding: 0 25px;
}
.banner-content {
  background: #ffffff;
  border-radius: 16px;
  padding: 45px 40px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.banner-content h1 {
  font-size: 34px;
  color: #1e293b;
  margin-bottom: 10px;
  font-weight: 600;
}
.banner-content p {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 25px;
}
.banner-btn {
  border-radius: 8px;
  padding: 8px 22px;
}

/* 功能区域 */
.func-area {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 25px;
}
.area-title {
  font-size: 22px;
  color: #1e293b;
  margin-bottom: 25px;
  font-weight: 600;
}
.func-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.func-item {
  background: #ffffff;
  border-radius: 16px;
  padding: 30px 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: 0.3s ease;
  cursor: pointer;
}
.func-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}
.func-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}
.order-icon { background: #eff6ff; color: #3b82f6; }
.user-icon { background: #f0fdf4; color: #16a34a; }
.collect-icon { background: #fffbeb; color: #f59e0b; }
.help-icon { background: #fef2f2; color: #ef4444; }
.func-name {
  font-size: 17px;
  color: #1e293b;
  font-weight: 500;
  margin-bottom: 6px;
}
.func-desc {
  font-size: 13px;
  color: #64748b;
}

/* 专属服务 */
.recommend-area {
  max-width: 1200px;
  margin: 0 auto 50px;
  padding: 0 25px;
}
.recommend-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 25px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.rec-text h3 {
  font-size: 18px;
  color: #1e293b;
  margin-bottom: 5px;
}
.rec-text p {
  font-size: 14px;
  color: #64748b;
}

/* 页脚 */
.user-footer {
  text-align: center;
  padding: 20px 0;
  color: #64748b;
  font-size: 14px;
}

/* 移动端响应式 */
@media (max-width: 900px) {
  .func-list { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 540px) {
  .func-list { grid-template-columns: 1fr; }
  .recommend-card { flex-direction: column; gap: 15px; text-align: center; }
}
</style>