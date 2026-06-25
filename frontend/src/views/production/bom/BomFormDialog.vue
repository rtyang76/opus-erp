<script setup lang="ts">
import { ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getItems } from '@/api/master-item'
import type { MdmItem } from '@/api/master-item'
import type { BomCreateRequest } from '@/api/production-bom'

interface Props {
  visible: boolean
  title: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [data: BomCreateRequest]
}>()

const formRef = ref<FormInstance>()

// 表单数据
const formData = ref<BomCreateRequest>({
  bomCode: '',
  itemId: 0,
  version: '1.0',
  baseQuantity: 1,
  status: 1,
  remark: '',
  details: [],
})

// 明细行
interface DetailItem {
  itemId: number | undefined
  quantity: number | undefined
  lossRate: number
  remark: string
}

const details = ref<DetailItem[]>([])

// 下拉数据
const itemList = ref<MdmItem[]>([])

// 表单验证规则
const rules: FormRules = {
  bomCode: [
    { required: true, message: '请输入BOM编码', trigger: 'blur' },
  ],
  itemId: [
    { required: true, message: '请选择母件物料', trigger: 'change' },
  ],
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
    loadItems()
    // 重置表单
    formData.value = {
      bomCode: '',
      itemId: 0,
      version: '1.0',
      baseQuantity: 1,
      status: 1,
      remark: '',
      details: [],
    }
    details.value = [{ itemId: undefined, quantity: undefined, lossRate: 0, remark: '' }]
  }
})

// 添加明细行
const addDetail = () => {
  details.value.push({ itemId: undefined, quantity: undefined, lossRate: 0, remark: '' })
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
      ElMessage.error('请至少添加一条有效的子件明细')
      return
    }

    // 检查明细行
    for (let i = 0; i < validDetails.length; i++) {
      if (!validDetails[i].quantity || validDetails[i].quantity! <= 0) {
        ElMessage.error(`第 ${i + 1} 行用量必须大于 0`)
        return
      }
    }

    // 组装提交数据
    const submitData: BomCreateRequest = {
      bomCode: formData.value.bomCode,
      itemId: formData.value.itemId,
      version: formData.value.version,
      baseQuantity: formData.value.baseQuantity,
      status: formData.value.status,
      remark: formData.value.remark,
      details: validDetails.map(d => ({
        itemId: d.itemId!,
        quantity: d.quantity!,
        lossRate: d.lossRate,
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
          <el-form-item label="BOM编码" prop="bomCode">
            <el-input v-model="formData.bomCode" placeholder="请输入BOM编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="母件物料" prop="itemId">
            <el-select v-model="formData.itemId" placeholder="请选择母件物料" filterable style="width: 100%">
              <el-option
                v-for="item in itemList"
                :key="item.id"
                :label="`${item.itemCode} - ${item.itemName}`"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="版本">
            <el-input v-model="formData.version" placeholder="请输入版本号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="基础数量">
            <el-input-number v-model="formData.baseQuantity" :min="1" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="formData.remark" placeholder="请输入备注" />
      </el-form-item>

      <!-- 明细行 -->
      <el-divider content-position="left">子件明细</el-divider>

      <div v-for="(detail, index) in details" :key="index" class="detail-row">
        <el-row :gutter="12">
          <el-col :span="6">
            <el-form-item :label="index === 0 ? '子件物料' : ''" :prop="`details.${index}.itemId`">
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
          <el-col :span="4">
            <el-form-item :label="index === 0 ? '用量' : ''" :prop="`details.${index}.quantity`">
              <el-input-number v-model="detail.quantity" :min="0.01" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item :label="index === 0 ? '损耗率%' : ''">
              <el-input-number v-model="detail.lossRate" :min="0" :max="100" :precision="1" style="width: 100%" />
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
        + 添加子件
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
