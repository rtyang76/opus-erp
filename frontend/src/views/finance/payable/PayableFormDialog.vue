<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getSuppliers } from '@/api/master-supplier'
import type { MdmSupplier } from '@/api/master-supplier'

interface Props {
  visible: boolean
  title: string
  formData: Partial<FinPayable>
}

interface FinPayable {
  id?: number
  payableNo: string
  supplierId: number
  amount: number
  paidAmount: number
  currency: string
  dueDate: string
  status: string
  remark: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: []
}>()

const formRef = ref<FormInstance>()

// 下拉数据
const supplierList = ref<MdmSupplier[]>([])

// 表单验证规则
const rules: FormRules = {
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' },
  ],
  amount: [
    { required: true, message: '请输入应付金额', trigger: 'blur' },
  ],
}

// 加载供应商列表
const loadSuppliers = async () => {
  try {
    const { data } = await getSuppliers({ pageNum: 1, pageSize: 100 })
    supplierList.value = data.records
  } catch (error) {
    console.error('加载供应商列表失败:', error)
  }
}

// 监听 visible 变化
watch(() => props.visible, (val) => {
  if (val) {
    loadSuppliers()
  }
})

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (valid) {
      emit('submit')
    }
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
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="formData.supplierId" placeholder="请选择供应商" filterable style="width: 100%">
          <el-option
            v-for="item in supplierList"
            :key="item.id"
            :label="item.supplierName"
            :value="item.id!"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="应付金额" prop="amount">
        <el-input-number v-model="formData.amount" :min="0" :precision="2" style="width: 100%" />
      </el-form-item>
      <el-form-item label="币种">
        <el-select v-model="formData.currency" style="width: 100%">
          <el-option label="人民币" value="CNY" />
          <el-option label="美元" value="USD" />
          <el-option label="欧元" value="EUR" />
        </el-select>
      </el-form-item>
      <el-form-item label="到期日">
        <el-date-picker
          v-model="formData.dueDate"
          type="date"
          placeholder="选择日期"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="formData.status" style="width: 100%">
          <el-option label="待付款" value="PENDING" />
          <el-option label="部分付款" value="PARTIAL" />
          <el-option label="已付完" value="PAID" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
