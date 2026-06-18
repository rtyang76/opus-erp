-- =====================================================
-- Opus ERP - Master Data Tables
-- Version: V3
-- Description: 创建基础资料相关表
-- =====================================================

-- 物料分类表
CREATE TABLE mdm_item_category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    category_code NVARCHAR(50) NOT NULL,
    category_name NVARCHAR(100) NOT NULL,
    category_type NVARCHAR(20),  -- RAW/SEMI/FINISHED/AUXILIARY
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_item_category_code UNIQUE(category_code)
);

-- 计量单位表
CREATE TABLE mdm_unit (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    unit_code NVARCHAR(20) NOT NULL,
    unit_name NVARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_unit_code UNIQUE(unit_code)
);

-- 物料档案表
CREATE TABLE mdm_item (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_code NVARCHAR(50) NOT NULL,
    item_name NVARCHAR(200) NOT NULL,
    category_id BIGINT,
    specification NVARCHAR(200),  -- 规格型号
    unit_id BIGINT,  -- 主单位
    aux_unit_id BIGINT,  -- 辅助单位
    unit_factor DECIMAL(18,6),  -- 单位换算系数
    safety_stock DECIMAL(18,4),  -- 安全库存
    abc_class CHAR(1),  -- A/B/C
    item_type NVARCHAR(20),  -- RAW/SEMI/FINISHED/AUXILIARY
    default_warehouse_id BIGINT,
    shelf_life_days INT,  -- 保质期天数
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_item_code UNIQUE(item_code)
);

-- 仓库档案表
CREATE TABLE mdm_warehouse (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    warehouse_code NVARCHAR(50) NOT NULL,
    warehouse_name NVARCHAR(100) NOT NULL,
    warehouse_type NVARCHAR(20),  -- RAW/SEMI/FINISHED/RETURN/DEFECTIVE
    address NVARCHAR(200),
    manager NVARCHAR(50),
    phone NVARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_warehouse_code UNIQUE(warehouse_code)
);

-- 库位表（可选，支持库位管理时使用）
CREATE TABLE mdm_bin (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    bin_code NVARCHAR(50) NOT NULL,
    bin_name NVARCHAR(100),
    bin_type NVARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_bin_code UNIQUE(warehouse_id, bin_code)
);

-- 供应商档案表
CREATE TABLE mdm_supplier (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    supplier_code NVARCHAR(50) NOT NULL,
    supplier_name NVARCHAR(200) NOT NULL,
    short_name NVARCHAR(50),
    contact_person NVARCHAR(50),
    phone NVARCHAR(30),
    email NVARCHAR(100),
    address NVARCHAR(200),
    tax_no NVARCHAR(30),  -- 税号
    bank_name NVARCHAR(100),
    bank_account NVARCHAR(50),
    payment_terms NVARCHAR(50),  -- 付款条款
    credit_limit DECIMAL(18,4),  -- 信用额度
    rating NVARCHAR(10),  -- 评级 A/B/C
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_supplier_code UNIQUE(supplier_code)
);

-- 客户档案表
CREATE TABLE mdm_customer (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    customer_code NVARCHAR(50) NOT NULL,
    customer_name NVARCHAR(200) NOT NULL,
    short_name NVARCHAR(50),
    contact_person NVARCHAR(50),
    phone NVARCHAR(30),
    email NVARCHAR(100),
    address NVARCHAR(200),
    tax_no NVARCHAR(30),
    bank_name NVARCHAR(100),
    bank_account NVARCHAR(50),
    payment_terms NVARCHAR(50),
    credit_limit DECIMAL(18,4),
    rating NVARCHAR(10),
    salesman_id BIGINT,  -- 对应业务员
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_customer_code UNIQUE(customer_code)
);

-- 创建索引
CREATE INDEX idx_mdm_item_category_parent ON mdm_item_category(parent_id);
CREATE INDEX idx_mdm_item_category_type ON mdm_item_category(category_type);

CREATE INDEX idx_mdm_item_category ON mdm_item(category_id);
CREATE INDEX idx_mdm_item_type ON mdm_item(item_type);
CREATE INDEX idx_mdm_item_status ON mdm_item(status);

CREATE INDEX idx_mdm_warehouse_type ON mdm_warehouse(warehouse_type);

CREATE INDEX idx_mdm_bin_warehouse ON mdm_bin(warehouse_id);

CREATE INDEX idx_mdm_supplier_status ON mdm_supplier(status);
CREATE INDEX idx_mdm_supplier_rating ON mdm_supplier(rating);

CREATE INDEX idx_mdm_customer_status ON mdm_customer(status);
CREATE INDEX idx_mdm_customer_rating ON mdm_customer(rating);
CREATE INDEX idx_mdm_customer_salesman ON mdm_customer(salesman_id);
