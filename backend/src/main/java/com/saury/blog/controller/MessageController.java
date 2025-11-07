package com.saury.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.common.Result;
import com.saury.blog.dto.MessageDTO;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.service.MessageService;
import com.saury.blog.service.SiteConfigService;
import com.saury.blog.utils.JwtUtils;
import com.saury.blog.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 留言控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SiteConfigService siteConfigService;
    private final JwtUtils jwtUtils;

    /**
     * 分页查询留言列表
     */
    @GetMapping("/list")
    public Result<Page<MessageVO>> getMessagePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        // 前台只查询已通过的留言
        if (status == null) {
            status = 1;
        }
        
        Page<MessageVO> result = messageService.getMessagePage(page, size, status);
        return Result.success(result);
    }

    /**
     * 保存留言
     */
    @PostMapping("/save")
    public Result<Long> saveMessage(@Valid @RequestBody MessageDTO messageDTO, HttpServletRequest request) {
        // 检查是否需要登录
        boolean loginRequired = siteConfigService.isMessageLoginRequired();
        
        // 尝试获取用户ID（可能未登录）
        Long userId = null;
        try {
            userId = jwtUtils.getUserIdFromRequest(request);
        } catch (Exception e) {
            // 游客留言
        }
        
        // 如果需要登录但用户未登录，抛出异常
        if (loginRequired && userId == null) {
            throw new BusinessException("请先登录后再留言");
        }
        
        String ip = getIpAddress(request);
        Long messageId = messageService.saveMessage(messageDTO, userId, ip);
        return Result.success(messageId);
    }

    /**
     * 删除留言
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return Result.success();
    }

    /**
     * 审核留言
     */
    @PutMapping("/audit/{id}")
    public Result<Void> auditMessage(@PathVariable Long id, @RequestParam Integer status) {
        messageService.auditMessage(id, status);
        return Result.success();
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}


