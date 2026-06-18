<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

const stats = ref([
  { title: '待处理订单', value: 12, icon: 'Document', color: '#409eff' },
  { title: '库存预警', value: 5, icon: 'Warning', color: '#e6a23c' },
  { title: '待收款', value: '¥125,800', icon: 'Money', color: '#67c23a' },
  { title: '待付款', value: '¥89,500', icon: 'Wallet', color: '#f56c6c' },
])

const recentOrders = ref([
  { orderNo: 'SO20240115001', customer: '客户A', amount: '¥15,800', status: '待审核', statusType: 'warning' },
  { orderNo: 'PO20240115002', supplier: '供应商B', amount: '¥28,500', status: '已审核', statusType: 'success' },
  { orderNo: 'SO20240115003', customer: '客户C', amount: '¥42,300', status: '已发货', statusType: 'primary' },
])
</script>

<template>
  <div class="dashboard">
    <div class="welcome">
      <h2>欢迎回来，{{ authStore.nickname }}</h2>
      <p>今天是 {{ new Date().toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
            <el-icon :size="48" :color="item.color">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>最近订单</span>
          </template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column label="客户/供应商" width="150">
              <template #default="{ row }">
                {{ row.customer || row.supplier }}
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.statusType">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button type="primary" link>查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/purchase/order')">
              新建采购订单
            </el-button>
            <el-button type="success" @click="$router.push('/sales/order')">
              新建销售订单
            </el-button>
            <el-button type="warning" @click="$router.push('/inventory/stock')">
              库存查询
            </el-button>
            <el-button type="info" @click="$router.push('/production/workorder')">
              生产工单
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
}

.welcome {
  margin-bottom: 24px;
}

.welcome h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.welcome p {
  color: #909399;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-actions .el-button {
  width: 100%;
}
</style>
