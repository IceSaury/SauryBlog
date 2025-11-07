package com.saury.blog.interceptor;

import com.saury.blog.config.JwtProperties;
import com.saury.blog.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 *
 * @author Saury
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader(jwtProperties.getHeader());
        
        if (StringUtils.isBlank(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未认证，请先登录\"}");
            return false;
        }

        // 去除token前缀
        if (token.startsWith(jwtProperties.getPrefix())) {
            token = token.substring(jwtProperties.getPrefix().length()).trim();
        }

        // 验证token
        if (!JwtUtils.validateToken(token, jwtProperties.getSecret())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // 获取用户ID并存入request
        String userId = JwtUtils.getSubject(token, jwtProperties.getSecret());
        request.setAttribute("userId", userId);

        return true;
    }
}

