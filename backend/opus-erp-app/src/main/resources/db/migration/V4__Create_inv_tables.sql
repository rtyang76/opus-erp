-- =====================================================
-- Opus ERP - Inventory Tables
-- Version: V4
-- Description: 创建库存管理相关表
-- 精度规范：数量 DECIMAL(18,4)，单价 DECIMAL(18,6)，金额 DECIMAL(18,2)
-- 命名规范：索引 idx_，唯一约束 uq_，全小写
-- =====================================================

-- 即时库存表
CREATE TABLE inv_stock (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    bin_id BIGINT,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL DEFAULT 0,  -- 库存数量
    avg_cost DECIMAL(18,6) NOT NULL DEFAULT 0,  -- 移动加权平均成本
    locked_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,  -- 锁定数量（待出库）
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uq_inv_stock UNIQUE(item_id, warehouse_id, bin_id, lot_no)
);

-- 库存金额计算列
ALTER TABLE inv_stock ADD total_cost AS (quantity * avg_cost) PERSISTED;
-- 可用数量计算列
ALTER TABLE inv_stock ADD available_quantity AS (quantity - locked_quantity) PERSISTED;

-- 库存交易流水表（不可变日志，只新增不修改）
CREATE TABLE inv_transaction (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transaction_no NVARCHAR(50) NOT NULL,  -- 交易流水号
    transaction_type NVARCHAR(20) NOT NULL,  -- RECEIPT/ISSUE/TRANSFER/ADJUSTMENT/RETURN
    transaction_date DATE NOT NULL,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    bin_id BIGINT,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,  -- 正数入库，负数出库
    unit_cost DECIMAL(18,6),
    total_cost DECIMAL(18,2),
    reference_type NVARCHAR(30),  -- PO/SO/WO/MANUAL/TRANSFER
    reference_id BIGINT,
    reference_no NVARCHAR(50),
    reason_code NVARCHAR(50),  -- 原因代码
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    posted BIT NOT NULL DEFAULT 0,  -- 已过账标识
    CONSTRAINT uq_inv_transaction_no UNIQUE(transaction_no)
);

-- 调拨单主表
CREATE TABLE inv_transfer (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transfer_no NVARCHAR(50) NOT NULL,  -- 调拨单号
    from_warehouse_id BIGINT NOT NULL,  -- 调出仓库
    to_warehouse_id BIGINT NOT NULL,    -- 调入仓库
    transfer_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_inv_transfer_no UNIQUE(transfer_no)
);

-- 调拨单明细表
CREATE TABLE inv_transfer_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,  -- 调拨数量
    remark NVARCHAR(200)
);

-- 盘点单主表
CREATE TABLE inv_stocktake (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    stocktake_no NVARCHAR(50) NOT NULL,  -- 盘点单号
    warehouse_id BIGINT NOT NULL,
    stocktake_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_inv_stocktake_no UNIQUE(stocktake_no)
);

-- 盘点单明细表
CREATE TABLE inv_stocktake_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    stocktake_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    system_quantity DECIMAL(18,4),  -- 系统数量
    actual_quantity DECIMAL(18,4),  -- 实盘数量
    remark NVARCHAR(200)
);

-- 盘点差异数量计算列
ALTER TABLE inv_stocktake_detail ADD diff_quantity AS (actual_quantity - system_quantity) PERSISTED;

-- 创建索引
CREATE INDEX idx_inv_stock_item ON inv_stock(item_id);
CREATE INDEX idx_inv_stock_warehouse ON inv_stock(warehouse_id);
CREATE INDEX idx_inv_stock_lot ON inv_stock(lot_no);

CREATE INDEX idx_inv_transaction_no ON inv_transaction(transaction_no);
CREATE INDEX idx_inv_transaction_item ON inv_transaction(item_id);
CREATE INDEX idx_inv_transaction_warehouse ON inv_transaction(warehouse_id);
CREATE INDEX idx_inv_transaction_date ON inv_transaction(transaction_date);
CREATE INDEX idx_inv_transaction_type ON inv_transaction(transaction_type);
CREATE INDEX idx_inv_transaction_reference ON inv_transaction(reference_type, reference_id);

CREATE INDEX idx_inv_transfer_from_warehouse ON inv_transfer(from_warehouse_id);
CREATE INDEX idx_inv_transfer_to_warehouse ON inv_transfer(to_warehouse_id);
CREATE INDEX idx_inv_transfer_status ON inv_transfer(status);

CREATE INDEX idx_inv_transfer_detail_transfer ON inv_transfer_detail(transfer_id);
CREATE INDEX idx_inv_transfer_detail_item ON inv_transfer_detail(item_id);

CREATE INDEX idx_inv_stocktake_warehouse ON inv_stocktake(warehouse_id);
CREATE INDEX idx_inv_stocktake_status ON inv_stocktake(status);

CREATE INDEX idx_inv_stocktake_detail_stocktake ON inv_stocktake_detail(stocktake_id);
CREATE INDEX idx_inv_stocktake_detail_item ON inv_stocktake_detail(item_id);
