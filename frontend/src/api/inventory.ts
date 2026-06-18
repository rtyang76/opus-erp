/**
 * 库存管理 API - 重新导出
 * 此文件保持向后兼容，实际实现已拆分为独立模块
 */

// 重新导出所有类型和函数
export type { R, PageResult } from './types'
export type { InvStock, StockQuery } from './inventory-stock'
export type { InvTransaction, TransactionQuery } from './inventory-transaction'
export type { InvTransfer, InvTransferDetail, TransferQuery, TransferCreateRequest } from './inventory-transfer'
export type { InvStocktake, InvStocktakeDetail, StocktakeQuery, StocktakeCreateRequest } from './inventory-stocktake'

export { getStock } from './inventory-stock'
export { getTransactions } from './inventory-transaction'
export { getTransfers, createTransfer, auditTransfer, cancelTransfer } from './inventory-transfer'
export { getStocktakes, createStocktake, auditStocktake, cancelStocktake } from './inventory-stocktake'
