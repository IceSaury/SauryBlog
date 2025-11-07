package com.saury.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目实体
 *
 * @author Saury
 */
@Data
@TableName("project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 项目图片（JSON数组）
     */
    private String images;

    /**
     * 技术栈（JSON数组）
     */
    private String techStack;

    /**
     * GitHub地址
     */
    private String githubUrl;

    /**
     * 演示地址
     */
    private String demoUrl;

    /**
     * 项目亮点（JSON数组）
     */
    private String highlights;

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
     * 逻辑删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

