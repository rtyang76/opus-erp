<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDictTypes,
  createDictType,
  updateDictType,
  deleteDictType,
  getDictDataByType,
  createDictData,
  updateDictData,
  deleteDictData
} from '@/api/system'
import type { SysDictType, SysDictData } from '@/api/system'
import DictTypeFormDialog from './DictTypeFormDialog.vue'
import DictDataFormDialog from './DictDataFormDialog.vue'

// 字典类型数据
const dictTypes = ref<SysDictType[]>([])
const loading = ref(false)

// 当前选中的字典类型
const selectedType = ref<SysDictType | null>(null)

// 字典数据
const dictDataList = ref<SysDictData[]>([])
const dataLoading = ref(false)

// 字典类型弹窗控制
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('新增字典类型')
const typeFormData = ref<Partial<SysDictType>>({
  dictType: '',
  dictName: '',
  status: 1,
  remark: '',
})

// 字典数据弹窗控制
const dataDialogVisible = ref(false)
const dataDialogTitle = ref('新增字典数据')
const dataFormData = ref<Partial<SysDictData>>({
  dictType: '',
  dictLabel: '',
  dictValue: '',
  dictColor: '',
  sortOrder: 0,
  status: 1,
  remark: '',
})

// 查询字典类型列表
const getTypes = async () => {
  loading.value = true
  try {
    const { data } = await getDictTypes()
    dictTypes.value = data
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询字典类型失败，请重试')
  } finally {
    loading.value = false
  }
}

// 查询字典数据
const getDictData = async (dictType: string) => {
  dataLoading.value = true
  try {
    const { data } = await getDictDataByType(dictType)
    dictDataList.value = data
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询字典数据失败，请重试')
  } finally {
    dataLoading.value = false
  }
}

// 选择字典类型
const handleSelectType = (type: SysDictType) => {
  selectedType.value = type
  getDictData(type.dictType)
}

// 新增字典类型
const handleAddType = () => {
  typeDialogTitle.value = '新增字典类型'
  typeFormData.value = {
    dictType: '',
    dictName: '',
    status: 1,
    remark: '',
  }
  typeDialogVisible.value = true
}

// 编辑字典类型
const handleEditType = (type: SysDictType) => {
  typeDialogTitle.value = '编辑字典类型'
  typeFormData.value = { ...type }
  typeDialogVisible.value = true
}

// 提交字典类型
const handleSubmitType = async () => {
  try {
    if (typeFormData.value.id) {
      await updateDictType(typeFormData.value.id, typeFormData.value)
      ElMessage.success('更新成功')
    } else {
      await createDictType(typeFormData.value)
      ElMessage.success('创建成功')
    }
    typeDialogVisible.value = false
    getTypes()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 删除字典类型
const handleDeleteType = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典类型吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteDictType(id)
    ElMessage.success('删除成功')
    if (selectedType.value?.id === id) {
      selectedType.value = null
      dictDataList.value = []
    }
    getTypes()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除字典类型失败，请重试')
  }
}

// 新增字典数据
const handleAddData = () => {
  if (!selectedType.value) {
    ElMessage.warning('请先选择字典类型')
    return
  }
  dataDialogTitle.value = '新增字典数据'
  dataFormData.value = {
    dictType: selectedType.value.dictType,
    dictLabel: '',
    dictValue: '',
    dictColor: '',
    sortOrder: 0,
    status: 1,
    remark: '',
  }
  dataDialogVisible.value = true
}

// 编辑字典数据
const handleEditData = (data: SysDictData) => {
  dataDialogTitle.value = '编辑字典数据'
  dataFormData.value = { ...data }
  dataDialogVisible.value = true
}

// 提交字典数据
const handleSubmitData = async () => {
  try {
    if (dataFormData.value.id) {
      await updateDictData(dataFormData.value.id, dataFormData.value)
      ElMessage.success('更新成功')
    } else {
      await createDictData(dataFormData.value)
      ElMessage.success('创建成功')
    }
    dataDialogVisible.value = false
    if (selectedType.value) {
      getDictData(selectedType.value.dictType)
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 删除字典数据
const handleDeleteData = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteDictData(id)
    ElMessage.success('删除成功')
    if (selectedType.value) {
      getDictData(selectedType.value.dictType)
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除字典数据失败，请重试')
  }
}

onMounted(() => {
  getTypes()
})
</script>

<template>
  <div class="page-container">
    <el-row :gutter="20">
      <!-- 左侧：字典类型列表 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>字典类型</span>
              <el-button type="primary" size="small" @click="handleAddType">新增</el-button>
            </div>
          </template>
          <el-table
            :data="dictTypes"
            v-loading="loading"
            stripe
            border
            highlight-current-row
            @current-change="handleSelectType"
            style="height: 500px"
          >
            <el-table-column prop="dictType" label="字典类型" width="150" show-overflow-tooltip />
            <el-table-column prop="dictName" label="字典名称" width="120" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click.stop="handleEditType(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click.stop="handleDeleteType(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：字典数据列表 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>字典数据 {{ selectedType ? `- ${selectedType.dictName}` : '' }}</span>
              <el-button type="primary" size="small" @click="handleAddData" :disabled="!selectedType">新增</el-button>
            </div>
          </template>
          <el-table
            :data="dictDataList"
            v-loading="dataLoading"
            stripe
            border
            style="height: 500px"
          >
            <el-table-column prop="dictLabel" label="字典标签" width="150" />
            <el-table-column prop="dictValue" label="字典值" width="120" />
            <el-table-column label="标签颜色" width="100">
              <template #default="{ row }">
                <el-tag :type="row.dictColor || 'info'" size="small">
                  {{ row.dictColor || '默认' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleEditData(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteData(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型弹窗 -->
    <DictTypeFormDialog
      v-model:visible="typeDialogVisible"
      :title="typeDialogTitle"
      :form-data="typeFormData"
      @submit="handleSubmitType"
    />

    <!-- 字典数据弹窗 -->
    <DictDataFormDialog
      v-model:visible="dataDialogVisible"
      :title="dataDialogTitle"
      :form-data="dataFormData"
      @submit="handleSubmitData"
    />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
