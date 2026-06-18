/**
 * 通用 API 响应类型
 */
export interface R<T> {
  code: number
  msg: string
  data: T
}

/**
 * 分页结果类型
 */
export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}
