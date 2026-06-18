<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getWarehouses, createWarehouse, updateWarehouse, deleteWarehouse } from '@/api/master'
import type { MdmWarehouse } from '@/api/master'
import { STATUS_ENABLED, STATUS_DISABLED, STATUS_OPTIONS } from '@/constants/status'

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  warehouseCode: '',
  warehouseName: '',
  warehouseType: '',
  status: undefined as number | undefined,
})

// 表格数据
const tableData = ref<MdmWarehouse[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增仓库')
const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<MdmWarehouse>({
  warehouseCode: '',
  warehouseName: '',
  warehouseType: '',
  address: '',
  manager: '',
  phone: '',
  status: STATUS_ENABLED,
  remark: '',
})

// 表单校验规则
const rules: FormRules = {
  warehouseCode: [
    { required: true, message: '请输入仓库编码', trigger: 'blur' },
    { max: 50, message: '仓库编码长度不能超过50个字符', trigger: 'blur' },
  ],
  warehouseName: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' },
    { max: 100, message: '仓库名称长度不能超过100个字符', trigger: 'blur' },
  ],
}

// 查询仓库列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getWarehouses(queryParams.value)
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
    warehouseCode: '',
    warehouseName: '',
    warehouseType: '',
    status: undefined,
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增仓库'
  formData.value = {
    warehouseCode: '',
    warehouseName: '',
    warehouseType: '',
    address: '',
    manager: '',
    phone: '',
    status: STATUS_ENABLED,
    remark: '',
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: MdmWarehouse) => {
  dialogTitle.value = '编辑仓库'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该仓库吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteWarehouse(id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (formData.value.id) {
        await updateWarehouse(formData.value.id, formData.value)
        ElMessage.success('更新成功')
      } else {
        await createWarehouse(formData.value)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  })
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

// 仓库类型选项
const warehouseTypeOptions = [
  { label: '原料仓', value: 'RAW' },
  { label: '半成品仓', value: 'SEMI' },
  { label: '成品仓', value: 'FINISHED' },
  { label: '退货仓', value: 'RETURN' },
  { label: '不良品仓', value: 'DEFECTIVE' },
]

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="仓库编码">
          <el-input v-model="queryParams.warehouseCode" placeholder="请输入仓库编码" clearable />
        </el-form-item>
        <el-form-item label="仓库名称">
          <el-input v-model="queryParams.warehouseName" placeholder="请输入仓库名称" clearable />
        </el-form-item>
        <el-form-item label="仓库类型">
          <el-select v-model="queryParams.warehouseType" placeholder="请选择" clearable>
            <el-option
              v-for="item in warehouseTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
    <el-button type="primary" @click="handleAdd" style="margin-bottom: 16px">
      新增仓库
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="warehouseCode" label="仓库编码" width="120" />
      <el-table-column prop="warehouseName" label="仓库名称" width="180" />
      <el-table-column label="仓库类型" width="100">
        <template #default="{ row }">
          {{ warehouseTypeOptions.find(item => item.value === row.warehouseType)?.label || row.warehouseType }}
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" width="200" show-overflow-tooltip />
      <el-table-column prop="manager" label="负责人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="120" />
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="仓库编码" prop="warehouseCode">
          <el-input v-model="formData.warehouseCode" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="formData.warehouseName" />
        </el-form-item>
        <el-form-item label="仓库类型">
          <el-select v-model="formData.warehouseType" placeholder="请选择">
            <el-option
              v-for="item in warehouseTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="formData.address" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.manager" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="STATUS_ENABLED">启用</el-radio>
            <el-radio :value="STATUS_DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
