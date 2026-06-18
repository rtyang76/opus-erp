-- =====================================================
-- Opus ERP - Production Tables
-- Version: V6
-- Description: 创建生产管理相关表
-- 精度规范：数量 DECIMAL(18,4)，损耗率 DECIMAL(5,2)
-- 命名规范：索引 idx_，唯一约束 uq_，全小写，完整字段名
-- =====================================================

-- BOM 主表
CREATE TABLE pp_bom (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bom_code NVARCHAR(50) NOT NULL,  -- BOM编码
    item_id BIGINT NOT NULL,  -- 成品/半成品物料ID
    version NVARCHAR(20) NOT NULL DEFAULT '1.0',  -- 版本号
    base_quantity DECIMAL(18,4) NOT NULL DEFAULT 1,  -- 基准数量
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_pp_bom_bom_code UNIQUE(bom_code)
);

-- BOM 明细表（子件）
CREATE TABLE pp_bom_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bom_id BIGINT NOT NULL,  -- BOM主表ID
    item_id BIGINT NOT NULL,  -- 子件物料ID
    quantity DECIMAL(18,6) NOT NULL,  -- 单位用量
    loss_rate DECIMAL(5,2) NOT NULL DEFAULT 0,  -- 损耗率%
    remark NVARCHAR(200)
);

-- 生产工单主表
CREATE TABLE pp_work_order (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_no NVARCHAR(50) NOT NULL,  -- 工单号
    bom_id BIGINT NOT NULL,  -- BOM ID
    item_id BIGINT NOT NULL,  -- 生产成品ID
    quantity DECIMAL(18,4) NOT NULL,  -- 计划数量
    completed_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,  -- 完工数量
    warehouse_id BIGINT,  -- 入库仓库
    plan_start_date DATE,  -- 计划开始日期
    plan_end_date DATE,  -- 计划结束日期
    actual_start_date DATE,  -- 实际开始日期
    actual_end_date DATE,  -- 实际结束日期
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING/RELEASED/IN_PROGRESS/COMPLETED/CLOSED
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

-- 领料单主表
CREATE TABLE pp_material_issue (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue_no NVARCHAR(50) NOT NULL,  -- 领料单号
    work_order_id BIGINT NOT NULL,  -- 关联工单ID
    warehouse_id BIGINT NOT NULL,  -- 领料仓库
    issue_date DATE NOT NULL,
    issue_type NVARCHAR(20) NOT NULL DEFAULT 'ISSUE',  -- ISSUE=领料, RETURN=退料
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT/AUDITED/CANCELLED
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

-- 领料单明细表
CREATE TABLE pp_material_issue_detail (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue_id BIGINT NOT NULL,  -- 领料单ID
    item_id BIGINT NOT NULL,  -- 物料ID
    lot_no NVARCHAR(50),  -- 批次号
    quantity DECIMAL(18,4) NOT NULL,  -- 领料/退料数量
    remark NVARCHAR(200)
);

-- 报工记录表
CREATE TABLE pp_report (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    report_no NVARCHAR(50) NOT NULL,  -- 报工单号
    work_order_id BIGINT NOT NULL,  -- 关联工单ID
    report_date DATE NOT NULL,
    qualified_quantity DECIMAL(18,4) NOT NULL,  -- 合格数量
    defect_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,  -- 不良数量
    work_hours DECIMAL(10,2),  -- 工时
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT uq_pp_report_report_no UNIQUE(report_no)
);

-- ========== 创建索引 ==========

-- BOM 索引
CREATE INDEX idx_pp_bom_item_id ON pp_bom(item_id);
CREATE INDEX idx_pp_bom_status ON pp_bom(status);

-- BOM 明细索引
CREATE INDEX idx_pp_bom_detail_bom_id ON pp_bom_detail(bom_id);
CREATE INDEX idx_pp_bom_detail_item_id ON pp_bom_detail(item_id);

-- 生产工单索引
CREATE INDEX idx_pp_work_order_bom_id ON pp_work_order(bom_id);
CREATE INDEX idx_pp_work_order_item_id ON pp_work_order(item_id);
CREATE INDEX idx_pp_work_order_status ON pp_work_order(status);
CREATE INDEX idx_pp_work_order_plan_start_date ON pp_work_order(plan_start_date);

-- 领料单索引
CREATE INDEX idx_pp_material_issue_work_order_id ON pp_material_issue(work_order_id);
CREATE INDEX idx_pp_material_issue_warehouse_id ON pp_material_issue(warehouse_id);
CREATE INDEX idx_pp_material_issue_status ON pp_material_issue(status);

-- 领料单明细索引
CREATE INDEX idx_pp_material_issue_detail_issue_id ON pp_material_issue_detail(issue_id);
CREATE INDEX idx_pp_material_issue_detail_item_id ON pp_material_issue_detail(item_id);

-- 报工记录索引
CREATE INDEX idx_pp_report_work_order_id ON pp_report(work_order_id);
CREATE INDEX idx_pp_report_report_date ON pp_report(report_date);
