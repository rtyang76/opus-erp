import request from './request'
import type { R, PageResult } from './types'

// ========== 盘点类型定义 ==========

export interface InvStocktake {
  id: number
  stocktakeNo: string
  warehouseId: number
  warehouseName: string
  stocktakeDate: string
  status: string
  remark: string
  createdByName: string
  createdAt: string
  details: InvStocktakeDetail[]
}

export interface InvStocktakeDetail {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  lotNo: string
  systemQuantity: number
  actualQuantity: number
  diffQuantity: number
  remark: string
}

export interface StocktakeQuery {
  pageNum: number
  pageSize: number
  stocktakeNo?: string
  warehouseId?: number
  status?: string
}

export interface StocktakeCreateRequest {
  warehouseId: number
  stocktakeDate: string
  remark?: string
  details: {
    itemId: number
    lotNo?: string
    systemQuantity?: number
    actualQuantity: number
    remark?: string
  }[]
}

// ========== 盘点 API ==========

export function getStocktakes(params: StocktakeQuery): Promise<R<PageResult<InvStocktake>>> {
  return request.get('/inventory/stocktakes', { params })
}

export function createStocktake(data: StocktakeCreateRequest): Promise<R<InvStocktake>> {
  return request.post('/inventory/stocktakes', data)
}

export function auditStocktake(id: number): Promise<R<void>> {
  return request.post(`/inventory/stocktakes/${id}/audit`)
}

export function cancelStocktake(id: number): Promise<R<void>> {
  return request.post(`/inventory/stocktakes/${id}/cancel`)
}
