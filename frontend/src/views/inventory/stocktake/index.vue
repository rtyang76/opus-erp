<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStocktakes, createStocktake, auditStocktake, cancelStocktake } from '@/api/inventory'
import type { InvStocktake, StocktakeQuery, StocktakeCreateRequest } from '@/api/inventory'
import { DOC_STATUS_OPTIONS } from '@/constants/status'
import StocktakeFormDialog from './StocktakeFormDialog.vue'

// 查询参数
const queryParams = ref<StocktakeQuery>({
  pageNum: 1,
  pageSize: 10,
  stocktakeNo: '',
  warehouseId: undefined,
  status: '',
})

// 表格数据
const tableData = ref<InvStocktake[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增盘点单')

// 查询盘点单列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getStocktakes(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询盘点单失败，请重试')
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
    stocktakeNo: '',
    warehouseId: undefined,
    status: '',
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增盘点单'
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async (data: StocktakeCreateRequest) => {
  try {
    await createStocktake(data)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 审核
const handleAudit = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要审核该盘点单吗？审核后将自动处理盈亏。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await auditStocktake(id)
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
    await ElMessageBox.confirm('确定要取消该盘点单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await cancelStocktake(id)
    ElMessage.success('取消成功')
    getList()
  } catch (error) {
    console.error('取消失败:', error)
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
const getStatusColor = (status: string): 'success' | 'primary' | 'warning' | 'info' | 'danger' => {
  const colorMap: Record<string, 'success' | 'primary' | 'warning' | 'info' | 'danger'> = {
    DRAFT: 'info',
    AUDITED: 'success',
    CANCELLED: 'danger',
  }
  return colorMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const item = DOC_STATUS_OPTIONS.find(opt => opt.value === status)
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
        <el-form-item label="盘点单号">
          <el-input v-model="queryParams.stocktakeNo" placeholder="请输入盘点单号" clearable />
        </el-form-item>
        <el-form-item label="仓库ID">
          <el-input v-model="queryParams.warehouseId" placeholder="请输入仓库ID" clearable />
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
    <el-button type="primary" style="margin-bottom: 16px" @click="handleAdd">
      新增盘点单
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="stocktakeNo" label="盘点单号" width="150" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="stocktakeDate" label="盘点日期" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusColor(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" type="success" link @click="handleAudit(row.id)">
            审核
          </el-button>
          <el-button v-if="row.status === 'DRAFT'" type="danger" link @click="handleCancel(row.id)">
            取消
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

    <!-- 新增弹窗 -->
    <StocktakeFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      @submit="handleSubmit"
    />
  </div>
</template>
