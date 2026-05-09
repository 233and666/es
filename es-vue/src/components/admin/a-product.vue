<template>
  <div class="admin-product-container">
    <header class="admin-header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="28" color="#fff" icon="Package" />
          <span class="logo-text">商品管理</span>
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
        <!-- 左上角刷新按钮 -->
        <div class="refresh-box">
          <el-button @click="handleRefresh" class="refresh-btn" type="primary">
            <el-icon icon="Refresh" />
            刷新
          </el-button>
        </div>
        <div class="search-box">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索商品名称" 
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch" class="search-btn">
                <el-icon icon="Search" />
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="filter-box">
          <el-select v-model="statusFilter" placeholder="选择状态" class="status-select">
            <el-option label="全部" value="全部" />
            <el-option label="在售" value="在售" />
            <el-option label="下架" value="下架" />
            <el-option label="已售" value="已售" />
          </el-select>
        </div>
      </div>

      <div class="product-table">
        <el-table :data="productList" border class="table" :loading="loading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="100">
            <template #default="scope">
              <el-image
                v-if="scope.row.image"
                :src="'http://localhost:8080' + scope.row.image"
                class="product-image"
                fit="cover"
                :preview-src-list="scope.row.image ? ['http://localhost:8080' + scope.row.image] : []"/><!-- 支持点击预览大图片 -->

              <span v-else class="no-image">无图片</span>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="商品名称" min-width="150" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="发布日期" width="160" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status !== '已售'"
                @click="handleOffProduct(scope.row)" 
                :type="scope.row.status === '在售' ? 'danger' : 'success'"
                size="small"
              >
                {{ scope.row.status === '在售' ? '封禁' : '解封' }}
              </el-button>
              <span v-else class="disabled-text">已售商品不可操作</span>
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
              :disabled="pagination.total === 0"
            />
          </div>
          <!-- 右下角返回按钮 -->
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
      <p>© 2026 ES管理系统 - 商品管理</p>
    </footer>
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
const searchKeyword = ref('')
const statusFilter = ref('全部')
const productList = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

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
  fetchProductList()
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
    case '在售': return 'success'
    case '下架': return 'warning'
    case '已售': return 'info'
    default: return 'default'
  }
}

const fetchProductList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      status: statusFilter.value,
      keyword: searchKeyword.value || undefined
    }
    const res = await api.admin.getProductList(params)
    if (res && res.data) {
      productList.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchProductList()
}

const handlePageChange = (pageNum) => {
  pagination.value.currentPage = pageNum
  fetchProductList()
}

const handleSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  pagination.value.currentPage = 1
  fetchProductList()
}

const handleOffProduct = async (product) => {
  const action = product.status === '在售' ? '封禁' : '解封'
  try {
    const res = await api.admin.offProduct(product.id)
    if (res) {
      ElMessage.success(`商品${action}成功`)
      fetchProductList()
    }
  } catch (error) {
    ElMessage.error(`商品${action}失败`)
    console.error(error)
  }
}

const handleRefresh = () => {
  pagination.value.currentPage = 1
  fetchProductList()
  ElMessage.success('刷新成功')
}

const handleBack = () => {
  router.back()
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

.admin-product-container {
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
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  align-items: center;
}

.refresh-box {
  order: 3;
}

.refresh-btn {
  background: #6366f1;
  border-color: #6366f1;
}

.refresh-btn:hover {
  background: #4f46e5;
  border-color: #4f46e5;
}

.search-box {
  flex: 1;
  min-width: 300px;
  order: 2;
}

.filter-box {
  order: 1;
}

.search-input {
  width: 100%;
  max-width: 500px;
}

.search-btn {
  background: #8b5cf6;
  border-color: #8b5cf6;
}

.filter-box {
  display: flex;
  align-items: center;
}

.status-select {
  width: 150px;
}

.product-table {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.table {
  width: 100%;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  cursor: pointer;
}

.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  color: #999;
  font-size: 12px;
  border-radius: 8px;
}

.disabled-text {
  color: #999;
  font-size: 12px;
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
  }

  .search-box {
    min-width: 100%;
  }

  .search-input {
    max-width: 100%;
  }
}
</style>