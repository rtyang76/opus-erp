<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getWarehouses } from '@/api/master-warehouse'
import { getSuppliers } from '@/api/master-supplier'
import { getItems } from '@/api/master-item'
import type { MdmWarehouse } from '@/api/master-warehouse'
import type { MdmSupplier } from '@/api/master-supplier'
import type { MdmItem } from '@/api/master-item'
import type { ReceiptCreateRequest } from '@/api/purchase'

interface Props {
  visible: boolean
  title: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [data: ReceiptCreateRequest]
}>()

const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<ReceiptCreateRequest>({
  supplierId: 0,
  warehouseId: 0,
  receiptDate: new Date().toISOString().split('T')[0],
  remark: '',
  details: [],
})

// 明细行
interface DetailItem {
  itemId: number | undefined
  lotNo: string
  quantity: number | undefined
  unitCost: number | undefined
  remark: string
}

const details = ref<DetailItem[]>([])

// 下拉数据
const warehouseList = ref<MdmWarehouse[]>([])
const supplierList = ref<MdmSupplier[]>([])
const itemList = ref<MdmItem[]>([])

// 表单验证规则
const rules: FormRules = {
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' },
  ],
  warehouseId: [
    { required: true, message: '请选择仓库', trigger: 'change' },
  ],
  receiptDate: [
    { required: true, message: '请选择入库日期', trigger: 'change' },
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

// 加载供应商列表
const loadSuppliers = async () => {
  try {
    const { data } = await getSuppliers({ pageNum: 1, pageSize: 100 })
    supplierList.value = data.records
  } catch (error) {
    console.error('加载供应商列表失败:', error)
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
    loadSuppliers()
    loadItems()
    // 重置表单
    formData.value = {
      supplierId: 0,
      warehouseId: 0,
      receiptDate: new Date().toISOString().split('T')[0],
      remark: '',
      details: [],
    }
    details.value = [{ itemId: undefined, lotNo: '', quantity: undefined, unitCost: undefined, remark: '' }]
  }
})

// 添加明细行
const addDetail = () => {
  details.value.push({ itemId: undefined, lotNo: '', quantity: undefined, unitCost: undefined, remark: '' })
}

// 删除明细行
const removeDetail = (index: number) => {
  if (details.value.length <= 1) {
    ElMessage.warning('至少保留一条明细')
    return
  }
  details.value.splice(index, 1)
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
    const validDetails = details.value.filter(d => d.itemId && d.quantity)
    if (validDetails.length === 0) {
      ElMessage.error('请至少添加一条有效的明细记录')
      return
    }

    // 检查明细行数量
    for (let i = 0; i < validDetails.length; i++) {
      if (!validDetails[i].quantity || validDetails[i].quantity! <= 0) {
        ElMessage.error(`第 ${i + 1} 行数量必须大于 0`)
        return
      }
    }

    // 组装提交数据
    const submitData: ReceiptCreateRequest = {
      supplierId: formData.value.supplierId,
      warehouseId: formData.value.warehouseId,
      receiptDate: formData.value.receiptDate,
      remark: formData.value.remark,
      details: validDetails.map(d => ({
        itemId: d.itemId!,
        lotNo: d.lotNo,
        quantity: d.quantity!,
        unitId: 1, // 默认单位
        unitCost: d.unitCost,
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
    width="900px"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <!-- 主表信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
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
        </el-col>
        <el-col :span="12">
          <el-form-item label="入库仓库" prop="warehouseId">
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
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="入库日期" prop="receiptDate">
            <el-date-picker
              v-model="formData.receiptDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注">
            <el-input v-model="formData.remark" placeholder="请输入备注" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 明细行 -->
      <el-divider content-position="left">入库明细</el-divider>

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
            <el-form-item :label="index === 0 ? '数量' : ''" :prop="`details.${index}.quantity`">
              <el-input-number v-model="detail.quantity" :min="0.01" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '单价' : ''">
              <el-input-number v-model="detail.unitCost" :min="0" :precision="4" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="index === 0 ? '备注' : ''">
              <el-input v-model="detail.remark" placeholder="备注" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
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
