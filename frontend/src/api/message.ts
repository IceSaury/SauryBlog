import request from '@/utils/request'

/**
 * 留言API
 */

// 分页查询留言列表
export const getMessagePage = (params: any) => {
  return request({
    url: '/message/list',
    method: 'get',
    params,
  })
}

// 保存留言
export const saveMessage = (data: any) => {
  return request({
    url: '/message/save',
    method: 'post',
    data,
  })
}

// 删除留言
export const deleteMessage = (id: number) => {
  return request({
    url: `/message/delete/${id}`,
    method: 'delete',
  })
}

// 审核留言
export const auditMessage = (id: number, status: number) => {
  return request({
    url: `/message/audit/${id}`,
    method: 'put',
    params: { status },
  })
}


