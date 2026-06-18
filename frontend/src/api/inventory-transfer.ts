import request from './request'
import type { R, PageResult } from './types'

// ========== 调拨类型定义 ==========

export interface InvTransfer {
  id: number
  transferNo: string
  fromWarehouseId: number
  fromWarehouseName: string
  toWarehouseId: number
  toWarehouseName: string
  transferDate: string
  status: string
  remark: string
  createdByName: string
  createdAt: string
  details: InvTransferDetail[]
}

export interface InvTransferDetail {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  lotNo: string
  quantity: number
  remark: string
}

export interface TransferQuery {
  pageNum: number
  pageSize: number
  transferNo?: string
  fromWarehouseId?: number
  toWarehouseId?: number
  status?: string
}

export interface TransferCreateRequest {
  fromWarehouseId: number
  toWarehouseId: number
  transferDate: string
  remark?: string
  details: {
    itemId: number
    lotNo?: string
    quantity: number
    remark?: string
  }[]
}

// ========== 调拨 API ==========

export function getTransfers(params: TransferQuery): Promise<R<PageResult<InvTransfer>>> {
  return request.get('/inventory/transfers', { params })
}

export function createTransfer(data: TransferCreateRequest): Promise<R<InvTransfer>> {
  return request.post('/inventory/transfers', data)
}

export function auditTransfer(id: number): Promise<R<void>> {
  return request.post(`/inventory/transfers/${id}/audit`)
}

export function cancelTransfer(id: number): Promise<R<void>> {
  return request.post(`/inventory/transfers/${id}/cancel`)
}
