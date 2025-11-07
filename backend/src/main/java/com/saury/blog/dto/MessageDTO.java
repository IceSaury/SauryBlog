package com.saury.blog.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 留言DTO
 *
 * @author Saury
 */
@Data
public class MessageDTO {

    /**
     * 留言内容
     */
    @NotBlank(message = "留言内容不能为空")
    private String content;

    /**
     * 昵称（游客）
     */
    private String nickname;

    /**
     * 邮箱（游客）
     */
    private String email;
}


