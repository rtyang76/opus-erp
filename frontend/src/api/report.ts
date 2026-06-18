import request from './request'
import type { R, PageResult } from './system'

// ========== 报表相关 ==========

export interface InventorySummary {
  itemId: number
  itemCode: string
  itemName: string
  specification: string
  unitName: string
  beginQuantity: number
  beginAmount: number
  receiptQuantity: number
  receiptAmount: number
  issueQuantity: number
  issueAmount: number
  endQuantity: number
  endAmount: number
}

export interface SalesProfit {
  customerId: number
  customerCode: string
  customerName: string
  itemId: number
  itemCode: string
  itemName: string
  salesQuantity: number
  salesAmount: number
  costAmount: number
  profit: number
  profitRate: number
}

export interface PurchaseSummary {
  supplierId: number
  supplierCode: string
  supplierName: string
  orderCount: number
  purchaseQuantity: number
  purchaseAmount: number
  receiptQuantity: number
  receiptAmount: number
}

export interface ProductionProgress {
  workOrderId: number
  orderNo: string
  itemCode: string
  itemName: string
  planQuantity: number
  completedQuantity: number
  completionRate: number
  planStartDate: string
  planEndDate: string
  status: string
  statusName: string
}

export interface ReportQuery {
  pageNum: number
  pageSize: number
  startDate?: string
  endDate?: string
  itemId?: number
  warehouseId?: number
  customerId?: number
  supplierId?: number
  status?: string
}

export function getInventorySummary(params: ReportQuery): Promise<R<PageResult<InventorySummary>>> {
  return request.get('/report/inventory-summary', { params })
}

export function getSalesProfit(params: ReportQuery): Promise<R<PageResult<SalesProfit>>> {
  return request.get('/report/sales-profit', { params })
}

export function getPurchaseSummary(params: ReportQuery): Promise<R<PageResult<PurchaseSummary>>> {
  return request.get('/report/purchase-summary', { params })
}

export function getProductionProgress(params: ReportQuery): Promise<R<PageResult<ProductionProgress>>> {
  return request.get('/report/production-progress', { params })
}
