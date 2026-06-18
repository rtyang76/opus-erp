/**
 * 销售管理 API - 重新导出
 * 此文件保持向后兼容，实际实现已拆分为独立模块
 */

// 重新导出所有类型和函数
export type { R, PageResult } from './types'
export type { SoOrder, SoOrderDetail, OrderQuery, OrderCreateRequest } from './sales-order'
export type { SoShipment, SoShipmentDetail, ShipmentQuery, ShipmentCreateRequest } from './sales-shipment'

export {
  getOrders,
  getOrder,
  createOrder,
  updateOrder,
  auditOrder,
  shipOrder,
  cancelOrder
} from './sales-order'

export {
  getShipments,
  getShipment,
  createShipment,
  auditShipment,
  cancelShipment
} from './sales-shipment'
