<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMaterialIssues, auditMaterialIssue, cancelMaterialIssue } from '@/api/production'
import type { PpMaterialIssue, MaterialIssueQuery } from '@/api/production'
import { DOC_STATUS_OPTIONS } from '@/constants/status'

// 领料类型选项
const issueTypeOptions = [
  { label: '领料', value: 'ISSUE' },
  { label: '退料', value: 'RETURN' },
]

// 查询参数
const queryParams = ref<MaterialIssueQuery>({
  pageNum: 1,
  pageSize: 10,
  issueNo: '',
  workOrderId: undefined,
  issueType: '',
  status: '',
})

// 表格数据
const tableData = ref<PpMaterialIssue[]>([])
const total = ref(0)
const loading = ref(false)

// 查询领料单列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getMaterialIssues(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询领料单列表失败，请重试')
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
    issueNo: '',
    workOrderId: undefined,
    issueType: '',
    status: '',
  }
  getList()
}

// 审核
const handleAudit = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要审核该领料单吗？审核后将更新库存。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await auditMaterialIssue(id)
    ElMessage.success('审核成功')
    getList()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核领料单失败，请重试')
  }
}

// 取消
const handleCancel = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该领料单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await cancelMaterialIssue(id)
    ElMessage.success('取消成功')
    getList()
  } catch (error) {
    console.error('取消失败:', error)
    ElMessage.error('取消领料单失败，请重试')
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
    CANCELLED: 'danger',
  }
  return colorMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const item = DOC_STATUS_OPTIONS.find(opt => opt.value === status)
  return item?.label || status
}

// 获取类型标签
const getTypeLabel = (type: string) => {
  const item = issueTypeOptions.find(opt => opt.value === type)
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
        <el-form-item label="领料单号">
          <el-input v-model="queryParams.issueNo" placeholder="请输入领料单号" clearable />
        </el-form-item>
        <el-form-item label="工单ID">
          <el-input v-model="queryParams.workOrderId" placeholder="请输入工单ID" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.issueType" placeholder="请选择" clearable>
            <el-option
              v-for="item in issueTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
      新增领料单（开发中）
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="issueNo" label="领料单号" width="150" />
      <el-table-column prop="workOrderNo" label="工单号" width="150" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="issueDate" label="领料日期" width="120" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.issueType === 'ISSUE' ? 'primary' : 'warning'">
            {{ getTypeLabel(row.issueType) }}
          </el-tag>
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
      <el-table-column label="操作" width="150" fixed="right">
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
  </div>
</template>
