<template>
  <div class="order-page">
    <!-- 顶部导航 -->
    <header class="order-header">
      <div class="header-wrap">
        <div class="logo">ES 用户中心</div>
        <div class="user-info" @click.stop="toggleUserMenu">
          <el-avatar :src="avatar" class="avatar" />
          <span class="name">{{ username }}</span>
        </div>
      </div>

      <!-- 用户下拉菜单 -->
      <div class="user-menu" v-show="showMenu">
        <div class="menu-item" @click="goPage('profile')">
          <el-icon><User /></el-icon> 个人资料
        </div>
        <div class="menu-item active">
          <el-icon><ShoppingCart /></el-icon> 我的订单
        </div>
        <div class="menu-item divider"></div>
        <div class="menu-item logout" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出登录
        </div>
      </div>
    </header>

    <!-- 页面标题 -->
    <section class="page-title">
      <div class="title-wrap">
        <h1>我的订单</h1>
        <p>查看您的全部订单信息</p>
      </div>
    </section>

    <!-- 订单类型切换 -->
    <section class="order-tabs">
      <div class="tabs-wrap">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'buy' }"
          @click="activeTab = 'buy'"
        >
          <el-icon><ShoppingBag /></el-icon>
          <span>我购买的</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'sell' }"
          @click="activeTab = 'sell'"
        >
          <el-icon><Store /></el-icon>
          <span>我卖出的</span>
        </div>
      </div>
    </section>

    <!-- 订单状态筛选 -->
    <section class="filter-bar">
      <div class="filter-wrap">
        <div 
          v-for="status in statusOptions" 
          :key="status.value"
          class="filter-item"
          :class="{ active: currentStatus === status.value }"
          @click="currentStatus = status.value"
        >
          {{ status.label }}
        </div>
      </div>
    </section>

    <!-- 订单列表 -->
    <section class="order-list">
      <div v-if="orderList.length === 0" class="empty-state">
        <el-icon size="64" class="empty-icon"><Package /></el-icon>
        <p>暂无订单</p>
        <el-button type="primary" @click="goPage('goods')">去购物</el-button>
      </div>

      <div v-else class="orders">
        <div v-for="order in orderList" :key="order.id" class="order-card">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-id">订单号：{{ order.id }}</span>
              <span class="order-time">{{ order.time }}</span>
            </div>
            <span class="order-status" :class="order.statusClass">{{ order.status }}</span>
          </div>

          <!-- 商品列表 -->
          <div class="order-items">
            <div 
              v-for="item in order.items" 
              :key="item.id" 
              class="order-item"
            >
              <img :src="item.image" :alt="item.name" class="item-image" />
              <div class="item-info">
                <h4 class="item-name">{{ item.name }}</h4>
                <p class="item-spec">{{ item.spec }}</p>
                <div class="item-bottom">
                  <span class="item-price">¥{{ item.price }}</span>
                  <span class="item-quantity">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-total">
              <span>合计：</span>
              <span class="total-price">¥{{ order.total }}</span>
            </div>
            <div class="order-actions">
              <el-button 
                v-if="order.showCancel" 
                type="text" 
                class="action-btn cancel"
                @click="cancelOrder(order.id)"
              >
                取消订单
              </el-button>
              <el-button 
                v-if="order.showPay && activeTab === 'buy'" 
                type="primary" 
                class="action-btn"
                @click="payOrder(order.id)"
              >
                立即支付
              </el-button>
              <el-button 
                v-if="order.showConfirm" 
                type="primary" 
                class="action-btn"
                @click="confirmOrder(order.id)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.showReview" 
                type="text" 
                class="action-btn review"
                @click="reviewOrder(order.id)"
              >
                评价
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="order-footer-bottom">
      <p>© 2026 ES 用户中心 版权所有</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/index.js'

const router = useRouter()
const username = ref('尊敬的用户')
const avatar = ref('https://cube.elemecdn.com/9/c2/f0e286d9074109dd6fdfc6ed9665dpng.png')
const showMenu = ref(false)
const activeTab = ref('buy')
const currentStatus = ref('all')
const ordersData = ref({ buy: [], sell: [] })
const loading = ref(false)

const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '待确认', value: 'pendingConfirm' },
  { label: '待付款', value: 'pending' },
  { label: '待发货', value: 'unshipped' },
  { label: '待收货', value: 'shipping' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const statusTextMap = {
  pending: '待付款',
  unshipped: '待发货',
  shipping: '待收货',
  completed: '已完成',
  cancelled: '已取消',
  pendingConfirm: '待确认'
}

const getStatusClass = (status) => {
  const classMap = {
    '待确认': 'pendingConfirm',
    '待付款': 'pending',
    '待发货': 'unshipped',
    '待收货': 'shipping',
    '已完成': 'completed',
    '已取消': 'cancelled'
  }
  return classMap[status] || status
}

const transformOrder = (order) => {
  const statusClass = getStatusClass(order.status)
  const product = order.product || {}
  
  return {
    id: order.id,
    time: order.createTime,
    status: order.status,
    statusClass: statusClass,
    total: product.price || 0,
    showCancel: statusClass === 'pending' || statusClass === 'pendingConfirm',
    showPay: statusClass === 'pending' || statusClass === 'pendingConfirm',
    showConfirm: statusClass === 'shipping',
    showReview: statusClass === 'completed',
    items: product.id ? [{
      id: product.id,
      name: product.title || `商品${order.productId}`,
      spec: product.category || '-',
      price: product.price || 0,
      quantity: 1,
      image: product.image ? `http://localhost:8080${product.image}` : 'https://cube.elemecdn.com/9/c2/f0e286d9074109dd6fdfc6ed9665dpng.png'
    }] : []
  }
}

// 初始化订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const [buyRes, sellRes] = await Promise.all([
      api.order.myBuy(),
      api.order.mySell()
    ])
    
    if (buyRes.data) {
      ordersData.value.buy = buyRes.data.map(transformOrder)
    }
    if (sellRes.data) {
      ordersData.value.sell = sellRes.data.map(transformOrder)
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 计算当前显示的订单列表
const orderList = computed(() => {
  let list = ordersData.value[activeTab.value] || []
  if (currentStatus.value !== 'all') {
    list = list.filter(order => order.statusClass === currentStatus.value)
  }
  return list
})
// 切换用户菜单显示状态
const toggleUserMenu = () => showMenu.value = !showMenu.value

const goPage = (type) => {
  const pageMap = {
    profile: '/',
    goods: '/searchProduct',
    order: '/myOrder'
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

// 取消订单
const cancelOrder = async (orderId) => {
  try {
    await api.order.cancel(orderId)
    ElMessage.success(`订单 ${orderId} 已取消`)
    fetchOrders()
  } catch (error) {
    ElMessage.error('取消订单失败')
  }
}
// 支付订单
const payOrder = async (orderId) => {
  try {
    await api.order.pay(orderId)
    ElMessage.success(`订单 ${orderId} 支付成功`)
    fetchOrders()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}
// 确认收货
const confirmOrder = async (orderId) => {
  try {
    await api.order.confirm(orderId)
    ElMessage.success(`订单 ${orderId} 已确认收货`)
    fetchOrders()
  } catch (error) {
    ElMessage.error('确认收货失败')
  }
}
// 评价订单
const reviewOrder = (orderId) => {
  ElMessage.info(`正在评价订单 ${orderId}`)
}

/*
页面加载完成 → onMounted 执行
              → 添加点击外部关闭菜单的监听
              → 调用 fetchOrders() 获取订单数据
              → 渲染订单列表
 */
onMounted(() => {
  document.addEventListener('click', (e) => {
    const userInfo = document.querySelector('.user-info')
    if (userInfo && !userInfo.contains(e.target)) {
      showMenu.value = false
    }
  })
  fetchOrders()
})

</script>

<style scoped>
:global(*) { margin: 0; padding: 0; box-sizing: border-box; }
:global(html),:global(body),:global(#app) {
  width: 100%; height: 100%; overflow-x: hidden;
  font-family: 'PingFang SC','Microsoft YaHei',sans-serif;
}

.order-page {
  width: 100vw;
  min-height: 100vh;
  background: #eef2f7;
  padding-bottom: 60px;
}

/* 顶部导航 */
.order-header {
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

/* 页面标题 */
.page-title {
  max-width: 1200px;
  margin: 0 auto;
  padding: 35px 25px 20px;
}
.title-wrap h1 {
  font-size: 28px;
  color: #1e293b;
  font-weight: 600;
  margin-bottom: 8px;
}
.title-wrap p {
  font-size: 14px;
  color: #64748b;
}

/* 订单类型切换 */
.order-tabs {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 25px;
  margin-bottom: 20px;
}
.tabs-wrap {
  display: inline-flex;
  background: #ffffff;
  border-radius: 10px;
  padding: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 30px;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  transition: 0.25s;
  font-size: 15px;
}
.tab-item:hover { color: #3b82f6; }
.tab-item.active {
  background: #3b82f6;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* 筛选栏 */
.filter-bar {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 25px;
  margin-bottom: 20px;
}
.filter-wrap {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.filter-item {
  padding: 8px 20px;
  background: #ffffff;
  border-radius: 20px;
  color: #64748b;
  font-size: 14px;
  cursor: pointer;
  transition: 0.25s;
  border: 1px solid transparent;
}
.filter-item:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}
.filter-item.active {
  background: #3b82f6;
  color: #ffffff;
}

/* 订单列表 */
.order-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 25px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}
.empty-icon {
  color: #cbd5e1;
  margin-bottom: 16px;
}
.empty-state p {
  color: #94a3b8;
  font-size: 16px;
  margin-bottom: 24px;
}

/* 订单卡片 */
.orders {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.order-card {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}
.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.order-id {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}
.order-time {
  font-size: 12px;
  color: #94a3b8;
}
.order-status {
  font-size: 14px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 12px;
}
.order-status.pending {
  background: #fffbeb;
  color: #f59e0b;
}
.order-status.unshipped {
  background: #eff6ff;
  color: #3b82f6;
}
.order-status.shipping {
  background: #dbeafe;
  color: #1d4ed8;
}
.order-status.completed {
  background: #dcfce7;
  color: #16a34a;
}
.order-status.cancelled {
  background: #fef2f2;
  color: #ef4444;
}
.order-status.pendingConfirm {
  background: #fef3c7;
  color: #d97706;
}

/* 商品列表 */
.order-items {
  padding: 16px 20px;
}
.order-item {
  display: flex;
  gap: 16px;
  padding: 12px 0;
}
.item-image {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  object-fit: cover;
  background: #f8fafc;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.item-name {
  font-size: 15px;
  color: #1e293b;
  font-weight: 500;
  margin-bottom: 6px;
  line-height: 1.5;
}
.item-spec {
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: auto;
}
.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.item-price {
  font-size: 16px;
  color: #dc2626;
  font-weight: 600;
}
.item-quantity {
  font-size: 14px;
  color: #64748b;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8fafc;
}
.order-total {
  font-size: 14px;
  color: #64748b;
}
.total-price {
  font-size: 18px;
  color: #dc2626;
  font-weight: 600;
}
.order-actions {
  display: flex;
  gap: 12px;
}
.action-btn {
  padding: 8px 20px;
  font-size: 14px;
  border-radius: 8px;
}
.action-btn.cancel { color: #64748b; }
.action-btn.cancel:hover { color: #ef4444; }
.action-btn.review { color: #3b82f6; }

/* 页脚 */
.order-footer-bottom {
  text-align: center;
  padding: 30px 0;
  color: #94a3b8;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .order-item {
    flex-direction: column;
  }
  .item-image {
    width: 100%;
    height: auto;
  }
  .order-footer {
    flex-direction: column;
    gap: 16px;
  }
  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>