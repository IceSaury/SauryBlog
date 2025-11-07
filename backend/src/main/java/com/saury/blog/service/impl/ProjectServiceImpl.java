package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.ProjectDTO;
import com.saury.blog.entity.Project;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.ProjectMapper;
import com.saury.blog.service.ProjectService;
import com.saury.blog.service.ViewStatsService;
import com.saury.blog.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final ViewStatsService viewStatsService;

    @Override
    public Page<ProjectVO> getProjectPage(Integer page, Integer size, String keyword, Integer type, Integer status) {
        Page<Project> projectPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isEmpty(), Project::getName, keyword)
               .eq(type != null, Project::getType, type)
               .eq(status != null, Project::getStatus, status)
               .orderByAsc(Project::getSort)
               .orderByDesc(Project::getCreateTime);

        Page<Project> result = projectMapper.selectPage(projectPage, wrapper);
        
        // 转换为VO
        Page<ProjectVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ProjectVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public List<ProjectVO> getAllVisibleProjects() {
        List<Project> projects = projectMapper.selectList(
            new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, 1)
                .orderByAsc(Project::getSort)
        );
        
        return projects.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public ProjectVO getProjectById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return convertToVO(project);
    }

    @Override
    public Long saveProject(ProjectDTO projectDTO) {
        Project project = BeanUtil.copyProperties(projectDTO, Project.class);
        
        // 处理JSON字段
        if (projectDTO.getImages() != null) {
            project.setImages(JSON.toJSONString(projectDTO.getImages()));
        }
        if (projectDTO.getTechStack() != null) {
            project.setTechStack(JSON.toJSONString(projectDTO.getTechStack()));
        }
        if (projectDTO.getHighlights() != null) {
            project.setHighlights(JSON.toJSONString(projectDTO.getHighlights()));
        }
        
        // 初始化默认值
        if (project.getId() == null) {
            project.setViewCount(0);
            project.setSort(project.getSort() != null ? project.getSort() : 0);
            project.setStatus(project.getStatus() != null ? project.getStatus() : 1);
        }
        
        if (project.getId() != null) {
            projectMapper.updateById(project);
        } else {
            projectMapper.insert(project);
        }
        
        return project.getId();
    }

    @Override
    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Project project = projectMapper.selectById(id);
        if (project != null) {
            project.setViewCount(project.getViewCount() + 1);
            projectMapper.updateById(project);
            
            // 记录每日访问量到Redis
            viewStatsService.recordDailyView();
        }
    }

    /**
     * 转换为VO
     */
    private ProjectVO convertToVO(Project project) {
        ProjectVO vo = BeanUtil.copyProperties(project, ProjectVO.class);
        
        // 解析JSON字段
        if (project.getImages() != null) {
            vo.setImages(JSON.parseArray(project.getImages(), String.class));
        }
        if (project.getTechStack() != null) {
            vo.setTechStack(JSON.parseArray(project.getTechStack(), String.class));
        }
        if (project.getHighlights() != null) {
            vo.setHighlights(JSON.parseArray(project.getHighlights(), String.class));
        }
        
        return vo;
    }
}


