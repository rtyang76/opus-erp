# CLAUDE.md — Opus-ERP 项目宪法

> 版本：v1.1
> 最后更新：2026-06-18

> 本文件是 Claude Code 在本项目中的最高优先级指令。
> 任何代码生成、修改、建议都必须遵守本文件规定。
>
> **特别说明**：本文件及 `docs/*.md` 规范文档的修改权归项目owner/架构师所有，AI 只有建议权，不得自行修改。

---

## 0. AI 行为边界（高优先级）

本章节专门约束 AI 在自动生成代码时的行为，防止“自我膨胀”和越界决策。

### 0.1 不可逾越的红线

- ❌ **AI 不得修改 `CLAUDE.md` 及 `docs/*.md` 规范文件**。如发现规范有冲突或需更新，必须向项目owner提出建议，由人工决策后修改。
- ❌ **AI 不得自行引入新的 Maven/npm 依赖或第三方服务**。新增依赖必须经人工审批。
- ❌ **AI 不得创建新的业务模块或核心表**。新增模块/表必须先在 `PLAN.md` 中确认位置，经人工确认后方可实施。
- ❌ **AI 不得在生产环境配置中写入明文密码、密钥或Token**。所有敏感配置必须从环境变量读取。
- ❌ **AI 遇到规范冲突或二义性需求时，必须暂停并询问**，不得自行折中或假设。

### 0.2 每次编码任务的强制动作

- [ ] 任务开始前，阅读与本任务相关的 `CONTRACTS.md` 章节。
- [ ] 涉及数据库变更时，必须编写新的 Flyway 迁移脚本，**禁止修改已执行脚本**。
- [ ] 涉及新接口时，必须同步更新 `CONTRACTS.md` 对应章节（如已存在）。
- [ ] 功能完成后，必须更新 `docs/PROGRESS.md` 标记对应任务状态。
- [ ] 完成后对照 `docs/AI_CHECKLIST.md` 进行自检。

### 0.3 规范冲突处理原则

当本文件与其他文档出现冲突时：
1. 优先以 `CLAUDE.md` 为准；
2. 若仍无法判断，**暂停并向用户/项目owner询问**，不要猜测。

---

## 1. 技术栈（不可更改）

| 层级 | 技术 | 版本 | 备注 |
|------|------|------|------|
| JDK | OpenJDK | 21 LTS | 已配置于 VSCode |
| 框架 | Spring Boot | 3.2.x | 不得降级到 2.x |
| ORM | MyBatis-Plus | 3.5.x | 必须配置 `DbType.SQL_SERVER` |
| 数据库 | SQL Server | 2022 | 所有表名/字段使用下划线命名 |
| 缓存 | Redis | 7.x | 用于 Token/字典缓存 |
| 数据库迁移 | Flyway | 10.x | 版本号递增，不可修改已执行脚本 |
| 前端框架 | Vue | 3.4.x | Composition API + `<script setup>` |
| UI 组件库 | Element Plus | 2.5.x | 按需导入 |
| 构建工具 | Vite | 5.x | - |
| 状态管理 | Pinia | 2.x | - |
| 类型系统 | TypeScript | 5.x | 前端强制使用 |
| 测试 | Playwright | 1.40.x | Node.js 版本 |
| 容器化 | Docker Compose | 2.x | - |

---

## 2. 命名规范

### 2.1 后端

| 元素 | 规范 | 示例 |
|------|------|------|
| 包名 | `com.opus.erp.{module}` | `com.opus.erp.purchase` |
| 实体类 | 大驼峰，表名去掉前缀 | 表 `po_order` → `PoOrder` |
| Mapper | 实体名 + Mapper | `PoOrderMapper` |
| Service | 实体名 + Service | `PoOrderService` |
| Controller | 实体名 + Controller | `PoOrderController` |
| DTO | 实体名 + DTO/Request/Response | `PoOrderDTO` |
| 枚举 | 大驼峰 + Enum 后缀 | `StatusEnum` |
| 常量 | 大写下划线 | `MAX_RETRY_COUNT` |

### 2.2 数据库

| 元素 | 规范 | 示例 |
|------|------|------|
| 表名 | 小写下划线，模块前缀 | `po_order`, `so_order`, `inv_stock` |
| 字段名 | 小写下划线 | `created_at`, `unit_price` |
| 主键 | `id` | BIGINT IDENTITY(1,1) |
| 外键 | `{关联表名}_id` | `supplier_id`, `item_id` |
| 索引 | `idx_{表名}_{字段}` | `idx_po_order_supplier_id` |
| 唯一约束 | `uq_{表名}_{字段}` | `uq_item_code` |

### 2.3 前端

| 元素 | 规范 | 示例 |
|------|------|------|
| 组件文件 | 大驼峰 `.vue` | `CrudTable.vue` |
| 页面文件 | 小写 kebab-case | `user-list.vue` |
| API 文件 | 小写 kebab-case | `purchase.ts` |
| Store 文件 | 小写 kebab-case | `auth.ts` |
| Composable | `use` 前缀 | `useCrud.ts` |
| CSS 类名 | BEM 或 kebab-case | `.po-order__header` |

### 2.4 API 路径

```
POST   /api/{module}/{resource}          # 创建
GET    /api/{module}/{resource}/{id}      # 查询单个
PUT    /api/{module}/{resource}/{id}      # 更新
DELETE /api/{module}/{resource}/{id}      # 删除
GET    /api/{module}/{resource}           # 分页查询
POST   /api/{module}/{resource}/action    # 特殊操作（审核/取消等）
```

示例：
- `POST /api/purchase/orders` — 创建采购订单
- `POST /api/purchase/orders/{id}/audit` — 审核采购订单
- `GET /api/inventory/stock` — 查询库存列表

---

## 3. 代码禁令（绝对不允许）

### 3.1 架构禁令

- ❌ **禁止在 Controller 中写业务逻辑** — Controller 只做参数校验 + 调用 Service
- ❌ **禁止直接操作库存表** — 所有库存变动必须通过 `InvTransactionService`
- ❌ **禁止跨模块直接调用 Mapper** — 必须通过 Service 接口
- ❌ **禁止使用 `@Autowired` 字段注入** — 必须使用构造器注入
- ❌ **禁止在 Entity 中写跨实体/跨模块业务方法** — Entity 以字段和 getter/setter 为主
  - ✅ 允许无状态的纯计算方法（如 `getTotalAmount()` 汇总本实体字段）
  - ❌ 禁止调用 Mapper/Service/其他实体完成业务

### 3.2 数据库禁令

- ❌ **禁止在代码中写 DDL** — 所有表结构变更必须通过 Flyway 迁移脚本
- ❌ **禁止修改已执行的 Flyway 脚本** — 只能新增脚本
- ❌ **禁止使用 `VARCHAR`** — 统一使用 `NVARCHAR` 支持中文
- ❌ **禁止使用 `FLOAT/DOUBLE` 存储金额** — 必须使用 `DECIMAL(18,4)` 或 `DECIMAL(18,6)`
- ❌ **禁止物理删除业务主数据** — 业务主表（订单、物料、仓库等）必须使用逻辑删除（`deleted` 字段）
  - ✅ 例外：系统日志、临时表、大数据归档表经评审后允许物理删除

### 3.3 前端禁令

- ❌ **禁止使用 Options API** — 必须使用 `<script setup>` + Composition API
- ❌ **禁止使用 JavaScript** — 必须使用 TypeScript
- ❌ **禁止在组件中直接调用 axios** — 必须通过 `api/` 目录下的封装
- ❌ **禁止无故使用 `any` 类型** — 必须优先定义明确类型；处理第三方库不完善或反序列化中间态时，使用 `unknown` 或加类型断言并注释原因
- ⚠️ **优先控制文件大小** — Vue 页面建议不超过 400 行，Vue 组件/TS 文件/Store 建议不超过 250-300 行；超出时优先拆分可复用组件或工具函数

### 3.4 通用禁令

- ❌ **禁止硬编码魔法数字** — 必须定义为常量或枚举
- ❌ **禁止空 catch 块** — 必须记录日志或抛出异常
- ❌ **禁止注释掉代码后提交** — 直接删除，Git 有历史记录
- ❌ **禁止在同一提交中混合多个功能** — 一个提交只做一件事

---

## 4. 错误处理模型

### 4.1 后端错误码

```java
public enum ErrorCode {
    // 成功
    SUCCESS(0, "操作成功"),

    // 通用错误 1xxx
    PARAM_ERROR(1001, "参数错误"),
    NOT_FOUND(1002, "数据不存在"),
    DUPLICATE(1003, "数据重复"),
    OPTIMISTIC_LOCK(1004, "数据已被他人修改，请刷新后重试"),

    // 认证授权 2xxx
    UNAUTHORIZED(2001, "未登录或登录已过期"),
    FORBIDDEN(2002, "无权限执行此操作"),

    // 业务错误 3xxx
    STOCK_INSUFFICIENT(3001, "库存不足"),
    ORDER_STATUS_INVALID(3002, "单据状态不允许此操作"),
    BOM_CIRCULAR(3003, "BOM 存在循环引用"),
    CREDIT_EXCEEDED(3004, "超出信用额度"),

    // 系统错误 9xxx
    SYSTEM_ERROR(9999, "系统内部错误");
}
```

### 4.2 统一响应格式

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {}
}
```

错误响应：
```json
{
  "code": 3001,
  "msg": "库存不足：物料 [M001] 在仓库 [WH01] 可用数量为 5，请求出库数量为 10",
  "data": null
}
```

### 4.3 前端错误处理

```typescript
// request.ts 中统一处理
if (response.data.code !== 0) {
  ElMessage.error(response.data.msg)
  // 401 → 跳转登录页
  // 其他 → 保持当前页面
  return Promise.reject(new Error(response.data.msg))
}
```

---

## 5. 提交规范

### 5.1 提交消息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### 5.2 Type 类型

| type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | 修复 Bug |
| `docs` | 文档变更 |
| `style` | 代码格式（不影响功能） |
| `refactor` | 重构（既不是新功能也不是修复） |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建/工具变更 |
| `db` | 数据库迁移脚本 |

### 5.3 Scope 范围

```
system, master, inventory, purchase, sales, production, finance, report, frontend, docker
```

### 5.4 示例

```
feat(purchase): 实现采购订单创建功能

- 添加 PoOrderController 创建接口
- 添加 PoOrderService 业务逻辑
- 添加 V10__Create_po_order_tables.sql 迁移脚本

Closes #42
```

```
db(inventory): 新增库存盘点表

- 新增 inv_stocktake 主表
- 新增 inv_stocktake_detail 明细表
```

---

## 6. 文件组织规则

### 6.1 后端模块职责边界

| 模块 | 职责 | 可以调用 |
|------|------|----------|
| `common` | 基础工具、配置、异常 | 无 |
| `system` | 认证、用户、角色、菜单、字典 | common |
| `master` | 物料、仓库、供应商、客户 | common |
| `inventory` | 库存变动、库存查询、盘点、调拨 | common, master |
| `purchase` | 采购订单、入库、退货、应付 | common, master, inventory |
| `sales` | 销售订单、出库、退货、应收 | common, master, inventory |
| `production` | BOM、工单、领料、报工、完工 | common, master, inventory |
| `finance` | 应收应付、收付款、核销 | common, master |
| `app` | 启动模块、聚合打包 | 所有模块 |

### 6.2 前端文件大小限制

| 文件类型 | 建议最大行数 | 超出处理 |
|----------|--------------|----------|
| Vue 页面 | 400 行 | 拆分可复用子组件或提取 composable |
| Vue 组件 | 250 行 | 拆分为子组件 |
| TypeScript 文件 | 200 行 | 拆分为多个文件 |
| API 文件 | 150 行 | 按子模块拆分 |
| Store 文件 | 100 行 | 拆分为多个 Store |

> 以上行数为**建议值**，AI 生成代码时应尽量遵守；若拆分会导致过度碎片化或降低可读性，可在注释中说明原因。

---

## 7. 安全规则

- 🔒 密码必须使用 BCrypt 加密存储
- 🔒 JWT Token 有效期 2 小时，Refresh Token 有效期 7 天
- 🔒 所有写操作必须记录操作日志
- 🔒 敏感配置（数据库密码、JWT Secret）必须从环境变量读取
- 🔒 API 接口必须进行参数校验（使用 `@Valid` + `@NotNull` 等注解）

---

## 8. 性能规则

- ⚡ 分页查询必须有默认 `LIMIT`，禁止全表扫描
- ⚡ 列表查询默认不返回大字段（如备注、详情）
- ⚡ 高频查询字段必须建立索引
- ⚡ Redis 缓存字典数据、用户权限、Token
- ⚡ 大批量操作使用事务，避免逐条提交

---

## 9. 当前开发阶段

**Phase 1: 基础框架 + 系统管理**

详见 [docs/PROGRESS.md](docs/PROGRESS.md)

---

## 10. 参考文档

- [整体规划](docs/PLAN.md)
- [接口契约](docs/CONTRACTS.md)
- [数据纪律](docs/DATA.md)
- [工作流程](docs/WORKFLOW.md)
- [进度追踪](docs/PROGRESS.md)
- [AI 编码自检清单](docs/AI_CHECKLIST.md)
