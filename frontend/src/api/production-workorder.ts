import request from './request'
import type { R, PageResult } from './system'

// ========== 生产工单相关 ==========

export interface PpWorkOrder {
  id: number
  orderNo: string
  bomId: number
  bomCode: string
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  quantity: number
  completedQuantity: number
  warehouseId: number
  warehouseName: string
  planStartDate: string
  planEndDate: string
  actualStartDate: string
  actualEndDate: string
  status: string
  remark: string
  createdAt: string
}

export interface WorkOrderQuery {
  pageNum: number
  pageSize: number
  orderNo?: string
  itemId?: number
  status?: string
}

export interface WorkOrderCreateRequest {
  bomId: number
  itemId: number
  quantity: number
  warehouseId?: number
  planStartDate?: string
  planEndDate?: string
  remark?: string
}

export function getWorkOrders(params: WorkOrderQuery): Promise<R<PageResult<PpWorkOrder>>> {
  return request.get('/production/work-orders', { params })
}

export function getWorkOrder(id: number): Promise<R<PpWorkOrder>> {
  return request.get(`/production/work-orders/${id}`)
}

export function createWorkOrder(data: WorkOrderCreateRequest): Promise<R<PpWorkOrder>> {
  return request.post('/production/work-orders', data)
}

export function releaseWorkOrder(id: number): Promise<R<void>> {
  return request.post(`/production/work-orders/${id}/release`)
}

export function startWorkOrder(id: number): Promise<R<void>> {
  return request.post(`/production/work-orders/${id}/start`)
}

export function completeWorkOrder(id: number, completedQuantity: number): Promise<R<void>> {
  return request.post(`/production/work-orders/${id}/complete`, null, { params: { completedQuantity } })
}

export function closeWorkOrder(id: number): Promise<R<void>> {
  return request.post(`/production/work-orders/${id}/close`)
}
