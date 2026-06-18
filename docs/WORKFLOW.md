# Opus-ERP 工作流程

> 本文档定义开发的"怎么做"流程：分支策略、每步产出、完成门禁。
> 所有开发工作必须遵循此流程。

---

## 1. 分支策略

### 1.1 分支模型

```
main (生产分支)
  │
  ├── develop (开发分支)
  │     │
  │     ├── feature/system    (功能分支)
  │     ├── feature/master
  │     ├── feature/inventory
  │     ├── feature/purchase
  │     └── ...
  │
  ├── release/1.0.0 (发布分支)
  │
  └── hotfix/fix-login-bug (热修复分支)
```

### 1.2 分支命名规范

| 分支类型 | 命名格式 | 示例 |
|----------|----------|------|
| 主分支 | `main` | - |
| 开发分支 | `develop` | - |
| 功能分支 | `feature/{模块名}` | `feature/system` |
| 发布分支 | `release/{版本号}` | `release/1.0.0` |
| 热修复 | `hotfix/{描述}` | `hotfix/fix-login-bug` |

### 1.3 分支操作流程

```bash
# 1. 从 develop 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/purchase

# 2. 在功能分支上开发
git add .
git commit -m "feat(purchase): 实现采购订单创建"

# 3. 完成功能后合并回 develop
git checkout develop
git pull origin develop
git merge --no-ff feature/purchase
git push origin develop
git branch -d feature/purchase

# 4. 发布时从 develop 创建 release 分支
git checkout -b release/1.0.0
# 修复 bug...
git checkout main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Release 1.0.0"
git checkout develop
git merge --no-ff release/1.0.0
```

---

## 2. 开发流程

### 2.1 每个功能模块的开发步骤

```
┌─────────────────────────────────────────────────────────────────┐
│  Step 1: 数据库设计                                               │
│  ├── 编写 Flyway 迁移脚本                                         │
│  ├── 创建表结构、索引、约束                                        │
│  └── 插入初始数据（字典、管理员账号等）                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 2: 后端实现                                                │
│  ├── Entity 实体类                                               │
│  ├── Mapper 接口 + XML（如需要）                                  │
│  ├── Service 接口 + 实现                                         │
│  ├── Controller 控制器                                           │
│  └── DTO 请求/响应对象                                           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 3: 前端实现                                                │
│  ├── API 接口定义                                                │
│  ├── TypeScript 类型定义                                         │
│  ├── 页面组件                                                    │
│  ├── 表单验证                                                    │
│  └── 单元测试（如需要）                                           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 4: 测试验证                                                │
│  ├── 手动功能测试                                                │
│  ├── 边界条件测试                                                │
│  ├── Playwright E2E 测试                                        │
│  └── 代码审查                                                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  Step 5: 文档更新                                                │
│  ├── 更新 PROGRESS.md                                            │
│  ├── 更新 API 文档（如使用 Swagger）                              │
│  └── 更新用户手册                                                │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 每个步骤的产出物

| 步骤 | 产出物 | 验收标准 |
|------|--------|----------|
| 数据库设计 | Flyway SQL 文件 | 可执行、有注释、有索引 |
| 后端实现 | Java 源代码 | 编译通过、符合规范 |
| 前端实现 | Vue/TS 源代码 | 无 TypeScript 错误 |
| 测试验证 | 测试用例/截图 | 所有测试通过 |
| 文档更新 | MD 文件 | 内容准确、格式规范 |

---

## 3. 代码审查清单

### 3.1 后端代码审查

```markdown
## 后端代码审查清单

### 架构层面
- [ ] Controller 是否只做参数校验和调用 Service？
- [ ] Service 是否包含业务逻辑？
- [ ] 是否跨模块直接调用 Mapper？（应该通过 Service）
- [ ] 是否使用构造器注入？

### 数据库层面
- [ ] 是否使用 Flyway 迁移脚本？
- [ ] 字段类型是否符合 DATA.md 规范？
- [ ] 是否有必要的索引？
- [ ] 是否有审计字段（created_by, created_at, updated_at）？

### 代码质量
- [ ] 是否有硬编码魔法数字？
- [ ] 是否有空 catch 块？
- [ ] 异常处理是否符合 CONTRACTS.md 错误码规范？
- [ ] 是否有 TODO/FIXME 需要处理？

### 安全性
- [ ] 是否有 SQL 注入风险？
- [ ] 是否有权限校验？
- [ ] 敏感数据是否加密？
```

### 3.2 前端代码审查

```markdown
## 前端代码审查清单

### 规范层面
- [ ] 是否使用 TypeScript？
- [ ] 是否使用 `<script setup>` + Composition API？
- [ ] 是否通过 api/ 目录调用接口？
- [ ] 文件行数是否超过 300 行？

### 代码质量
- [ ] 是否有 `any` 类型？
- [ ] 是否有未使用的导入？
- [ ] 是否有硬编码字符串？
- [ ] 表单是否有验证规则？

### 用户体验
- [ ] 是否有 loading 状态？
- [ ] 是否有错误提示？
- [ ] 是否有确认对话框（删除等危险操作）？
- [ ] 是否有空状态提示？
```

---

## 4. 完成门禁（Definition of Done）

### 4.1 功能完成门禁

一个功能模块"完成"必须满足以下所有条件：

```markdown
## 功能完成门禁 checklist

### 数据库
- [ ] Flyway 迁移脚本已创建并测试
- [ ] 表结构符合 DATA.md 规范
- [ ] 必要的索引已创建
- [ ] 初始数据已插入

### 后端
- [ ] Entity 已创建
- [ ] Mapper 已创建
- [ ] Service 接口和实现已创建
- [ ] Controller 已创建
- [ ] DTO 已创建
- [ ] 参数校验已添加
- [ ] 异常处理已实现
- [ ] 代码符合 CLAUDE.md 规范

### 前端
- [ ] API 接口已定义
- [ ] TypeScript 类型已定义
- [ ] 列表页面已实现
- [ ] 新增/编辑表单已实现
- [ ] 删除功能已实现
- [ ] 查询/分页已实现
- [ ] 状态显示已实现

### 测试
- [ ] 手动测试通过
- [ ] 边界条件测试通过
- [ ] Playwright E2E 测试已编写（核心流程）

### 文档
- [ ] PROGRESS.md 已更新（标记本功能对应任务为完成/进行中状态）
- [ ] 如修改了接口契约，CONTRACTS.md 已同步更新
- [ ] API 文档已更新（如使用 Swagger）
```

### 4.2 发布完成门禁

一个版本"可以发布"必须满足以下所有条件：

```markdown
## 发布完成门禁 checklist

### 功能完整性
- [ ] 所有计划功能已实现
- [ ] 所有功能通过完成门禁

### 质量保证
- [ ] 所有单元测试通过
- [ ] 所有 E2E 测试通过
- [ ] 代码审查已完成
- [ ] 无 Critical/High 级别 Bug

### 部署准备
- [ ] Docker Compose 配置已测试
- [ ] 环境变量已文档化
- [ ] 数据库迁移已测试
- [ ] 备份策略已就绪

### 文档完整性
- [ ] README.md 已更新
- [ ] 部署文档已更新
- [ ] 用户手册已更新
- [ ] CHANGELOG.md 已更新
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

| type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | feat(purchase): 实现采购订单创建 |
| `fix` | 修复 Bug | fix(inventory): 修复库存计算错误 |
| `docs` | 文档变更 | docs: 更新 README.md |
| `style` | 代码格式 | style: 统一代码缩进 |
| `refactor` | 重构 | refactor(system): 重构用户服务 |
| `perf` | 性能优化 | perf(inventory): 优化库存查询 |
| `test` | 测试 | test: 添加用户模块单元测试 |
| `chore` | 构建/工具 | chore: 更新 Maven 依赖 |
| `db` | 数据库迁移 | db(purchase): 新增采购订单表 |

### 5.3 提交粒度

```bash
# ✅ 正确：一个提交只做一件事
git commit -m "feat(purchase): 实现采购订单创建接口"
git commit -m "feat(purchase): 实现采购订单查询接口"
git commit -m "feat(purchase): 实现采购订单审核接口"

# ❌ 错误：一个提交混合多个功能
git commit -m "feat: 实现采购模块全部功能"
```

### 5.4 提交前检查

```bash
# 后端
mvn clean compile    # 编译检查
mvn test            # 运行测试

# 前端
npm run lint        # ESLint 检查
npm run type-check  # TypeScript 检查
npm run build       # 构建检查
```

---

## 6. 版本发布流程

### 6.1 版本号规范

```
主版本号.次版本号.修订号

例如：1.0.0

- 主版本号：重大功能变更或不兼容更新
- 次版本号：新功能添加
- 修订号：Bug 修复
```

### 6.2 发布步骤

```bash
# 1. 创建 release 分支
git checkout develop
git checkout -b release/1.0.0

# 2. 最后的修复和调整
# ...

# 3. 更新版本号
# pom.xml
# package.json

# 4. 更新 CHANGELOG.md
# ...

# 5. 合并到 main
git checkout main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Release 1.0.0"

# 6. 合并回 develop
git checkout develop
git merge --no-ff release/1.0.0

# 7. 删除 release 分支
git branch -d release/1.0.0

# 8. 推送到远程
git push origin main --tags
git push origin develop
```

### 6.3 CHANGELOG 模板

```markdown
# Changelog

## [1.0.0] - 2024-01-15

### Added
- 系统管理模块（用户、角色、菜单、字典）
- 基础资料模块（物料、仓库、供应商、客户）
- 采购管理模块（采购订单、采购入库）
- 销售管理模块（销售订单、销售出库）
- 库存管理模块（入库、出库、调拨、盘点）

### Changed
- 无

### Fixed
- 无

### Security
- 无
```

---

## 7. 紧急修复流程

### 7.1 热修复步骤

```bash
# 1. 从 main 创建 hotfix 分支
git checkout main
git checkout -b hotfix/fix-login-bug

# 2. 修复 Bug
# ...

# 3. 测试修复
# ...

# 4. 合并到 main
git checkout main
git merge --no-ff hotfix/fix-login-bug
git tag -a v1.0.1 -m "Hotfix: 修复登录 Bug"

# 5. 合并到 develop
git checkout develop
git merge --no-ff hotfix/fix-login-bug

# 6. 删除 hotfix 分支
git branch -d hotfix/fix-login-bug

# 7. 推送到远程
git push origin main --tags
git push origin develop
```

---

## 8. 协作规范

### 8.1 代码所有权

| 模块 | 负责人 | 备注 |
|------|--------|------|
| common | 所有人 | 公共模块 |
| system | - | 系统管理 |
| master | - | 基础资料 |
| inventory | - | 库存管理 |
| purchase | - | 采购管理 |
| sales | - | 销售管理 |
| production | - | 生产管理 |
| finance | - | 财务管理 |
| frontend | - | 前端 |

### 8.2 沟通规范

```markdown
## 每日站会（可选）

- 昨天完成了什么？
- 今天计划做什么？
- 遇到什么阻碍？

## 周报（可选）

- 本周完成的功能
- 下周计划
- 风险和问题
```

### 8.3 冲突解决

```bash
# 1. 先拉取最新代码
git checkout develop
git pull origin develop

# 2. 在功能分支上 rebase
git checkout feature/purchase
git rebase develop

# 3. 解决冲突
# ...

# 4. 继续 rebase
git rebase --continue

# 5. 强制推送到远程功能分支
git push origin feature/purchase --force-with-lease
```

---

## 9. 工具链

### 9.1 开发工具

| 工具 | 用途 | 版本 |
|------|------|------|
| IntelliJ IDEA / VSCode | IDE | 最新 |
| Git | 版本控制 | 2.x |
| Maven | 后端构建 | 3.9.x |
| Node.js | 前端运行时 | 20.x LTS |
| pnpm / npm | 前端包管理 | 最新 |
| Docker | 容器化 | 24.x |
| Postman / Apifox | API 测试 | 最新 |

### 9.2 质量工具

| 工具 | 用途 | 集成方式 |
|------|------|----------|
| ESLint | 前端代码检查 | IDE + CI |
| Prettier | 代码格式化 | IDE + CI |
| SonarQube | 代码质量分析 | CI（可选） |
| Playwright | E2E 测试 | CI |
| JaCoCo | 代码覆盖率 | CI（可选） |

---

## 10. 环境管理

### 10.1 环境列表

| 环境 | 用途 | 配置文件 |
|------|------|----------|
| dev | 开发环境 | application-dev.yml |
| test | 测试环境 | application-test.yml |
| prod | 生产环境 | application-prod.yml |

### 10.2 环境变量

```bash
# 必须从环境变量读取的配置
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=opus_erp
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=YourPassword123!
JWT_SECRET=your-jwt-secret-key
REDIS_HOST=localhost
REDIS_PORT=6379
```

### 10.3 Docker Compose 配置

```yaml
# docker-compose.dev.yml (开发环境)
version: '3.8'
services:
  sqlserver:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      SA_PASSWORD: "DevPassword123!"
      ACCEPT_EULA: "Y"
    ports:
      - "1433:1433"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```
