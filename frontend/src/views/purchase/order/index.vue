<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, auditOrder, cancelOrder, closeOrder } from '@/api/purchase'
import type { PoOrder, OrderQuery } from '@/api/purchase'
import { DOC_STATUS_OPTIONS } from '@/constants/status'

// 查询参数
const queryParams = ref<OrderQuery>({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  supplierId: undefined,
  status: '',
  startDate: '',
  endDate: '',
})

// 表格数据
const tableData = ref<PoOrder[]>([])
const total = ref(0)
const loading = ref(false)

// 查询采购订单列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getOrders(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('操作失败，请重试')
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
    supplierId: undefined,
    status: '',
    startDate: '',
    endDate: '',
  }
  getList()
}

// 审核
const handleAudit = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要审核该采购订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await auditOrder(id)
    ElMessage.success('审核成功')
    getList()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 取消
const handleCancel = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该采购订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await cancelOrder(id)
    ElMessage.success('取消成功')
    getList()
  } catch (error) {
    console.error('取消失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 关闭
const handleClose = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要关闭该采购订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await closeOrder(id)
    ElMessage.success('关闭成功')
    getList()
  } catch (error) {
    console.error('关闭失败:', error)
    ElMessage.error('操作失败，请重试')
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
    DRAFT: 'info',
    AUDITED: 'success',
    CLOSED: 'warning',
    CANCELLED: 'danger',
  }
  return colorMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const item = DOC_STATUS_OPTIONS.find(opt => opt.value === status)
  return item?.label || status
}

// 格式化金额
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
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="供应商ID">
          <el-input v-model="queryParams.supplierId" placeholder="请输入供应商ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option
              v-for="item in DOC_STATUS_OPTIONS"
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
      新增采购订单（开发中）
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="orderNo" label="订单号" width="150" />
      <el-table-column prop="supplierName" label="供应商" width="150" show-overflow-tooltip />
      <el-table-column prop="orderDate" label="订单日期" width="120" />
      <el-table-column prop="deliveryDate" label="交货日期" width="120" />
      <el-table-column label="总金额" width="120" align="right">
        <template #default="{ row }">
          {{ formatMoney(row.totalAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="税额" width="100" align="right">
        <template #default="{ row }">
          {{ formatMoney(row.taxAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusColor(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" type="success" link @click="handleAudit(row.id)">
            审核
          </el-button>
          <el-button v-if="row.status === 'DRAFT' || row.status === 'AUDITED'" type="danger" link @click="handleCancel(row.id)">
            取消
          </el-button>
          <el-button v-if="row.status === 'AUDITED'" type="warning" link @click="handleClose(row.id)">
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
