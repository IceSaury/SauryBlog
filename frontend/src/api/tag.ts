import request from '@/utils/request'

/**
 * 标签API
 */

// 查询所有标签
export const getTagList = () => {
  return request({
    url: '/tag/list',
    method: 'get',
  })
}

// 查询标签详情
export const getTagDetail = (id: number) => {
  return request({
    url: `/tag/detail/${id}`,
    method: 'get',
  })
}

// 保存标签
export const saveTag = (data: any) => {
  return request({
    url: '/tag/save',
    method: 'post',
    data,
  })
}

// 删除标签
export const deleteTag = (id: number) => {
  return request({
    url: `/tag/delete/${id}`,
    method: 'delete',
  })
}


