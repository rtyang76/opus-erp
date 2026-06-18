import request from './request'
import type { R, PageResult } from './types'

// ========== 销售出库类型定义 ==========

export interface SoShipment {
  id: number
  shipmentNo: string
  orderId: number
  orderNo: string
  customerId: number
  customerName: string
  warehouseId: number
  warehouseName: string
  shipmentDate: string
  status: string
  remark: string
  createdByName: string
  createdAt: string
  details: SoShipmentDetail[]
}

export interface SoShipmentDetail {
  id: number
  orderDetailId: number
  itemId: number
  itemCode: string
  itemName: string
  lotNo: string
  quantity: number
  unitId: number
  unitName: string
  unitPrice: number
  remark: string
}

export interface ShipmentQuery {
  pageNum: number
  pageSize: number
  shipmentNo?: string
  orderId?: number
  customerId?: number
  status?: string
}

export interface ShipmentCreateRequest {
  orderId?: number
  customerId: number
  warehouseId: number
  shipmentDate: string
  remark?: string
  details: {
    orderDetailId?: number
    itemId: number
    lotNo?: string
    quantity: number
    unitId: number
    unitPrice?: number
    remark?: string
  }[]
}

// ========== 销售出库 API ==========

export function getShipments(params: ShipmentQuery): Promise<R<PageResult<SoShipment>>> {
  return request.get('/sales/shipments', { params })
}

export function getShipment(id: number): Promise<R<SoShipment>> {
  return request.get(`/sales/shipments/${id}`)
}

export function createShipment(data: ShipmentCreateRequest): Promise<R<SoShipment>> {
  return request.post('/sales/shipments', data)
}

export function auditShipment(id: number): Promise<R<void>> {
  return request.post(`/sales/shipments/${id}/audit`)
}

export function cancelShipment(id: number): Promise<R<void>> {
  return request.post(`/sales/shipments/${id}/cancel`)
}
