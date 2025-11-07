package com.saury.blog.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 文章DTO
 *
 * @author Saury
 */
@Data
public class ArticleDTO {

    /**
     * 文章ID（编辑时需要）
     */
    private Long id;

    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 文章标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章封面
     */
    private String cover;

    /**
     * 文章内容（Markdown）
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 是否置顶：0-否 1-是
     */
    private Integer isTop;

    /**
     * 是否推荐：0-否 1-是
     */
    private Integer isFeatured;

    /**
     * 状态：0-草稿 1-已发布 2-已下架
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}


