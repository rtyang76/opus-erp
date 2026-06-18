<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWorkOrders, releaseWorkOrder, startWorkOrder, closeWorkOrder } from '@/api/production'
import type { PpWorkOrder, WorkOrderQuery } from '@/api/production'

// 工单状态选项
const statusOptions = [
  { label: '待下达', value: 'PENDING' },
  { label: '已下达', value: 'RELEASED' },
  { label: '生产中', value: 'IN_PROGRESS' },
  { label: '已完工', value: 'COMPLETED' },
  { label: '已关闭', value: 'CLOSED' },
]

// 查询参数
const queryParams = ref<WorkOrderQuery>({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  itemId: undefined,
  status: '',
})

// 表格数据
const tableData = ref<PpWorkOrder[]>([])
const total = ref(0)
const loading = ref(false)

// 查询工单列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getWorkOrders(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询工单列表失败，请重试')
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
    orderNo: '',
    itemId: undefined,
    status: '',
  }
  getList()
}

// 下达
const handleRelease = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要下达该工单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await releaseWorkOrder(id)
    ElMessage.success('下达成功')
    getList()
  } catch (error) {
    console.error('下达失败:', error)
    ElMessage.error('下达工单失败，请重试')
  }
}

// 开工
const handleStart = async (id: number) => {
  try {
    await startWorkOrder(id)
    ElMessage.success('开工成功')
    getList()
  } catch (error) {
    console.error('开工失败:', error)
    ElMessage.error('开工失败，请重试')
  }
}

// 关闭
const handleClose = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要关闭该工单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await closeWorkOrder(id)
    ElMessage.success('关闭成功')
    getList()
  } catch (error) {
    console.error('关闭失败:', error)
    ElMessage.error('关闭工单失败，请重试')
  }
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

// 获取状态标签颜色
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    PENDING: 'info',
    RELEASED: 'warning',
    IN_PROGRESS: 'primary',
    COMPLETED: 'success',
    CLOSED: 'info',
  }
  return colorMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const item = statusOptions.find(opt => opt.value === status)
  return item?.label || status
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
        <el-form-item label="工单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入工单号" clearable />
        </el-form-item>
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.itemId" placeholder="请输入物料ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <el-button type="primary" style="margin-bottom: 16px" disabled>
      新增工单（开发中）
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="orderNo" label="工单号" width="150" />
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column label="计划数量" width="100" align="right">
        <template #default="{ row }">
          {{ row.quantity?.toFixed(4) }}
        </template>
      </el-table-column>
      <el-table-column label="完工数量" width="100" align="right">
        <template #default="{ row }">
          {{ row.completedQuantity?.toFixed(4) }}
        </template>
      </el-table-column>
      <el-table-column prop="planStartDate" label="计划开始" width="120" />
      <el-table-column prop="planEndDate" label="计划结束" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusColor(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" type="warning" link @click="handleRelease(row.id)">
            下达
          </el-button>
          <el-button v-if="row.status === 'RELEASED'" type="primary" link @click="handleStart(row.id)">
            开工
          </el-button>
          <el-button v-if="row.status === 'COMPLETED'" type="info" link @click="handleClose(row.id)">
            关闭
          </el-button>
        </template>
      </el-table-column>
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
