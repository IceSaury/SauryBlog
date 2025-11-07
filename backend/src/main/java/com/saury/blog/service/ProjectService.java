package com.saury.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.ProjectDTO;
import com.saury.blog.vo.ProjectVO;

import java.util.List;

/**
 * 项目服务接口
 *
 * @author Saury
 */
public interface ProjectService {

    /**
     * 分页查询项目列表
     *
     * @param page    页码
     * @param size    每页数量
     * @param keyword 关键词
     * @param type    项目类型
     * @param status  状态
     * @return 项目分页列表
     */
    Page<ProjectVO> getProjectPage(Integer page, Integer size, String keyword, Integer type, Integer status);

    /**
     * 查询所有显示的项目
     *
     * @return 项目列表
     */
    List<ProjectVO> getAllVisibleProjects();

    /**
     * 根据ID查询项目详情
     *
     * @param id 项目ID
     * @return 项目详情
     */
    ProjectVO getProjectById(Long id);

    /**
     * 保存项目（新增或编辑）
     *
     * @param projectDTO 项目DTO
     * @return 项目ID
     */
    Long saveProject(ProjectDTO projectDTO);

    /**
     * 删除项目
     *
     * @param id 项目ID
     */
    void deleteProject(Long id);

    /**
     * 增加浏览量
     *
     * @param id 项目ID
     */
    void incrementViewCount(Long id);
}


