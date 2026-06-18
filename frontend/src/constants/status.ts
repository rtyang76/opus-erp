/**
 * 状态常量定义
 * 避免在代码中硬编码 1/0 等魔法数字
 */

// 选项类型
interface OptionItem {
  label: string
  value: string | number
}

// 通用状态
export const STATUS_ENABLED = 1
export const STATUS_DISABLED = 0

export const STATUS_OPTIONS: OptionItem[] = [
  { label: '启用', value: STATUS_ENABLED },
  { label: '禁用', value: STATUS_DISABLED },
]

// 物料类型
export const ITEM_TYPE_RAW = 'RAW'
export const ITEM_TYPE_SEMI = 'SEMI'
export const ITEM_TYPE_FINISHED = 'FINISHED'
export const ITEM_TYPE_AUXILIARY = 'AUXILIARY'

export const ITEM_TYPE_OPTIONS: OptionItem[] = [
  { label: '原材料', value: ITEM_TYPE_RAW },
  { label: '半成品', value: ITEM_TYPE_SEMI },
  { label: '成品', value: ITEM_TYPE_FINISHED },
  { label: '辅材', value: ITEM_TYPE_AUXILIARY },
]

// 仓库类型
export const WAREHOUSE_TYPE_RAW = 'RAW'
export const WAREHOUSE_TYPE_SEMI = 'SEMI'
export const WAREHOUSE_TYPE_FINISHED = 'FINISHED'
export const WAREHOUSE_TYPE_RETURN = 'RETURN'
export const WAREHOUSE_TYPE_DEFECTIVE = 'DEFECTIVE'

export const WAREHOUSE_TYPE_OPTIONS: OptionItem[] = [
  { label: '原料仓', value: WAREHOUSE_TYPE_RAW },
  { label: '半成品仓', value: WAREHOUSE_TYPE_SEMI },
  { label: '成品仓', value: WAREHOUSE_TYPE_FINISHED },
  { label: '退货仓', value: WAREHOUSE_TYPE_RETURN },
  { label: '不良品仓', value: WAREHOUSE_TYPE_DEFECTIVE },
]

// ABC分类
export const ABC_CLASS_A = 'A'
export const ABC_CLASS_B = 'B'
export const ABC_CLASS_C = 'C'

export const ABC_CLASS_OPTIONS: OptionItem[] = [
  { label: 'A类', value: ABC_CLASS_A },
  { label: 'B类', value: ABC_CLASS_B },
  { label: 'C类', value: ABC_CLASS_C },
]

// 评级
export const RATING_A = 'A'
export const RATING_B = 'B'
export const RATING_C = 'C'

export const RATING_OPTIONS: OptionItem[] = [
  { label: 'A级', value: RATING_A },
  { label: 'B级', value: RATING_B },
  { label: 'C级', value: RATING_C },
]

// 单据状态
export const DOC_STATUS_DRAFT = 'DRAFT'
export const DOC_STATUS_AUDITED = 'AUDITED'
export const DOC_STATUS_COMPLETED = 'COMPLETED'
export const DOC_STATUS_CANCELLED = 'CANCELLED'
export const DOC_STATUS_CLOSED = 'CLOSED'

export const DOC_STATUS_OPTIONS: OptionItem[] = [
  { label: '草稿', value: DOC_STATUS_DRAFT },
  { label: '已审核', value: DOC_STATUS_AUDITED },
  { label: '已完成', value: DOC_STATUS_COMPLETED },
  { label: '已取消', value: DOC_STATUS_CANCELLED },
  { label: '已关闭', value: DOC_STATUS_CLOSED },
]

// 库存交易类型
export const TRANSACTION_TYPE_RECEIPT = 'RECEIPT'
export const TRANSACTION_TYPE_ISSUE = 'ISSUE'
export const TRANSACTION_TYPE_TRANSFER = 'TRANSFER'
export const TRANSACTION_TYPE_ADJUSTMENT = 'ADJUSTMENT'
export const TRANSACTION_TYPE_RETURN = 'RETURN'

export const TRANSACTION_TYPE_OPTIONS: OptionItem[] = [
  { label: '入库', value: TRANSACTION_TYPE_RECEIPT },
  { label: '出库', value: TRANSACTION_TYPE_ISSUE },
  { label: '调拨', value: TRANSACTION_TYPE_TRANSFER },
  { label: '调整', value: TRANSACTION_TYPE_ADJUSTMENT },
  { label: '退货', value: TRANSACTION_TYPE_RETURN },
]

// 付款方式
export const PAYMENT_METHOD_BANK = 'BANK'
export const PAYMENT_METHOD_CASH = 'CASH'
export const PAYMENT_METHOD_CHECK = 'CHECK'

export const PAYMENT_METHOD_OPTIONS: OptionItem[] = [
  { label: '银行转账', value: PAYMENT_METHOD_BANK },
  { label: '现金', value: PAYMENT_METHOD_CASH },
  { label: '支票', value: PAYMENT_METHOD_CHECK },
]
