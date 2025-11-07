import request from '@/utils/request'
import type { LoginForm, RegisterForm } from '@/types'

/**
 * 用户注册
 */
export const register = (data: RegisterForm) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data,
  })
}

/**
 * 用户登录
 */
export const login = (data: LoginForm) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  })
}

/**
 * 用户登出
 */
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}

