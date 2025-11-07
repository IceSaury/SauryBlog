package com.saury.blog.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 项目DTO
 *
 * @author Saury
 */
@Data
public class ProjectDTO {

    /**
     * 项目ID（编辑时需要）
     */
    private Long id;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String name;

    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空")
    private String description;

    /**
     * 项目封面
     */
    private String cover;

    /**
     * 项目图片列表
     */
    private List<String> images;

    /**
     * 技术栈
     */
    private List<String> techStack;

    /**
     * GitHub地址
     */
    private String githubUrl;

    /**
     * 演示地址
     */
    private String demoUrl;

    /**
     * 项目亮点
     */
    private List<String> highlights;

    /**
     * 项目类型：0-企业项目 1-个人项目 2-开源项目
     */
    private Integer type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-隐藏 1-显示
     */
    private Integer status;
}


