<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTransactions } from '@/api/inventory'
import type { InvTransaction, TransactionQuery } from '@/api/inventory'
import { TRANSACTION_TYPE_OPTIONS } from '@/constants/status'

// 查询参数
const queryParams = ref<TransactionQuery>({
  pageNum: 1,
  pageSize: 10,
  transactionType: '',
  itemId: undefined,
  warehouseId: undefined,
  startDate: '',
  endDate: '',
})

// 表格数据
const tableData = ref<InvTransaction[]>([])
const total = ref(0)
const loading = ref(false)

// 查询流水列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getTransactions(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询库存流水失败，请重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.value.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    transactionType: '',
    itemId: undefined,
    warehouseId: undefined,
    startDate: '',
    endDate: '',
  }
  getList()
}

// 分页变化
const handleSizeChange = (val: number) => {
  queryParams.value.pageSize = val
  getList()
}

const handleCurrentChange = (val: number) => {
  queryParams.value.pageNum = val
  getList()
}

// 格式化数字
const formatNumber = (num: number, decimals = 4) => {
  return num?.toFixed(decimals) || '0.0000'
}

const formatMoney = (num: number) => {
  return num?.toFixed(2) || '0.00'
}

// 获取交易类型标签颜色
const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    RECEIPT: 'success',
    ISSUE: 'danger',
    TRANSFER: 'primary',
    ADJUSTMENT: 'warning',
    RETURN: 'info',
  }
  return colorMap[type] || 'info'
}

// 获取交易类型标签
const getTypeLabel = (type: string) => {
  const item = TRANSACTION_TYPE_OPTIONS.find(opt => opt.value === type)
  return item?.label || type
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="交易类型">
          <el-select v-model="queryParams.transactionType" placeholder="请选择" clearable>
            <el-option
              v-for="item in TRANSACTION_TYPE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.itemId" placeholder="请输入物料ID" clearable />
        </el-form-item>
        <el-form-item label="仓库ID">
          <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库ID" clearable />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="queryParams.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="queryParams.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="transactionNo" label="流水号" width="150" />
      <el-table-column label="交易类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeColor(row.transactionType)">
            {{ getTypeLabel(row.transactionType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="transactionDate" label="交易日期" width="120" />
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column prop="warehouseName" label="仓库" width="100" />
      <el-table-column prop="lotNo" label="批次号" width="120" />
      <el-table-column label="数量" width="120" align="right">
        <template #default="{ row }">
          <span :class="{ 'text-success': row.quantity > 0, 'text-danger': row.quantity < 0 }">
            {{ row.quantity > 0 ? '+' : '' }}{{ formatNumber(row.quantity) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="单位成本" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.unitCost, 6) }}
        </template>
      </el-table-column>
      <el-table-column label="总成本" width="120" align="right">
        <template #default="{ row }">
          {{ formatMoney(row.totalCost) }}
        </template>
      </el-table-column>
      <el-table-column prop="referenceNo" label="参考单号" width="150" />
      <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.text-success {
  color: #67c23a;
  font-weight: bold;
}
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
