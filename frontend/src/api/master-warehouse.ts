import request from './request'
import type { R, PageResult } from './system'

// ========== 仓库管理 ==========

export interface MdmWarehouse {
  id?: number
  warehouseCode: string
  warehouseName: string
  warehouseType?: string
  address?: string
  manager?: string
  phone?: string
  status: number
  remark?: string
}

export interface WarehouseQuery {
  pageNum: number
  pageSize: number
  warehouseCode?: string
  warehouseName?: string
  warehouseType?: string
  status?: number
}

export function getWarehouses(params: WarehouseQuery): Promise<R<PageResult<MdmWarehouse>>> {
  return request.get('/master/warehouses', { params })
}

export function getWarehouse(id: number): Promise<R<MdmWarehouse>> {
  return request.get(`/master/warehouses/${id}`)
}

export function createWarehouse(data: MdmWarehouse): Promise<R<MdmWarehouse>> {
  return request.post('/master/warehouses', data)
}

export function updateWarehouse(id: number, data: MdmWarehouse): Promise<R<MdmWarehouse>> {
  return request.put(`/master/warehouses/${id}`, data)
}

export function deleteWarehouse(id: number): Promise<R<void>> {
  return request.delete(`/master/warehouses/${id}`)
}
