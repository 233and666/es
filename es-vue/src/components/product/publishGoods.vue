<template>
  <div class="publish-goods-container">
    <!-- 页面标题 -->
    <el-page-header content="发布闲置商品" />

    <!-- 商品发布表单 -->
    <el-form
        ref="goodsFormRef"
        :model="goodsForm"
        :rules="formRules"
        label-width="100px"
        class="publish-form"
    >
      <!-- 1. 商品名称 -->
      <el-form-item label="商品名称" prop="name">
        <el-input
            v-model="goodsForm.title"
            placeholder="请输入商品名称（如：99新苹果14）"
            maxlength="50"
            show-word-limit
        />
      </el-form-item>

      <!-- 2. 商品价格 -->
      <el-form-item label="商品价格（元）" prop="price">
        <el-input
            v-model="goodsForm.price"
            type="number"
            placeholder="请输入商品价格（0.01起）"
            :min="0.01"
            step="0.01"
        />
      </el-form-item>

      <!-- 3. 商品分类 -->
      <el-form-item label="商品分类" prop="category">
        <el-select v-model="goodsForm.category" placeholder="请选择商品分类">
          <el-option label="数码产品" value="数码产品" />
          <el-option label="家居用品" value="家居用品" />
          <el-option label="服饰鞋包" value="服饰鞋包" />
          <el-option label="图书音像" value="图书音像" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <!-- 4. 商品描述 -->
      <el-form-item label="商品描述">
        <el-input
            v-model="goodsForm.desc"
            type="textarea"
            placeholder="请描述商品成色、使用情况、是否有瑕疵等（选填）"
            :rows="5"
            maxlength="500"
            show-word-limit
        />
      </el-form-item>

      <!-- 5. 商品图片上传 -->
      <el-form-item label="商品图片">
        <el-upload
            class="avatar-uploader"
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            :limit="1"
            accept="image/jpeg,image/png"
            :before-upload="beforeUpload"
        >
          <el-button icon="el-icon-plus" type="primary">选择图片</el-button>
          <template #tip>
            <div class="el-upload__tip">支持jpg/png格式，单张不超过5MB</div>
          </template>
        </el-upload>
        <!-- 图片预览 -->
        <el-image
            v-if="previewUrl"
            :src="previewUrl"
            class="img-preview"
            fit="cover"
        />
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <el-button
            type="primary"
            @click="submitPublish"
            :loading="isSubmitting"
        >
          发布商品
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'


import { api } from '../../api/index.js'

const router = useRouter()

// 表单Ref
const goodsFormRef = ref(null)

// 提交状态
const isSubmitting = ref(false)

// 图片预览URL
const previewUrl = ref('')

// 上传文件列表
const fileList = ref([])

// 商品表单数据
const goodsForm = reactive({
  title: '',       // 商品名称
  price: '',      // 商品价格
  category: '',   // 商品分类
  desc: '',       // 商品描述
  file: null      // 上传的文件
})

// 表单校验规则
const formRules = reactive({
  title: [
    { required: true, message: '商品名称不能为空', trigger: 'blur' },
    { max: 50, message: '商品名称不能超过50个字', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '商品价格不能为空', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (Number(value) < 0.01) {
          callback(new Error('商品价格不能低于0.01元'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ]
})

// 图片上传前校验
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传jpg/png格式的图片！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 处理文件选择（预览）
const handleFileChange = (file) => {
  if (file.status === 'ready') {
    // 校验文件
    if (!beforeUpload(file.raw)) {
      fileList.value = []
      return
    }
    // 保存文件并生成预览
    goodsForm.file = file.raw
    previewUrl.value = URL.createObjectURL(file.raw)//前端生成临时预览地址（基于浏览器内存，刷新后失效）
    fileList.value = [file] // 只保留一张图片
  }
}

// 重置表单
const resetForm = () => {
  goodsFormRef.value.resetFields()
  fileList.value = []
  previewUrl.value = ''
  goodsForm.file = null
}

// 提交发布商品
const submitPublish = async () => {

  // 先做表单校验
  try {
   if (!goodsForm.title || !goodsForm.price || !goodsForm.category || !goodsForm.desc) {
    ElMessage.error('请完善必填项后提交！')
    return
   }
  } catch (error) {
    ElMessage.error('请完善必填项后提交！')
    return
  }
  isSubmitting.value = true
  try {
    // 构建提交数据
    const formData = new FormData()
    formData.append('title', goodsForm.title)
    formData.append('price', goodsForm.price)
    formData.append('category', goodsForm.category)
    formData.append('description', goodsForm.desc)
    if (goodsForm.file) {
      formData.append('image', goodsForm.file)
    }

    // 调用发布商品接口（替换为真实接口）
    const res = await api.product.publish(formData)
    // 模拟成功返回
    //const res = { success: true, message: '商品发布成功' }
    if (res.success) {
      ElMessage.success(res.message)
      // 发布成功后跳转
      await ElMessageBox.alert('商品发布成功，将跳转到我的商品页面', '成功', {
        confirmButtonText: '确定'
      })
      router.push('/myProduct')
    } else {
      ElMessage.error('发布失败：' + res.message)
    }
  } catch (error) {
    ElMessage.error('网络异常，商品发布失败')
    console.error('发布失败：', error)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* 页面容器 */
.publish-goods-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 表单样式 */
.publish-form {
  margin-top: 20px;
}

/* 图片上传区域 */
.avatar-uploader {
  margin-bottom: 10px;
}

/* 图片预览 */
.img-preview {
  width: 200px;
  height: 200px;
  border: 1px solid #eee;
  border-radius: 4px;
}

/* 适配Element Plus样式 */
:deep(.el-page-header) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-upload__tip) {
  color: #999;
  font-size: 12px;
}
</style>