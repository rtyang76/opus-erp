import request from './request'
import type { R, PageResult } from './types'

// ========== 库存流水类型定义 ==========

export interface InvTransaction {
  id: number
  transactionNo: string
  transactionType: string
  transactionDate: string
  itemId: number
  itemCode: string
  itemName: string
  warehouseId: number
  warehouseName: string
  lotNo: string
  quantity: number
  unitCost: number
  totalCost: number
  referenceType: string
  referenceId: number
  referenceNo: string
  reasonCode: string
  remark: string
  createdByName: string
  createdAt: string
}

export interface TransactionQuery {
  pageNum: number
  pageSize: number
  transactionType?: string
  itemId?: number
  warehouseId?: number
  startDate?: string
  endDate?: string
}

// ========== 库存流水 API ==========

export function getTransactions(params: TransactionQuery): Promise<R<PageResult<InvTransaction>>> {
  return request.get('/inventory/transactions', { params })
}
