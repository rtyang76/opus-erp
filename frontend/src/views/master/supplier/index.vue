<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSuppliers, createSupplier, updateSupplier, deleteSupplier } from '@/api/master-supplier'
import type { MdmSupplier, SupplierQuery } from '@/api/master-supplier'
import { STATUS_ENABLED } from '@/constants/status'
import SupplierSearchForm from './SupplierSearchForm.vue'
import SupplierFormDialog from './SupplierFormDialog.vue'

// 查询参数
const queryParams = ref<SupplierQuery>({
  pageNum: 1,
  pageSize: 10,
  supplierCode: '',
  supplierName: '',
  rating: '',
  status: undefined,
})

// 表格数据
const tableData = ref<MdmSupplier[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增供应商')

// 表单数据
const formData = ref<Partial<MdmSupplier>>({
  supplierCode: '',
  supplierName: '',
  shortName: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  taxNo: '',
  bankName: '',
  bankAccount: '',
  paymentTerms: '',
  creditLimit: undefined,
  rating: '',
  status: STATUS_ENABLED,
  remark: '',
})

// 查询供应商列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getSuppliers(queryParams.value)
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
    supplierCode: '',
    supplierName: '',
    rating: '',
    status: undefined,
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增供应商'
  formData.value = {
    supplierCode: '',
    supplierName: '',
    shortName: '',
    contactPerson: '',
    phone: '',
    email: '',
    address: '',
    taxNo: '',
    bankName: '',
    bankAccount: '',
    paymentTerms: '',
    creditLimit: undefined,
    rating: '',
    status: STATUS_ENABLED,
    remark: '',
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: MdmSupplier) => {
  dialogTitle.value = '编辑供应商'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该供应商吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteSupplier(id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (formData.value.id) {
      await updateSupplier(formData.value.id, formData.value as MdmSupplier)
      ElMessage.success('更新成功')
    } else {
      await createSupplier(formData.value as MdmSupplier)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
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

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <SupplierSearchForm
      :query-params="queryParams"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 操作按钮 -->
    <el-button type="primary" @click="handleAdd" style="margin-bottom: 16px">
      新增供应商
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="supplierCode" label="供应商编码" width="120" />
      <el-table-column prop="supplierName" label="供应商名称" width="200" show-overflow-tooltip />
      <el-table-column prop="shortName" label="简称" width="100" />
      <el-table-column prop="contactPerson" label="联系人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="120" />
      <el-table-column label="评级" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.rating" :type="row.rating === 'A' ? 'success' : row.rating === 'B' ? 'warning' : 'danger'">
            {{ row.rating }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === STATUS_ENABLED ? 'success' : 'danger'">
            {{ row.status === STATUS_ENABLED ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
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

    <!-- 弹窗 -->
    <SupplierFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      @submit="handleSubmit"
    />
  </div>
</template>
