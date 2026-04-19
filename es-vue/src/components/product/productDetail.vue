<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star, StarFilled, ShoppingCart } from '@element-plus/icons-vue'
import { api } from '../../api/index.js'

const router = useRouter()
const route = useRoute()

// 商品详情数据
const product = ref(null)
// 加载状态
const loading = ref(false)
// 收藏状态
const isFavorited = ref(false)
// 操作加载状态
const actionLoading = ref(false)

// 获取商品详情
const getProductDetail = async () => {
  try {
    loading.value = true
    const id = route.params.id
    const response = await api.product.detail(id)
    if (response.code === 200) {
      product.value = response.data
      // 获取收藏状态
      await checkFavoriteStatus(id)
    } else {
      ElMessage.error('获取商品详情失败')
      // 跳回商品浏览页面
      router.push('/searchProduct')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
    // 跳回商品浏览页面
    router.push('/searchProduct')
  } finally {
    loading.value = false
  }
}

// 检查收藏状态
const checkFavoriteStatus = async (productId) => {
  try {
    const response = await api.favorite.isFavorite(productId)
    if (response.code === 200) {
      isFavorited.value = response.data
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!product.value) return
  
  try {
    actionLoading.value = true
    const productId = product.value.id
    
    if (isFavorited.value) {
      // 取消收藏
      const response = await api.favorite.cancel(productId)
      if (response.code === 200) {
        isFavorited.value = false
        ElMessage.success('取消收藏成功')
      } else {
        ElMessage.error('取消收藏失败')
      }
    } else {
      // 添加收藏
      const response = await api.favorite.add(productId)
      if (response.code === 200) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
      } else {
        ElMessage.error('收藏失败')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    actionLoading.value = false
  }
}

// 购买商品
const buyProduct = async () => {
  if (!product.value) return
  
  try {
    actionLoading.value = true
    const response = await api.order.create(product.value.id)
    if (response.code === 200) {
      ElMessage.success('订单创建成功')
      // 跳转到订单页面
      router.push('/order/myBuy')
    } else {
      ElMessage.error('订单创建失败')
    }
  } catch (error) {
    ElMessage.error('购买失败，请稍后重试')
  } finally {
    actionLoading.value = false
  }
}

// 返回商品浏览页面
const goBack = () => {
  router.push('/searchProduct')
}

onMounted(() => {
  getProductDetail()
})
</script>

<template>
  <div class="product-detail">
    <div class="page-header">
      <div class="header-left">
        <el-button
            type="primary"
            size="medium"
            @click="goBack"
        >
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
        <h2>商品详情</h2>
      </div>
    </div>

    <div class="detail-content" v-loading="loading">
      <div v-if="product" class="product-info">
        <!-- 商品图片 -->
        <div class="product-image">
          <img v-if="product.image" :src="'http://localhost:8080' + product.image" :alt="product.title" />
          <div v-else class="no-image">无图片</div>
        </div>

        <!-- 商品信息 -->
        <div class="product-details">
          <h3 class="product-title">{{ product.title }}</h3>
          <div class="product-price">¥{{ product.price }}</div>
          <div class="product-category">
            <span class="label">分类：</span>
            <span class="value">{{ product.category }}</span>
          </div>
          <div class="product-status">
            <span class="label">状态：</span>
            <el-tag :type="product.status === '在售' ? 'success' : 'info'">{{ product.status }}</el-tag>
          </div>
          <div class="product-time">
            <span class="label">发布时间：</span>
            <span class="value">{{ product.createTime }}</span>
          </div>
          <div class="product-description">
            <h4>商品描述</h4>
            <p>{{ product.description }}</p>
          </div>
          
          <!-- 操作按钮 -->
          <div class="product-actions">
            <el-button
                type="primary"
                size="large"
                :loading="actionLoading"
                @click="buyProduct"
                :disabled="product.status !== '在售'"
                class="buy-button"
            >
              <el-icon><ShoppingCart /></el-icon>
              立即购买
            </el-button>
            <el-button
                :type="isFavorited ? 'danger' : 'primary'"
                size="large"
                :loading="actionLoading"
                @click="toggleFavorite"
                class="favorite-button"
            >
              <el-icon v-if="isFavorited"><StarFilled /></el-icon>
              <el-icon v-else><Star /></el-icon>
              {{ isFavorited ? '取消收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-wrapper">
        <el-empty
            description="商品不存在"
            style="margin: 60px 0;"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.detail-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.product-info {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.product-image {
  flex: 0 0 400px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #909399;
  font-size: 16px;
}

.product-details {
  flex: 1;
  min-width: 300px;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  line-height: 1.4;
}

.product-price {
  font-size: 28px;
  font-weight: 600;
  color: #f56c6c;
  margin: 0 0 30px 0;
}

.product-category,
.product-status,
.product-time {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
}

.label {
  color: #606266;
  margin-right: 10px;
  min-width: 80px;
}

.value {
  color: #303133;
}

.product-description {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.product-description h4 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 15px 0;
}

.product-description p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
}

/* 操作按钮样式 */
.product-actions {
  display: flex;
  gap: 20px;
  margin-top: 40px;
  flex-wrap: wrap;
}

.buy-button {
  flex: 1;
  min-width: 200px;
}

.favorite-button {
  flex: 1;
  min-width: 200px;
}

.empty-wrapper {
  text-align: center;
  padding: 60px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail {
    padding: 10px;
  }
  
  .detail-content {
    padding: 15px;
  }
  
  .product-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .product-image {
    flex: 0 0 250px;
    height: 250px;
  }
  
  .product-title {
    font-size: 20px;
  }
  
  .product-price {
    font-size: 24px;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .buy-button,
  .favorite-button {
    width: 100%;
  }
}
</style>