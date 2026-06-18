-- =====================================================
-- Opus ERP - System Management Tables
-- Version: V1
-- Description: 创建系统管理相关表
-- =====================================================

-- 用户表
CREATE TABLE sys_user (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL,
    password NVARCHAR(200) NOT NULL,
    nickname NVARCHAR(50),
    email NVARCHAR(100),
    phone NVARCHAR(20),
    avatar NVARCHAR(200),
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    dept_id BIGINT,
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT UQ_sys_user_username UNIQUE(username)
);

-- 角色表
CREATE TABLE sys_role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_code NVARCHAR(50) NOT NULL,
    role_name NVARCHAR(50) NOT NULL,
    description NVARCHAR(200),
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT UQ_sys_role_role_code UNIQUE(role_code)
);

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT UQ_sys_user_role UNIQUE(user_id, role_id)
);

-- 菜单/权限表
CREATE TABLE sys_menu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    menu_name NVARCHAR(50) NOT NULL,
    menu_type CHAR(1) NOT NULL,  -- M=目录, C=菜单, F=按钮
    path NVARCHAR(200),
    component NVARCHAR(200),
    perms NVARCHAR(100),
    icon NVARCHAR(100),
    sort_order INT NOT NULL DEFAULT 0,
    visible TINYINT NOT NULL DEFAULT 1,  -- 1=显示, 0=隐藏
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0
);

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT UQ_sys_role_menu UNIQUE(role_id, menu_id)
);

-- 字典类型表
CREATE TABLE sys_dict_type (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dict_type NVARCHAR(100) NOT NULL,
    dict_name NVARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT UQ_sys_dict_type_dict_type UNIQUE(dict_type)
);

-- 字典数据表
CREATE TABLE sys_dict_data (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dict_type NVARCHAR(100) NOT NULL,
    dict_label NVARCHAR(100) NOT NULL,
    dict_value NVARCHAR(100) NOT NULL,
    dict_color NVARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,  -- 1=启用, 0=禁用
    remark NVARCHAR(500),
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted BIT NOT NULL DEFAULT 0,
    CONSTRAINT UQ_sys_dict_data UNIQUE(dict_type, dict_value)
);

-- 操作日志表
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
    status TINYINT,  -- 1=成功, 0=失败
    error_msg NVARCHAR(MAX),
    duration BIGINT,  -- 执行时长（毫秒）
    oper_time DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 登录日志表
CREATE TABLE sys_login_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50),
    ip NVARCHAR(50),
    location NVARCHAR(100),
    browser NVARCHAR(100),
    os NVARCHAR(100),
    status TINYINT NOT NULL,  -- 1=成功, 0=失败
    msg NVARCHAR(200),
    login_time DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 创建索引
CREATE INDEX idx_sys_user_username ON sys_user(username);
CREATE INDEX idx_sys_user_status ON sys_user(status);
CREATE INDEX idx_sys_user_dept_id ON sys_user(dept_id);

CREATE INDEX idx_sys_role_status ON sys_role(status);

CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);

CREATE INDEX idx_sys_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_sys_menu_type ON sys_menu(menu_type);

CREATE INDEX idx_sys_role_menu_role_id ON sys_role_menu(role_id);
CREATE INDEX idx_sys_role_menu_menu_id ON sys_role_menu(menu_id);

CREATE INDEX idx_sys_dict_type_dict_type ON sys_dict_type(dict_type);

CREATE INDEX idx_sys_dict_data_dict_type ON sys_dict_data(dict_type);

CREATE INDEX idx_sys_oper_log_user_id ON sys_oper_log(user_id);
CREATE INDEX idx_sys_oper_log_oper_time ON sys_oper_log(oper_time);

CREATE INDEX idx_sys_login_log_username ON sys_login_log(username);
CREATE INDEX idx_sys_login_log_login_time ON sys_login_log(login_time);
