import request from './request'
import type { R, PageResult } from './system'

// ========== BOM 相关 ==========

export interface PpBom {
  id: number
  bomCode: string
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  version: string
  baseQuantity: number
  status: number
  remark: string
  details: PpBomDetail[]
  createdAt: string
}

export interface PpBomDetail {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  unitName: string
  quantity: number
  lossRate: number
  remark: string
}

export interface BomQuery {
  pageNum: number
  pageSize: number
  bomCode?: string
  itemId?: number
  status?: number
}

export interface BomCreateRequest {
  bomCode: string
  itemId: number
  version?: string
  baseQuantity?: number
  status?: number
  remark?: string
  details: {
    itemId: number
    quantity: number
    lossRate?: number
    remark?: string
  }[]
}

export function getBoms(params: BomQuery): Promise<R<PageResult<PpBom>>> {
  return request.get('/production/boms', { params })
}

export function getBom(id: number): Promise<R<PpBom>> {
  return request.get(`/production/boms/${id}`)
}

export function createBom(data: BomCreateRequest): Promise<R<PpBom>> {
  return request.post('/production/boms', data)
}

export function updateBom(id: number, data: BomCreateRequest): Promise<R<PpBom>> {
  return request.put(`/production/boms/${id}`, data)
}

export function deleteBom(id: number): Promise<R<void>> {
  return request.delete(`/production/boms/${id}`)
}
