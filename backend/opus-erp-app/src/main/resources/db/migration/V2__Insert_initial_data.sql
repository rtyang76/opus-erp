-- =====================================================
-- Opus ERP - Initial Data
-- Version: V2
-- Description: 插入初始数据（管理员、角色、菜单、字典等）
-- =====================================================

-- 插入默认角色
INSERT INTO sys_role (role_code, role_name, description, sort_order, status, created_by, created_at, updated_by, updated_at, deleted)
VALUES
('ADMIN', '系统管理员', '拥有所有权限', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('USER', '普通用户', '基本操作权限', 2, 1, 1, GETDATE(), 1, GETDATE(), 0);

-- 插入默认用户（密码：admin123，BCrypt加密）
INSERT INTO sys_user (username, password, nickname, email, phone, status, created_by, created_at, updated_by, updated_at, deleted)
VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@opus-erp.com', '13800138000', 1, 1, GETDATE(), 1, GETDATE(), 0);

-- 插入用户角色关联（admin -> ADMIN）
INSERT INTO sys_user_role (user_id, role_id, created_at)
VALUES (1, 1, GETDATE());

-- 插入菜单数据
INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, visible, status, created_by, created_at, updated_by, updated_at, deleted)
VALUES
-- 一级菜单：系统管理
(0, '系统管理', 'M', '/system', NULL, NULL, 'Setting', 1, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：基础资料
(0, '基础资料', 'M', '/master', NULL, NULL, 'Document', 2, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：采购管理
(0, '采购管理', 'M', '/purchase', NULL, NULL, 'ShoppingCart', 3, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：销售管理
(0, '销售管理', 'M', '/sales', NULL, NULL, 'Sell', 4, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：库存管理
(0, '库存管理', 'M', '/inventory', NULL, NULL, 'Box', 5, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：生产管理
(0, '生产管理', 'M', '/production', NULL, NULL, 'SetUp', 6, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：财务管理
(0, '财务管理', 'M', '/finance', NULL, NULL, 'Money', 7, 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 一级菜单：报表中心
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

-- 角色菜单关联（管理员拥有所有菜单权限）
INSERT INTO sys_role_menu (role_id, menu_id, created_at)
SELECT 1, id, GETDATE() FROM sys_menu WHERE deleted = 0;

-- 插入字典类型
INSERT INTO sys_dict_type (dict_type, dict_name, status, created_by, created_at, updated_by, updated_at, deleted)
VALUES
('item_type', '物料类型', 1, 1, GETDATE(), 1, GETDATE(), 0),
('warehouse_type', '仓库类型', 1, 1, GETDATE(), 1, GETDATE(), 0),
('doc_status', '单据状态', 1, 1, GETDATE(), 1, GETDATE(), 0),
('abc_class', 'ABC分类', 1, 1, GETDATE(), 1, GETDATE(), 0),
('payment_method', '付款方式', 1, 1, GETDATE(), 1, GETDATE(), 0),
('transaction_type', '库存交易类型', 1, 1, GETDATE(), 1, GETDATE(), 0);

-- 插入字典数据
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_color, sort_order, status, created_by, created_at, updated_by, updated_at, deleted)
VALUES
-- 物料类型
('item_type', '原材料', 'RAW', 'primary', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('item_type', '半成品', 'SEMI', 'warning', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
('item_type', '成品', 'FINISHED', 'success', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
('item_type', '辅材', 'AUXILIARY', 'info', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 仓库类型
('warehouse_type', '原料仓', 'RAW', 'primary', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('warehouse_type', '半成品仓', 'SEMI', 'warning', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
('warehouse_type', '成品仓', 'FINISHED', 'success', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
('warehouse_type', '退货仓', 'RETURN', 'danger', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
('warehouse_type', '不良品仓', 'DEFECTIVE', 'danger', 5, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 单据状态
('doc_status', '草稿', 'DRAFT', 'info', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('doc_status', '已审核', 'AUDITED', 'success', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
('doc_status', '已完成', 'COMPLETED', 'success', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
('doc_status', '已取消', 'CANCELLED', 'danger', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
('doc_status', '已关闭', 'CLOSED', 'info', 5, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- ABC分类
('abc_class', 'A类', 'A', 'danger', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('abc_class', 'B类', 'B', 'warning', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
('abc_class', 'C类', 'C', 'info', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 付款方式
('payment_method', '银行转账', 'BANK', 'primary', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('payment_method', '现金', 'CASH', 'success', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
('payment_method', '支票', 'CHECK', 'warning', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
-- 库存交易类型
('transaction_type', '入库', 'RECEIPT', 'success', 1, 1, 1, GETDATE(), 1, GETDATE(), 0),
('transaction_type', '出库', 'ISSUE', 'danger', 2, 1, 1, GETDATE(), 1, GETDATE(), 0),
('transaction_type', '调拨', 'TRANSFER', 'primary', 3, 1, 1, GETDATE(), 1, GETDATE(), 0),
('transaction_type', '调整', 'ADJUSTMENT', 'warning', 4, 1, 1, GETDATE(), 1, GETDATE(), 0),
('transaction_type', '退货', 'RETURN', 'info', 5, 1, 1, GETDATE(), 1, GETDATE(), 0);
