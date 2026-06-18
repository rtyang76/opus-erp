<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStock } from '@/api/inventory'
import type { InvStock, StockQuery } from '@/api/inventory'

// 查询参数
const queryParams = ref<StockQuery>({
  pageNum: 1,
  pageSize: 10,
  itemId: undefined,
  warehouseId: undefined,
  lotNo: '',
  minQuantity: undefined,
})

// 表格数据
const tableData = ref<InvStock[]>([])
const total = ref(0)
const loading = ref(false)

// 查询库存列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getStock(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询库存失败，请重试')
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
    itemId: undefined,
    warehouseId: undefined,
    lotNo: '',
    minQuantity: undefined,
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

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.itemId" placeholder="请输入物料ID" clearable />
        </el-form-item>
        <el-form-item label="仓库ID">
          <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库ID" clearable />
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="queryParams.lotNo" placeholder="请输入批次号" clearable />
        </el-form-item>
        <el-form-item label="最小数量">
          <el-input-number v-model="queryParams.minQuantity" :min="0" :precision="4" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column prop="specification" label="规格型号" width="120" show-overflow-tooltip />
      <el-table-column prop="warehouseName" label="仓库" width="100" />
      <el-table-column prop="lotNo" label="批次号" width="120" />
      <el-table-column label="库存数量" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.quantity) }}
        </template>
      </el-table-column>
      <el-table-column label="平均成本" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.avgCost, 6) }}
        </template>
      </el-table-column>
      <el-table-column label="库存金额" width="120" align="right">
        <template #default="{ row }">
          {{ formatMoney(row.totalCost) }}
        </template>
      </el-table-column>
      <el-table-column label="锁定数量" width="100" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.lockedQuantity) }}
        </template>
      </el-table-column>
      <el-table-column label="可用数量" width="120" align="right">
        <template #default="{ row }">
          <span :class="{ 'text-danger': row.availableQuantity <= 0 }">
            {{ formatNumber(row.availableQuantity) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="updatedAt" label="更新时间" width="180" />
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
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
