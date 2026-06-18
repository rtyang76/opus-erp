# Opus-ERP 数据纪律

> 本文档定义数据存储的精度规则、编码规则、NULL 语义、审计字段和软删除策略。
> 所有数据库设计和代码实现都必须遵守本规范。

---

## 1. 字段精度规范

### 1.1 数值类型

| 用途 | 类型 | 精度 | 说明 |
|------|------|------|------|
| 数量 | `DECIMAL(18,4)` | 4 位小数 | 物料数量、库存数量 |
| 单价 | `DECIMAL(18,6)` | 6 位小数 | 支持更小计量单位 |
| 金额 | `DECIMAL(18,2)` | 2 位小数 | 总金额、税额 |
| 税率 | `DECIMAL(5,2)` | 2 位小数 | 百分比，如 13.00 |
| 换算系数 | `DECIMAL(18,6)` | 6 位小数 | 单位换算 |
| 损耗率 | `DECIMAL(5,2)` | 2 位小数 | 百分比，如 2.00 |
| 重量 | `DECIMAL(18,4)` | 4 位小数 | 千克 |
| 体积 | `DECIMAL(18,4)` | 4 位小数 | 立方米 |

### 1.2 整数类型

| 用途 | 类型 | 说明 |
|------|------|------|
| 主键 | `BIGINT IDENTITY(1,1)` | 自增主键 |
| 外键 | `BIGINT` | 关联其他表 |
| 状态 | `TINYINT` | 0/1 或枚举值 |
| 排序号 | `INT` | 排序用 |
| 天数 | `INT` | 保质期等 |

### 1.3 计算列

```sql
-- 库存金额 = 数量 × 平均成本
total_cost AS (quantity * avg_cost) PERSISTED

-- 可用数量 = 库存数量 - 锁定数量
available_quantity AS (quantity - locked_quantity) PERSISTED

-- 订单明细金额
amount AS (quantity * unit_price) PERSISTED

-- 订单明细税额
tax_amount AS (quantity * unit_price * tax_rate / 100) PERSISTED

-- 应收/应付未付金额
unpaid_amount AS (amount - paid_amount) PERSISTED

-- 盘点差异数量
diff_quantity AS (actual_quantity - system_quantity) PERSISTED
```

### 1.4 金额计算规则

```java
// Java 中使用 BigDecimal，禁止使用 double/float
BigDecimal quantity = new BigDecimal("1000");
BigDecimal unitPrice = new BigDecimal("5.50");
BigDecimal amount = quantity.multiply(unitPrice);  // 5500.00

// 设置舍入模式
BigDecimal taxAmount = amount.multiply(taxRate)
    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
```

---

## 2. 编码规则

### 2.1 单据编号规则

| 单据类型 | 前缀 | 格式 | 示例 |
|----------|------|------|------|
| 采购订单 | PO | PO + yyyyMMdd + 4位序号 | PO202401150001 |
| 采购入库 | PR | PR + yyyyMMdd + 4位序号 | PR202401150001 |
| 销售订单 | SO | SO + yyyyMMdd + 4位序号 | SO202401150001 |
| 销售出库 | SH | SH + yyyyMMdd + 4位序号 | SH202401150001 |
| 生产工单 | WO | WO + yyyyMMdd + 4位序号 | WO202401150001 |
| 领料单 | MI | MI + yyyyMMdd + 4位序号 | MI202401150001 |
| 应收单 | AR | AR + yyyyMMdd + 4位序号 | AR202401150001 |
| 应付单 | AP | AP + yyyyMMdd + 4位序号 | AP202401150001 |
| 收款单 | RC | RC + yyyyMMdd + 4位序号 | RC202401150001 |
| 付款单 | PA | PA + yyyyMMdd + 4位序号 | PA202401150001 |
| 调拨单 | TR | TR + yyyyMMdd + 4位序号 | TR202401150001 |
| 盘点单 | ST | ST + yyyyMMdd + 4位序号 | ST202401150001 |
| 库存流水 | TX | TX + yyyyMMdd + 4位序号 | TX202401150001 |

### 2.2 档案编码规则

| 档案类型 | 前缀 | 格式 | 示例 |
|----------|------|------|------|
| 物料 | M | M + 4位序号 | M0001 |
| 半成品 | SP | SP + 4位序号 | SP0001 |
| 成品 | FG | FG + 4位序号 | FG0001 |
| 仓库 | WH | WH + 2位序号 | WH01 |
| 供应商 | S | S + 4位序号 | S0001 |
| 客户 | C | C + 4位序号 | C0001 |

### 2.3 编码生成工具

```java
public class CodeGenerator {
    /**
     * 生成单据编号
     * @param prefix 前缀
     * @param date 日期
     * @param sequence 当日序号
     * @return 单据编号
     */
    public static String generateDocNo(String prefix, LocalDate date, int sequence) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + dateStr + String.format("%04d", sequence);
    }

    /**
     * 生成档案编码
     * @param prefix 前缀
     * @param sequence 序号
     * @return 档案编码
     */
    public static String generateCode(String prefix, int sequence) {
        return prefix + String.format("%04d", sequence);
    }
}
```

---

## 3. NULL 语义

### 3.1 允许 NULL 的字段

| 字段类型 | 说明 | 示例 |
|----------|------|------|
| 可选文本 | 备注、描述 | `remark NVARCHAR(500) NULL` |
| 可选日期 | 交货日期、计划日期 | `delivery_date DATE NULL` |
| 可选外键 | 非必填关联 | `aux_unit_id BIGINT NULL` |
| 可选数值 | 安全库存、信用额度 | `safety_stock DECIMAL(18,4) NULL` |
| 审核字段 | 未审核时为空 | `audited_by BIGINT NULL` |

### 3.2 NOT NULL 字段

| 字段类型 | 说明 | 示例 |
|----------|------|------|
| 主键 | 必填 | `id BIGINT NOT NULL` |
| 编码 | 必填且唯一 | `item_code NVARCHAR(50) NOT NULL` |
| 名称 | 必填 | `item_name NVARCHAR(200) NOT NULL` |
| 数量 | 必填，默认 0 | `quantity DECIMAL(18,4) NOT NULL DEFAULT 0` |
| 金额 | 必填，默认 0 | `amount DECIMAL(18,2) NOT NULL DEFAULT 0` |
| 状态 | 必填，默认值 | `status TINYINT NOT NULL DEFAULT 1` |
| 创建时间 | 必填，默认当前 | `created_at DATETIME2 NOT NULL DEFAULT GETDATE()` |

### 3.3 Java 中的 NULL 处理

```java
// Entity 中使用包装类型，允许 NULL
public class PoOrder {
    private Long id;                    // 主键，数据库生成
    private String orderNo;             // 非空
    private LocalDate deliveryDate;     // 可空
    private BigDecimal totalAmount;     // 非空，默认 0
    private String remark;              // 可空
}

// DTO 中使用 @NotNull 注解校验
public class PoOrderCreateRequest {
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;

    private LocalDate deliveryDate;     // 可空，不加注解

    private String remark;              // 可空
}
```

---

## 4. 审计字段

### 4.1 标准审计字段

所有业务表都必须包含以下审计字段：

```sql
-- 创建相关
created_by    BIGINT       NOT NULL,      -- 创建人 ID
created_at    DATETIME2    NOT NULL DEFAULT GETDATE(),  -- 创建时间

-- 更新相关
updated_at    DATETIME2    NOT NULL DEFAULT GETDATE(),  -- 更新时间

-- 审核相关（可空，审核后填入）
audited_by    BIGINT       NULL,          -- 审核人 ID
audited_at    DATETIME2    NULL           -- 审核时间
```

### 4.2 审计字段填充策略

```java
@MappedSuperclass
public abstract class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}

// MyBatis-Plus 自动填充处理器
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdBy", Long.class, SecurityUtils.getCurrentUserId());
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 4.3 审核字段处理

```java
// 审核时手动填充审核字段
public void audit(Long id, Long auditorId) {
    PoOrder order = poOrderMapper.selectById(id);
    if (order == null) {
        throw new BusinessException(ErrorCode.NOT_FOUND);
    }
    if (!DocStatus.DRAFT.getCode().equals(order.getStatus())) {
        throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
    }

    order.setStatus(DocStatus.AUDITED.getCode());
    order.setAuditedBy(auditorId);
    order.setAuditedAt(LocalDateTime.now());
    poOrderMapper.updateById(order);
}
```

---

## 5. 软删除策略

### 5.1 数据库层面

```sql
-- 所有业务表都包含 deleted 字段
deleted BIT NOT NULL DEFAULT 0

-- 创建过滤删除的视图（可选）
CREATE VIEW v_po_order AS
SELECT * FROM po_order WHERE deleted = 0;
```

### 5.2 MyBatis-Plus 配置

```yaml
# application.yml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted      # 全局逻辑删除字段
      logic-delete-value: 1            # 删除值
      logic-not-delete-value: 0        # 未删除值
```

### 5.3 代码中的使用

```java
// Entity 中声明逻辑删除字段
@TableName("po_order")
public class PoOrder extends BaseEntity {
    @TableLogic
    private Integer deleted;
}

// 删除操作自动转换为 UPDATE
// poOrderMapper.deleteById(id) 
// → UPDATE po_order SET deleted = 1 WHERE id = ?

// 查询自动过滤已删除数据
// poOrderMapper.selectList(null)
// → SELECT * FROM po_order WHERE deleted = 0
```

### 5.4 特殊场景处理

```java
// 场景1：需要查询已删除数据（如回收站）
@Select("SELECT * FROM po_order WHERE deleted = 1")
List<PoOrder> selectDeleted();

// 场景2：物理删除（仅限系统管理日志等）
@Delete("DELETE FROM sys_oper_log WHERE oper_time < #{date}")
int deleteOldLogs(@Param("date") LocalDate date);

// 场景3：级联删除时需手动处理关联表
public void deleteOrder(Long id) {
    // 先删除明细
    poOrderDetailMapper.delete(
        new LambdaQueryWrapper<PoOrderDetail>()
            .eq(PoOrderDetail::getOrderId, id)
    );
    // 再删除主表（逻辑删除）
    poOrderMapper.deleteById(id);
}
```

---

## 6. 多租户预留

### 6.1 租户字段

```sql
-- 所有业务表预留 tenant_id 字段
tenant_id BIGINT NOT NULL DEFAULT 1

-- 创建索引
CREATE INDEX idx_po_order_tenant_id ON po_order(tenant_id);
```

### 6.2 MyBatis-Plus 租户插件（预留）

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 租户插件（暂时注释，单租户模式不需要）
        // interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
        //     @Override
        //     public Expression getTenantId() {
        //         return new LongValue(SecurityUtils.getCurrentTenantId());
        //     }
        //     @Override
        //     public String getTenantIdColumn() {
        //         return "tenant_id";
        //     }
        // }));

        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.SQL_SERVER));

        return interceptor;
    }
}
```

---

## 7. 数据字典规范

### 7.1 字典类型定义

| 字典类型 | 说明 | 字典值 |
|----------|------|--------|
| `item_type` | 物料类型 | RAW=原材料, SEMI=半成品, FINISHED=成品, AUXILIARY=辅材 |
| `warehouse_type` | 仓库类型 | RAW=原料仓, SEMI=半成品仓, FINISHED=成品仓, RETURN=退货仓, DEFECTIVE=不良品仓 |
| `doc_status` | 单据状态 | DRAFT=草稿, AUDITED=已审核, COMPLETED=已完成, CANCELLED=已取消, CLOSED=已关闭 |
| `po_status` | 采购订单状态 | DRAFT=草稿, AUDITED=已审核, CLOSED=已关闭, CANCELLED=已取消 |
| `so_status` | 销售订单状态 | DRAFT=草稿, AUDITED=已审核, SHIPPED=已发货, COMPLETED=已完成, CANCELLED=已取消 |
| `wo_status` | 工单状态 | PENDING=待下达, RELEASED=已下达, IN_PROGRESS=生产中, COMPLETED=已完工, CLOSED=已关闭 |
| `abc_class` | ABC分类 | A=A类, B=B类, C=C类 |
| `payment_method` | 付款方式 | BANK=银行转账, CASH=现金, CHECK=支票 |
| `transaction_type` | 库存交易类型 | RECEIPT=入库, ISSUE=出库, TRANSFER=调拨, ADJUSTMENT=调整, RETURN=退货 |
| `reference_type` | 参考单据类型 | PO=采购订单, SO=销售订单, WO=生产工单, MANUAL=手工录入 |

### 7.2 前端字典使用

```typescript
// composables/useDict.ts
export function useDict(dictType: string) {
  const dictData = ref<DictItem[]>([])

  const loadDict = async () => {
    const { data } = await getDictDataByType(dictType)
    dictData.value = data
  }

  const getLabel = (value: string) => {
    return dictData.value.find(item => item.value === value)?.label || value
  }

  const getColor = (value: string) => {
    return dictData.value.find(item => item.value === value)?.color || ''
  }

  onMounted(() => loadDict())

  return { dictData, getLabel, getColor }
}

// 使用示例
const { getLabel, getColor } = useDict('doc_status')

// 模板中
<el-tag :type="getColor(row.status)">{{ getLabel(row.status) }}</el-tag>
```

---

## 8. 索引规范

### 8.1 必须创建索引的字段

| 场景 | 索引字段 | 说明 |
|------|----------|------|
| 外键 | 所有 `_id` 字段 | 关联查询 |
| 查询条件 | `status` | 状态过滤 |
| 查询条件 | 编码字段 | 精确查询 |
| 查询条件 | 日期字段 | 范围查询 |
| 排序 | `created_at` | 按时间排序 |

### 8.2 索引命名规范

```sql
-- 主键索引（自动创建）
PK_{表名}

-- 唯一索引
UQ_{表名}_{字段}
-- 示例：UQ_mdm_item_item_code

-- 普通索引
IDX_{表名}_{字段}
-- 示例：IDX_po_order_supplier_id

-- 复合索引
IDX_{表名}_{字段1}_{字段2}
-- 示例：IDX_inv_stock_item_id_warehouse_id
```

### 8.3 示例

```sql
-- 物料表索引
CREATE UNIQUE INDEX UQ_mdm_item_item_code ON mdm_item(item_code);
CREATE INDEX IDX_mdm_item_category_id ON mdm_item(category_id);
CREATE INDEX IDX_mdm_item_status ON mdm_item(status);

-- 采购订单表索引
CREATE UNIQUE INDEX UQ_po_order_order_no ON po_order(order_no);
CREATE INDEX IDX_po_order_supplier_id ON po_order(supplier_id);
CREATE INDEX IDX_po_order_status ON po_order(status);
CREATE INDEX IDX_po_order_order_date ON po_order(order_date);

-- 库存表索引
CREATE INDEX IDX_inv_stock_item_id ON inv_stock(item_id);
CREATE INDEX IDX_inv_stock_warehouse_id ON inv_stock(warehouse_id);
CREATE UNIQUE INDEX UQ_inv_stock_item_wh_bin_lot ON inv_stock(item_id, warehouse_id, bin_id, lot_no);

-- 库存流水表索引
CREATE INDEX IDX_inv_transaction_item_id ON inv_transaction(item_id);
CREATE INDEX IDX_inv_transaction_warehouse_id ON inv_transaction(warehouse_id);
CREATE INDEX IDX_inv_transaction_date ON inv_transaction(transaction_date);
CREATE INDEX IDX_inv_transaction_reference ON inv_transaction(reference_type, reference_id);
```

---

## 9. 数据完整性约束

### 9.1 外键约束

**原则**：ERP 系统数据一致性优先于极端性能，核心业务表应在数据库层面保留外键约束。

```sql
-- 核心业务表必须创建外键，使用 ON DELETE NO ACTION
-- 原因：
-- 1. 防止 AI 或人工误写导致脏数据
-- 2. ERP 数据关系复杂，数据库层约束是最后一道防线
-- 3. ON DELETE NO ACTION 可避免级联误删

ALTER TABLE po_order_detail
ADD CONSTRAINT FK_po_order_detail_order_id
FOREIGN KEY (order_id) REFERENCES po_order(id)
ON DELETE NO ACTION;
```

**例外**：以下场景经架构评审后可不创建外键：
- 海量日志表、归档表
- 明确的分库分表场景
- 第三方同步的临时表

**应用层补充**：无论是否创建数据库外键，代码中必须对关键外键做存在性校验，禁止直接插入无关联的 ID。

### 9.2 唯一约束

```sql
-- 编码字段必须唯一
ALTER TABLE mdm_item ADD CONSTRAINT UQ_mdm_item_item_code UNIQUE(item_code);
ALTER TABLE mdm_warehouse ADD CONSTRAINT UQ_mdm_warehouse_warehouse_code UNIQUE(warehouse_code);
ALTER TABLE mdm_supplier ADD CONSTRAINT UQ_mdm_supplier_supplier_code UNIQUE(supplier_code);
ALTER TABLE mdm_customer ADD CONSTRAINT UQ_mdm_customer_customer_code UNIQUE(customer_code);

-- 单据编号必须唯一
ALTER TABLE po_order ADD CONSTRAINT UQ_po_order_order_no UNIQUE(order_no);
ALTER TABLE so_order ADD CONSTRAINT UQ_so_order_order_no UNIQUE(order_no);

-- 库存表组合唯一
ALTER TABLE inv_stock ADD CONSTRAINT UQ_inv_stock 
UNIQUE(item_id, warehouse_id, bin_id, lot_no);
```

### 9.3 检查约束

```sql
-- 数量必须大于 0
ALTER TABLE po_order_detail ADD CONSTRAINT CK_po_order_detail_quantity 
CHECK (quantity > 0);

-- 单价必须大于 0
ALTER TABLE po_order_detail ADD CONSTRAINT CK_po_order_detail_unit_price 
CHECK (unit_price > 0);

-- 税率范围 0-100
ALTER TABLE po_order_detail ADD CONSTRAINT CK_po_order_detail_tax_rate 
CHECK (tax_rate >= 0 AND tax_rate <= 100);

-- 库存数量不能为负（可选，根据业务需求）
ALTER TABLE inv_stock ADD CONSTRAINT CK_inv_stock_quantity 
CHECK (quantity >= 0);
```

---

## 10. 数据备份策略

### 10.1 备份频率

| 数据类型 | 备份频率 | 保留时间 |
|----------|----------|----------|
| 系统配置 | 每日 | 30 天 |
| 业务数据 | 每日 | 90 天 |
| 日志数据 | 每周 | 30 天 |

### 10.2 备份命令

```bash
# SQL Server 备份命令
BACKUP DATABASE [opus_erp] 
TO DISK = '/backup/opus_erp_full.bak'
WITH FORMAT, COMPRESSION;

# 差异备份
BACKUP DATABASE [opus_erp] 
TO DISK = '/backup/opus_erp_diff.bak'
WITH DIFFERENTIAL, COMPRESSION;
```
