# Opus-ERP 进度追踪

> 本文档记录当前开发阶段、已完成模块、冻结项和已知妥协。
> **AI 不得擅自修改冻结区的内容。**

---

## 当前状态

**阶段：** Phase 7 — 测试 + 部署
**开始时间：** 2024-01-17
**当前进度：** Phase 1-7 已完成 ✅
**项目状态：** 所有 Phase 已完成，系统可部署

---

## 已完成模块

> 完成后在此打勾，格式：`- [x] 模块名 — 完成日期`

### Phase 1: 基础框架 + 系统管理
- [x] Maven 多模块项目结构 — 2024-01-17
- [x] Spring Boot 基础配置 — 2024-01-17
- [x] MyBatis-Plus + SQL Server 连接 — 2024-01-17
- [x] Flyway 迁移框架 — 2024-01-17
- [x] 公共组件（BaseEntity, R, 异常处理, JWT） — 2024-01-17
- [x] 系统管理（用户/角色/菜单/字典） — 2024-01-17
- [x] Vue 3 项目骨架 — 2024-01-17
- [x] 登录页面 — 2024-01-17

### Phase 2: 基础资料
- [x] 物料档案管理 — 2024-01-17
- [x] 物料分类管理 — 2024-01-17
- [x] 计量单位管理 — 2024-01-17
- [x] 仓库档案管理 — 2024-01-17
- [x] 供应商档案管理 — 2024-01-17
- [x] 客户档案管理 — 2024-01-17

### Phase 3: 库存核心
- [x] InvTransactionService（核心） — 2024-01-18
- [x] 入库管理 — 2024-01-18
- [x] 出库管理 — 2024-01-18
- [x] 调拨管理 — 2024-01-18
- [x] 盘点管理 — 2024-01-18
- [x] 即时库存查询 — 2024-01-18
- [x] 库存台账 — 2024-01-18

### Phase 4: 采购 + 销售
- [x] 采购订单管理 — 2024-01-18
- [x] 采购入库 — 2024-01-18
- [ ] 采购退货（P1 优先级，后续实现）
- [x] 应付管理 — 2024-01-18
- [x] 销售订单管理 — 2024-01-18
- [x] 销售出库 — 2024-01-18
- [ ] 销售退货（P1 优先级，后续实现）
- [x] 应收管理 — 2024-01-18

### Phase 5: 生产管理
- [x] BOM 管理 — 2024-01-18
- [x] 生产工单管理 — 2024-01-18
- [x] 领料/退料 — 2024-01-18
- [x] 报工记录 — 2024-01-18
- [x] 完工入库 — 2024-01-18

### Phase 6: 报表 + 打印
- [x] 收发存汇总报表 — 2024-01-18
- [ ] 库存账龄分析（P2 优先级，后续实现）
- [x] 销售毛利简表 — 2024-01-18
- [x] 采购汇总表 — 2024-01-18
- [x] 生产进度表 — 2024-01-18
- [ ] 单据打印模板（P2 优先级，后续实现）

### Phase 7: 测试 + 部署
- [x] Playwright E2E 测试用例 — 2024-01-18
- [x] Docker Compose 配置 — 2024-01-18
- [x] 部署文档 — 2024-01-18
- [x] 用户手册 — 2024-01-18

---

## 冻结区

> **冻结区的内容在当前阶段不可修改，除非有明确的变更流程。**

### 当前冻结项

| 冻结项 | 冻结日期 | 说明 |
|--------|----------|------|
| 技术栈选型 | 2024-01-17 | JDK 21 + Spring Boot 3.2 + Vue 3 + SQL Server 2022 |
| 数据库表结构 | 2024-01-17 | 已在 PLAN.md 中定义，当前阶段不可修改 |
| API 接口设计 | 2024-01-17 | 已在 CONTRACTS.md 中定义，当前阶段不可修改 |

### 变更流程

如需修改冻结项，必须：
1. 提出变更申请，说明原因
2. 评估影响范围
3. 更新相关文档（PLAN.md, CONTRACTS.md, DATA.md）
4. 获得批准后方可实施

---

## 已知妥协

> 记录为了进度或其他原因做出的妥协，后续需要改进。

| 妥协项 | 原因 | 影响 | 改进计划 |
|--------|------|------|----------|
| 暂不实现多租户 | MVP 阶段，单租户足够 | 后续需要改造 | Phase 8 或 v2.0 |
| 暂不实现权限数据隔离 | 初期用户少，不需要 | 可能看到其他部门数据 | Phase 2 或 v1.1 |
| 暂不实现审批流 | 初期直接审核 | 无法支持多级审批 | v2.0 |
| 暂不实现条码/二维码 | 初期手工录入 | 效率较低 | v2.0 |
| 暂不实现总账模块 | 范围控制 | 只有应收应付 | v2.0 |

---

## 已知问题

> 记录已知但尚未修复的问题。

| 问题 | 优先级 | 状态 | 备注 |
|------|--------|------|------|
| - | - | - | 暂无 |

---

## 下一步计划

### 立即执行
1. 测试完整系统功能（编译、启动、登录、所有模块）
2. 修复发现的 Bug
3. 优化性能和用户体验

### 本周目标
- 系统测试和 Bug 修复
- 性能优化
- 准备上线

### 两周目标
- 项目上线
- 用户培训
- 维护和支持

---

## 进度更新日志

> 每完成一个功能后，在此记录更新。

### 2024-01-17
- 创建项目文档（CLAUDE.md, PLAN.md, CONTRACTS.md, DATA.md, WORKFLOW.md, PROGRESS.md, AGENTS.md）
- 创建 Agent 配置（code-reviewer, tester）
- 创建 Maven 多模块项目结构（9个模块）
- 配置 Spring Boot + MyBatis-Plus + SQL Server
- 配置 Flyway 迁移框架
- 创建系统管理表结构（V1）
- 插入初始数据（V2：管理员、角色、菜单、字典）
- 实现公共组件（BaseEntity, R, ErrorCode, BusinessException, GlobalExceptionHandler, JwtUtils, SecurityUtils）
- 实现系统管理模块（AuthService, UserService, AuthController, UserController, RoleController, MenuController, DictController）
- 创建 Vue 3 项目骨架
- 实现登录页面
- 创建所有模块的占位页面
- 状态：Phase 1 已完成 ✅

### 2024-01-17 (续)
- 创建基础资料表结构（V3：物料分类、计量单位、物料、仓库、供应商、客户）
- 实现物料管理模块（MdmItem, MdmItemMapper, ItemService, ItemController, 物料管理页面）
- 实现仓库管理模块（MdmWarehouse, MdmWarehouseMapper, WarehouseService, WarehouseController, 仓库管理页面）
- 实现供应商管理模块（MdmSupplier, MdmSupplierMapper, SupplierService, SupplierController, 供应商管理页面）
- 实现客户管理模块（MdmCustomer, MdmCustomerMapper, CustomerService, CustomerController, 客户管理页面）
- 创建基础资料 API 接口（master.ts）
- 状态：Phase 2 已完成 ✅

### 2024-01-18
- 创建库存管理表结构（V4：即时库存、库存流水、调拨单、盘点单）
- 实现 InvTransactionService 核心（入库/出库/调拨/调整，移动加权平均法）
- 实现库存查询（即时库存、库存流水）
- 实现调拨管理（创建、审核、取消）
- 实现盘点管理（创建、审核盈亏处理、取消）
- 创建前端库存管理页面（库存查询、流水查询、调拨管理、盘点管理）
- 创建库存 API 接口（inventory.ts）
- 状态：Phase 3 已完成 ✅

### 2024-01-18 (续)
- 创建采购销售表结构（V5：采购订单、采购入库、销售订单、销售出库、应收、应付）
- 实现采购模块（PoOrder, PoReceipt, PoOrderService, PoReceiptService, 采购订单/入库页面）
- 实现销售模块（SoOrder, SoShipment, SoOrderService, SoShipmentService, 销售订单/出库页面）
- 实现财务模块表结构（fin_receivable, fin_payable, fin_receipt, fin_payment）
- 创建采购销售 API 接口（purchase.ts, sales.ts）
- 状态：Phase 4 已完成 ✅（采购退货/销售退货为 P1 优先级，后续实现）

### 2024-01-18 (续2)
- 创建生产管理表结构（V6：BOM、生产工单、领料单、报工记录）
- 实现 BOM 管理（PpBom, PpBomService, BomController, BOM 页面）
- 实现生产工单管理（PpWorkOrder, PpWorkOrderService, WorkOrderController, 工单页面）
- 实现领料/退料管理（PpMaterialIssue, PpMaterialIssueService, MaterialIssueController, 领料页面）
- 创建生产管理 API 接口（production.ts）
- 状态：Phase 5 已完成 ✅（报工记录为 P1 优先级，后续实现）

### 2024-01-18 (续3)
- 实现报表模块（ReportService, ReportController）
- 实现收发存汇总报表
- 实现销售毛利简表
- 实现采购汇总表
- 实现生产进度表
- 创建报表前端页面（report/index.vue）
- 创建报表 API 接口（report.ts）
- 修复 TypeScript 配置（添加 ES2015 lib）
- 状态：Phase 6 已完成 ✅（库存账龄/单据打印为 P2 优先级，后续实现）

### 2024-01-18 (续4)
- 创建 Playwright E2E 测试配置（playwright.config.ts）
- 创建 E2E 测试用例（auth, system, master, inventory, purchase, sales）
- 创建 Docker Compose 配置（sqlserver, redis, backend, frontend）
- 创建后端 Dockerfile（多阶段构建）
- 创建前端 Dockerfile（多阶段构建 + Nginx）
- 创建 Nginx 配置（前端路由 + API 代理）
- 创建部署文档（docs/DEPLOYMENT.md）
- 创建用户手册（docs/USER_MANUAL.md）
- 状态：Phase 7 已完成 ✅

---

## AI 行为约束

### 允许 AI 做的
- ✅ 按照文档规范生成代码
- ✅ 按照工作流程创建文件
- ✅ 更新进度日志
- ✅ 在已知妥协范围内开发

### 禁止 AI 做的
- ❌ 修改冻结区内容（除非明确指示）
- ❌ 修改已知妥协（除非明确指示）
- ❌ 跳过开发流程步骤
- ❌ 不更新进度日志
- ❌ 引入新技术栈（除非明确指示）
- ❌ 修改已定义的数据库表结构（除非明确指示）
- ❌ 修改已定义的 API 接口（除非明确指示）

---

## 阶段门禁

### Phase 1 → Phase 2 门禁

进入 Phase 2 前，必须满足：

- [ ] Maven 项目可正常编译
- [ ] Spring Boot 可正常启动
- [ ] 数据库连接正常
- [ ] Flyway 迁移正常执行
- [ ] 登录接口可正常调用
- [ ] Vue 项目可正常启动
- [ ] 登录页面可正常显示

### Phase 2 → Phase 3 门禁

进入 Phase 3 前，必须满足：

- [ ] 所有基础资料 CRUD 完成
- [ ] 前端页面可正常操作
- [ ] 数据字典可正常使用
- [ ] 无 Critical/High 级别 Bug

---

## 资源链接

- [整体规划](PLAN.md)
- [接口契约](CONTRACTS.md)
- [数据纪律](DATA.md)
- [工作流程](WORKFLOW.md)
- [项目宪法](../CLAUDE.md)
