package com.saury.blog.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;

/**
 * 用户信息DTO
 *
 * @author Saury
 */
@Data
public class UserDTO {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * GitHub地址
     */
    private String github;

    /**
     * CSDN地址
     */
    private String csdn;

    /**
     * 个人简介
     */
    private String intro;
}

