<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getCustomers } from '@/api/master-customer'
import { getItems } from '@/api/master-item'
import type { MdmCustomer } from '@/api/master-customer'
import type { MdmItem } from '@/api/master-item'
import type { OrderCreateRequest } from '@/api/sales'

interface Props {
  visible: boolean
  title: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [data: OrderCreateRequest]
}>()

const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<OrderCreateRequest>({
  customerId: 0,
  orderDate: new Date().toISOString().split('T')[0],
  deliveryDate: '',
  salesmanId: undefined,
  remark: '',
  details: [],
})

// 明细行
interface DetailItem {
  itemId: number | undefined
  specification: string
  quantity: number | undefined
  unitId: number | undefined
  unitPrice: number | undefined
  taxRate: number
  remark: string
}

const details = ref<DetailItem[]>([])

// 下拉数据
const customerList = ref<MdmCustomer[]>([])
const itemList = ref<MdmItem[]>([])

// 表单验证规则
const rules: FormRules = {
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' },
  ],
  orderDate: [
    { required: true, message: '请选择订单日期', trigger: 'change' },
  ],
}

// 加载客户列表
const loadCustomers = async () => {
  try {
    const { data } = await getCustomers({ pageNum: 1, pageSize: 100 })
    customerList.value = data.records
  } catch (error) {
    console.error('加载客户列表失败:', error)
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
    loadCustomers()
    loadItems()
    // 重置表单
    formData.value = {
      customerId: 0,
      orderDate: new Date().toISOString().split('T')[0],
      deliveryDate: '',
      salesmanId: undefined,
      remark: '',
      details: [],
    }
    details.value = [{
      itemId: undefined,
      specification: '',
      quantity: undefined,
      unitId: undefined,
      unitPrice: undefined,
      taxRate: 13,
      remark: '',
    }]
  }
})

// 添加明细行
const addDetail = () => {
  details.value.push({
    itemId: undefined,
    specification: '',
    quantity: undefined,
    unitId: undefined,
    unitPrice: undefined,
    taxRate: 13,
    remark: '',
  })
}

// 删除明细行
const removeDetail = (index: number) => {
  if (details.value.length <= 1) {
    ElMessage.warning('至少保留一条明细')
    return
  }
  details.value.splice(index, 1)
}

// 计算金额
const getAmount = (detail: DetailItem) => {
  if (detail.quantity && detail.unitPrice) {
    return (detail.quantity * detail.unitPrice).toFixed(2)
  }
  return '0.00'
}

// 计算税额
const getTaxAmount = (detail: DetailItem) => {
  if (detail.quantity && detail.unitPrice && detail.taxRate) {
    const amount = detail.quantity * detail.unitPrice
    return (amount * detail.taxRate / 100).toFixed(2)
  }
  return '0.00'
}

// 计算总金额
const totalAmount = computed(() => {
  return details.value.reduce((sum, d) => {
    if (d.quantity && d.unitPrice) {
      return sum + d.quantity * d.unitPrice
    }
    return sum
  }, 0).toFixed(2)
})

// 计算总税额
const totalTaxAmount = computed(() => {
  return details.value.reduce((sum, d) => {
    if (d.quantity && d.unitPrice && d.taxRate) {
      const amount = d.quantity * d.unitPrice
      return sum + amount * d.taxRate / 100
    }
    return sum
  }, 0).toFixed(2)
})

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
    const validDetails = details.value.filter(d => d.itemId && d.quantity && d.unitPrice)
    if (validDetails.length === 0) {
      ElMessage.error('请至少添加一条有效的明细记录')
      return
    }

    // 检查明细行
    for (let i = 0; i < validDetails.length; i++) {
      if (!validDetails[i].quantity || validDetails[i].quantity! <= 0) {
        ElMessage.error(`第 ${i + 1} 行数量必须大于 0`)
        return
      }
      if (!validDetails[i].unitPrice || validDetails[i].unitPrice! <= 0) {
        ElMessage.error(`第 ${i + 1} 行单价必须大于 0`)
        return
      }
    }

    // 组装提交数据
    const submitData: OrderCreateRequest = {
      customerId: formData.value.customerId,
      orderDate: formData.value.orderDate,
      deliveryDate: formData.value.deliveryDate,
      salesmanId: formData.value.salesmanId,
      remark: formData.value.remark,
      details: validDetails.map(d => ({
        itemId: d.itemId!,
        specification: d.specification,
        quantity: d.quantity!,
        unitId: d.unitId!,
        unitPrice: d.unitPrice!,
        taxRate: d.taxRate,
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
    width="1100px"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <!-- 主表信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="客户" prop="customerId">
            <el-select v-model="formData.customerId" placeholder="请选择客户" filterable style="width: 100%">
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.customerName"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单日期" prop="orderDate">
            <el-date-picker
              v-model="formData.orderDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="交货日期">
            <el-date-picker
              v-model="formData.deliveryDate"
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
      <el-divider content-position="left">订单明细</el-divider>

      <div v-for="(detail, index) in details" :key="index" class="detail-row">
        <el-row :gutter="8">
          <el-col :span="4">
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
          <el-col :span="2">
            <el-form-item :label="index === 0 ? '规格' : ''">
              <el-input v-model="detail.specification" placeholder="规格" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item :label="index === 0 ? '数量' : ''" :prop="`details.${index}.quantity`">
              <el-input-number v-model="detail.quantity" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item :label="index === 0 ? '单价' : ''" :prop="`details.${index}.unitPrice`">
              <el-input-number v-model="detail.unitPrice" :min="0" :precision="4" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item :label="index === 0 ? '税率%' : ''">
              <el-input-number v-model="detail.taxRate" :min="0" :max="100" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item :label="index === 0 ? '金额' : ''">
              <el-input :model-value="getAmount(detail)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item :label="index === 0 ? '税额' : ''">
              <el-input :model-value="getTaxAmount(detail)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item :label="index === 0 ? '备注' : ''">
              <el-input v-model="detail.remark" placeholder="备注" />
            </el-form-item>
          </el-col>
          <el-col :span="1">
            <el-form-item :label="index === 0 ? '操作' : ''">
              <el-button type="danger" link @click="removeDetail(index)">删除</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <el-row :gutter="20" style="margin-top: 12px">
        <el-col :span="12">
          <el-button type="primary" link @click="addDetail">
            + 添加明细
          </el-button>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <span style="margin-right: 20px">总金额: <strong>{{ totalAmount }}</strong></span>
          <span>总税额: <strong>{{ totalTaxAmount }}</strong></span>
        </el-col>
      </el-row>
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
