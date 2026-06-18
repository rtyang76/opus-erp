<script setup lang="ts">
import type { SupplierQuery } from '@/api/master-supplier'
import { STATUS_OPTIONS, RATING_OPTIONS } from '@/constants/status'

interface Props {
  queryParams: SupplierQuery
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
      <el-form-item label="供应商编码">
        <el-input v-model="queryParams.supplierCode" placeholder="请输入供应商编码" clearable />
      </el-form-item>
      <el-form-item label="供应商名称">
        <el-input v-model="queryParams.supplierName" placeholder="请输入供应商名称" clearable />
      </el-form-item>
      <el-form-item label="评级">
        <el-select v-model="queryParams.rating" placeholder="请选择" clearable>
          <el-option
            v-for="item in RATING_OPTIONS"
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
