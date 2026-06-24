-- =====================================================
-- Opus ERP - Add deleted field to inventory tables
-- Version: V8
-- Description: 为 inv_stock 和 inv_transaction 表添加 deleted 字段
-- =====================================================

-- 为 inv_stock 表添加 deleted 字段
-- 注意：inv_stock 是即时库存表，通过 InvTransactionService 管理
-- 逻辑删除字段用于 MyBatis-Plus @TableLogic 兼容
ALTER TABLE inv_stock ADD deleted BIT NOT NULL DEFAULT 0;

-- 为 inv_transaction 表添加 deleted 字段
-- 注意：inv_transaction 是不可变日志，正常情况下不会删除记录
-- 但 BaseEntity 包含 @TableLogic，需要此字段以避免 SQL 错误
ALTER TABLE inv_transaction ADD deleted BIT NOT NULL DEFAULT 0;
