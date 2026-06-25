-- =====================================================
-- Opus ERP - 添加逻辑删除字段到明细表
-- Version: V9
-- Description: 为所有明细表添加 deleted 字段，支持逻辑删除
-- =====================================================

-- 采购订单明细表
ALTER TABLE po_order_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 采购入库明细表
ALTER TABLE po_receipt_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 销售订单明细表
ALTER TABLE so_order_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 销售出库明细表
ALTER TABLE so_shipment_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 生产领料明细表
ALTER TABLE pp_material_issue_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 调拨明细表
ALTER TABLE inv_transfer_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 盘点明细表
ALTER TABLE inv_stocktake_detail ADD deleted BIT NOT NULL DEFAULT 0;
