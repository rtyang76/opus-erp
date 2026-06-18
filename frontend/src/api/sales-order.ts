import request from './request'
import type { R, PageResult } from './types'

// ========== 销售订单类型定义 ==========

export interface SoOrder {
  id: number
  orderNo: string
  customerId: number
  customerName: string
  orderDate: string
  deliveryDate: string
  salesmanId: number
  salesmanName: string
  status: string
  totalAmount: number
  taxAmount: number
  remark: string
  createdByName: string
  createdAt: string
  details: SoOrderDetail[]
}

export interface SoOrderDetail {
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
  shippedQuantity: number
  remark: string
}

export interface OrderQuery {
  pageNum: number
  pageSize: number
  orderNo?: string
  customerId?: number
  salesmanId?: number
  status?: string
  startDate?: string
  endDate?: string
}

export interface OrderCreateRequest {
  customerId: number
  orderDate: string
  deliveryDate?: string
  salesmanId?: number
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

// ========== 销售订单 API ==========

export function getOrders(params: OrderQuery): Promise<R<PageResult<SoOrder>>> {
  return request.get('/sales/orders', { params })
}

export function getOrder(id: number): Promise<R<SoOrder>> {
  return request.get(`/sales/orders/${id}`)
}

export function createOrder(data: OrderCreateRequest): Promise<R<SoOrder>> {
  return request.post('/sales/orders', data)
}

export function updateOrder(id: number, data: OrderCreateRequest): Promise<R<SoOrder>> {
  return request.put(`/sales/orders/${id}`, data)
}

export function auditOrder(id: number): Promise<R<void>> {
  return request.post(`/sales/orders/${id}/audit`)
}

export function shipOrder(id: number): Promise<R<void>> {
  return request.post(`/sales/orders/${id}/ship`)
}

export function cancelOrder(id: number): Promise<R<void>> {
  return request.post(`/sales/orders/${id}/cancel`)
}
