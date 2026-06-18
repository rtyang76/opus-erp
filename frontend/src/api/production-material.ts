import request from './request'
import type { R, PageResult } from './system'

// ========== 领料单相关 ==========

export interface PpMaterialIssue {
  id: number
  issueNo: string
  workOrderId: number
  workOrderNo: string
  warehouseId: number
  warehouseName: string
  issueDate: string
  issueType: string
  status: string
  remark: string
  details: PpMaterialIssueDetail[]
  createdAt: string
}

export interface PpMaterialIssueDetail {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  unitName: string
  lotNo: string
  quantity: number
  remark: string
}

export interface MaterialIssueQuery {
  pageNum: number
  pageSize: number
  issueNo?: string
  workOrderId?: number
  issueType?: string
  status?: string
}

export interface MaterialIssueCreateRequest {
  workOrderId: number
  warehouseId: number
  issueDate: string
  issueType?: string
  remark?: string
  details: {
    itemId: number
    lotNo?: string
    quantity: number
    remark?: string
  }[]
}

export function getMaterialIssues(params: MaterialIssueQuery): Promise<R<PageResult<PpMaterialIssue>>> {
  return request.get('/production/material-issues', { params })
}

export function getMaterialIssue(id: number): Promise<R<PpMaterialIssue>> {
  return request.get(`/production/material-issues/${id}`)
}

export function createMaterialIssue(data: MaterialIssueCreateRequest): Promise<R<PpMaterialIssue>> {
  return request.post('/production/material-issues', data)
}

export function auditMaterialIssue(id: number): Promise<R<void>> {
  return request.post(`/production/material-issues/${id}/audit`)
}

export function cancelMaterialIssue(id: number): Promise<R<void>> {
  return request.post(`/production/material-issues/${id}/cancel`)
}
