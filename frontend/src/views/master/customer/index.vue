<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomers, createCustomer, updateCustomer, deleteCustomer } from '@/api/master-customer'
import type { MdmCustomer, CustomerQuery } from '@/api/master-customer'
import { STATUS_ENABLED } from '@/constants/status'
import CustomerSearchForm from './CustomerSearchForm.vue'
import CustomerFormDialog from './CustomerFormDialog.vue'

// 查询参数
const queryParams = ref<CustomerQuery>({
  pageNum: 1,
  pageSize: 10,
  customerCode: '',
  customerName: '',
  rating: '',
  status: undefined,
})

// 表格数据
const tableData = ref<MdmCustomer[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增客户')

// 表单数据
const formData = ref<Partial<MdmCustomer>>({
  customerCode: '',
  customerName: '',
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
  salesmanId: undefined,
  status: STATUS_ENABLED,
  remark: '',
})

// 查询客户列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getCustomers(queryParams.value)
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
    customerCode: '',
    customerName: '',
    rating: '',
    status: undefined,
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增客户'
  formData.value = {
    customerCode: '',
    customerName: '',
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
    salesmanId: undefined,
    status: STATUS_ENABLED,
    remark: '',
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: MdmCustomer) => {
  dialogTitle.value = '编辑客户'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteCustomer(id)
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
      await updateCustomer(formData.value.id, formData.value as MdmCustomer)
      ElMessage.success('更新成功')
    } else {
      await createCustomer(formData.value as MdmCustomer)
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
    <CustomerSearchForm
      :query-params="queryParams"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 操作按钮 -->
    <el-button type="primary" @click="handleAdd" style="margin-bottom: 16px">
      新增客户
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="customerCode" label="客户编码" width="120" />
      <el-table-column prop="customerName" label="客户名称" width="200" show-overflow-tooltip />
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
    <CustomerFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      @submit="handleSubmit"
    />
  </div>
</template>
