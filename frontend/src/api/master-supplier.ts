import request from './request'
import type { R, PageResult } from './system'

// ========== 供应商管理 ==========

export interface MdmSupplier {
  id?: number
  supplierCode: string
  supplierName: string
  shortName?: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
  taxNo?: string
  bankName?: string
  bankAccount?: string
  paymentTerms?: string
  creditLimit?: number
  rating?: string
  status: number
  remark?: string
}

export interface SupplierQuery {
  pageNum: number
  pageSize: number
  supplierCode?: string
  supplierName?: string
  rating?: string
  status?: number
}

export function getSuppliers(params: SupplierQuery): Promise<R<PageResult<MdmSupplier>>> {
  return request.get('/master/suppliers', { params })
}

export function getSupplier(id: number): Promise<R<MdmSupplier>> {
  return request.get(`/master/suppliers/${id}`)
}

export function createSupplier(data: MdmSupplier): Promise<R<MdmSupplier>> {
  return request.post('/master/suppliers', data)
}

export function updateSupplier(id: number, data: MdmSupplier): Promise<R<MdmSupplier>> {
  return request.put(`/master/suppliers/${id}`, data)
}

export function deleteSupplier(id: number): Promise<R<void>> {
  return request.delete(`/master/suppliers/${id}`)
}
