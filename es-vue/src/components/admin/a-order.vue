<template>
  <div class="admin-order-container">
    <header class="admin-header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="28" color="#fff" icon="ShoppingCart" />
          <span class="logo-text">订单管理</span>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info" @click="toggleUserMenu">
          <el-avatar :src="userAvatar" class="user-avatar"></el-avatar>
          <span class="username">{{ userName }}</span>
          <el-icon size="16" color="#fff" icon="ArrowDown" />
        </div>
        <div class="user-dropdown" v-show="showUserMenu">
          <div class="dropdown-item logout" @click="handleLogout">
            <el-icon icon="SwitchButton" />
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </header>

    <main class="admin-main">
      <div class="search-section">
        <div class="filter-box">
          <el-select v-model="statusFilter" placeholder="选择状态" class="status-select">
            <el-option label="全部" value="全部" />
            <el-option label="待确认" value="待确认" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </div>
        <div class="user-id-box">
          <el-input 
            v-model="userIdFilter" 
            placeholder="用户ID" 
            class="user-id-input"
            type="number"
          />
        </div>
        <div class="time-range">
          <el-date-picker
            v-model="startTime"
            type="datetime"
            placeholder="开始时间"
            class="time-input"
          />
          <span class="time-separator">至</span>
          <el-date-picker
            v-model="endTime"
            type="datetime"
            placeholder="结束时间"
            class="time-input"
          />
        </div>
        <div class="refresh-box">
          <el-button @click="handleRefresh" class="refresh-btn" type="primary">
            <el-icon icon="Refresh" />
            刷新
          </el-button>
        </div>
      </div>

      <div class="order-table">
        <el-table :data="orderList" border class="table" :loading="loading">
          <el-table-column prop="id" label="订单ID" width="100" />
          <el-table-column prop="product.title" label="商品名称" min-width="150">
            <template #default="scope">
              {{ scope.row.product ? scope.row.product.title : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="product.price" label="商品价格" width="100">
            <template #default="scope">
              ¥{{ scope.row.product ? scope.row.product.price : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="buyerId" label="买家ID" width="100" />
          <el-table-column prop="sellerId" label="卖家ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="交易时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                @click="handleViewDetail(scope.row)" 
                type="primary" 
                size="small"
              >
                <el-icon icon="Eye" />
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-section">
          <div class="pagination">
            <el-pagination
              :current-page="pagination.currentPage"
              :page-size="pagination.pageSize"
              :total="pagination.total"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
              layout="total, sizes, prev, pager, next, jumper"
              :page-sizes="[10, 20, 30, 50]"
              prev-text="上一页"
              next-text="下一页"
              jumper-text="跳转到"
            />
          </div>
          <div class="back-box">
            <el-button @click="handleBack" class="back-btn">
              <el-icon icon="ArrowLeft" />
              返回
            </el-button>
          </div>
        </div>
      </div>
    </main>

    <footer class="admin-footer">
      <p>© 2026 ES管理系统 - 订单管理</p>
    </footer>

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" v-model="showDetailDialog" width="500px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-row">
          <span class="label">订单ID：</span>
          <span class="value">{{ currentOrder.id }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品名称：</span>
          <span class="value">{{ currentOrder.product ? currentOrder.product.title : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品价格：</span>
          <span class="value">¥{{ currentOrder.product ? currentOrder.product.price : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品分类：</span>
          <span class="value">{{ currentOrder.product ? currentOrder.product.category : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">买家ID：</span>
          <span class="value">{{ currentOrder.buyerId }}</span>
        </div>
        <div class="detail-row">
          <span class="label">卖家ID：</span>
          <span class="value">{{ currentOrder.sellerId }}</span>
        </div>
        <div class="detail-row">
          <span class="label">订单状态：</span>
          <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.status }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="label">交易时间：</span>
          <span class="value">{{ currentOrder.createTime }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api/index.js'

const router = useRouter()

const userName = ref('管理员')
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')
const showUserMenu = ref(false)

const loading = ref(false)
const statusFilter = ref('全部')
const userIdFilter = ref('')
const startTime = ref('')
const endTime = ref('')
const orderList = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const showDetailDialog = ref(false)
const currentOrder = ref(null)

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
  fetchOrderList()
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('rememberedAdmin')
  ElMessage.success('退出登录成功')
  router.push('/admin/login')
}

const getStatusType = (status) => {
  switch (status) {
    case '待确认': return 'warning'
    case '已完成': return 'success'
    case '已取消': return 'danger'
    default: return 'default'
  }
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      status: statusFilter.value,
      userId: userIdFilter.value ? parseInt(userIdFilter.value) : undefined,
      startTime: startTime.value,
      endTime: endTime.value
    }
    const res = await api.admin.getOrderList(params)
    if (res && res.data) {
      orderList.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchOrderList()
}

const handleRefresh = () => {
  pagination.value.currentPage = 1
  fetchOrderList()
  ElMessage.success('刷新成功')
}

const handleBack = () => {
  router.back()
}

const handlePageChange = (pageNum) => {
  pagination.value.currentPage = pageNum
  fetchOrderList()
}

const handleSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  pagination.value.currentPage = 1
  fetchOrderList()
}

const handleViewDetail = async (order) => {
  try {
    const res = await api.admin.getOrderDetail(order.id)
    if (res && res.data) {
      currentOrder.value = res.data
      showDetailDialog.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    console.error(error)
  }
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

.admin-order-container {
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
  width: 150px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 200;
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
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.search-section {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-box {
  order: 1;
}

.status-select {
  width: 120px;
}

.user-id-box {
  order: 2;
}

.user-id-input {
  width: 150px;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 10px;
  order: 3;
}

.time-input {
  width: 180px;
}

.time-separator {
  color: rgba(255, 255, 255, 0.8);
}

.refresh-box {
  order: 4;
}

.refresh-btn {
  background: #6366f1;
  border-color: #6366f1;
}

.refresh-btn:hover {
  background: #4f46e5;
  border-color: #4f46e5;
}

.order-table {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.table {
  width: 100%;
}

.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.back-box {
  display: flex;
  justify-content: flex-end;
}

.back-btn {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}

.back-btn:hover {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
}

.order-detail {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  width: 100px;
  font-weight: 500;
  color: #666;
}

.detail-row .value {
  flex: 1;
  color: #333;
}

.admin-footer {
  padding: 20px 30px;
  background: rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  font-size: 14px;
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

  .admin-main {
    padding: 20px 15px;
  }

  .search-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-box,
  .user-id-box,
  .time-range,
  .refresh-box {
    order: initial;
  }

  .time-range {
    justify-content: space-between;
  }

  .time-input {
    flex: 1;
    width: auto;
  }
}
</style>