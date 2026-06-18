<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { MdmSupplier } from '@/api/master-supplier'
import { STATUS_ENABLED, STATUS_DISABLED, RATING_OPTIONS } from '@/constants/status'

interface Props {
  visible: boolean
  title: string
  formData: Partial<MdmSupplier>
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: []
}>()

const formRef = ref<FormInstance>()

// 表单校验规则
const rules: FormRules = {
  supplierCode: [
    { required: true, message: '请输入供应商编码', trigger: 'blur' },
    { max: 50, message: '供应商编码长度不能超过50个字符', trigger: 'blur' },
  ],
  supplierName: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' },
    { max: 200, message: '供应商名称长度不能超过200个字符', trigger: 'blur' },
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
          <el-form-item label="供应商编码" prop="supplierCode">
            <el-input v-model="formData.supplierCode" :disabled="!!formData.id" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="供应商名称" prop="supplierName">
            <el-input v-model="formData.supplierName" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="简称">
            <el-input v-model="formData.shortName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系人">
            <el-input v-model="formData.contactPerson" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系电话">
            <el-input v-model="formData.phone" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="地址">
        <el-input v-model="formData.address" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="税号">
            <el-input v-model="formData.taxNo" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="评级">
            <el-select v-model="formData.rating" placeholder="请选择" clearable>
              <el-option
                v-for="item in RATING_OPTIONS"
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
          <el-form-item label="开户银行">
            <el-input v-model="formData.bankName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="银行账号">
            <el-input v-model="formData.bankAccount" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="付款条款">
            <el-input v-model="formData.paymentTerms" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="信用额度">
            <el-input-number v-model="formData.creditLimit" :min="0" :precision="4" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
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
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
