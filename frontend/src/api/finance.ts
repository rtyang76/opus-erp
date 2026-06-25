/**
 * 财务管理 API
 */

import request from './request'
import type { R, PageResult } from './types'

// ========== 应收管理 ==========

export interface FinReceivable {
  id: number
  receivableNo: string
  customerId: number
  customerName: string
  referenceType: string
  referenceId: number
  referenceNo: string
  amount: number
  paidAmount: number
  unpaidAmount: number
  currency: string
  dueDate: string
  status: string
  remark: string
  createdAt: string
}

export interface ReceivableQuery {
  pageNum: number
  pageSize: number
  customerId?: number
  status?: string
}

export function getReceivables(params: ReceivableQuery): Promise<R<PageResult<FinReceivable>>> {
  return request.get('/finance/receivables', { params })
}

export function getReceivable(id: number): Promise<R<FinReceivable>> {
  return request.get(`/finance/receivables/${id}`)
}

export function createReceivable(data: Partial<FinReceivable>): Promise<R<FinReceivable>> {
  return request.post('/finance/receivables', data)
}

export function updateReceivable(id: number, data: Partial<FinReceivable>): Promise<R<FinReceivable>> {
  return request.put(`/finance/receivables/${id}`, data)
}

export function deleteReceivable(id: number): Promise<R<void>> {
  return request.delete(`/finance/receivables/${id}`)
}

// ========== 应付管理 ==========

export interface FinPayable {
  id: number
  payableNo: string
  supplierId: number
  supplierName: string
  referenceType: string
  referenceId: number
  referenceNo: string
  amount: number
  paidAmount: number
  unpaidAmount: number
  currency: string
  dueDate: string
  status: string
  remark: string
  createdAt: string
}

export interface PayableQuery {
  pageNum: number
  pageSize: number
  supplierId?: number
  status?: string
}

export function getPayables(params: PayableQuery): Promise<R<PageResult<FinPayable>>> {
  return request.get('/finance/payables', { params })
}

export function getPayable(id: number): Promise<R<FinPayable>> {
  return request.get(`/finance/payables/${id}`)
}

export function createPayable(data: Partial<FinPayable>): Promise<R<FinPayable>> {
  return request.post('/finance/payables', data)
}

export function updatePayable(id: number, data: Partial<FinPayable>): Promise<R<FinPayable>> {
  return request.put(`/finance/payables/${id}`, data)
}

export function deletePayable(id: number): Promise<R<void>> {
  return request.delete(`/finance/payables/${id}`)
}
