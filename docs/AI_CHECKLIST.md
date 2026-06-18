# AI 编码自检清单

> 本清单供 AI 在完成每次编码任务后快速自检。
> 任务完成后，AI 应对照本清单逐项确认，并在回复中说明检查结果。

---

## 一、架构与规范

- [ ] 只使用了 `CLAUDE.md` 中规定的技术栈，未引入新依赖或服务
- [ ] 后端包名符合 `com.opus.erp.{module}` 规范
- [ ] 类/接口/方法命名符合 `CLAUDE.md` 命名规范
- [ ] 未在 Controller 中写业务逻辑
- [ ] 未跨模块直接调用 Mapper
- [ ] 使用了构造器注入，未使用 `@Autowired` 字段注入
- [ ] Entity 中只包含字段、getter/setter 及无状态纯计算方法
- [ ] 未在代码中写 DDL，所有表结构变更通过 Flyway 迁移脚本
- [ ] 未修改已执行的 Flyway 脚本
- [ ] 未使用 `VARCHAR`，统一使用 `NVARCHAR`
- [ ] 未使用 `FLOAT/DOUBLE` 存储金额/数量
- [ ] 业务主表使用逻辑删除（`deleted` 字段），未物理删除业务主数据

---

## 二、数据库与数据

- [ ] 字段精度符合 `DATA.md` 规范（数量 `DECIMAL(18,4)`、单价 `DECIMAL(18,6)`、金额 `DECIMAL(18,2)` 等）
- [ ] 新业务表包含标准审计字段：`created_by`、`created_at`、`updated_at`
- [ ] 编码字段或单据号字段已加唯一约束
- [ ] 高频查询字段、外键字段已创建索引
- [ ] 核心业务表已创建外键约束（`ON DELETE NO ACTION`）或应用层存在性校验
- [ ] 数量/金额等数值字段在 Java 中使用 `BigDecimal`，未使用 `Double`/`Float`

---

## 三、接口与前后端

- [ ] API 路径符合 `CLAUDE.md` §2.4 规范
- [ ] 响应格式符合统一 `code + msg + data` 结构
- [ ] 错误码使用 `ErrorCode` 枚举，未硬编码数字
- [ ] 分页查询有默认 `LIMIT`，未全表扫描
- [ ] 写操作接口已添加 `@Valid` + 参数校验注解
- [ ] 前端使用 `<script setup>` + Composition API + TypeScript
- [ ] 前端未直接调用 axios，通过 `api/` 目录下封装调用
- [ ] 前端未无故使用 `any` 类型
- [ ] 前端文件大小尽量控制在建议范围内

---

## 四、安全与性能

- [ ] 密码等敏感数据使用 BCrypt 加密
- [ ] 敏感配置从环境变量读取，未硬编码
- [ ] 写操作已记录操作日志
- [ ] 列表查询默认不返回大字段（如长备注、详情）
- [ ] Redis 缓存用于字典、权限、Token 等高频数据
- [ ] 大批量操作使用事务

---

## 五、库存核心规则

- [ ] 所有库存变动通过 `InvTransactionService` 处理
- [ ] 未直接 `UPDATE`/`INSERT`/`DELETE` `inv_stock` 表
- [ ] 出库/调拨/盘点等操作先校验可用库存是否充足
- [ ] 库存流水记录（`inv_transaction`）不可变，只新增不修改

---

## 六、文档与进度

- [ ] 功能完成后更新了 `docs/PROGRESS.md`
- [ ] 如修改接口，同步更新了 `docs/CONTRACTS.md`
- [ ] 如修改数据规范，同步更新了 `docs/DATA.md`
- [ ] commit message 符合 `type(scope): subject` 格式
- [ ] 一个 commit 只做一件事

---

## 七、AI 行为红线

- [ ] 未修改 `CLAUDE.md` 及 `docs/*.md` 规范文件
- [ ] 未自行引入新的 Maven/npm 依赖
- [ ] 未自行创建新的业务模块或核心表
- [ ] 未在生产配置中写入明文密码/密钥
- [ ] 遇到规范冲突或二义性时，已暂停并询问用户

---

## 检查结果输出模板

```markdown
## AI 自检结果

- 检查项总数：34
- 通过：34
- 未通过/需说明：0

### 未通过项说明
（如有，请逐条说明原因及处理方案）

### 结论
✅ 自检通过，符合项目规范。
```
