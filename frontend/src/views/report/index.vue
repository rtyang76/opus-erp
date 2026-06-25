<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventorySummary, getSalesProfit, getPurchaseSummary, getProductionProgress } from '@/api/report'
import type { InventorySummary, SalesProfit, PurchaseSummary, ProductionProgress, ReportQuery } from '@/api/report'

// 报表类型定义
type ReportType = 'inventory' | 'sales' | 'purchase' | 'production'

// 当前激活的报表
const activeReport = ref<ReportType>('inventory')

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 查询参数
const queryParams = ref<ReportQuery>({
  pageNum: 1,
  pageSize: 10,
  startDate: undefined,
  endDate: undefined,
})

// 各报表数据
const inventoryData = ref<InventorySummary[]>([])
const salesData = ref<SalesProfit[]>([])
const purchaseData = ref<PurchaseSummary[]>([])
const productionData = ref<ProductionProgress[]>([])
const total = ref(0)
const loading = ref(false)

// 报表选项
const reportOptions: { label: string; value: ReportType }[] = [
  { label: '收发存汇总', value: 'inventory' },
  { label: '销售毛利', value: 'sales' },
  { label: '采购汇总', value: 'purchase' },
  { label: '生产进度', value: 'production' },
]

// 查询报表
const fetchReport = async () => {
  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    queryParams.value.startDate = dateRange.value[0]
    queryParams.value.endDate = dateRange.value[1]
  } else {
    queryParams.value.startDate = undefined
    queryParams.value.endDate = undefined
  }

  loading.value = true
  try {
    switch (activeReport.value) {
      case 'inventory': {
        const { data } = await getInventorySummary(queryParams.value)
        inventoryData.value = data.records
        total.value = data.total
        break
      }
      case 'sales': {
        const { data } = await getSalesProfit(queryParams.value)
        salesData.value = data.records
        total.value = data.total
        break
      }
      case 'purchase': {
        const { data } = await getPurchaseSummary(queryParams.value)
        purchaseData.value = data.records
        total.value = data.total
        break
      }
      case 'production': {
        const { data } = await getProductionProgress(queryParams.value)
        productionData.value = data.records
        total.value = data.total
        break
      }
    }
  } catch {
    ElMessage.error('查询报表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 切换报表
const handleReportChange = () => {
  queryParams.value.pageNum = 1
  fetchReport()
}

// 分页变化
const handleSizeChange = (val: number) => {
  queryParams.value.pageSize = val
  fetchReport()
}

const handleCurrentChange = (val: number) => {
  queryParams.value.pageNum = val
  fetchReport()
}

// 格式化数字
const formatNumber = (num: number, decimals = 4) => {
  return num?.toFixed(decimals) || '0.0000'
}

const formatMoney = (num: number) => {
  return num?.toFixed(2) || '0.00'
}

const formatRate = (num: number) => {
  return num?.toFixed(2) || '0.00'
}

// 获取状态标签颜色
const getStatusColor = (status: string): 'success' | 'primary' | 'warning' | 'info' | 'danger' => {
  const colorMap: Record<string, 'success' | 'primary' | 'warning' | 'info' | 'danger'> = {
    PENDING: 'info',
    RELEASED: 'warning',
    IN_PROGRESS: 'primary',
    COMPLETED: 'success',
    CLOSED: 'info',
  }
  return colorMap[status] || 'info'
}

onMounted(() => {
  fetchReport()
})
</script>

<template>
  <div class="page-container">
    <!-- 报表选择 -->
    <div class="search-area">
      <el-form :inline="true">
        <el-form-item label="报表类型">
          <el-select v-model="activeReport" @change="handleReportChange">
            <el-option
              v-for="item in reportOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchReport">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 收发存汇总报表 -->
    <el-table v-if="activeReport === 'inventory'" :data="inventoryData" v-loading="loading" stripe border>
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column prop="specification" label="规格型号" width="120" />
      <el-table-column prop="unitName" label="单位" width="80" />
      <el-table-column label="期初数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.beginQuantity) }}</template>
      </el-table-column>
      <el-table-column label="期初金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.beginAmount) }}</template>
      </el-table-column>
      <el-table-column label="入库数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.receiptQuantity) }}</template>
      </el-table-column>
      <el-table-column label="入库金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.receiptAmount) }}</template>
      </el-table-column>
      <el-table-column label="出库数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.issueQuantity) }}</template>
      </el-table-column>
      <el-table-column label="出库金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.issueAmount) }}</template>
      </el-table-column>
      <el-table-column label="期末数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.endQuantity) }}</template>
      </el-table-column>
      <el-table-column label="期末金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.endAmount) }}</template>
      </el-table-column>
    </el-table>

    <!-- 销售毛利简表 -->
    <el-table v-if="activeReport === 'sales'" :data="salesData" v-loading="loading" stripe border>
      <el-table-column prop="customerCode" label="客户编码" width="120" />
      <el-table-column prop="customerName" label="客户名称" width="180" show-overflow-tooltip />
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column label="销售数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.salesQuantity) }}</template>
      </el-table-column>
      <el-table-column label="销售金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.salesAmount) }}</template>
      </el-table-column>
      <el-table-column label="成本金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.costAmount) }}</template>
      </el-table-column>
      <el-table-column label="毛利" width="120" align="right">
        <template #default="{ row }">
          <span :class="{ 'text-danger': row.profit < 0 }">{{ formatMoney(row.profit) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="毛利率" width="100" align="right">
        <template #default="{ row }">{{ formatRate(row.profitRate) }}%</template>
      </el-table-column>
    </el-table>

    <!-- 采购汇总表 -->
    <el-table v-if="activeReport === 'purchase'" :data="purchaseData" v-loading="loading" stripe border>
      <el-table-column prop="supplierCode" label="供应商编码" width="120" />
      <el-table-column prop="supplierName" label="供应商名称" width="200" show-overflow-tooltip />
      <el-table-column prop="orderCount" label="订单数" width="100" align="right" />
      <el-table-column label="采购数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.purchaseQuantity) }}</template>
      </el-table-column>
      <el-table-column label="采购金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.purchaseAmount) }}</template>
      </el-table-column>
      <el-table-column label="入库数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.receiptQuantity) }}</template>
      </el-table-column>
      <el-table-column label="入库金额" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.receiptAmount) }}</template>
      </el-table-column>
    </el-table>

    <!-- 生产进度表 -->
    <el-table v-if="activeReport === 'production'" :data="productionData" v-loading="loading" stripe border>
      <el-table-column prop="orderNo" label="工单号" width="150" />
      <el-table-column prop="itemCode" label="物料编码" width="120" />
      <el-table-column prop="itemName" label="物料名称" width="180" show-overflow-tooltip />
      <el-table-column label="计划数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.planQuantity) }}</template>
      </el-table-column>
      <el-table-column label="完工数量" width="120" align="right">
        <template #default="{ row }">{{ formatNumber(row.completedQuantity) }}</template>
      </el-table-column>
      <el-table-column label="完成率" width="100" align="right">
        <template #default="{ row }">{{ formatRate(row.completionRate) }}%</template>
      </el-table-column>
      <el-table-column prop="planStartDate" label="计划开始" width="120" />
      <el-table-column prop="planEndDate" label="计划结束" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusColor(row.status)">{{ row.statusName }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
