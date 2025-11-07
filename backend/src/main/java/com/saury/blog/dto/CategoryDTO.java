package com.saury.blog.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 分类DTO
 *
 * @author Saury
 */
@Data
public class CategoryDTO {

    /**
     * 分类ID（编辑时需要）
     */
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;
}


