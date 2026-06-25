<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPayables, createPayable, updatePayable, deletePayable } from '@/api/finance'
import type { FinPayable, PayableQuery } from '@/api/finance'
import PayableFormDialog from './PayableFormDialog.vue'

// 应付状态选项
const statusOptions = [
  { label: '待付款', value: 'PENDING' },
  { label: '部分付款', value: 'PARTIAL' },
  { label: '已付完', value: 'PAID' },
]

// 查询参数
const queryParams = ref<PayableQuery>({
  pageNum: 1,
  pageSize: 10,
  supplierId: undefined,
  status: '',
})

// 表格数据
const tableData = ref<FinPayable[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增应付单')
const formData = ref<Partial<FinPayable>>({
  supplierId: 0,
  amount: 0,
  currency: 'CNY',
  dueDate: '',
  status: 'PENDING',
  remark: '',
})

// 查询应付单列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getPayables(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询应付单列表失败，请重试')
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
    supplierId: undefined,
    status: '',
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增应付单'
  formData.value = {
    supplierId: 0,
    amount: 0,
    currency: 'CNY',
    dueDate: '',
    status: 'PENDING',
    remark: '',
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: FinPayable) => {
  dialogTitle.value = '编辑应付单'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (formData.value.id) {
      await updatePayable(formData.value.id, formData.value)
      ElMessage.success('更新成功')
    } else {
      await createPayable(formData.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该应付单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deletePayable(id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除应付单失败，请重试')
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
const getStatusColor = (status: string): 'success' | 'primary' | 'warning' | 'info' | 'danger' => {
  const colorMap: Record<string, 'success' | 'primary' | 'warning' | 'info' | 'danger'> = {
    PENDING: 'warning',
    PARTIAL: 'primary',
    PAID: 'success',
  }
  return colorMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const item = statusOptions.find(opt => opt.value === status)
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
    <el-button type="primary" style="margin-bottom: 16px" @click="handleAdd">
      新增应付单
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="payableNo" label="应付单号" width="150" />
      <el-table-column prop="supplierName" label="供应商" width="150" show-overflow-tooltip />
      <el-table-column label="应付金额" width="120" align="right">
        <template #default="{ row }">
          {{ formatMoney(row.amount) }}
        </template>
      </el-table-column>
      <el-table-column label="已付金额" width="120" align="right">
        <template #default="{ row }">
          {{ formatMoney(row.paidAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="未付金额" width="120" align="right">
        <template #default="{ row }">
          <span :class="{ 'text-danger': row.unpaidAmount > 0 }">
            {{ formatMoney(row.unpaidAmount) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="dueDate" label="到期日" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusColor(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <PayableFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      @submit="handleSubmit"
    />
  </div>
</template>

<style scoped>
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
