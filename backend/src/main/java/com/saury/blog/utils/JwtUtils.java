package com.saury.blog.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.saury.blog.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT工具类
 *
 * @author Saury
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    /**
     * 生成Token
     *
     * @param subject    主题（通常为用户ID）
     * @param expiration 过期时间（毫秒）
     * @param secret     密钥
     * @return token
     */
    public static String generateToken(String subject, Long expiration, String secret) {
        return generateToken(subject, null, expiration, secret);
    }

    /**
     * 生成Token（带额外信息）
     *
     * @param subject    主题（通常为用户ID）
     * @param claims     额外信息
     * @param expiration 过期时间（毫秒）
     * @param secret     密钥
     * @return token
     */
    public static String generateToken(String subject, Map<String, Object> claims, Long expiration, String secret) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration * 1000);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        var builder = Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(key);

        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        return builder.compact();
    }

    /**
     * 解析Token
     *
     * @param token  token
     * @param secret 密钥
     * @return Claims
     */
    public static Claims parseToken(String token, String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取Token中的主题
     *
     * @param token  token
     * @param secret 密钥
     * @return 主题
     */
    public static String getSubject(String token, String secret) {
        return parseToken(token, secret).getSubject();
    }

    /**
     * 验证Token是否过期
     *
     * @param token  token
     * @param secret 密钥
     * @return true-已过期 false-未过期
     */
    public static boolean isTokenExpired(String token, String secret) {
        try {
            Date expiration = parseToken(token, secret).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token  token
     * @param secret 密钥
     * @return true-有效 false-无效
     */
    public static boolean validateToken(String token, String secret) {
        try {
            parseToken(token, secret);
            return !isTokenExpired(token, secret);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求中获取Token
     *
     * @param request 请求
     * @return token
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(jwtProperties.getHeader());
        if (StringUtils.isNotBlank(token)) {
            // 去除所有空格
            token = token.trim();
            if (token.startsWith(jwtProperties.getPrefix())) {
                String extractedToken = token.substring(jwtProperties.getPrefix().length()).trim();
                // 再次确保没有空格
                return extractedToken.replaceAll("\\s+", "");
            }
        }
        return null;
    }

    /**
     * 从请求中获取用户ID
     *
     * @param request 请求
     * @return 用户ID
     */
    public Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (StringUtils.isBlank(token)) {
                return null;
            }
            
            // 验证token格式
            if (!isValidJwtFormat(token)) {
                log.warn("Invalid JWT format: {}", token);
                return null;
            }
            
            String subject = getSubject(token, jwtProperties.getSecret());
            return Long.parseLong(subject);
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 验证JWT格式是否有效
     *
     * @param token JWT token
     * @return true-有效 false-无效
     */
    private boolean isValidJwtFormat(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        
        // JWT应该包含两个点号分隔的三部分
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        
        // 检查每部分都不为空且不包含空格
        for (String part : parts) {
            if (StringUtils.isBlank(part) || part.contains(" ")) {
                return false;
            }
        }
        
        return true;
    }
}

