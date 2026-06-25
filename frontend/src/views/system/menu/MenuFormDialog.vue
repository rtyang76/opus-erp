<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getMenuTree } from '@/api/system'
import type { SysMenu } from '@/api/system'

interface Props {
  visible: boolean
  title: string
  formData: Partial<SysMenu>
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: []
}>()

const formRef = ref<FormInstance>()

// 菜单树（用于选择父菜单）
const menuTree = ref<SysMenu[]>([])

// 菜单类型选项
const menuTypeOptions = [
  { label: '目录', value: 'M' },
  { label: '菜单', value: 'C' },
  { label: '按钮', value: 'B' },
]

// 表单验证规则
const rules: FormRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' },
  ],
}

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const { data } = await getMenuTree()
    menuTree.value = [{ id: 0, menuName: '顶级菜单', children: data } as SysMenu]
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
      <el-form-item label="父菜单">
        <el-tree-select
          v-model="formData.parentId"
          :data="menuTree"
          :props="{ label: 'menuName', children: 'children', value: 'id' }"
          check-strictly
          :render-after-expand="false"
          placeholder="请选择父菜单"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="formData.menuType">
          <el-radio-button
            v-for="item in menuTypeOptions"
            :key="item.value"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="formData.menuName" placeholder="请输入菜单名称" />
      </el-form-item>
      <el-form-item v-if="formData.menuType !== 'B'" label="路由地址">
        <el-input v-model="formData.path" placeholder="请输入路由地址" />
      </el-form-item>
      <el-form-item v-if="formData.menuType === 'C'" label="组件路径">
        <el-input v-model="formData.component" placeholder="请输入组件路径" />
      </el-form-item>
      <el-form-item v-if="formData.menuType !== 'M'" label="权限标识">
        <el-input v-model="formData.perms" placeholder="请输入权限标识" />
      </el-form-item>
      <el-form-item v-if="formData.menuType !== 'B'" label="图标">
        <el-input v-model="formData.icon" placeholder="请输入图标名称" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="formData.sortOrder" :min="0" :max="999" style="width: 100%" />
      </el-form-item>
      <el-form-item v-if="formData.menuType !== 'B'" label="显示状态">
        <el-radio-group v-model="formData.visible">
          <el-radio :value="1">显示</el-radio>
          <el-radio :value="0">隐藏</el-radio>
        </el-radio-group>
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
