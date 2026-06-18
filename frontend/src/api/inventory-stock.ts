import request from './request'
import type { R, PageResult } from './types'

// ========== 库存类型定义 ==========

export interface InvStock {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  warehouseId: number
  warehouseName: string
  binId: number
  lotNo: string
  quantity: number
  avgCost: number
  totalCost: number
  lockedQuantity: number
  availableQuantity: number
  updatedAt: string
}

export interface StockQuery {
  pageNum: number
  pageSize: number
  itemId?: number
  warehouseId?: number
  lotNo?: string
  minQuantity?: number
}

// ========== 库存 API ==========

export function getStock(params: StockQuery): Promise<R<PageResult<InvStock>>> {
  return request.get('/inventory/stock', { params })
}
