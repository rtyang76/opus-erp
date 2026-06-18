-- =====================================================
-- Opus ERP - Purchase & Sales Tables
-- Version: V5
-- Description: 创建采购和销售相关表
-- 精度规范：数量 DECIMAL(18,4)，单价 DECIMAL(18,6)，金额 DECIMAL(18,2)
-- 命名规范：索引 idx_，唯一约束 uq_，全小写
-- =====================================================

-- ========== 采购模块 ==========

-- 采购订单主表
CREATE TABLE po_order (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_no NVARCHAR(50) NOT NULL,  -- 采购订单号
    supplier_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,  -- 交货日期
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CLOSED/CANCELLED
    total_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_po_order_order_no UNIQUE(order_no)
);

-- 采购订单明细表
CREATE TABLE po_order_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    specification NVARCHAR(200),
    quantity DECIMAL(18,4) NOT NULL,  -- 订购数量
    unit_id BIGINT,
    unit_price DECIMAL(18,6) NOT NULL,  -- 单价
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 13.00,  -- 税率
    received_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,  -- 已收货数量
    remark NVARCHAR(200)
);

-- 采购订单明细金额计算列
ALTER TABLE po_order_detail ADD amount AS (quantity * unit_price) PERSISTED;
ALTER TABLE po_order_detail ADD tax_amount AS (quantity * unit_price * tax_rate / 100) PERSISTED;

-- 采购入库单主表
CREATE TABLE po_receipt (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_no NVARCHAR(50) NOT NULL,  -- 入库单号
    order_id BIGINT,  -- 关联采购订单
    supplier_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,  -- 入库仓库
    receipt_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_po_receipt_receipt_no UNIQUE(receipt_no)
);

-- 采购入库单明细表
CREATE TABLE po_receipt_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    order_detail_id BIGINT,  -- 关联订单明细
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),  -- 批次号
    quantity DECIMAL(18,4) NOT NULL,  -- 入库数量
    unit_id BIGINT,
    unit_cost DECIMAL(18,6),  -- 入库成本
    remark NVARCHAR(200)
);

-- ========== 销售模块 ==========

-- 销售订单主表
CREATE TABLE so_order (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_no NVARCHAR(50) NOT NULL,  -- 销售订单号
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,  -- 交货日期
    salesman_id BIGINT,  -- 业务员
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/SHIPPED/COMPLETED/CANCELLED
    total_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_so_order_order_no UNIQUE(order_no)
);

-- 销售订单明细表
CREATE TABLE so_order_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    specification NVARCHAR(200),
    quantity DECIMAL(18,4) NOT NULL,  -- 订购数量
    unit_id BIGINT,
    unit_price DECIMAL(18,6) NOT NULL,  -- 单价
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 13.00,  -- 税率
    shipped_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,  -- 已出库数量
    remark NVARCHAR(200)
);

-- 销售订单明细金额计算列
ALTER TABLE so_order_detail ADD amount AS (quantity * unit_price) PERSISTED;
ALTER TABLE so_order_detail ADD tax_amount AS (quantity * unit_price * tax_rate / 100) PERSISTED;

-- 销售出库单主表
CREATE TABLE so_shipment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    shipment_no NVARCHAR(50) NOT NULL,  -- 出库单号
    order_id BIGINT,  -- 关联销售订单
    customer_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,  -- 出库仓库
    shipment_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_so_shipment_shipment_no UNIQUE(shipment_no)
);

-- 销售出库单明细表
CREATE TABLE so_shipment_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    order_detail_id BIGINT,  -- 关联订单明细
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),  -- 批次号
    quantity DECIMAL(18,4) NOT NULL,  -- 出库数量
    unit_id BIGINT,
    unit_price DECIMAL(18,6),  -- 出库单价
    remark NVARCHAR(200)
);

-- ========== 财务模块 ==========

-- 应收单
CREATE TABLE fin_receivable (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receivable_no NVARCHAR(50) NOT NULL,  -- 应收单号
    customer_id BIGINT NOT NULL,
    reference_type NVARCHAR(30),  -- SO/RETURN
    reference_id BIGINT,
    reference_no NVARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,  -- 应收金额
    paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0,  -- 已收金额
    currency NVARCHAR(10) NOT NULL DEFAULT 'CNY',
    due_date DATE,  -- 到期日
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING/PARTIAL/PAID
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_fin_receivable_receivable_no UNIQUE(receivable_no)
);

-- 应收未付金额计算列
ALTER TABLE fin_receivable ADD unpaid_amount AS (amount - paid_amount) PERSISTED;

-- 收款单
CREATE TABLE fin_receipt (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_no NVARCHAR(50) NOT NULL,  -- 收款单号
    customer_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,  -- 收款金额
    payment_method NVARCHAR(30),  -- BANK/CASH/CHECK
    bank_info NVARCHAR(200),
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_fin_receipt_receipt_no UNIQUE(receipt_no)
);

-- 收款核销明细
CREATE TABLE fin_receipt_allocation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    receivable_id BIGINT NOT NULL,
    allocated_amount DECIMAL(18,2) NOT NULL  -- 核销金额
);

-- 应付单
CREATE TABLE fin_payable (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    payable_no NVARCHAR(50) NOT NULL,  -- 应付单号
    supplier_id BIGINT NOT NULL,
    reference_type NVARCHAR(30),  -- PO/RETURN
    reference_id BIGINT,
    reference_no NVARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,  -- 应付金额
    paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0,  -- 已付金额
    currency NVARCHAR(10) NOT NULL DEFAULT 'CNY',
    due_date DATE,  -- 到期日
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING/PARTIAL/PAID
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_fin_payable_payable_no UNIQUE(payable_no)
);

-- 应付未付金额计算列
ALTER TABLE fin_payable ADD unpaid_amount AS (amount - paid_amount) PERSISTED;

-- 付款单
CREATE TABLE fin_payment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    payment_no NVARCHAR(50) NOT NULL,  -- 付款单号
    supplier_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,  -- 付款金额
    payment_method NVARCHAR(30),  -- BANK/CASH/CHECK
    bank_info NVARCHAR(200),
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_fin_payment_payment_no UNIQUE(payment_no)
);

-- 付款核销明细
CREATE TABLE fin_payment_allocation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    payable_id BIGINT NOT NULL,
    allocated_amount DECIMAL(18,2) NOT NULL  -- 核销金额
);

-- ========== 创建索引 ==========

-- 采购订单索引
CREATE INDEX idx_po_order_supplier_id ON po_order(supplier_id);
CREATE INDEX idx_po_order_status ON po_order(status);
CREATE INDEX idx_po_order_order_date ON po_order(order_date);

-- 采购订单明细索引
CREATE INDEX idx_po_order_detail_order_id ON po_order_detail(order_id);
CREATE INDEX idx_po_order_detail_item_id ON po_order_detail(item_id);

-- 采购入库索引
CREATE INDEX idx_po_receipt_order_id ON po_receipt(order_id);
CREATE INDEX idx_po_receipt_supplier_id ON po_receipt(supplier_id);
CREATE INDEX idx_po_receipt_warehouse_id ON po_receipt(warehouse_id);
CREATE INDEX idx_po_receipt_status ON po_receipt(status);

-- 采购入库明细索引
CREATE INDEX idx_po_receipt_detail_receipt_id ON po_receipt_detail(receipt_id);
CREATE INDEX idx_po_receipt_detail_item_id ON po_receipt_detail(item_id);

-- 销售订单索引
CREATE INDEX idx_so_order_customer_id ON so_order(customer_id);
CREATE INDEX idx_so_order_salesman_id ON so_order(salesman_id);
CREATE INDEX idx_so_order_status ON so_order(status);
CREATE INDEX idx_so_order_order_date ON so_order(order_date);

-- 销售订单明细索引
CREATE INDEX idx_so_order_detail_order_id ON so_order_detail(order_id);
CREATE INDEX idx_so_order_detail_item_id ON so_order_detail(item_id);

-- 销售出库索引
CREATE INDEX idx_so_shipment_order_id ON so_shipment(order_id);
CREATE INDEX idx_so_shipment_customer_id ON so_shipment(customer_id);
CREATE INDEX idx_so_shipment_warehouse_id ON so_shipment(warehouse_id);
CREATE INDEX idx_so_shipment_status ON so_shipment(status);

-- 销售出库明细索引
CREATE INDEX idx_so_shipment_detail_shipment_id ON so_shipment_detail(shipment_id);
CREATE INDEX idx_so_shipment_detail_item_id ON so_shipment_detail(item_id);

-- 应收索引
CREATE INDEX idx_fin_receivable_customer_id ON fin_receivable(customer_id);
CREATE INDEX idx_fin_receivable_status ON fin_receivable(status);

-- 收款索引
CREATE INDEX idx_fin_receipt_customer_id ON fin_receipt(customer_id);
CREATE INDEX idx_fin_receipt_status ON fin_receipt(status);

-- 收款核销索引
CREATE INDEX idx_fin_receipt_alloc_receipt_id ON fin_receipt_allocation(receipt_id);
CREATE INDEX idx_fin_receipt_alloc_receivable_id ON fin_receipt_allocation(receivable_id);

-- 应付索引
CREATE INDEX idx_fin_payable_supplier_id ON fin_payable(supplier_id);
CREATE INDEX idx_fin_payable_status ON fin_payable(status);

-- 付款索引
CREATE INDEX idx_fin_payment_supplier_id ON fin_payment(supplier_id);
CREATE INDEX idx_fin_payment_status ON fin_payment(status);

-- 付款核销索引
CREATE INDEX idx_fin_payment_alloc_payment ON fin_payment_allocation(payment_id);
CREATE INDEX idx_fin_payment_alloc_payable ON fin_payment_allocation(payable_id);
