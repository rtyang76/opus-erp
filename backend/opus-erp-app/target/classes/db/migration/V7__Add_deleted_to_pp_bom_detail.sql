-- =====================================================
-- Opus ERP - Add deleted field to pp_bom_detail
-- Version: V7
-- Description: 为 BOM 明细表添加逻辑删除字段
-- =====================================================

-- 为 pp_bom_detail 表添加 deleted 字段
ALTER TABLE pp_bom_detail ADD deleted BIT NOT NULL DEFAULT 0;

-- 添加审计字段（与 BaseEntity 一致）
ALTER TABLE pp_bom_detail ADD created_by BIGINT;
ALTER TABLE pp_bom_detail ADD created_at DATETIME2 NOT NULL DEFAULT GETDATE();
ALTER TABLE pp_bom_detail ADD updated_by BIGINT;
ALTER TABLE pp_bom_detail ADD updated_at DATETIME2 NOT NULL DEFAULT GETDATE();
