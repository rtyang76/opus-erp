# Opus-ERP 接口契约中心

> 本文档定义每张单据的 API 语义、状态码、字段精度和幂等性规则。
> 这是前后端协作的"合同"，任何接口实现都必须严格遵守。

---

## 1. 通用约定

### 1.1 响应格式

```json
{
  "code": 0,        // 0=成功, 非0=失败
  "msg": "操作成功", // 提示信息
  "data": {}        // 响应数据，可以是对象、数组或 null
}
```

### 1.2 分页请求参数

```typescript
interface PageRequest {
  pageNum: number      // 页码，从 1 开始
  pageSize: number     // 每页条数，默认 10，最大 100
  orderBy?: string     // 排序字段
  orderDir?: 'asc' | 'desc'  // 排序方向
}
```

### 1.3 分页响应格式

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {
    "records": [],      // 数据列表
    "total": 100,       // 总记录数
    "pageNum": 1,       // 当前页码
    "pageSize": 10,     // 每页条数
    "pages": 10         // 总页数
  }
}
```

### 1.4 字段精度标准

| 类型 | 精度 | 说明 |
|------|------|------|
| 数量 | `DECIMAL(18,4)` | 物料数量 |
| 单价 | `DECIMAL(18,6)` | 单价（支持更小单位） |
| 金额 | `DECIMAL(18,2)` | 总金额 |
| 税率 | `DECIMAL(5,2)` | 百分比，如 13.00 |
| 换算系数 | `DECIMAL(18,6)` | 单位换算 |

### 1.5 日期时间格式

```
日期：yyyy-MM-dd（如 2024-01-15）
时间：yyyy-MM-dd HH:mm:ss（如 2024-01-15 14:30:00）
```

### 1.6 状态枚举

```java
// 通用单据状态
public enum DocStatus {
    DRAFT("DRAFT", "草稿"),
    AUDITED("AUDITED", "已审核"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消"),
    CLOSED("CLOSED", "已关闭");
}

// 采购订单状态
public enum PoOrderStatus {
    DRAFT, AUDITED, CLOSED, CANCELLED
}

// 销售订单状态
public enum SoOrderStatus {
    DRAFT, AUDITED, SHIPPED, COMPLETED, CANCELLED
}

// 生产工单状态
public enum WorkOrderStatus {
    PENDING, RELEASED, IN_PROGRESS, COMPLETED, CLOSED
}
```

---

## 2. 系统管理接口

### 2.1 认证接口

#### POST /api/auth/login — 用户登录

**请求：**
```json
{
  "username": "admin",      // 必填，50 字符内
  "password": "******"      // 必填，BCrypt 加密传输
}
```

**响应：**
```json
{
  "code": 0,
  "msg": "登录成功",
  "data": {
    "accessToken": "eyJ...",   // JWT Token，2 小时有效
    "refreshToken": "abc...",  // 刷新 Token，7 天有效
    "expiresIn": 7200,         // 过期秒数
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "roles": ["ADMIN"],
      "permissions": ["sys:user:list", "sys:user:add"]
    }
  }
}
```

**错误码：**
- `2001` — 用户名或密码错误
- `2002` — 账号已被禁用

#### POST /api/auth/refresh — 刷新 Token

**请求：**
```json
{
  "refreshToken": "abc..."
}
```

**幂等性：** ✅ 同一 refreshToken 多次调用返回相同 accessToken

#### POST /api/auth/logout — 退出登录

**请求头：** `Authorization: Bearer {accessToken}`

**幂等性：** ✅ 多次调用无副作用

---

### 2.2 用户管理接口

#### GET /api/system/users — 分页查询用户列表

**请求参数：**
```
pageNum=1&pageSize=10&username=admin&status=1&deptId=1
```

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "username": "admin",
  "nickname": "管理员",
  "email": "admin@example.com",
  "phone": "13800138000",
  "status": 1,              // 1=启用, 0=禁用
  "deptId": 1,
  "deptName": "总经办",
  "createdAt": "2024-01-01 10:00:00"
}
```

#### POST /api/system/users — 创建用户

**请求：**
```json
{
  "username": "zhangsan",     // 必填，唯一，50 字符内
  "password": "******",       // 必填，6-20 位
  "nickname": "张三",         // 必填
  "email": "zs@example.com", // 选填
  "phone": "13800138000",    // 选填
  "deptId": 1,               // 选填
  "roleIds": [1, 2]          // 选填，角色 ID 列表
}
```

**幂等性：** ❌ username 重复时返回错误码 `1003`

#### PUT /api/system/users/{id} — 更新用户

**幂等性：** ✅ 相同数据多次更新结果一致

#### DELETE /api/system/users/{id} — 删除用户（逻辑删除）

**幂等性：** ✅ 已删除的数据再次删除不报错

#### PUT /api/system/users/{id}/status — 启用/禁用用户

**请求：**
```json
{
  "status": 0    // 1=启用, 0=禁用
}
```

#### PUT /api/system/users/{id}/password — 重置密码

**请求：**
```json
{
  "newPassword": "******"    // 必填，6-20 位
}
```

---

## 3. 基础资料接口

### 3.1 物料档案接口

#### GET /api/master/items — 分页查询物料列表

**请求参数：**
```
pageNum=1&pageSize=10&itemCode=M001&itemName=螺丝&categoryId=1&itemType=RAW&status=1
```

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "itemCode": "M001",
  "itemName": "螺丝",
  "categoryId": 1,
  "categoryName": "原材料",
  "specification": "M6x20",
  "unitId": 1,
  "unitName": "个",
  "auxUnitId": 2,
  "auxUnitName": "箱",
  "unitFactor": 1000,
  "safetyStock": 10000,
  "abcClass": "A",
  "itemType": "RAW",
  "defaultWarehouseId": 1,
  "defaultWarehouseName": "原料仓",
  "status": 1,
  "remark": "",
  "createdAt": "2024-01-01 10:00:00"
}
```

#### POST /api/master/items — 创建物料

**请求：**
```json
{
  "itemCode": "M001",           // 必填，唯一，50 字符内
  "itemName": "螺丝",           // 必填，200 字符内
  "categoryId": 1,              // 必填
  "specification": "M6x20",    // 选填
  "unitId": 1,                  // 必填
  "auxUnitId": 2,              // 选填
  "unitFactor": 1000,          // 辅助单位换算系数
  "safetyStock": 10000,        // 安全库存
  "abcClass": "A",             // A/B/C
  "itemType": "RAW",           // RAW/SEMI/FINISHED/AUXILIARY
  "defaultWarehouseId": 1,     // 默认仓库
  "remark": ""
}
```

**幂等性：** ❌ itemCode 重复时返回错误码 `1003`

#### PUT /api/master/items/{id} — 更新物料

#### DELETE /api/master/items/{id} — 删除物料（逻辑删除）

#### GET /api/master/items/{id} — 查询物料详情

---

### 3.2 仓库档案接口

#### GET /api/master/warehouses — 查询仓库列表

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "warehouseCode": "WH01",
  "warehouseName": "原料仓",
  "warehouseType": "RAW",     // RAW/SEMI/FINISHED/RETURN/DEFECTIVE
  "address": "A栋1楼",
  "manager": "张三",
  "status": 1
}
```

#### POST /api/master/warehouses — 创建仓库

**请求：**
```json
{
  "warehouseCode": "WH01",    // 必填，唯一
  "warehouseName": "原料仓",   // 必填
  "warehouseType": "RAW",     // 必填
  "address": "A栋1楼",        // 选填
  "manager": "张三",          // 选填
  "remark": ""
}
```

---

### 3.3 供应商档案接口

#### GET /api/master/suppliers — 分页查询供应商

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "supplierCode": "S001",
  "supplierName": "XX供应商",
  "shortName": "XX",
  "contactPerson": "李四",
  "phone": "13900139000",
  "email": "ls@supplier.com",
  "address": "上海市XX路",
  "taxNo": "91310000...",
  "paymentTerms": "月结30天",
  "creditLimit": 100000,
  "rating": "A",
  "status": 1
}
```

#### POST /api/master/suppliers — 创建供应商

**幂等性：** ❌ supplierCode 重复时返回错误码 `1003`

---

### 3.4 客户档案接口

#### GET /api/master/customers — 分页查询客户

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "customerCode": "C001",
  "customerName": "XX客户",
  "shortName": "XX",
  "contactPerson": "王五",
  "phone": "13700137000",
  "paymentTerms": "月结30天",
  "creditLimit": 200000,
  "rating": "A",
  "salesmanId": 1,
  "salesmanName": "张三",
  "status": 1
}
```

#### POST /api/master/customers — 创建客户

**幂等性：** ❌ customerCode 重复时返回错误码 `1003`

---

## 4. 采购管理接口

### 4.1 采购订单接口

#### GET /api/purchase/orders — 分页查询采购订单

**请求参数：**
```
pageNum=1&pageSize=10&orderNo=PO20240101&supplierId=1&status=DRAFT&startDate=2024-01-01&endDate=2024-01-31
```

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "orderNo": "PO20240101001",
  "supplierId": 1,
  "supplierName": "XX供应商",
  "orderDate": "2024-01-15",
  "deliveryDate": "2024-01-25",
  "status": "DRAFT",
  "totalAmount": 10000.00,
  "taxAmount": 1300.00,
  "remark": "",
  "createdBy": 1,
  "createdByName": "管理员",
  "createdAt": "2024-01-15 10:00:00"
}
```

#### POST /api/purchase/orders — 创建采购订单

**请求：**
```json
{
  "supplierId": 1,                    // 必填
  "orderDate": "2024-01-15",          // 必填
  "deliveryDate": "2024-01-25",       // 选填
  "remark": "",                       // 选填
  "details": [                        // 必填，至少一条
    {
      "itemId": 1,                    // 必填
      "quantity": 1000,               // 必填，> 0
      "unitId": 1,                    // 必填
      "unitPrice": 5.00,             // 必填，> 0
      "taxRate": 13.00,              // 默认 13
      "remark": ""
    }
  ]
}
```

**业务规则：**
- 状态初始为 `DRAFT`
- `totalAmount` = Σ(quantity × unitPrice)
- `taxAmount` = Σ(quantity × unitPrice × taxRate / 100)

**幂等性：** ❌ 每次调用生成新订单号

#### GET /api/purchase/orders/{id} — 查询采购订单详情

**响应 data 字段：**
```json
{
  "id": 1,
  "orderNo": "PO20240101001",
  "supplierId": 1,
  "supplierName": "XX供应商",
  "orderDate": "2024-01-15",
  "deliveryDate": "2024-01-25",
  "status": "DRAFT",
  "totalAmount": 10000.00,
  "taxAmount": 1300.00,
  "remark": "",
  "details": [
    {
      "id": 1,
      "itemId": 1,
      "itemCode": "M001",
      "itemName": "螺丝",
      "specification": "M6x20",
      "quantity": 1000,
      "unitId": 1,
      "unitName": "个",
      "unitPrice": 5.00,
      "taxRate": 13.00,
      "amount": 5000.00,
      "taxAmount": 650.00,
      "receivedQuantity": 0,
      "remark": ""
    }
  ]
}
```

#### PUT /api/purchase/orders/{id} — 更新采购订单

**业务规则：** 只有 `DRAFT` 状态的订单可以修改

#### POST /api/purchase/orders/{id}/audit — 审核采购订单

**业务规则：**
- 只有 `DRAFT` 状态的订单可以审核
- 审核后状态变为 `AUDITED`
- 记录审核人和审核时间

**幂等性：** ✅ 已审核的订单再次审核不报错

#### POST /api/purchase/orders/{id}/cancel — 取消采购订单

**业务规则：**
- 只有 `DRAFT` 或 `AUDITED` 状态的订单可以取消
- 如果已有入库记录，不允许取消
- 取消后状态变为 `CANCELLED`

#### POST /api/purchase/orders/{id}/close — 关闭采购订单

**业务规则：**
- 只有 `AUDITED` 状态的订单可以关闭
- 关闭后状态变为 `CLOSED`

---

### 4.2 采购入库接口

#### POST /api/purchase/receipts — 创建采购入库单

**请求：**
```json
{
  "orderId": 1,                       // 关联采购订单 ID
  "supplierId": 1,                    // 必填
  "warehouseId": 1,                   // 必填，入库仓库
  "receiptDate": "2024-01-20",        // 必填
  "remark": "",
  "details": [
    {
      "orderDetailId": 1,            // 关联订单明细 ID
      "itemId": 1,                    // 必填
      "lotNo": "LOT20240120",        // 选填，批次号
      "quantity": 500,                // 必填，> 0
      "unitId": 1,                    // 必填
      "unitCost": 5.00,              // 入库成本
      "remark": ""
    }
  ]
}
```

**业务规则：**
- 状态初始为 `DRAFT`
- 入库数量不能超过订单剩余可收数量（可配置允许超收）
- 审核时调用 `InvTransactionService.createReceipt()` 更新库存

#### POST /api/purchase/receipts/{id}/audit — 审核采购入库

**业务规则：**
- 只有 `DRAFT` 状态可以审核
- 审核时调用 `InvTransactionService`
- 更新采购订单的 `receivedQuantity`
- 生成应付单（如果配置了自动挂账）

**幂等性：** ❌ 不可重复审核（会重复更新库存）

---

## 5. 销售管理接口

### 5.1 销售订单接口

#### GET /api/sales/orders — 分页查询销售订单

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "orderNo": "SO20240101001",
  "customerId": 1,
  "customerName": "XX客户",
  "orderDate": "2024-01-15",
  "deliveryDate": "2024-01-25",
  "status": "DRAFT",
  "totalAmount": 20000.00,
  "taxAmount": 2600.00,
  "salesmanId": 1,
  "salesmanName": "张三",
  "remark": ""
}
```

#### POST /api/sales/orders — 创建销售订单

**请求：**
```json
{
  "customerId": 1,                    // 必填
  "orderDate": "2024-01-15",          // 必填
  "deliveryDate": "2024-01-25",       // 选填
  "salesmanId": 1,                    // 选填
  "remark": "",
  "details": [
    {
      "itemId": 1,                    // 必填
      "quantity": 200,                // 必填，> 0
      "unitId": 1,                    // 必填
      "unitPrice": 10.00,            // 必填，> 0
      "taxRate": 13.00,
      "remark": ""
    }
  ]
}
```

**业务规则：**
- 检查客户信用额度（如果启用）
- 状态初始为 `DRAFT`

#### POST /api/sales/orders/{id}/audit — 审核销售订单

#### POST /api/sales/orders/{id}/ship — 销售发货

**业务规则：**
- 只有 `AUDITED` 状态可以发货
- 创建出库单时调用 `InvTransactionService`
- 更新订单状态为 `SHIPPED`

---

### 5.2 销售出库接口

#### POST /api/sales/shipments — 创建销售出库单

**请求：**
```json
{
  "orderId": 1,                       // 关联销售订单 ID
  "customerId": 1,
  "warehouseId": 1,                   // 出库仓库
  "shipmentDate": "2024-01-20",
  "remark": "",
  "details": [
    {
      "orderDetailId": 1,
      "itemId": 1,
      "lotNo": "LOT20240120",
      "quantity": 100,
      "unitId": 1,
      "unitPrice": 10.00,
      "remark": ""
    }
  ]
}
```

**业务规则：**
- 出库数量不能超过可用库存
- 审核时调用 `InvTransactionService.createShipment()`

---

## 6. 库存管理接口

### 6.1 即时库存查询

#### GET /api/inventory/stock — 查询库存列表

**请求参数：**
```
pageNum=1&pageSize=10&itemId=1&warehouseId=1&lotNo=LOT001&minQuantity=0
```

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "itemId": 1,
  "itemCode": "M001",
  "itemName": "螺丝",
  "specification": "M6x20",
  "warehouseId": 1,
  "warehouseName": "原料仓",
  "lotNo": "LOT20240120",
  "quantity": 5000.0000,        // 库存数量
  "avgCost": 5.000000,          // 平均成本
  "totalCost": 25000.00,        // 库存金额
  "lockedQuantity": 100.0000,   // 锁定数量
  "availableQuantity": 4900.0000 // 可用数量
}
```

#### GET /api/inventory/stock/summary — 库存汇总查询

**请求参数：**
```
itemId=1&warehouseId=1
```

**响应：**
```json
{
  "itemId": 1,
  "itemCode": "M001",
  "itemName": "螺丝",
  "totalQuantity": 15000.0000,
  "totalCost": 75000.00,
  "warehouses": [
    {
      "warehouseId": 1,
      "warehouseName": "原料仓",
      "quantity": 10000.0000
    },
    {
      "warehouseId": 2,
      "warehouseName": "车间仓",
      "quantity": 5000.0000
    }
  ]
}
```

### 6.2 库存流水查询

#### GET /api/inventory/transactions — 查询库存流水

**请求参数：**
```
pageNum=1&pageSize=10&transactionType=RECEIPT&itemId=1&warehouseId=1&startDate=2024-01-01&endDate=2024-01-31
```

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "transactionNo": "TXN20240120001",
  "transactionType": "RECEIPT",      // RECEIPT/ISSUE/TRANSFER/ADJUSTMENT/RETURN
  "transactionDate": "2024-01-20",
  "itemId": 1,
  "itemCode": "M001",
  "itemName": "螺丝",
  "warehouseId": 1,
  "warehouseName": "原料仓",
  "lotNo": "LOT20240120",
  "quantity": 500.0000,             // 正数=入库，负数=出库
  "unitCost": 5.000000,
  "totalCost": 2500.00,
  "referenceType": "PO",            // PO/SO/WO/MANUAL
  "referenceId": 1,
  "referenceNo": "PO20240101001",
  "remark": "采购入库",
  "createdByName": "管理员",
  "createdAt": "2024-01-20 10:00:00"
}
```

### 6.3 调拨管理接口

#### POST /api/inventory/transfers — 创建调拨单

**请求：**
```json
{
  "fromWarehouseId": 1,              // 调出仓库
  "toWarehouseId": 2,                // 调入仓库
  "transferDate": "2024-01-20",
  "remark": "",
  "details": [
    {
      "itemId": 1,
      "lotNo": "LOT20240120",
      "quantity": 100,               // 调拨数量
      "remark": ""
    }
  ]
}
```

**业务规则：**
- 调出仓库的可用库存必须充足
- 审核时调用 `InvTransactionService.createTransfer()`，同时生成一出一入两条流水

#### POST /api/inventory/transfers/{id}/audit — 审核调拨单

---

### 6.4 盘点管理接口

#### POST /api/inventory/stocktakes — 创建盘点单

**请求：**
```json
{
  "warehouseId": 1,                   // 必填
  "stocktakeDate": "2024-01-25",      // 必填
  "remark": "",
  "details": [
    {
      "itemId": 1,
      "lotNo": "LOT20240120",
      "systemQuantity": 5000,         // 系统数量（自动带出）
      "actualQuantity": 4980,         // 实盘数量
      "remark": "盘亏20个"
    }
  ]
}
```

#### POST /api/inventory/stocktakes/{id}/audit — 审核盘点单

**业务规则：**
- 审核时根据盈亏调用 `InvTransactionService.createAdjustment()`
- 盘盈：生成入库流水
- 盘亏：生成出库流水

---

## 7. 生产管理接口

### 7.1 BOM 管理接口

#### GET /api/production/boms — 查询 BOM 列表

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "bomCode": "BOM001",
  "itemId": 10,
  "itemCode": "FG001",
  "itemName": "成品A",
  "version": "1.0",
  "baseQuantity": 1,
  "status": 1,
  "details": [
    {
      "id": 1,
      "itemId": 1,
      "itemCode": "M001",
      "itemName": "螺丝",
      "quantity": 10,
      "lossRate": 2.00
    }
  ]
}
```

#### POST /api/production/boms — 创建 BOM

**请求：**
```json
{
  "bomCode": "BOM001",               // 必填，唯一
  "itemId": 10,                      // 必填，成品/半成品
  "version": "1.0",                  // 默认 1.0
  "baseQuantity": 1,                 // 基准数量
  "remark": "",
  "details": [
    {
      "itemId": 1,                   // 子件物料
      "quantity": 10,                // 单位用量
      "lossRate": 2.00,             // 损耗率 %
      "remark": ""
    }
  ]
}
```

**业务规则：**
- BOM 子件不能引用自己（防止循环）
- 同一成品同一版本只能有一个 BOM

---

### 7.2 生产工单接口

#### GET /api/production/work-orders — 查询工单列表

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "orderNo": "WO20240101001",
  "bomId": 1,
  "bomCode": "BOM001",
  "itemId": 10,
  "itemCode": "FG001",
  "itemName": "成品A",
  "quantity": 100,
  "completedQuantity": 0,
  "warehouseId": 3,
  "warehouseName": "成品仓",
  "planStartDate": "2024-01-20",
  "planEndDate": "2024-01-25",
  "status": "PENDING"
}
```

#### POST /api/production/work-orders — 创建生产工单

**请求：**
```json
{
  "bomId": 1,                        // 必填
  "itemId": 10,                      // 必填
  "quantity": 100,                   // 必填，计划数量
  "warehouseId": 3,                  // 完工入库仓库
  "planStartDate": "2024-01-20",
  "planEndDate": "2024-01-25",
  "remark": ""
}
```

#### POST /api/production/work-orders/{id}/release — 下达工单

**业务规则：**
- 只有 `PENDING` 状态可以下达
- 下达后状态变为 `RELEASED`
- 自动计算领料需求（BOM 用量 × 计划数量 × (1 + 损耗率)）

#### POST /api/production/work-orders/{id}/complete — 完工入库

**业务规则：**
- 只有 `IN_PROGRESS` 状态可以完工
- 完工数量不能超过计划数量
- 调用 `InvTransactionService.createReceipt()` 入库成品
- 更新 `completedQuantity`

---

### 7.3 领料单接口

#### POST /api/production/material-issues — 创建领料单

**请求：**
```json
{
  "workOrderId": 1,                  // 关联工单
  "warehouseId": 1,                  // 领料仓库
  "issueDate": "2024-01-20",
  "remark": "",
  "details": [
    {
      "itemId": 1,
      "lotNo": "LOT20240120",
      "quantity": 1000,              // 领料数量
      "remark": ""
    }
  ]
}
```

**业务规则：**
- 领料数量不能超过工单剩余需求量（可配置允许超额）
- 审核时调用 `InvTransactionService.createIssue()` 扣减库存

---

## 8. 财务管理接口

### 8.1 应收管理接口

#### GET /api/finance/receivables — 查询应收单列表

**响应 data.records[] 字段：**
```json
{
  "id": 1,
  "receivableNo": "AR20240101001",
  "customerId": 1,
  "customerName": "XX客户",
  "referenceType": "SO",
  "referenceId": 1,
  "referenceNo": "SO20240101001",
  "amount": 20000.00,
  "paidAmount": 10000.00,
  "unpaidAmount": 10000.00,
  "dueDate": "2024-02-15",
  "status": "PARTIAL"               // PENDING/PARTIAL/PAID
}
```

#### POST /api/finance/receipts — 创建收款单

**请求：**
```json
{
  "customerId": 1,
  "receiptDate": "2024-01-25",
  "amount": 10000.00,
  "paymentMethod": "BANK",           // BANK/CASH/CHECK
  "bankInfo": "工商银行XX支行",
  "remark": "",
  "allocations": [
    {
      "receivableId": 1,            // 应收单 ID
      "allocatedAmount": 10000.00   // 核销金额
    }
  ]
}
```

**业务规则：**
- 核销金额之和不能超过收款金额
- 核销金额不能超过应收单未收金额
- 审核时更新应收单的 `paidAmount` 和 `status`

---

## 9. 幂等性汇总

| 接口 | 幂等 | 说明 |
|------|------|------|
| 创建类（POST） | ❌ | 每次调用生成新记录 |
| 更新类（PUT） | ✅ | 相同数据多次更新结果一致 |
| 删除类（DELETE） | ✅ | 已删除再次删除不报错 |
| 审核/取消/关闭 | ✅ | 已审核再次审核不报错 |
| 登录 | ❌ | 每次生成新 Token |
| 刷新 Token | ✅ | 同一 refreshToken 返回相同 accessToken |

---

## 10. 错误码速查表

| 错误码 | 含义 | 常见场景 |
|--------|------|----------|
| 0 | 成功 | - |
| 1001 | 参数错误 | 缺少必填字段、格式错误 |
| 1002 | 数据不存在 | ID 查询无结果 |
| 1003 | 数据重复 | 唯一约束冲突（编码重复） |
| 1004 | 乐观锁冲突 | 并发修改同一记录 |
| 2001 | 未登录 | Token 过期或无效 |
| 2002 | 无权限 | 操作不在权限范围内 |
| 3001 | 库存不足 | 出库/领料数量超过可用库存 |
| 3002 | 单据状态不允许 | 非草稿状态尝试修改 |
| 3003 | BOM 循环引用 | 子件引用了父件 |
| 3004 | 超出信用额度 | 销售金额超过客户信用 |
| 9999 | 系统内部错误 | 未捕获的异常 |
