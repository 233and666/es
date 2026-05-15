




暂时不使用这个页面了


<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api/index.js'

const route = useRoute()
const router = useRouter()

// 商品ID
const productId = ref(route.params.id)

// 商品数据
const product = reactive({
  title: '',
  price: '',
  category: '',
  description: '',
  image: null,
  currentImage: ''
})

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 分类选项
const categories = [
  { value: '数码产品', label: '数码产品' },
  { value: '家居用品', label: '家居用品' },
  { value: '服装鞋包', label: '服装鞋包' },
  { value: '运动户外', label: '运动户外' },
  { value: '图书音像', label: '图书音像' },
  { value: '乐器设备', label: '乐器设备' },
  { value: '办公用品', label: '办公用品' },
  { value: '其他', label: '其他' }
]

// 表单验证
const validateForm = () => {
  if (!product.title.trim()) {
    ElMessage.error('请输入商品标题')
    return false
  }
  if (!product.price || isNaN(Number(product.price)) || Number(product.price) <= 0) {
    ElMessage.error('请输入有效的商品价格（大于0的数字）')
    return false
  }
  if (!product.category) {
    ElMessage.error('请选择商品分类')
    return false
  }
  if (!product.description.trim()) {
    ElMessage.error('请输入商品描述')
    return false
  }
  return true
}

// 获取商品详情
const getProductDetail = async () => {
  try {
    loading.value = true
    const response = await api.product.detail(productId.value)
    if (response.code === 200) {
      const data = response.data
      product.title = data.title
      product.price = data.price.toString()
      product.category = data.category
      product.description = data.description
      product.currentImage = data.image || ''
    } else {
      ElMessage.error('获取商品详情失败')
      router.push('/myProduct')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
    router.push('/myProduct')
  } finally {
    loading.value = false
  }
}

// 图片选择处理
const handleImageChange = (file) => {
  product.image = file.raw
}

// 提交更新
const handleUpdate = async () => {
  if (!validateForm()) return

  try {
    submitting.value = true

    // 构建FormData
    const formData = new FormData()
    formData.append('title', product.title.trim())
    formData.append('price', product.price.trim())
    formData.append('category', product.category)
    formData.append('description', product.description.trim())
    if (product.image) {
      formData.append('image', product.image)
    }

    const response = await api.product.update(productId.value, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.code === 200) {
      ElMessage.success('商品更新成功')
      router.push('/myProduct')
    } else {
      ElMessage.error('更新失败：' + (response.message || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  ElMessageBox.confirm('确定要取消编辑吗？未保存的更改将丢失。', '取消编辑', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push('/myProduct')
  }).catch(() => {
    // 取消操作
  })
}

onMounted(() => {
  getProductDetail()
})
</script>

<template>
  <div class="update-product">
    <div class="page-header">
      <h2>编辑商品</h2>
      <div class="header-actions">
        <el-button
            type="primary"
            :loading="submitting"
            @click="handleUpdate"
        >
          <el-icon><Save /></el-icon>
          保存修改
        </el-button>
        <el-button
            @click="handleCancel"
        >
          <el-icon><Close /></el-icon>
          取消
        </el-button>
      </div>
    </div>

    <div class="update-form" v-loading="loading">
      <el-card>
        <!-- 商品标题 -->
        <el-form-item label="商品标题" required>
          <el-input
              v-model="product.title"
              placeholder="请输入商品标题（如：99新苹果14手机）"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <!-- 商品价格 -->
        <el-form-item label="商品价格" required>
          <el-input-number
              v-model="product.price"
              :min="0.01"
              :step="0.01"
              :precision="2"
              placeholder="请输入商品价格"
              style="width: 100%"
          />
        </el-form-item>

        <!-- 商品分类 -->
        <el-form-item label="商品分类" required>
          <el-select
              v-model="product.category"
              placeholder="请选择商品分类"
              style="width: 100%"
          >
            <el-option
                v-for="category in categories"
                :key="category.value"
                :label="category.label"
                :value="category.value"
            />
          </el-select>
        </el-form-item>

        <!-- 商品描述 -->
        <el-form-item label="商品描述" required>
          <el-input
              v-model="product.description"
              type="textarea"
              placeholder="请详细描述商品情况（成色、使用时长、配件等）"
              :rows="5"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>

        <!-- 商品图片 -->
        <el-form-item label="商品图片">
          <div class="image-upload">
            <!-- 当前图片 -->
            <div v-if="product.currentImage" class="current-image">
              <img :src="'http://localhost:8080' + product.currentImage" alt="当前图片" />
              <el-button
                  type="danger"
                  size="small"
                  @click="product.currentImage = ''; product.image = null"
              >
                删除图片
              </el-button>
            </div>

            <!-- 图片上传 -->
            <div class="upload-area">
              <el-upload
                  class="avatar-uploader"
                  action=""
                  :auto-upload="false"
                  :on-change="handleImageChange"
                  :show-file-list="false"
                  accept="image/*"
              >
                <el-button size="small" type="primary">
                  <el-icon><Upload /></el-icon>
                  选择图片
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持JPG、PNG格式，大小不超过5MB
                  </div>
                </template>
              </el-upload>
            </div>
          </div>
        </el-form-item>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.update-product {
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
}

.update-form {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 0;
}

.el-card {
  border: none;
  border-radius: 8px;
}

.image-upload {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.current-image {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fafafa;
}

.current-image img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
}

.upload-area {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .update-product {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .current-image {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .current-image img {
    width: 100%;
    max-width: 200px;
  }
}
</style>