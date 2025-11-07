import request from '@/utils/request'

/**
 * 文章API
 */

// 分页查询文章列表
export const getArticlePage = (params: any) => {
  return request({
    url: '/article/list',
    method: 'get',
    params,
  })
}

// 查询文章详情
export const getArticleDetail = (id: number) => {
  return request({
    url: `/article/detail/${id}`,
    method: 'get',
  })
}

// 保存文章
export const saveArticle = (data: any) => {
  return request({
    url: '/article/save',
    method: 'post',
    data,
  })
}

// 删除文章
export const deleteArticle = (id: number) => {
  return request({
    url: `/article/delete/${id}`,
    method: 'delete',
  })
}

// 下架文章
export const unlistArticle = (id: number) => {
  return request({
    url: `/article/unlist/${id}`,
    method: 'put',
  })
}


