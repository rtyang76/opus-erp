<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getItems, createItem, updateItem, deleteItem } from '@/api/master-item'
import type { MdmItem, ItemQuery } from '@/api/master-item'
import { STATUS_ENABLED, ITEM_TYPE_OPTIONS } from '@/constants/status'
import ItemSearchForm from './ItemSearchForm.vue'
import ItemFormDialog from './ItemFormDialog.vue'

// 查询参数
const queryParams = ref<ItemQuery>({
  pageNum: 1,
  pageSize: 10,
  itemCode: '',
  itemName: '',
  categoryId: undefined,
  itemType: '',
  status: undefined,
})

// 表格数据
const tableData = ref<MdmItem[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增物料')

// 表单数据
const formData = ref<Partial<MdmItem>>({
  itemCode: '',
  itemName: '',
  categoryId: undefined,
  specification: '',
  unitId: undefined,
  auxUnitId: undefined,
  unitFactor: undefined,
  safetyStock: undefined,
  abcClass: '',
  itemType: '',
  defaultWarehouseId: undefined,
  shelfLifeDays: undefined,
  status: STATUS_ENABLED,
  remark: '',
})

// 使用常量定义物料类型选项
const itemTypeOptions = ITEM_TYPE_OPTIONS

// 查询物料列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getItems(queryParams.value)
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
    itemCode: '',
    itemName: '',
    categoryId: undefined,
    itemType: '',
    status: undefined,
  }
  getList()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增物料'
  formData.value = {
    itemCode: '',
    itemName: '',
    categoryId: undefined,
    specification: '',
    unitId: undefined,
    auxUnitId: undefined,
    unitFactor: undefined,
    safetyStock: undefined,
    abcClass: '',
    itemType: '',
    defaultWarehouseId: undefined,
    shelfLifeDays: undefined,
    status: STATUS_ENABLED,
    remark: '',
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: MdmItem) => {
  dialogTitle.value = '编辑物料'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该物料吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteItem(id)
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
      await updateItem(formData.value.id, formData.value as MdmItem)
      ElMessage.success('更新成功')
    } else {
      await createItem(formData.value as MdmItem)
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
    <ItemSearchForm
      :query-params="queryParams"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 操作按钮 -->
    <el-button type="primary" @click="handleAdd" style="margin-bottom: 16px">
      新增物料
    </el-button>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border>
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="200" show-overflow-tooltip />
      <el-table-column prop="specification" label="规格型号" width="150" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="unitName" label="主单位" width="80" />
      <el-table-column label="物料类型" width="100">
        <template #default="{ row }">
          {{ itemTypeOptions.find(item => item.value === row.itemType)?.label || row.itemType }}
        </template>
      </el-table-column>
      <el-table-column prop="safetyStock" label="安全库存" width="100" />
      <el-table-column label="ABC分类" width="80">
        <template #default="{ row }">
          {{ row.abcClass || '-' }}
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
    <ItemFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      @submit="handleSubmit"
    />
  </div>
</template>
