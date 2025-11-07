package com.saury.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果状态码枚举
 *
 * @author Saury
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(4001, "用户名或密码错误"),

    /**
     * 用户已存在
     */
    USER_EXIST(4002, "用户已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(4003, "用户不存在"),

    /**
     * Token无效
     */
    TOKEN_INVALID(4011, "Token无效"),

    /**
     * Token过期
     */
    TOKEN_EXPIRED(4012, "Token已过期"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(5000, "系统异常，请稍后重试");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;
}

