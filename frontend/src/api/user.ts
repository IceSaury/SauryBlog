import request from '@/utils/request'

/**
 * 用户API
 */

// 获取当前用户信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get',
  })
}

// 更新用户信息
export const updateUserInfo = (data: any) => {
  return request({
    url: '/user/update',
    method: 'put',
    data,
  })
}

// 修改密码
export const updatePassword = (data: any) => {
  return request({
    url: '/user/password',
    method: 'put',
    data,
  })
}

