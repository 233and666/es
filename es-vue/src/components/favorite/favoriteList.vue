<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElEmpty, ElLoading } from 'element-plus'
import { api } from '../../api/index.js'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

// 收藏列表数据
const favorites = ref([])
// 加载状态
const loading = ref(false)

// 获取收藏列表
const getFavoriteList = async () => {
  try {
    loading.value = true
    const response = await api.favorite.list()
    if (response.code === 200) {
      favorites.value = response.data
    } else {
      ElMessage.error('获取收藏列表失败')
    }
  } catch (error) {
    ElMessage.error('wd网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 取消收藏
const cancelFavorite = async (productId) => {
  try {
    const response = await api.favorite.cancel(productId)
    if (response.code === 200) {
      ElMessage.success('取消收藏成功')
      // 重新获取收藏列表
      await getFavoriteList()
    } else {
      ElMessage.error('取消收藏失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 跳转到商品详情
const goToProductDetail = (productId) => {
  router.push(`/productDetail/${productId}`)
}

// 购买商品
const buyProduct = async (productId) => {
  try {
    const response = await api.order.create(productId)
    if (response.code === 200) {
      ElMessage.success('订单创建成功')
      // 跳转到订单页面
      router.push('/order/myBuy')
    } else {
      ElMessage.error('订单创建失败')
    }
  } catch (error) {
    ElMessage.error('购买失败，请稍后重试')
  }
}

onMounted(() => {
  getFavoriteList()
})
</script>

<template>
  <div class="favorite-list">
    <div class="page-header">
      <h2>我的收藏</h2>
    </div>

    <div class="favorite-content" v-loading="loading">
      <div v-if="favorites.length > 0" class="favorite-items">
        <div v-for="item in favorites" :key="item.id" class="favorite-item">
          <div class="item-image">
            <img v-if="item.image" :src="'http://localhost:8080' + item.image" :alt="item.title" />
            <div v-else class="no-image">无图片</div>
          </div>
          <div class="item-info">
            <h3 class="item-title" @click="goToProductDetail(item.id)">{{ item.title }}</h3>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-category">
              <span class="label">分类：</span>
              <span class="value">{{ item.category }}</span>
            </div>
            <div class="item-status">
              <span class="label">状态：</span>
              <el-tag :type="item.status === '在售' ? 'success' : 'info'">{{ item.status }}</el-tag>
            </div>
            <div class="item-actions">
              <el-button
                  type="primary"
                  size="small"
                  @click="buyProduct(item.id)"
                  :disabled="item.status !== '在售'"
                  class="buy-button"
              >
                <el-icon><ShoppingCart /></el-icon>
                立即购买
              </el-button>
              <el-button
                  type="danger"
                  size="small"
                  @click="cancelFavorite(item.id)"
                  class="cancel-button"
              >
                <el-icon><Trash /></el-icon>
                取消收藏
              </el-button>
              <el-button
                  type="info"
                  size="small"
                  @click="goToProductDetail(item.id)"
                  class="detail-button"
              >
                查看详情
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-wrapper">
        <el-empty
            description="暂无收藏商品"
            style="margin: 60px 0;"
        />
      </div>
    </div>
    
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button 
        class="back-button" 
        type="primary" 
        size="large"
        @click="$router.back()"
      >
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.favorite-list {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.page-header {
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

.favorite-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.favorite-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.favorite-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.favorite-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.item-image {
  flex: 0 0 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #909399;
  font-size: 14px;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-title:hover {
  color: #409eff;
}

.item-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
  margin: 0 0 10px 0;
}

.item-category,
.item-status {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #606266;
  margin-right: 10px;
  min-width: 60px;
}

.value {
  color: #303133;
}

.item-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  flex-wrap: wrap;
}

.buy-button,
.cancel-button,
.detail-button {
  flex: 0 0 auto;
}

.empty-wrapper {
  text-align: center;
  padding: 60px 0;
}

/* 返回按钮样式 */
.back-button-container {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 1000;
}

.back-button {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
  font-size: 16px;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.back-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.back-button:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorite-list {
    padding: 10px;
  }
  
  .favorite-content {
    padding: 15px;
  }
  
  .favorite-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .item-image {
    flex: 0 0 200px;
    width: 100%;
    height: 200px;
  }
  
  .item-actions {
    width: 100%;
  }
  
  .buy-button,
  .cancel-button,
  .detail-button {
    flex: 1;
  }
  
  .back-button-container {
    right: 20px;
    bottom: 20px;
  }
  
  .back-button {
    font-size: 14px;
    padding: 10px 20px;
  }
}
</style>