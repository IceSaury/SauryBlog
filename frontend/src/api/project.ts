import request from '@/utils/request'

/**
 * 项目API
 */

// 分页查询项目列表
export const getProjectPage = (params: any) => {
  return request({
    url: '/project/list',
    method: 'get',
    params,
  })
}

// 查询所有显示的项目
export const getAllProjects = () => {
  return request({
    url: '/project/all',
    method: 'get',
  })
}

// 查询项目详情
export const getProjectDetail = (id: number) => {
  return request({
    url: `/project/detail/${id}`,
    method: 'get',
  })
}

// 保存项目
export const saveProject = (data: any) => {
  return request({
    url: '/project/save',
    method: 'post',
    data,
  })
}

// 删除项目
export const deleteProject = (id: number) => {
  return request({
    url: `/project/delete/${id}`,
    method: 'delete',
  })
}


