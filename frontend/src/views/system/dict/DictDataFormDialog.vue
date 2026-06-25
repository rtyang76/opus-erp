<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { SysDictData } from '@/api/system'

interface Props {
  visible: boolean
  title: string
  formData: Partial<SysDictData>
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: []
}>()

const formRef = ref<FormInstance>()

// 表单验证规则
const rules: FormRules = {
  dictLabel: [
    { required: true, message: '请输入字典标签', trigger: 'blur' },
  ],
  dictValue: [
    { required: true, message: '请输入字典值', trigger: 'blur' },
  ],
}

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
    width="500px"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="字典标签" prop="dictLabel">
        <el-input v-model="formData.dictLabel" placeholder="请输入字典标签" />
      </el-form-item>
      <el-form-item label="字典值" prop="dictValue">
        <el-input v-model="formData.dictValue" placeholder="请输入字典值" />
      </el-form-item>
      <el-form-item label="标签颜色">
        <el-select v-model="formData.dictColor" placeholder="请选择颜色" clearable>
          <el-option label="默认" value="" />
          <el-option label="成功" value="success" />
          <el-option label="警告" value="warning" />
          <el-option label="危险" value="danger" />
          <el-option label="信息" value="info" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="formData.sortOrder" :min="0" :max="999" style="width: 100%" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
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
