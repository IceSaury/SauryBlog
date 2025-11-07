package com.saury.blog.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 标签DTO
 *
 * @author Saury
 */
@Data
public class TagDTO {

    /**
     * 标签ID（编辑时需要）
     */
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /**
     * 标签颜色
     */
    private String color;
}


