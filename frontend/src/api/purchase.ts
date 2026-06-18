/**
 * 采购管理 API - 重新导出
 * 此文件保持向后兼容，实际实现已拆分为独立模块
 */

// 重新导出所有类型和函数
export type { R, PageResult } from './types'
export type { PoOrder, PoOrderDetail, OrderQuery, OrderCreateRequest } from './purchase-order'
export type { PoReceipt, PoReceiptDetail, ReceiptQuery, ReceiptCreateRequest } from './purchase-receipt'

export {
  getOrders,
  getOrder,
  createOrder,
  updateOrder,
  auditOrder,
  cancelOrder,
  closeOrder
} from './purchase-order'

export {
  getReceipts,
  getReceipt,
  createReceipt,
  auditReceipt,
  cancelReceipt
} from './purchase-receipt'
