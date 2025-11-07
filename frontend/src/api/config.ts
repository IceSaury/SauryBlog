import request from '@/utils/request'
import type { SiteConfig } from '@/types'

// 获取所有配置
export const getAllConfigs = () => {
  return request({
    url: '/config/list',
    method: 'get',
  })
}

// 获取所有配置（以Map形式返回）
export const getAllConfigsAsMap = () => {
  return request({
    url: '/config/map',
    method: 'get',
  })
}

// 根据配置键获取配置值
export const getConfigValue = (configKey: string) => {
  return request({
    url: `/config/${configKey}`,
    method: 'get',
  })
}

// 保存或更新单个配置
export const saveOrUpdateConfig = (configKey: string, configValue: string) => {
  return request({
    url: `/config/${configKey}`,
    method: 'put',
    params: { configValue },
  })
}

// 批量保存或更新配置
export const batchSaveOrUpdate = (configs: Record<string, string>) => {
  return request({
    url: '/config/batch',
    method: 'put',
    data: configs,
  })
}

// 删除配置
export const deleteConfig = (configKey: string) => {
  return request({
    url: `/config/${configKey}`,
    method: 'delete',
  })
}

// 判断留言是否需要登录
export const isMessageLoginRequired = () => {
  return request<boolean>({
    url: '/config/message/login-required',
    method: 'get',
  })
}

// 判断评论是否需要登录
export const isCommentLoginRequired = () => {
  return request<boolean>({
    url: '/config/comment/login-required',
    method: 'get',
  })
}

