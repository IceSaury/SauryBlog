import request from '@/utils/request'

/**
 * 分类API
 */

// 查询所有分类
export const getCategoryList = () => {
  return request({
    url: '/category/list',
    method: 'get',
  })
}

// 查询分类详情
export const getCategoryDetail = (id: number) => {
  return request({
    url: `/category/detail/${id}`,
    method: 'get',
  })
}

// 保存分类
export const saveCategory = (data: any) => {
  return request({
    url: '/category/save',
    method: 'post',
    data,
  })
}

// 删除分类
export const deleteCategory = (id: number) => {
  return request({
    url: `/category/delete/${id}`,
    method: 'delete',
  })
}


