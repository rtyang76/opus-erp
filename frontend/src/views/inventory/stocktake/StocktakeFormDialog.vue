<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getWarehouses } from '@/api/master-warehouse'
import { getItems } from '@/api/master-item'
import type { MdmWarehouse } from '@/api/master-warehouse'
import type { MdmItem } from '@/api/master-item'
import type { StocktakeCreateRequest } from '@/api/inventory'

interface Props {
  visible: boolean
  title: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [data: StocktakeCreateRequest]
}>()

const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<StocktakeCreateRequest>({
  warehouseId: 0,
  stocktakeDate: new Date().toISOString().split('T')[0],
  remark: '',
  details: [],
})

// 明细行
interface DetailItem {
  itemId: number | undefined
  lotNo: string
  systemQuantity: number | undefined
  actualQuantity: number | undefined
  remark: string
}

const details = ref<DetailItem[]>([])

// 下拉数据
const warehouseList = ref<MdmWarehouse[]>([])
const itemList = ref<MdmItem[]>([])

// 表单验证规则
const rules: FormRules = {
  warehouseId: [
    { required: true, message: '请选择仓库', trigger: 'change' },
  ],
  stocktakeDate: [
    { required: true, message: '请选择盘点日期', trigger: 'change' },
  ],
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

// 加载物料列表
const loadItems = async () => {
  try {
    const { data } = await getItems({ pageNum: 1, pageSize: 100 })
    itemList.value = data.records
  } catch (error) {
    console.error('加载物料列表失败:', error)
  }
}

// 监听 visible 变化，初始化数据
watch(() => props.visible, (val) => {
  if (val) {
    loadWarehouses()
    loadItems()
    // 重置表单
    formData.value = {
      warehouseId: 0,
      stocktakeDate: new Date().toISOString().split('T')[0],
      remark: '',
      details: [],
    }
    details.value = [{ itemId: undefined, lotNo: '', systemQuantity: undefined, actualQuantity: undefined, remark: '' }]
  }
})

// 添加明细行
const addDetail = () => {
  details.value.push({ itemId: undefined, lotNo: '', systemQuantity: undefined, actualQuantity: undefined, remark: '' })
}

// 删除明细行
const removeDetail = (index: number) => {
  if (details.value.length <= 1) {
    ElMessage.warning('至少保留一条明细')
    return
  }
  details.value.splice(index, 1)
}

// 计算差异
const getDiff = (detail: DetailItem) => {
  if (detail.systemQuantity !== undefined && detail.actualQuantity !== undefined) {
    return detail.actualQuantity - detail.systemQuantity
  }
  return 0
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

    // 验证明细行
    const validDetails = details.value.filter(d => d.itemId && d.actualQuantity !== undefined)
    if (validDetails.length === 0) {
      ElMessage.error('请至少添加一条有效的明细记录')
      return
    }

    // 检查明细行数量
    for (let i = 0; i < validDetails.length; i++) {
      if (validDetails[i].actualQuantity === undefined || validDetails[i].actualQuantity! < 0) {
        ElMessage.error(`第 ${i + 1} 行实盘数量必须大于等于 0`)
        return
      }
    }

    // 组装提交数据
    const submitData: StocktakeCreateRequest = {
      warehouseId: formData.value.warehouseId,
      stocktakeDate: formData.value.stocktakeDate,
      remark: formData.value.remark,
      details: validDetails.map(d => ({
        itemId: d.itemId!,
        lotNo: d.lotNo,
        systemQuantity: d.systemQuantity,
        actualQuantity: d.actualQuantity!,
        remark: d.remark,
      })),
    }

    emit('submit', submitData)
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
    width="1000px"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <!-- 主表信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="仓库" prop="warehouseId">
            <el-select v-model="formData.warehouseId" placeholder="请选择仓库" style="width: 100%">
              <el-option
                v-for="item in warehouseList"
                :key="item.id"
                :label="item.warehouseName"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="盘点日期" prop="stocktakeDate">
            <el-date-picker
              v-model="formData.stocktakeDate"
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

      <!-- 明细行 -->
      <el-divider content-position="left">盘点明细</el-divider>

      <div v-for="(detail, index) in details" :key="index" class="detail-row">
        <el-row :gutter="12">
          <el-col :span="5">
            <el-form-item :label="index === 0 ? '物料' : ''" :prop="`details.${index}.itemId`">
              <el-select v-model="detail.itemId" placeholder="选择物料" filterable style="width: 100%">
                <el-option
                  v-for="item in itemList"
                  :key="item.id"
                  :label="`${item.itemCode} - ${item.itemName}`"
                  :value="item.id!"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '批次号' : ''">
              <el-input v-model="detail.lotNo" placeholder="批次号" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '账面数量' : ''">
              <el-input-number v-model="detail.systemQuantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '实盘数量' : ''" :prop="`details.${index}.actualQuantity`">
              <el-input-number v-model="detail.actualQuantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '差异' : ''">
              <el-input :model-value="getDiff(detail)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="index === 0 ? '备注' : ''">
              <el-input v-model="detail.remark" placeholder="备注" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '操作' : ''">
              <el-button type="danger" link @click="removeDetail(index)">删除</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <el-button type="primary" link @click="addDetail" style="margin-top: 8px">
        + 添加明细
      </el-button>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.detail-row {
  border-bottom: 1px dashed #eee;
  padding-bottom: 8px;
  margin-bottom: 8px;
}

.detail-row:last-child {
  border-bottom: none;
  margin-bottom: 0;
}
</style>
