<script setup >
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Edit, Delete, SwitchButton, Plus, Refresh, ArrowDown, Upload } from '@element-plus/icons-vue'
import { api } from '../../api/index.js'

const router = useRouter()

// 商品列表数据
const productList = ref([])
// 加载状态
const loading = ref(false)
// 刷新加载状态
const refreshLoading = ref(false)
// 编辑弹窗
const editDialogVisible = ref(false)
// 编辑加载状态
const editLoading = ref(false)
// 编辑提交状态
const editSubmitting = ref(false)
// 编辑商品数据
const editProduct = reactive({
  id: '',
  title: '',
  price: 0,
  category: '',
  description: '',
  image: null,
  currentImage: '',
  deleteImage: false,
  previewImage: ''
})
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
// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
// 商品状态筛选
const statusFilter = ref('全部')

// 状态选项
const statusOptions = [
  { label: '全部', value: '全部' },
  { label: '在售', value: '在售' },
  { label: '下架', value: '下架' }
]

// 获取商品列表
const getProductList = async () => {
  try {
    loading.value = true
    const response = await api.product.my({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: statusFilter.value
    })
    
    if (response.code === 200) {
      productList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error('获取商品列表失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 页面变化处理
const handlePageChange = (val) => {
  getProductList()
}

// 状态筛选变化
const handleStatusChange = () => {
  pagination.pageNum = 1
  getProductList()
}

// 编辑商品
const handleEdit = async (id) => {
  try {
    editLoading.value = true
    const response = await api.product.detail(id)
    if (response.code === 200) {
      const data = response.data
      editProduct.id = data.id
      editProduct.title = data.title
      editProduct.price = Number(data.price)
      editProduct.category = data.category
      editProduct.description = data.description
      editProduct.currentImage = data.image || ''
      editProduct.image = null
      editProduct.deleteImage = false
      editDialogVisible.value = true
    } else {
      ElMessage.error('获取商品详情失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    editLoading.value = false
  }
}



// 下架/上架商品
const handleOff = async (id, currentStatus) => {
  try {
    const response = await api.product.off(id)
    if (response.code === 200) {
      ElMessage.success(currentStatus === '在售' ? '商品已下架' : '商品已上架')
      await getProductList() // 刷新列表
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
  }
}

// 删除商品
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.product.delete(id)
    if (response.code === 200) {
      ElMessage.success('商品删除成功')
      await getProductList() // 刷新列表
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('网络异常，请稍后重试')
    }
  }
}

// 去发布商品
const goPublish = () => {
  router.push('/publishGoods')
}

// 返回首页
const goBack = () => {
  router.push('/home')

}

// 表单验证
const validateForm = () => {
  if (!editProduct.title.trim()) {
    ElMessage.error('请输入商品标题')
    return false
  }
  if (!editProduct.price || isNaN(Number(editProduct.price)) || Number(editProduct.price) <= 0) {
    ElMessage.error('请输入有效的商品价格（大于0的数字）')
    return false
  }
  if (!editProduct.category) {
    ElMessage.error('请选择商品分类')
    return false
  }
  if (!editProduct.description.trim()) {
    ElMessage.error('请输入商品描述')
    return false
  }
  return true
}

// 图片选择处理
const handleImageChange = (file) => {
  editProduct.image = file.raw
  // 生成预览图片 URL
  const reader = new FileReader()
  reader.onload = (e) => {
    editProduct.previewImage = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 提交更新
const handleUpdate = async () => {
  if (!validateForm()) return

  try {
    editSubmitting.value = true
    
    // 构建FormData
    const formData = new FormData()
    formData.append('title', editProduct.title.trim())
    formData.append('price', editProduct.price.toString())
    formData.append('category', editProduct.category)
    formData.append('description', editProduct.description.trim())
    formData.append('deleteImage', editProduct.deleteImage.toString())
    if (editProduct.image) {
      formData.append('image', editProduct.image)
    }

    const response = await api.product.update(editProduct.id, formData)

    if (response.code === 200) {
      ElMessage.success('商品更新成功')
      editDialogVisible.value = false
      await getProductList()
    } else {
      ElMessage.error('更新失败：' + (response.message || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试===')
  } finally {
    editSubmitting.value = false
  }
}

// 取消编辑
const handleCancelEdit = () => {
  editDialogVisible.value = false
  // 重置表单数据
  editProduct.id = ''
  editProduct.title = ''
  editProduct.price = 0
  editProduct.category = ''
  editProduct.description = ''
  editProduct.image = null
  editProduct.currentImage = ''
  editProduct.deleteImage = false
  editProduct.previewImage = ''
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

onMounted(() => {
  getProductList()
})
</script>

<template>
  <div class="my-product">
    <div class="page-header">
      <h2>我的商品</h2>
      <div class="header-actions">
        <el-select
            v-model="statusFilter"
            placeholder="选择状态"
            size="small"
            @change="handleStatusChange"
        >
          <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          />
        </el-select>
        <el-button
            type="primary"
            size="small"
            icon="Plus"
            @click="goPublish"
        >
          发布商品
        </el-button>
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

    <div class="product-content">
      <!-- 商品列表 -->
      <el-table
          v-if="productList.length > 0"
          :data="productList"
          style="width: 100%"
          border
          stripe
          :loading="loading"
      >
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="scope">
            <div class="product-image">
              <img v-if="scope.row.image" :src="'http://localhost:8080'+scope.row.image" :alt="scope.row.title" />
              <div v-else class="no-image">无图片</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品标题" min-width="200">
          <template #default="scope">
            <div class="product-title">
              {{ scope.row.title }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            <span class="price">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '在售' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-dropdown>
              <el-button size="small">
                操作
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleEdit(scope.row.id)">
                    <el-icon><Edit /></el-icon>
                    <span>编辑</span>
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleOff(scope.row.id, scope.row.status)">
                    <el-icon><SwitchButton /></el-icon>
                    <span>{{ scope.row.status === '在售' ? '下架' : '上架' }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(scope.row.id)">
                    <el-icon><Delete /></el-icon>
                    <span>删除</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据展示 -->
      <div v-else-if="productList.length === 0" class="empty-wrapper">
        <el-empty
            description="暂无已发布的商品"
            style="margin: 40px 0;"
        >
          <el-button
              type="primary"
              icon="Plus"
              @click="goPublish"
          >
            去发布商品
          </el-button>
        </el-empty>
      </div>

      <!-- 分页组件 -->
      <div v-if="pagination.total > 0" class="pagination-wrapper">
        <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handlePageChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 编辑商品弹窗 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑商品"
        width="600px"
        :before-close="handleCancelEdit"
    >
      <div v-loading="editLoading" style="min-height: 400px;">
        <!-- 商品标题 -->
        <el-form-item label="商品标题" required>
          <el-input
              v-model="editProduct.title"
              placeholder="请输入商品标题（如：99新苹果14手机）"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <!-- 商品价格 -->
        <el-form-item label="商品价格" required>
          <el-input-number
              v-model="editProduct.price"
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
              v-model="editProduct.category"
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
              v-model="editProduct.description"
              type="textarea"
              placeholder="请详细描述商品情况（成色、使用时长、配件等）"
              :rows="4"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>

        <!-- 商品图片 -->
        <el-form-item label="商品图片">
          <div class="image-upload">
            <!-- 当前图片 -->
            <div v-if="editProduct.currentImage" class="current-image">
              <img :src="'http://localhost:8080' + editProduct.currentImage" alt="当前图片" />
              <el-button
                  type="danger"
                  size="small"
                  @click="editProduct.currentImage = ''; editProduct.image = null; editProduct.deleteImage = true"
              >
                删除图片
              </el-button>
            </div>
            
            <!-- 图片上传 -->
            <div class="upload-area">
              <!-- 预览图片 -->
              <div v-if="editProduct.previewImage" class="preview-image">
                <img :src="editProduct.previewImage" alt="预览图片" />
                <el-button
                    type="danger"
                    size="small"
                    @click="editProduct.image = null; editProduct.previewImage = ''"
                >
                  移除图片
                </el-button>
              </div>

              <el-upload
                  v-if="!editProduct.previewImage"
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
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancelEdit">取消</el-button>
          <el-button
              type="primary"
              :loading="editSubmitting"
              @click="handleUpdate"
          >
            保存修改
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 返回按钮 -->
    <div class="back-button-wrapper">
      <el-button
          type="primary"
          size="medium"
          @click="$router.back()"
      >
        返  回
      </el-button>
    </div>
  </div>


</template>

<style scoped>
.my-product {
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

.product-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.product-title {
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.product-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #909399;
  font-size: 12px;
}

.price {
  font-weight: 600;
  color: #f56c6c;
}

.empty-wrapper {
  text-align: center;
  padding: 60px 0;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.back-button-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 编辑弹窗样式 */
.image-upload {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.current-image {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fafafa;
}

.current-image img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.upload-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start;
}

.preview-image {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fafafa;
}

.preview-image img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-product {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .product-content {
    padding: 10px;
  }
  
  .el-table {
    font-size: 14px;
  }
  
  .el-table-column {
    min-width: 80px;
  }
}
</style>