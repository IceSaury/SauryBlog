package com.saury.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 *
 * @author Saury
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 密钥
     */
    private String secret;

    /**
     * token有效期（秒）
     */
    private Long expiration;

    /**
     * token请求头名称
     */
    private String header;

    /**
     * token前缀
     */
    private String prefix;
}

