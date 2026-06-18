<script setup lang="ts">
import type { ItemQuery } from '@/api/master-item'
import { STATUS_OPTIONS, ITEM_TYPE_OPTIONS } from '@/constants/status'

interface Props {
  queryParams: ItemQuery
}

defineProps<Props>()

const emit = defineEmits<{
  search: []
  reset: []
}>()
</script>

<template>
  <div class="search-area">
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="物料编码">
        <el-input v-model="queryParams.itemCode" placeholder="请输入物料编码" clearable />
      </el-form-item>
      <el-form-item label="物料名称">
        <el-input v-model="queryParams.itemName" placeholder="请输入物料名称" clearable />
      </el-form-item>
      <el-form-item label="物料类型">
        <el-select v-model="queryParams.itemType" placeholder="请选择" clearable>
          <el-option
            v-for="item in ITEM_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option
            v-for="item in STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="emit('search')">搜索</el-button>
        <el-button @click="emit('reset')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
