import request from './request'
import type { R, PageResult } from './types'

// ========== 采购订单类型定义 ==========

export interface PoOrder {
  id: number
  orderNo: string
  supplierId: number
  supplierName: string
  orderDate: string
  deliveryDate: string
  status: string
  totalAmount: number
  taxAmount: number
  remark: string
  createdByName: string
  createdAt: string
  details: PoOrderDetail[]
}

export interface PoOrderDetail {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  quantity: number
  unitId: number
  unitName: string
  unitPrice: number
  taxRate: number
  amount: number
  taxAmount: number
  receivedQuantity: number
  remark: string
}

export interface OrderQuery {
  pageNum: number
  pageSize: number
  orderNo?: string
  supplierId?: number
  status?: string
  startDate?: string
  endDate?: string
}

export interface OrderCreateRequest {
  supplierId: number
  orderDate: string
  deliveryDate?: string
  remark?: string
  details: {
    itemId: number
    specification?: string
    quantity: number
    unitId: number
    unitPrice: number
    taxRate?: number
    remark?: string
  }[]
}

// ========== 采购订单 API ==========

export function getOrders(params: OrderQuery): Promise<R<PageResult<PoOrder>>> {
  return request.get('/purchase/orders', { params })
}

export function getOrder(id: number): Promise<R<PoOrder>> {
  return request.get(`/purchase/orders/${id}`)
}

export function createOrder(data: OrderCreateRequest): Promise<R<PoOrder>> {
  return request.post('/purchase/orders', data)
}

export function updateOrder(id: number, data: OrderCreateRequest): Promise<R<PoOrder>> {
  return request.put(`/purchase/orders/${id}`, data)
}

export function auditOrder(id: number): Promise<R<void>> {
  return request.post(`/purchase/orders/${id}/audit`)
}

export function cancelOrder(id: number): Promise<R<void>> {
  return request.post(`/purchase/orders/${id}/cancel`)
}

export function closeOrder(id: number): Promise<R<void>> {
  return request.post(`/purchase/orders/${id}/close`)
}
