import request from './request'
import type { R, PageResult } from './system'

// ========== 客户管理 ==========

export interface MdmCustomer {
  id?: number
  customerCode: string
  customerName: string
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
  salesmanId?: number
  status: number
  remark?: string
  salesmanName?: string
}

export interface CustomerQuery {
  pageNum: number
  pageSize: number
  customerCode?: string
  customerName?: string
  rating?: string
  status?: number
}

export function getCustomers(params: CustomerQuery): Promise<R<PageResult<MdmCustomer>>> {
  return request.get('/master/customers', { params })
}

export function getCustomer(id: number): Promise<R<MdmCustomer>> {
  return request.get(`/master/customers/${id}`)
}

export function createCustomer(data: MdmCustomer): Promise<R<MdmCustomer>> {
  return request.post('/master/customers', data)
}

export function updateCustomer(id: number, data: MdmCustomer): Promise<R<MdmCustomer>> {
  return request.put(`/master/customers/${id}`, data)
}

export function deleteCustomer(id: number): Promise<R<void>> {
  return request.delete(`/master/customers/${id}`)
}
