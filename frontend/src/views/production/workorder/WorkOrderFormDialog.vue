<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getBoms } from '@/api/production-bom'
import { getItems } from '@/api/master-item'
import { getWarehouses } from '@/api/master-warehouse'
import type { PpBom } from '@/api/production-bom'
import type { MdmItem } from '@/api/master-item'
import type { MdmWarehouse } from '@/api/master-warehouse'
import type { WorkOrderCreateRequest } from '@/api/production-workorder'

interface Props {
  visible: boolean
  title: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [data: WorkOrderCreateRequest]
}>()

const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<WorkOrderCreateRequest>({
  bomId: 0,
  itemId: 0,
  quantity: 1,
  warehouseId: undefined,
  planStartDate: '',
  planEndDate: '',
  remark: '',
})

// 下拉数据
const bomList = ref<PpBom[]>([])
const itemList = ref<MdmItem[]>([])
const warehouseList = ref<MdmWarehouse[]>([])

// 表单验证规则
const rules: FormRules = {
  bomId: [
    { required: true, message: '请选择BOM', trigger: 'change' },
  ],
  itemId: [
    { required: true, message: '请选择产品', trigger: 'change' },
  ],
  quantity: [
    { required: true, message: '请输入计划数量', trigger: 'blur' },
  ],
}

// 加载 BOM 列表
const loadBoms = async () => {
  try {
    const { data } = await getBoms({ pageNum: 1, pageSize: 100 })
    bomList.value = data.records
  } catch (error) {
    console.error('加载BOM列表失败:', error)
  }
}

// 加载物料列表
const loadItems = async () => {
  try {
    const { data } = await getItems({ pageNum: 1, pageSize: 100 })
    itemList.value = data.records
  } catch (error) {
    console.error('加载物料列表失败:', error)
  }
}

// 加载仓库列表
const loadWarehouses = async () => {
  try {
    const { data } = await getWarehouses({ pageNum: 1, pageSize: 100 })
    warehouseList.value = data.records
  } catch (error) {
    console.error('加载仓库列表失败:', error)
  }
}

// 监听 visible 变化，初始化数据
watch(() => props.visible, (val) => {
  if (val) {
    loadBoms()
    loadItems()
    loadWarehouses()
    // 重置表单
    formData.value = {
      bomId: 0,
      itemId: 0,
      quantity: 1,
      warehouseId: undefined,
      planStartDate: '',
      planEndDate: '',
      remark: '',
    }
  }
})

// BOM 选择变化
const handleBomChange = (bomId: number) => {
  const bom = bomList.value.find(b => b.id === bomId)
  if (bom) {
    formData.value.itemId = bom.itemId
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (!valid) return

    // 检查数量
    if (!formData.value.quantity || formData.value.quantity <= 0) {
      ElMessage.error('计划数量必须大于 0')
      return
    }

    emit('submit', { ...formData.value })
  })
}

defineExpose({
  resetFields: () => formRef.value?.resetFields(),
})
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    width="600px"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="BOM" prop="bomId">
        <el-select
          v-model="formData.bomId"
          placeholder="请选择BOM"
          filterable
          style="width: 100%"
          @change="handleBomChange"
        >
          <el-option
            v-for="item in bomList"
            :key="item.id"
            :label="`${item.bomCode} - ${item.itemName}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="产品" prop="itemId">
        <el-select v-model="formData.itemId" placeholder="请选择产品" filterable style="width: 100%">
          <el-option
            v-for="item in itemList"
            :key="item.id"
            :label="`${item.itemCode} - ${item.itemName}`"
            :value="item.id!"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="计划数量" prop="quantity">
        <el-input-number v-model="formData.quantity" :min="1" :precision="2" style="width: 100%" />
      </el-form-item>
      <el-form-item label="入库仓库">
        <el-select v-model="formData.warehouseId" placeholder="请选择入库仓库" style="width: 100%">
          <el-option
            v-for="item in warehouseList"
            :key="item.id"
            :label="item.warehouseName"
            :value="item.id!"
          />
        </el-select>
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="计划开始">
            <el-date-picker
              v-model="formData.planStartDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划结束">
            <el-date-picker
              v-model="formData.planEndDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
