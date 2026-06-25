<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBoms, createBom, deleteBom } from '@/api/production'
import type { PpBom, BomQuery, BomCreateRequest } from '@/api/production'
import { STATUS_OPTIONS } from '@/constants/status'
import BomFormDialog from './BomFormDialog.vue'

// 查询参数
const queryParams = ref<BomQuery>({
  pageNum: 1,
  pageSize: 10,
  bomCode: '',
  itemId: undefined,
  status: undefined,
})

// 表格数据
const tableData = ref<PpBom[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增BOM')

// 查询 BOM 列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getBoms(queryParams.value)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询BOM列表失败，请重试')
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
    bomCode: '',
    itemId: undefined,
    status: undefined,
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增BOM'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (_row: PpBom) => {
  dialogTitle.value = '编辑BOM'
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async (data: BomCreateRequest) => {
  try {
    await createBom(data)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该BOM吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteBom(id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除BOM失败，请重试')
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

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="BOM编码">
          <el-input v-model="queryParams.bomCode" placeholder="请输入BOM编码" clearable />
        </el-form-item>
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.itemId" placeholder="请输入物料ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option
              v-for="item in STATUS_OPTIONS"
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
      新增BOM
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="bomCode" label="BOM编码" width="120" />
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column prop="specification" label="规格型号" width="120" />
      <el-table-column prop="version" label="版本" width="80" />
      <el-table-column prop="baseQuantity" label="基准数量" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
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

    <!-- 新增弹窗 -->
    <BomFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      @submit="handleSubmit"
    />
  </div>
</template>
