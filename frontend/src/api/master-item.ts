import request from './request'
import type { R, PageResult } from './system'

// ========== 物料管理 ==========

export interface MdmItem {
  id?: number
  itemCode: string
  itemName: string
  categoryId?: number
  specification?: string
  unitId?: number
  auxUnitId?: number
  unitFactor?: number
  safetyStock?: number
  abcClass?: string
  itemType?: string
  defaultWarehouseId?: number
  shelfLifeDays?: number
  status: number
  remark?: string
  categoryName?: string
  unitName?: string
  auxUnitName?: string
  defaultWarehouseName?: string
}

export interface ItemQuery {
  pageNum: number
  pageSize: number
  itemCode?: string
  itemName?: string
  categoryId?: number
  itemType?: string
  status?: number
}

export function getItems(params: ItemQuery): Promise<R<PageResult<MdmItem>>> {
  return request.get('/master/items', { params })
}

export function getItem(id: number): Promise<R<MdmItem>> {
  return request.get(`/master/items/${id}`)
}

export function createItem(data: MdmItem): Promise<R<MdmItem>> {
  return request.post('/master/items', data)
}

export function updateItem(id: number, data: MdmItem): Promise<R<MdmItem>> {
  return request.put(`/master/items/${id}`, data)
}

export function deleteItem(id: number): Promise<R<void>> {
  return request.delete(`/master/items/${id}`)
}
