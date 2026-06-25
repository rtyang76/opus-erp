<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getMenuTree } from '@/api/system'
import type { SysMenu } from '@/api/system'

interface Props {
  visible: boolean
  title: string
  formData: Partial<SysRole>
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: []
}>()

interface SysRole {
  id?: number
  roleCode: string
  roleName: string
  description: string
  sortOrder: number
  status: number
  menuIds: number[]
}

const formRef = ref<FormInstance>()

// 菜单树
const menuTree = ref<SysMenu[]>([])

// 表单验证规则
const rules: FormRules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
  ],
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
  ],
}

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const { data } = await getMenuTree()
    menuTree.value = data
  } catch (error) {
    console.error('加载菜单树失败:', error)
  }
}

// 监听 visible 变化
watch(() => props.visible, (val) => {
  if (val) {
    loadMenuTree()
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

// 处理菜单树勾选事件
const handleMenuCheck = (_node: unknown, checked: { checkedKeys: number[] }) => {
  props.formData.menuIds = checked.checkedKeys
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
      <el-form-item label="角色编码" prop="roleCode">
        <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
      </el-form-item>
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
      <el-form-item label="菜单权限">
        <el-tree
          v-if="menuTree.length > 0"
          :data="menuTree"
          :props="{ label: 'menuName', children: 'children' }"
          show-checkbox
          node-key="id"
          :default-checked-keys="formData.menuIds"
          @check="handleMenuCheck"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
