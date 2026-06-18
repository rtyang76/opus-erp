import request from './request'
import type { R, PageResult } from './types'

// ========== 采购入库类型定义 ==========

export interface PoReceipt {
  id: number
  receiptNo: string
  orderId: number
  orderNo: string
  supplierId: number
  supplierName: string
  warehouseId: number
  warehouseName: string
  receiptDate: string
  status: string
  remark: string
  createdByName: string
  createdAt: string
  details: PoReceiptDetail[]
}

export interface PoReceiptDetail {
  id: number
  orderDetailId: number
  itemId: number
  itemCode: string
  itemName: string
  lotNo: string
  quantity: number
  unitId: number
  unitName: string
  unitCost: number
  remark: string
}

export interface ReceiptQuery {
  pageNum: number
  pageSize: number
  receiptNo?: string
  orderId?: number
  supplierId?: number
  status?: string
}

export interface ReceiptCreateRequest {
  orderId?: number
  supplierId: number
  warehouseId: number
  receiptDate: string
  remark?: string
  details: {
    orderDetailId?: number
    itemId: number
    lotNo?: string
    quantity: number
    unitId: number
    unitCost?: number
    remark?: string
  }[]
}

// ========== 采购入库 API ==========

export function getReceipts(params: ReceiptQuery): Promise<R<PageResult<PoReceipt>>> {
  return request.get('/purchase/receipts', { params })
}

export function getReceipt(id: number): Promise<R<PoReceipt>> {
  return request.get(`/purchase/receipts/${id}`)
}

export function createReceipt(data: ReceiptCreateRequest): Promise<R<PoReceipt>> {
  return request.post('/purchase/receipts', data)
}

export function auditReceipt(id: number): Promise<R<void>> {
  return request.post(`/purchase/receipts/${id}/audit`)
}

export function cancelReceipt(id: number): Promise<R<void>> {
  return request.post(`/purchase/receipts/${id}/cancel`)
}
