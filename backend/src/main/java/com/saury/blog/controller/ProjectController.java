package com.saury.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.common.Result;
import com.saury.blog.dto.ProjectDTO;
import com.saury.blog.service.ProjectService;
import com.saury.blog.vo.ProjectVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 分页查询项目列表
     */
    @GetMapping("/list")
    public Result<Page<ProjectVO>> getProjectPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        
        // 前台只查询显示的项目（如果status未指定且未提供keyword，则默认为1）
        if (status == null && keyword == null) {
            status = 1;
        }
        
        Page<ProjectVO> result = projectService.getProjectPage(page, size, keyword, type, status);
        return Result.success(result);
    }

    /**
     * 查询所有显示的项目
     */
    @GetMapping("/all")
    public Result<List<ProjectVO>> getAllProjects() {
        List<ProjectVO> projects = projectService.getAllVisibleProjects();
        return Result.success(projects);
    }

    /**
     * 查询项目详情
     */
    @GetMapping("/detail/{id}")
    public Result<ProjectVO> getProjectDetail(@PathVariable Long id) {
        ProjectVO project = projectService.getProjectById(id);
        // 增加浏览量
        projectService.incrementViewCount(id);
        return Result.success(project);
    }

    /**
     * 保存项目（新增或编辑）
     */
    @PostMapping("/save")
    public Result<Long> saveProject(@Valid @RequestBody ProjectDTO projectDTO) {
        Long projectId = projectService.saveProject(projectDTO);
        return Result.success(projectId);
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}


