-- =====================================================
-- Opus ERP - 完整建库脚本
-- 说明：合并所有迁移脚本的最终表结构 + 初始数据
-- 适用：全新安装或重建数据库
-- 数据库：SQL Server 2022
-- 执行前请先创建数据库：CREATE DATABASE opus_erp;
-- =====================================================

USE opus_erp;
GO

-- =====================================================
-- 一、系统管理模块 (sys_*)
-- =====================================================

-- 用户表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_user')
CREATE TABLE sys_user (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL,
    password NVARCHAR(200) NOT NULL,
    nickname NVARCHAR(50),
    email NVARCHAR(100),
    phone NVARCHAR(20),
    avatar NVARCHAR(200),
    status TINYINT NOT NULL DEFAULT 1,
    dept_id BIGINT,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_sys_user_username UNIQUE(username)
);

-- 角色表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_role')
CREATE TABLE sys_role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_code NVARCHAR(50) NOT NULL,
    role_name NVARCHAR(50) NOT NULL,
    description NVARCHAR(200),
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_sys_role_role_code UNIQUE(role_code)
);

-- 用户角色关联表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_user_role')
CREATE TABLE sys_user_role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uq_sys_user_role UNIQUE(user_id, role_id)
);

-- 菜单/权限表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_menu')
CREATE TABLE sys_menu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    menu_name NVARCHAR(50) NOT NULL,
    menu_type NCHAR(1) NOT NULL,
    path NVARCHAR(200),
    component NVARCHAR(200),
    perms NVARCHAR(100),
    icon NVARCHAR(100),
    sort_order INT NOT NULL DEFAULT 0,
    visible TINYINT NOT NULL DEFAULT 1,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- 角色菜单关联表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_role_menu')
CREATE TABLE sys_role_menu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uq_sys_role_menu UNIQUE(role_id, menu_id)
);

-- 字典类型表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_dict_type')
CREATE TABLE sys_dict_type (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dict_type NVARCHAR(100) NOT NULL,
    dict_name NVARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_sys_dict_type_dict_type UNIQUE(dict_type)
);

-- 字典数据表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_dict_data')
CREATE TABLE sys_dict_data (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dict_type NVARCHAR(100) NOT NULL,
    dict_label NVARCHAR(100) NOT NULL,
    dict_value NVARCHAR(100) NOT NULL,
    dict_color NVARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_sys_dict_data UNIQUE(dict_type, dict_value)
);

-- 操作日志表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_oper_log')
CREATE TABLE sys_oper_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    module NVARCHAR(50),
    description NVARCHAR(200),
    request_method NVARCHAR(10),
    request_url NVARCHAR(500),
    request_params NVARCHAR(MAX),
    response_result NVARCHAR(MAX),
    user_id BIGINT,
    username NVARCHAR(50),
    ip NVARCHAR(50),
    status TINYINT,
    error_msg NVARCHAR(MAX),
    duration BIGINT,
    oper_time DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 登录日志表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'sys_login_log')
CREATE TABLE sys_login_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50),
    ip NVARCHAR(50),
    location NVARCHAR(100),
    browser NVARCHAR(100),
    os NVARCHAR(100),
    status TINYINT NOT NULL,
    msg NVARCHAR(200),
    login_time DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 系统管理索引
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_user_username' AND object_id = OBJECT_ID('sys_user'))
    CREATE INDEX idx_sys_user_username ON sys_user(username);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_user_status' AND object_id = OBJECT_ID('sys_user'))
    CREATE INDEX idx_sys_user_status ON sys_user(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_user_dept_id' AND object_id = OBJECT_ID('sys_user'))
    CREATE INDEX idx_sys_user_dept_id ON sys_user(dept_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_role_status' AND object_id = OBJECT_ID('sys_role'))
    CREATE INDEX idx_sys_role_status ON sys_role(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_user_role_user_id' AND object_id = OBJECT_ID('sys_user_role'))
    CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_user_role_role_id' AND object_id = OBJECT_ID('sys_user_role'))
    CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_menu_parent_id' AND object_id = OBJECT_ID('sys_menu'))
    CREATE INDEX idx_sys_menu_parent_id ON sys_menu(parent_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_menu_type' AND object_id = OBJECT_ID('sys_menu'))
    CREATE INDEX idx_sys_menu_type ON sys_menu(menu_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_role_menu_role_id' AND object_id = OBJECT_ID('sys_role_menu'))
    CREATE INDEX idx_sys_role_menu_role_id ON sys_role_menu(role_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_role_menu_menu_id' AND object_id = OBJECT_ID('sys_role_menu'))
    CREATE INDEX idx_sys_role_menu_menu_id ON sys_role_menu(menu_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_dict_type_dict_type' AND object_id = OBJECT_ID('sys_dict_type'))
    CREATE INDEX idx_sys_dict_type_dict_type ON sys_dict_type(dict_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_dict_data_dict_type' AND object_id = OBJECT_ID('sys_dict_data'))
    CREATE INDEX idx_sys_dict_data_dict_type ON sys_dict_data(dict_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_oper_log_user_id' AND object_id = OBJECT_ID('sys_oper_log'))
    CREATE INDEX idx_sys_oper_log_user_id ON sys_oper_log(user_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_oper_log_oper_time' AND object_id = OBJECT_ID('sys_oper_log'))
    CREATE INDEX idx_sys_oper_log_oper_time ON sys_oper_log(oper_time);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_login_log_username' AND object_id = OBJECT_ID('sys_login_log'))
    CREATE INDEX idx_sys_login_log_username ON sys_login_log(username);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_sys_login_log_login_time' AND object_id = OBJECT_ID('sys_login_log'))
    CREATE INDEX idx_sys_login_log_login_time ON sys_login_log(login_time);

PRINT '系统管理模块表创建完成';
GO

-- =====================================================
-- 二、基础资料模块 (mdm_*)
-- =====================================================

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_item_category')
CREATE TABLE mdm_item_category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    category_code NVARCHAR(50) NOT NULL,
    category_name NVARCHAR(100) NOT NULL,
    category_type NVARCHAR(20),
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_item_category_code UNIQUE(category_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_unit')
CREATE TABLE mdm_unit (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    unit_code NVARCHAR(20) NOT NULL,
    unit_name NVARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_unit_code UNIQUE(unit_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_item')
CREATE TABLE mdm_item (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_code NVARCHAR(50) NOT NULL,
    item_name NVARCHAR(200) NOT NULL,
    category_id BIGINT,
    specification NVARCHAR(200),
    unit_id BIGINT,
    aux_unit_id BIGINT,
    unit_factor DECIMAL(18,6),
    safety_stock DECIMAL(18,4),
    abc_class NCHAR(1),
    item_type NVARCHAR(20),
    default_warehouse_id BIGINT,
    shelf_life_days INT,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_item_code UNIQUE(item_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_warehouse')
CREATE TABLE mdm_warehouse (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    warehouse_code NVARCHAR(50) NOT NULL,
    warehouse_name NVARCHAR(100) NOT NULL,
    warehouse_type NVARCHAR(20),
    address NVARCHAR(200),
    manager NVARCHAR(50),
    phone NVARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_warehouse_code UNIQUE(warehouse_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_bin')
CREATE TABLE mdm_bin (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    bin_code NVARCHAR(50) NOT NULL,
    bin_name NVARCHAR(100),
    bin_type NVARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_bin_code UNIQUE(warehouse_id, bin_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_supplier')
CREATE TABLE mdm_supplier (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    supplier_code NVARCHAR(50) NOT NULL,
    supplier_name NVARCHAR(200) NOT NULL,
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
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_supplier_code UNIQUE(supplier_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'mdm_customer')
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
    salesman_id BIGINT,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_mdm_customer_code UNIQUE(customer_code)
);

-- 基础资料索引
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_category_parent' AND object_id = OBJECT_ID('mdm_item_category'))
    CREATE INDEX idx_mdm_item_category_parent ON mdm_item_category(parent_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_category_type' AND object_id = OBJECT_ID('mdm_item_category'))
    CREATE INDEX idx_mdm_item_category_type ON mdm_item_category(category_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_category' AND object_id = OBJECT_ID('mdm_item'))
    CREATE INDEX idx_mdm_item_category ON mdm_item(category_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_type' AND object_id = OBJECT_ID('mdm_item'))
    CREATE INDEX idx_mdm_item_type ON mdm_item(item_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_status' AND object_id = OBJECT_ID('mdm_item'))
    CREATE INDEX idx_mdm_item_status ON mdm_item(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_unit_id' AND object_id = OBJECT_ID('mdm_item'))
    CREATE INDEX idx_mdm_item_unit_id ON mdm_item(unit_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_aux_unit_id' AND object_id = OBJECT_ID('mdm_item'))
    CREATE INDEX idx_mdm_item_aux_unit_id ON mdm_item(aux_unit_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_item_default_warehouse_id' AND object_id = OBJECT_ID('mdm_item'))
    CREATE INDEX idx_mdm_item_default_warehouse_id ON mdm_item(default_warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_warehouse_type' AND object_id = OBJECT_ID('mdm_warehouse'))
    CREATE INDEX idx_mdm_warehouse_type ON mdm_warehouse(warehouse_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_bin_warehouse' AND object_id = OBJECT_ID('mdm_bin'))
    CREATE INDEX idx_mdm_bin_warehouse ON mdm_bin(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_supplier_status' AND object_id = OBJECT_ID('mdm_supplier'))
    CREATE INDEX idx_mdm_supplier_status ON mdm_supplier(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_supplier_rating' AND object_id = OBJECT_ID('mdm_supplier'))
    CREATE INDEX idx_mdm_supplier_rating ON mdm_supplier(rating);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_customer_status' AND object_id = OBJECT_ID('mdm_customer'))
    CREATE INDEX idx_mdm_customer_status ON mdm_customer(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_customer_rating' AND object_id = OBJECT_ID('mdm_customer'))
    CREATE INDEX idx_mdm_customer_rating ON mdm_customer(rating);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_mdm_customer_salesman' AND object_id = OBJECT_ID('mdm_customer'))
    CREATE INDEX idx_mdm_customer_salesman ON mdm_customer(salesman_id);

PRINT '基础资料模块表创建完成';
GO

-- =====================================================
-- 三、库存管理模块 (inv_*)
-- =====================================================

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'inv_stock')
CREATE TABLE inv_stock (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    bin_id BIGINT,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    avg_cost DECIMAL(18,6) NOT NULL DEFAULT 0,
    locked_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_inv_stock UNIQUE(item_id, warehouse_id, bin_id, lot_no)
);

-- inv_stock 计算列
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('inv_stock') AND name = 'total_cost')
    ALTER TABLE inv_stock ADD total_cost AS (quantity * avg_cost) PERSISTED;
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('inv_stock') AND name = 'available_quantity')
    ALTER TABLE inv_stock ADD available_quantity AS (quantity - locked_quantity) PERSISTED;

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'inv_transaction')
CREATE TABLE inv_transaction (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transaction_no NVARCHAR(50) NOT NULL,
    transaction_type NVARCHAR(20) NOT NULL,
    transaction_date DATE NOT NULL,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    bin_id BIGINT,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,
    unit_cost DECIMAL(18,6),
    total_cost DECIMAL(18,2),
    reference_type NVARCHAR(30),
    reference_id BIGINT,
    reference_no NVARCHAR(50),
    reason_code NVARCHAR(50),
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    posted BIT NOT NULL DEFAULT 0,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_inv_transaction_no UNIQUE(transaction_no)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'inv_transfer')
CREATE TABLE inv_transfer (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transfer_no NVARCHAR(50) NOT NULL,
    from_warehouse_id BIGINT NOT NULL,
    to_warehouse_id BIGINT NOT NULL,
    transfer_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'inv_transfer_detail')
CREATE TABLE inv_transfer_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'inv_stocktake')
CREATE TABLE inv_stocktake (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    stocktake_no NVARCHAR(50) NOT NULL,
    warehouse_id BIGINT NOT NULL,
    stocktake_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'inv_stocktake_detail')
CREATE TABLE inv_stocktake_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    stocktake_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    system_quantity DECIMAL(18,4),
    actual_quantity DECIMAL(18,4),
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- inv_stocktake_detail 计算列
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('inv_stocktake_detail') AND name = 'diff_quantity')
    ALTER TABLE inv_stocktake_detail ADD diff_quantity AS (actual_quantity - system_quantity) PERSISTED;

-- 库存索引
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stock_item' AND object_id = OBJECT_ID('inv_stock'))
    CREATE INDEX idx_inv_stock_item ON inv_stock(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stock_warehouse' AND object_id = OBJECT_ID('inv_stock'))
    CREATE INDEX idx_inv_stock_warehouse ON inv_stock(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stock_lot' AND object_id = OBJECT_ID('inv_stock'))
    CREATE INDEX idx_inv_stock_lot ON inv_stock(lot_no);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transaction_no' AND object_id = OBJECT_ID('inv_transaction'))
    CREATE INDEX idx_inv_transaction_no ON inv_transaction(transaction_no);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transaction_item' AND object_id = OBJECT_ID('inv_transaction'))
    CREATE INDEX idx_inv_transaction_item ON inv_transaction(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transaction_warehouse' AND object_id = OBJECT_ID('inv_transaction'))
    CREATE INDEX idx_inv_transaction_warehouse ON inv_transaction(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transaction_date' AND object_id = OBJECT_ID('inv_transaction'))
    CREATE INDEX idx_inv_transaction_date ON inv_transaction(transaction_date);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transaction_type' AND object_id = OBJECT_ID('inv_transaction'))
    CREATE INDEX idx_inv_transaction_type ON inv_transaction(transaction_type);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transaction_reference' AND object_id = OBJECT_ID('inv_transaction'))
    CREATE INDEX idx_inv_transaction_reference ON inv_transaction(reference_type, reference_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transfer_from_warehouse' AND object_id = OBJECT_ID('inv_transfer'))
    CREATE INDEX idx_inv_transfer_from_warehouse ON inv_transfer(from_warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transfer_to_warehouse' AND object_id = OBJECT_ID('inv_transfer'))
    CREATE INDEX idx_inv_transfer_to_warehouse ON inv_transfer(to_warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transfer_status' AND object_id = OBJECT_ID('inv_transfer'))
    CREATE INDEX idx_inv_transfer_status ON inv_transfer(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transfer_detail_transfer' AND object_id = OBJECT_ID('inv_transfer_detail'))
    CREATE INDEX idx_inv_transfer_detail_transfer ON inv_transfer_detail(transfer_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_transfer_detail_item' AND object_id = OBJECT_ID('inv_transfer_detail'))
    CREATE INDEX idx_inv_transfer_detail_item ON inv_transfer_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stocktake_warehouse' AND object_id = OBJECT_ID('inv_stocktake'))
    CREATE INDEX idx_inv_stocktake_warehouse ON inv_stocktake(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stocktake_status' AND object_id = OBJECT_ID('inv_stocktake'))
    CREATE INDEX idx_inv_stocktake_status ON inv_stocktake(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stocktake_detail_stocktake' AND object_id = OBJECT_ID('inv_stocktake_detail'))
    CREATE INDEX idx_inv_stocktake_detail_stocktake ON inv_stocktake_detail(stocktake_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_inv_stocktake_detail_item' AND object_id = OBJECT_ID('inv_stocktake_detail'))
    CREATE INDEX idx_inv_stocktake_detail_item ON inv_stocktake_detail(item_id);

PRINT '库存管理模块表创建完成';
GO

-- =====================================================
-- 四、采购 + 销售 + 财务模块 (po_* / so_* / fin_*)
-- =====================================================

-- 采购订单主表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'po_order')
CREATE TABLE po_order (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_no NVARCHAR(50) NOT NULL,
    supplier_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'po_order_detail')
CREATE TABLE po_order_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    specification NVARCHAR(200),
    quantity DECIMAL(18,4) NOT NULL,
    unit_id BIGINT,
    unit_price DECIMAL(18,6) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 13.00,
    received_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- po_order_detail 计算列
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('po_order_detail') AND name = 'amount')
    ALTER TABLE po_order_detail ADD amount AS (quantity * unit_price) PERSISTED;
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('po_order_detail') AND name = 'tax_amount')
    ALTER TABLE po_order_detail ADD tax_amount AS (quantity * unit_price * tax_rate / 100) PERSISTED;

-- 采购入库单主表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'po_receipt')
CREATE TABLE po_receipt (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_no NVARCHAR(50) NOT NULL,
    order_id BIGINT,
    supplier_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'po_receipt_detail')
CREATE TABLE po_receipt_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    order_detail_id BIGINT,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,
    unit_id BIGINT,
    unit_cost DECIMAL(18,6),
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- 销售订单主表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'so_order')
CREATE TABLE so_order (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_no NVARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE,
    salesman_id BIGINT,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'so_order_detail')
CREATE TABLE so_order_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    specification NVARCHAR(200),
    quantity DECIMAL(18,4) NOT NULL,
    unit_id BIGINT,
    unit_price DECIMAL(18,6) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 13.00,
    shipped_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- so_order_detail 计算列
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('so_order_detail') AND name = 'amount')
    ALTER TABLE so_order_detail ADD amount AS (quantity * unit_price) PERSISTED;
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('so_order_detail') AND name = 'tax_amount')
    ALTER TABLE so_order_detail ADD tax_amount AS (quantity * unit_price * tax_rate / 100) PERSISTED;

-- 销售出库单主表
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'so_shipment')
CREATE TABLE so_shipment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    shipment_no NVARCHAR(50) NOT NULL,
    order_id BIGINT,
    customer_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    shipment_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'so_shipment_detail')
CREATE TABLE so_shipment_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    order_detail_id BIGINT,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,
    unit_id BIGINT,
    unit_price DECIMAL(18,6),
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- 应收单
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'fin_receivable')
CREATE TABLE fin_receivable (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receivable_no NVARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    reference_type NVARCHAR(30),
    reference_id BIGINT,
    reference_no NVARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,
    paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    currency NVARCHAR(10) NOT NULL DEFAULT 'CNY',
    due_date DATE,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_fin_receivable_receivable_no UNIQUE(receivable_no)
);

-- fin_receivable 计算列
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('fin_receivable') AND name = 'unpaid_amount')
    ALTER TABLE fin_receivable ADD unpaid_amount AS (amount - paid_amount) PERSISTED;

-- 收款单
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'fin_receipt')
CREATE TABLE fin_receipt (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_no NVARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    payment_method NVARCHAR(30),
    bank_info NVARCHAR(200),
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'fin_receipt_allocation')
CREATE TABLE fin_receipt_allocation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    receivable_id BIGINT NOT NULL,
    allocated_amount DECIMAL(18,2) NOT NULL
);

-- 应付单
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'fin_payable')
CREATE TABLE fin_payable (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    payable_no NVARCHAR(50) NOT NULL,
    supplier_id BIGINT NOT NULL,
    reference_type NVARCHAR(30),
    reference_id BIGINT,
    reference_no NVARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,
    paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    currency NVARCHAR(10) NOT NULL DEFAULT 'CNY',
    due_date DATE,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_fin_payable_payable_no UNIQUE(payable_no)
);

-- fin_payable 计算列
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('fin_payable') AND name = 'unpaid_amount')
    ALTER TABLE fin_payable ADD unpaid_amount AS (amount - paid_amount) PERSISTED;

-- 付款单
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'fin_payment')
CREATE TABLE fin_payment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    payment_no NVARCHAR(50) NOT NULL,
    supplier_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    payment_method NVARCHAR(30),
    bank_info NVARCHAR(200),
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
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
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'fin_payment_allocation')
CREATE TABLE fin_payment_allocation (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    payable_id BIGINT NOT NULL,
    allocated_amount DECIMAL(18,2) NOT NULL
);

-- 采购/销售/财务索引
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_order_supplier_id' AND object_id = OBJECT_ID('po_order'))
    CREATE INDEX idx_po_order_supplier_id ON po_order(supplier_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_order_status' AND object_id = OBJECT_ID('po_order'))
    CREATE INDEX idx_po_order_status ON po_order(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_order_order_date' AND object_id = OBJECT_ID('po_order'))
    CREATE INDEX idx_po_order_order_date ON po_order(order_date);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_order_detail_order_id' AND object_id = OBJECT_ID('po_order_detail'))
    CREATE INDEX idx_po_order_detail_order_id ON po_order_detail(order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_order_detail_item_id' AND object_id = OBJECT_ID('po_order_detail'))
    CREATE INDEX idx_po_order_detail_item_id ON po_order_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_order_detail_unit_id' AND object_id = OBJECT_ID('po_order_detail'))
    CREATE INDEX idx_po_order_detail_unit_id ON po_order_detail(unit_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_order_id' AND object_id = OBJECT_ID('po_receipt'))
    CREATE INDEX idx_po_receipt_order_id ON po_receipt(order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_supplier_id' AND object_id = OBJECT_ID('po_receipt'))
    CREATE INDEX idx_po_receipt_supplier_id ON po_receipt(supplier_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_warehouse_id' AND object_id = OBJECT_ID('po_receipt'))
    CREATE INDEX idx_po_receipt_warehouse_id ON po_receipt(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_status' AND object_id = OBJECT_ID('po_receipt'))
    CREATE INDEX idx_po_receipt_status ON po_receipt(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_detail_receipt_id' AND object_id = OBJECT_ID('po_receipt_detail'))
    CREATE INDEX idx_po_receipt_detail_receipt_id ON po_receipt_detail(receipt_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_detail_item_id' AND object_id = OBJECT_ID('po_receipt_detail'))
    CREATE INDEX idx_po_receipt_detail_item_id ON po_receipt_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_detail_unit_id' AND object_id = OBJECT_ID('po_receipt_detail'))
    CREATE INDEX idx_po_receipt_detail_unit_id ON po_receipt_detail(unit_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_po_receipt_detail_order_detail_id' AND object_id = OBJECT_ID('po_receipt_detail'))
    CREATE INDEX idx_po_receipt_detail_order_detail_id ON po_receipt_detail(order_detail_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_customer_id' AND object_id = OBJECT_ID('so_order'))
    CREATE INDEX idx_so_order_customer_id ON so_order(customer_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_salesman_id' AND object_id = OBJECT_ID('so_order'))
    CREATE INDEX idx_so_order_salesman_id ON so_order(salesman_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_status' AND object_id = OBJECT_ID('so_order'))
    CREATE INDEX idx_so_order_status ON so_order(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_order_date' AND object_id = OBJECT_ID('so_order'))
    CREATE INDEX idx_so_order_order_date ON so_order(order_date);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_detail_order_id' AND object_id = OBJECT_ID('so_order_detail'))
    CREATE INDEX idx_so_order_detail_order_id ON so_order_detail(order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_detail_item_id' AND object_id = OBJECT_ID('so_order_detail'))
    CREATE INDEX idx_so_order_detail_item_id ON so_order_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_order_detail_unit_id' AND object_id = OBJECT_ID('so_order_detail'))
    CREATE INDEX idx_so_order_detail_unit_id ON so_order_detail(unit_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_order_id' AND object_id = OBJECT_ID('so_shipment'))
    CREATE INDEX idx_so_shipment_order_id ON so_shipment(order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_customer_id' AND object_id = OBJECT_ID('so_shipment'))
    CREATE INDEX idx_so_shipment_customer_id ON so_shipment(customer_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_warehouse_id' AND object_id = OBJECT_ID('so_shipment'))
    CREATE INDEX idx_so_shipment_warehouse_id ON so_shipment(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_status' AND object_id = OBJECT_ID('so_shipment'))
    CREATE INDEX idx_so_shipment_status ON so_shipment(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_detail_shipment_id' AND object_id = OBJECT_ID('so_shipment_detail'))
    CREATE INDEX idx_so_shipment_detail_shipment_id ON so_shipment_detail(shipment_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_detail_item_id' AND object_id = OBJECT_ID('so_shipment_detail'))
    CREATE INDEX idx_so_shipment_detail_item_id ON so_shipment_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_detail_unit_id' AND object_id = OBJECT_ID('so_shipment_detail'))
    CREATE INDEX idx_so_shipment_detail_unit_id ON so_shipment_detail(unit_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_so_shipment_detail_order_detail_id' AND object_id = OBJECT_ID('so_shipment_detail'))
    CREATE INDEX idx_so_shipment_detail_order_detail_id ON so_shipment_detail(order_detail_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receivable_customer_id' AND object_id = OBJECT_ID('fin_receivable'))
    CREATE INDEX idx_fin_receivable_customer_id ON fin_receivable(customer_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receivable_status' AND object_id = OBJECT_ID('fin_receivable'))
    CREATE INDEX idx_fin_receivable_status ON fin_receivable(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receivable_reference_id' AND object_id = OBJECT_ID('fin_receivable'))
    CREATE INDEX idx_fin_receivable_reference_id ON fin_receivable(reference_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receipt_customer_id' AND object_id = OBJECT_ID('fin_receipt'))
    CREATE INDEX idx_fin_receipt_customer_id ON fin_receipt(customer_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receipt_status' AND object_id = OBJECT_ID('fin_receipt'))
    CREATE INDEX idx_fin_receipt_status ON fin_receipt(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receipt_alloc_receipt_id' AND object_id = OBJECT_ID('fin_receipt_allocation'))
    CREATE INDEX idx_fin_receipt_alloc_receipt_id ON fin_receipt_allocation(receipt_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_receipt_alloc_receivable_id' AND object_id = OBJECT_ID('fin_receipt_allocation'))
    CREATE INDEX idx_fin_receipt_alloc_receivable_id ON fin_receipt_allocation(receivable_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payable_supplier_id' AND object_id = OBJECT_ID('fin_payable'))
    CREATE INDEX idx_fin_payable_supplier_id ON fin_payable(supplier_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payable_status' AND object_id = OBJECT_ID('fin_payable'))
    CREATE INDEX idx_fin_payable_status ON fin_payable(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payable_reference_id' AND object_id = OBJECT_ID('fin_payable'))
    CREATE INDEX idx_fin_payable_reference_id ON fin_payable(reference_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payment_supplier_id' AND object_id = OBJECT_ID('fin_payment'))
    CREATE INDEX idx_fin_payment_supplier_id ON fin_payment(supplier_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payment_status' AND object_id = OBJECT_ID('fin_payment'))
    CREATE INDEX idx_fin_payment_status ON fin_payment(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payment_alloc_payment' AND object_id = OBJECT_ID('fin_payment_allocation'))
    CREATE INDEX idx_fin_payment_alloc_payment ON fin_payment_allocation(payment_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_fin_payment_alloc_payable' AND object_id = OBJECT_ID('fin_payment_allocation'))
    CREATE INDEX idx_fin_payment_alloc_payable ON fin_payment_allocation(payable_id);

PRINT '采购/销售/财务模块表创建完成';
GO

-- =====================================================
-- 五、生产管理模块 (pp_*)
-- =====================================================

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'pp_bom')
CREATE TABLE pp_bom (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bom_code NVARCHAR(50) NOT NULL,
    item_id BIGINT NOT NULL,
    version NVARCHAR(20) NOT NULL DEFAULT '1.0',
    base_quantity DECIMAL(18,4) NOT NULL DEFAULT 1,
    status TINYINT NOT NULL DEFAULT 1,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_pp_bom_bom_code UNIQUE(bom_code)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'pp_bom_detail')
CREATE TABLE pp_bom_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bom_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity DECIMAL(18,6) NOT NULL,
    loss_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'pp_work_order')
CREATE TABLE pp_work_order (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_no NVARCHAR(50) NOT NULL,
    bom_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    completed_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    warehouse_id BIGINT,
    plan_start_date DATE,
    plan_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    released_by BIGINT,
    released_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_pp_work_order_order_no UNIQUE(order_no)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'pp_material_issue')
CREATE TABLE pp_material_issue (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue_no NVARCHAR(50) NOT NULL,
    work_order_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    issue_date DATE NOT NULL,
    issue_type NVARCHAR(20) NOT NULL DEFAULT 'ISSUE',
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    audited_by BIGINT,
    audited_at DATETIME2,
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_pp_material_issue_issue_no UNIQUE(issue_no)
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'pp_material_issue_detail')
CREATE TABLE pp_material_issue_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    lot_no NVARCHAR(50),
    quantity DECIMAL(18,4) NOT NULL,
    remark NVARCHAR(200),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'pp_report')
CREATE TABLE pp_report (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    report_no NVARCHAR(50) NOT NULL,
    work_order_id BIGINT NOT NULL,
    report_date DATE NOT NULL,
    qualified_quantity DECIMAL(18,4) NOT NULL,
    defect_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
    work_hours DECIMAL(10,2),
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_pp_report_report_no UNIQUE(report_no)
);

-- 生产管理索引
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_bom_item_id' AND object_id = OBJECT_ID('pp_bom'))
    CREATE INDEX idx_pp_bom_item_id ON pp_bom(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_bom_status' AND object_id = OBJECT_ID('pp_bom'))
    CREATE INDEX idx_pp_bom_status ON pp_bom(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_bom_detail_bom_id' AND object_id = OBJECT_ID('pp_bom_detail'))
    CREATE INDEX idx_pp_bom_detail_bom_id ON pp_bom_detail(bom_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_bom_detail_item_id' AND object_id = OBJECT_ID('pp_bom_detail'))
    CREATE INDEX idx_pp_bom_detail_item_id ON pp_bom_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_work_order_bom_id' AND object_id = OBJECT_ID('pp_work_order'))
    CREATE INDEX idx_pp_work_order_bom_id ON pp_work_order(bom_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_work_order_item_id' AND object_id = OBJECT_ID('pp_work_order'))
    CREATE INDEX idx_pp_work_order_item_id ON pp_work_order(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_work_order_status' AND object_id = OBJECT_ID('pp_work_order'))
    CREATE INDEX idx_pp_work_order_status ON pp_work_order(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_work_order_plan_start_date' AND object_id = OBJECT_ID('pp_work_order'))
    CREATE INDEX idx_pp_work_order_plan_start_date ON pp_work_order(plan_start_date);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_work_order_warehouse_id' AND object_id = OBJECT_ID('pp_work_order'))
    CREATE INDEX idx_pp_work_order_warehouse_id ON pp_work_order(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_material_issue_work_order_id' AND object_id = OBJECT_ID('pp_material_issue'))
    CREATE INDEX idx_pp_material_issue_work_order_id ON pp_material_issue(work_order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_material_issue_warehouse_id' AND object_id = OBJECT_ID('pp_material_issue'))
    CREATE INDEX idx_pp_material_issue_warehouse_id ON pp_material_issue(warehouse_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_material_issue_status' AND object_id = OBJECT_ID('pp_material_issue'))
    CREATE INDEX idx_pp_material_issue_status ON pp_material_issue(status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_material_issue_detail_issue_id' AND object_id = OBJECT_ID('pp_material_issue_detail'))
    CREATE INDEX idx_pp_material_issue_detail_issue_id ON pp_material_issue_detail(issue_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_material_issue_detail_item_id' AND object_id = OBJECT_ID('pp_material_issue_detail'))
    CREATE INDEX idx_pp_material_issue_detail_item_id ON pp_material_issue_detail(item_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_report_work_order_id' AND object_id = OBJECT_ID('pp_report'))
    CREATE INDEX idx_pp_report_work_order_id ON pp_report(work_order_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_pp_report_report_date' AND object_id = OBJECT_ID('pp_report'))
    CREATE INDEX idx_pp_report_report_date ON pp_report(report_date);

PRINT '生产管理模块表创建完成';
GO

-- =====================================================
-- 六、初始数据
-- =====================================================

-- 仅当 sys_role 表为空时插入初始数据（防止重复）
IF NOT EXISTS (SELECT 1 FROM sys_role)
BEGIN
    -- 默认角色
    INSERT INTO sys_role (role_code, role_name, description, sort_order, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    ('ADMIN', '系统管理员', '拥有所有权限', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('USER', '普通用户', '基本操作权限', 2, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 默认用户（密码：admin123，BCrypt加密）
    INSERT INTO sys_user (username, password, nickname, email, phone, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@opus-erp.com', '13800138000', 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 用户角色关联
    INSERT INTO sys_user_role (user_id, role_id, created_at) VALUES (1, 1, GETDATE());

    -- 一级菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (0, '系统管理', 'M', '/system', NULL, NULL, 'Setting', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '基础资料', 'M', '/master', NULL, NULL, 'Document', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '采购管理', 'M', '/purchase', NULL, NULL, 'ShoppingCart', 3, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '销售管理', 'M', '/sales', NULL, NULL, 'Sell', 4, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '库存管理', 'M', '/inventory', NULL, NULL, 'Box', 5, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '生产管理', 'M', '/production', NULL, NULL, 'SetUp', 6, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '财务管理', 'M', '/finance', NULL, NULL, 'Money', 7, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (0, '报表中心', 'M', '/report', NULL, NULL, 'DataLine', 8, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 系统管理子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (1, '用户管理', 'C', '/system/user', 'system/user/index', 'sys:user:list', 'User', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (1, '角色管理', 'C', '/system/role', 'system/role/index', 'sys:role:list', 'UserFilled', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'sys:menu:list', 'Menu', 3, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (1, '字典管理', 'C', '/system/dict', 'system/dict/index', 'sys:dict:list', 'Collection', 4, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 用户管理按钮权限
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (9, '用户查询', 'F', NULL, NULL, 'sys:user:query', NULL, 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (9, '用户新增', 'F', NULL, NULL, 'sys:user:add', NULL, 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (9, '用户修改', 'F', NULL, NULL, 'sys:user:edit', NULL, 3, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (9, '用户删除', 'F', NULL, NULL, 'sys:user:remove', NULL, 4, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 基础资料子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (2, '物料管理', 'C', '/master/item', 'master/item/index', 'mdm:item:list', 'Goods', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (2, '仓库管理', 'C', '/master/warehouse', 'master/warehouse/index', 'mdm:warehouse:list', 'House', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (2, '供应商管理', 'C', '/master/supplier', 'master/supplier/index', 'mdm:supplier:list', 'Avatar', 3, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (2, '客户管理', 'C', '/master/customer', 'master/customer/index', 'mdm:customer:list', 'User', 4, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 采购管理子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (3, '采购订单', 'C', '/purchase/order', 'purchase/order/index', 'po:order:list', 'Document', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (3, '采购入库', 'C', '/purchase/receipt', 'purchase/receipt/index', 'po:receipt:list', 'DocumentAdd', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 销售管理子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (4, '销售订单', 'C', '/sales/order', 'sales/order/index', 'so:order:list', 'Document', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (4, '销售出库', 'C', '/sales/shipment', 'sales/shipment/index', 'so:shipment:list', 'DocumentRemove', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 库存管理子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (5, '即时库存', 'C', '/inventory/stock', 'inventory/stock/index', 'inv:stock:list', 'Box', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (5, '库存流水', 'C', '/inventory/transaction', 'inventory/transaction/index', 'inv:transaction:list', 'List', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (5, '调拨管理', 'C', '/inventory/transfer', 'inventory/transfer/index', 'inv:transfer:list', 'Sort', 3, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (5, '盘点管理', 'C', '/inventory/stocktake', 'inventory/stocktake/index', 'inv:stocktake:list', 'Notebook', 4, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 生产管理子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (6, 'BOM管理', 'C', '/production/bom', 'production/bom/index', 'pp:bom:list', 'Tickets', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (6, '生产工单', 'C', '/production/workorder', 'production/workorder/index', 'pp:workorder:list', 'Document', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 财务管理子菜单
    INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    (7, '应收管理', 'C', '/finance/receivable', 'finance/receivable/index', 'fin:receivable:list', 'Money', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    (7, '应付管理', 'C', '/finance/payable', 'finance/payable/index', 'fin:payable:list', 'Wallet', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 角色菜单关联
    INSERT INTO sys_role_menu (role_id, menu_id, created_at)
    SELECT 1, id, GETDATE() FROM sys_menu WHERE deleted = 0;

    -- 字典类型
    INSERT INTO sys_dict_type (dict_type, dict_name, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    ('item_type', '物料类型', 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('warehouse_type', '仓库类型', 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('doc_status', '单据状态', 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('abc_class', 'ABC分类', 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('payment_method', '付款方式', 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('transaction_type', '库存交易类型', 1, 1, GETDATE(), 1, GETDATE(), 0);

    -- 字典数据
    INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_color, sort_order, status, created_by, created_at, updated_by, updated_at, deleted)
    VALUES
    ('item_type', '原材料', 'RAW', 'primary', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('item_type', '半成品', 'SEMI', 'warning', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('item_type', '成品', 'FINISHED', 'success', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('item_type', '辅材', 'AUXILIARY', 'info', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('warehouse_type', '原料仓', 'RAW', 'primary', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('warehouse_type', '半成品仓', 'SEMI', 'warning', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('warehouse_type', '成品仓', 'FINISHED', 'success', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('warehouse_type', '退货仓', 'RETURN', 'danger', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('warehouse_type', '不良品仓', 'DEFECTIVE', 'danger', 5, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('doc_status', '草稿', 'DRAFT', 'info', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('doc_status', '已审核', 'AUDITED', 'success', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('doc_status', '已完成', 'COMPLETED', 'success', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('doc_status', '已取消', 'CANCELLED', 'danger', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('doc_status', '已关闭', 'CLOSED', 'info', 5, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('abc_class', 'A类', 'A', 'danger', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('abc_class', 'B类', 'B', 'warning', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('abc_class', 'C类', 'C', 'info', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('payment_method', '银行转账', 'BANK', 'primary', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('payment_method', '现金', 'CASH', 'success', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('payment_method', '支票', 'CHECK', 'warning', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('transaction_type', '入库', 'RECEIPT', 'success', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('transaction_type', '出库', 'ISSUE', 'danger', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('transaction_type', '调拨', 'TRANSFER', 'primary', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('transaction_type', '调整', 'ADJUSTMENT', 'warning', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
    ('transaction_type', '退货', 'RETURN', 'info', 5, 1, 1, GETDATE(), 1, GETDATE(), 0);

    PRINT '初始数据插入完成';
END
ELSE
    PRINT '初始数据已存在，跳过插入';
GO

PRINT '========================================';
PRINT 'Opus ERP 数据库初始化完成！';
PRINT '========================================';
GO
