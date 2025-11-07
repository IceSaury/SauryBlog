package com.saury.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目VO
 *
 * @author Saury
 */
@Data
public class ProjectVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
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
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 状态：0-隐藏 1-显示
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}


