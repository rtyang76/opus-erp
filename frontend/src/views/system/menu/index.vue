<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/system'
import type { SysMenu } from '@/api/system'
import MenuFormDialog from './MenuFormDialog.vue'

// 表格数据
const tableData = ref<SysMenu[]>([])
const loading = ref(false)

// 弹窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formData = ref<Partial<SysMenu>>({
  parentId: 0,
  menuName: '',
  menuType: 'M',
  path: '',
  component: '',
  perms: '',
  icon: '',
  sortOrder: 0,
  visible: 1,
  status: 1,
  remark: '',
})

// 查询菜单树
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getMenuTree()
    tableData.value = data
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询菜单列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = (parentId?: number) => {
  dialogTitle.value = '新增菜单'
  formData.value = {
    parentId: parentId || 0,
    menuName: '',
    menuType: 'M',
    path: '',
    component: '',
    perms: '',
    icon: '',
    sortOrder: 0,
    visible: 1,
    status: 1,
    remark: '',
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: SysMenu) => {
  dialogTitle.value = '编辑菜单'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (formData.value.id) {
      await updateMenu(formData.value.id, formData.value)
      ElMessage.success('更新成功')
    } else {
      await createMenu(formData.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？删除后子菜单也将被删除。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteMenu(id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除菜单失败，请重试')
  }
}

// 获取菜单类型标签
const getMenuTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    M: '目录',
    C: '菜单',
    B: '按钮',
  }
  return map[type] || type
}

// 获取菜单类型颜色
const getMenuTypeColor = (type: string): 'success' | 'primary' | 'warning' | 'info' | 'danger' => {
  const map: Record<string, 'success' | 'primary' | 'warning' | 'info' | 'danger'> = {
    M: 'primary',
    C: 'success',
    B: 'warning',
  }
  return map[type] || 'info'
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page-container">
    <!-- 操作按钮 -->
    <el-button type="primary" style="margin-bottom: 16px" @click="handleAdd()">
      新增菜单
    </el-button>

    <!-- 表格 -->
    <el-table
      :data="tableData"
      v-loading="loading"
      stripe
      border
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="menuName" label="菜单名称" width="200" />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="getMenuTypeColor(row.menuType)">
            {{ getMenuTypeLabel(row.menuType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由地址" width="150" />
      <el-table-column prop="component" label="组件路径" width="150" />
      <el-table-column prop="perms" label="权限标识" width="150" />
      <el-table-column prop="icon" label="图标" width="80" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="显示" width="80">
        <template #default="{ row }">
          <el-tag :type="row.visible === 1 ? 'success' : 'info'">
            {{ row.visible === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleAdd(row.id)">新增子菜单</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <MenuFormDialog
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form-data="formData"
      @submit="handleSubmit"
    />
  </div>
</template>
