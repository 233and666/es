<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, ArrowDown, Sort } from '@element-plus/icons-vue'
import { api } from '../../api/index.js'

const router = useRouter()

// 商品列表数据
const productList = ref([])
// 加载状态
const loading = ref(false)
// 刷新加载状态
const refreshLoading = ref(false)
// 分类选项
const categories = [
  { value: '', label: '全部分类' },
  { value: '数码产品', label: '数码产品' },
  { value: '家居用品', label: '家居用品' },
  { value: '服装鞋包', label: '服装鞋包' },
  { value: '运动户外', label: '运动户外' },
  { value: '图书音像', label: '图书音像' },
  { value: '乐器设备', label: '乐器设备' },
  { value: '办公用品', label: '办公用品' },
  { value: '其他', label: '其他' }
]
// 排序选项
const sortOptions = [
  { value: 'createTime_desc', label: '最新发布' },
  { value: 'price_min', label: '价格从低到高' },
  { value: 'price_max', label: '价格从高到低' }
]
// 搜索表单
const searchForm = reactive({
  keyword: '',
  category: '',
  sort: 'createTime_desc',
  pageNum: 1,
  pageSize: 12
})
// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

// 获取商品列表
const getProductList = async () => {
  try {
    loading.value = true
    const response = await api.product.search({
      keyword: searchForm.keyword,
      category: searchForm.category,
      sort: searchForm.sort,
      pageNum: searchForm.pageNum,
      pageSize: searchForm.pageSize,
      status: '在售'
    })
    
    if (response.code === 200) {
      productList.value = response.data.records || []
      pagination.total = response.data.total || 0
      pagination.pageNum = response.data.current || 1
      pagination.pageSize = response.data.size || 12
    } else {
      ElMessage.error('获取商品列表失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索商品
const handleSearch = () => {
  searchForm.pageNum = 1
  getProductList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  searchForm.sort = 'createTime_desc'
  searchForm.pageNum = 1
  getProductList()
}

// 页面变化处理
const handlePageChange = (val) => {
  searchForm.pageNum = val
  getProductList()
}

// 页面大小变化处理
const handleSizeChange = (val) => {
  searchForm.pageSize = val
  searchForm.pageNum = 1
  getProductList()
}

// 排序变化处理
const handleSortChange = () => {
  searchForm.pageNum = 1
  getProductList()
}

// 分类变化处理
const handleCategoryChange = () => {
  searchForm.pageNum = 1
  getProductList()
}

// 刷新列表
const refreshList = async () => {
  refreshLoading.value = true
  try {
    await getProductList()
  } finally {
    refreshLoading.value = false
  }
}

// 查看商品详情
const viewProductDetail = (id) => {
  router.push(`/productDetail/${id}`)
}
// 返回首页
const goBack = () => {
  router.push('/home')
}

onMounted(() => {
  getProductList()
})

</script>

<template>
  <div class="search-product">
    <div class="page-header">
      <h2>商品浏览</h2>
      <div class="header-actions">
        <el-button
            size="small"
            :loading="refreshLoading"
            @click="refreshList"
        >
          <el-icon v-if="!refreshLoading"><Refresh /></el-icon>
          <span v-if="!refreshLoading">刷新</span>
          <span v-else>刷新中...</span>
        </el-button>
      </div>
    </div>

    <div class="search-content">
      <!-- 搜索和筛选区域 -->
      <div class="search-filter">
        <el-form :inline="true" @submit.prevent="handleSearch">
          <el-form-item>
            <el-input
                v-model="searchForm.keyword"
                placeholder="搜索商品"
                style="width: 300px"
                @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-select
                v-model="searchForm.category"
                placeholder="选择分类"
                style="width: 150px"
                @change="handleCategoryChange"
            >
              <el-option
                  v-for="category in categories"
                  :key="category.value"
                  :label="category.label"
                  :value="category.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
                v-model="searchForm.sort"
                placeholder="排序方式"
                style="width: 150px"
                @change="handleSortChange"
            >
              <el-option
                  v-for="option in sortOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品列表 -->
      <div class="product-list" v-loading="loading">
        <div v-if="productList.length > 0" class="product-grid">
          <div v-for="product in productList" :key="product.id" class="product-card" @click="viewProductDetail(product.id)">
            <div class="product-image">
              <img v-if="product.image" :src="'http://localhost:8080' + product.image" :alt="product.title" />
              <div v-else class="no-image">无图片</div>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <div class="product-price">¥{{ product.price }}</div>
              <div class="product-category">{{ product.category }}</div>
              <div class="product-time">{{ product.createTime }}</div>
            </div>
          </div>
        </div>
        <div v-else class="empty-wrapper">
          <el-empty
              description="暂无商品"
              style="margin: 60px 0;"
          />
        </div>
      </div>

      <!-- 分页组件 -->
      <div v-if="pagination.total > 0" class="pagination-wrapper">
        <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[12, 24, 36, 48]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>

    </div>
    <div class="back-button-wrapper">
      <el-button
          type="primary"
          size="medium"
          @click="goBack"
      >
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.back-button-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.search-product {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.search-filter {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.product-list {
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-image {
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #909399;
  font-size: 14px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
  margin: 0 0 8px 0;
}

.product-category {
  font-size: 12px;
  color: #606266;
  margin: 0 0 8px 0;
}

.product-time {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.empty-wrapper {
  text-align: center;
  padding: 60px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-product {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .search-content {
    padding: 10px;
  }
  
  .search-filter {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
  }
  
  .product-image {
    height: 120px;
  }
  
  .product-info {
    padding: 10px;
  }
  
  .product-title {
    font-size: 12px;
  }
  
  .product-price {
    font-size: 14px;
  }
}

</style>