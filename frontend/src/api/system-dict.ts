import request from './request'
import type { R } from './types'

// ========== 字典类型定义 ==========

export interface SysDictType {
  id: number
  dictType: string
  dictName: string
  status: number
  remark: string
  createdAt: string
}

export interface SysDictData {
  id: number
  dictType: string
  dictLabel: string
  dictValue: string
  dictColor: string
  sortOrder: number
  status: number
  remark: string
  createdAt: string
}

// ========== 字典类型 API ==========

export function getDictTypes(): Promise<R<SysDictType[]>> {
  return request.get('/system/dicts/types')
}

export function getDictType(id: number): Promise<R<SysDictType>> {
  return request.get(`/system/dicts/types/${id}`)
}

export function createDictType(data: Partial<SysDictType>): Promise<R<SysDictType>> {
  return request.post('/system/dicts/types', data)
}

export function updateDictType(id: number, data: Partial<SysDictType>): Promise<R<SysDictType>> {
  return request.put(`/system/dicts/types/${id}`, data)
}

export function deleteDictType(id: number): Promise<R<void>> {
  return request.delete(`/system/dicts/types/${id}`)
}

// ========== 字典数据 API ==========

export function getDictDataByType(dictType: string): Promise<R<SysDictData[]>> {
  return request.get(`/system/dicts/data/${dictType}`)
}

export function getDictData(id: number): Promise<R<SysDictData>> {
  return request.get(`/system/dicts/data/detail/${id}`)
}

export function createDictData(data: Partial<SysDictData>): Promise<R<SysDictData>> {
  return request.post('/system/dicts/data', data)
}

export function updateDictData(id: number, data: Partial<SysDictData>): Promise<R<SysDictData>> {
  return request.put(`/system/dicts/data/${id}`, data)
}

export function deleteDictData(id: number): Promise<R<void>> {
  return request.delete(`/system/dicts/data/${id}`)
}
