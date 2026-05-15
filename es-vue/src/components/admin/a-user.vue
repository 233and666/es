<template>
  <div class="admin-container">
    <header class="admin-header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="28" color="#fff" icon="UserFilled" />
          <span class="logo-text">用户管理</span>
        </div>
      </div>
      <div class="header-right">
        <el-button color="#6366f1" plain @click="$router.push('/adminHome')">
          <el-icon style="margin-right: 5px"><Back /></el-icon>
          返回控制台
        </el-button>
      </div>
    </header>

    <main class="admin-main">
      <div class="content-card">
        <div class="card-header">
          <h2>系统用户列表</h2>
          <p>您可以在此查看所有注册用户，并对违规账号执行封禁操作。</p>
        </div>

        <el-table :data="tableData" border stripe style="width: 100%" class="custom-table">
          <el-table-column prop="id" label="用户ID" width="100" align="center"></el-table-column>
          <el-table-column prop="username" label="用户名" width="180" align="center"></el-table-column>

          <el-table-column label="账号状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'danger' : 'success'" effect="dark">
                {{ scope.row.status === 1 ? '已封禁' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center">
            <template #default="scope">
              <el-popconfirm
                  v-if="scope.row.status !== 1"
                  title="确定要封禁该用户吗？该用户将无法登录系统。"
                  confirm-button-text="确认封禁"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="handleStatusChange(scope.row.id, 1)"
              >
                <template #reference>
                  <el-button type="danger" size="small" plain>封禁账号</el-button>
                </template>
              </el-popconfirm>

              <el-popconfirm
                  v-else
                  title="确定要解除该用户的封禁吗？"
                  confirm-button-text="确认解封"
                  cancel-button-text="取消"
                  confirm-button-type="success"
                  @confirm="handleStatusChange(scope.row.id, 0)"
              >
                <template #reference>
                  <el-button type="success" size="small" plain>解除封禁</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../api/request'

const tableData = ref([])

// 获取用户列表
const loadUsers = () => {
  request.get('/admin/users').then(res => {
    if (res.code === '200' || res.code === 200) {
      tableData.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取数据失败')
    }
  })
}

// 修改状态
const handleStatusChange = (id, status) => {
  request.put(`/admin/user/status/${id}?status=${status}`).then(res => {
    if (res.code === '200' || res.code === 200) {
      ElMessage.success(status === 1 ? '账号已封禁' : '账号已解封')
      loadUsers() // 重新刷新列表
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  })
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
/* 保持与 a-home.vue 一致的全局背景和排版 */
.admin-container {
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

.admin-main {
  flex: 1;
  padding: 40px 30px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* 白色数据展示卡片 */
.content-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-header {
  margin-bottom: 25px;
}

.card-header h2 {
  font-size: 24px;
  color: #1f2f3f;
  margin-bottom: 8px;
  font-weight: 600;
}

.card-header p {
  color: #6c757d;
  font-size: 14px;
}

/* 优化表格圆角和样式 */
.custom-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f5f7fa !important;
  color: #333;
  font-weight: bold;
}
</style>