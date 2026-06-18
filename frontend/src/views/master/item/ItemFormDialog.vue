<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { MdmItem } from '@/api/master-item'
import { STATUS_ENABLED, STATUS_DISABLED, ITEM_TYPE_OPTIONS, ABC_CLASS_OPTIONS } from '@/constants/status'

interface Props {
  visible: boolean
  title: string
  formData: Partial<MdmItem>
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: []
}>()

const formRef = ref<FormInstance>()

// 表单校验规则
const rules: FormRules = {
  itemCode: [
    { required: true, message: '请输入物料编码', trigger: 'blur' },
    { max: 50, message: '物料编码长度不能超过50个字符', trigger: 'blur' },
  ],
  itemName: [
    { required: true, message: '请输入物料名称', trigger: 'blur' },
    { max: 200, message: '物料名称长度不能超过200个字符', trigger: 'blur' },
  ],
}

// 使用常量定义
const itemTypeOptions = ITEM_TYPE_OPTIONS
const abcClassOptions = ABC_CLASS_OPTIONS

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

// 暴露表单引用供父组件重置
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="物料编码" prop="itemCode">
            <el-input v-model="formData.itemCode" :disabled="!!formData.id" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="物料名称" prop="itemName">
            <el-input v-model="formData.itemName" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="规格型号">
            <el-input v-model="formData.specification" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="物料类型">
            <el-select v-model="formData.itemType" placeholder="请选择">
              <el-option
                v-for="item in itemTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="安全库存">
            <el-input-number v-model="formData.safetyStock" :min="0" :precision="4" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="ABC分类">
            <el-select v-model="formData.abcClass" placeholder="请选择" clearable>
              <el-option
                v-for="item in abcClassOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="保质期(天)">
            <el-input-number v-model="formData.shelfLifeDays" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-radio-group v-model="formData.status">
              <el-radio :value="STATUS_ENABLED">启用</el-radio>
              <el-radio :value="STATUS_DISABLED">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
