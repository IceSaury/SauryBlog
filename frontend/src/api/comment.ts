import request from '@/utils/request'

/**
 * 评论API
 */

// 根据文章ID查询评论列表
export const getCommentsByArticleId = (articleId: number) => {
  return request({
    url: `/comment/list/${articleId}`,
    method: 'get',
  })
}

// 分页查询评论列表（后台管理）
export const getCommentPage = (params: any) => {
  return request({
    url: '/comment/page',
    method: 'get',
    params,
  })
}

// 保存评论
export const saveComment = (data: any) => {
  return request({
    url: '/comment/save',
    method: 'post',
    data,
  })
}

// 删除评论
export const deleteComment = (id: number) => {
  return request({
    url: `/comment/delete/${id}`,
    method: 'delete',
  })
}

// 审核评论
export const auditComment = (id: number, status: number) => {
  return request({
    url: `/comment/audit/${id}`,
    method: 'put',
    params: { status },
  })
}

// 点赞/取消点赞评论
export const likeComment = (id: number) => {
  return request({
    url: `/comment/like/${id}`,
    method: 'post',
  })
}


