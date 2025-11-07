package com.saury.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.common.Result;
import com.saury.blog.dto.CommentDTO;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.service.CommentService;
import com.saury.blog.service.SiteConfigService;
import com.saury.blog.utils.JwtUtils;
import com.saury.blog.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final SiteConfigService siteConfigService;
    private final JwtUtils jwtUtils;

    /**
     * 根据文章ID查询评论列表（树形结构）
     */
    @GetMapping("/list/{articleId}")
    public Result<List<CommentVO>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<CommentVO> comments = commentService.getCommentsByArticleId(articleId);
        return Result.success(comments);
    }

    /**
     * 分页查询评论列表（后台管理）
     */
    @GetMapping("/page")
    public Result<Page<CommentVO>> getCommentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Integer status) {
        
        Page<CommentVO> result = commentService.getCommentPage(page, size, articleId, status);
        return Result.success(result);
    }

    /**
     * 保存评论
     */
    @PostMapping("/save")
    public Result<Long> saveComment(@Valid @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        // 检查是否需要登录
        boolean loginRequired = siteConfigService.isCommentLoginRequired();
        
        // 尝试获取用户ID（可能未登录）
        Long userId = null;
        try {
            userId = jwtUtils.getUserIdFromRequest(request);
        } catch (Exception e) {
            // 游客评论
        }
        
        // 如果需要登录但用户未登录，抛出异常
        if (loginRequired && userId == null) {
            throw new BusinessException("请先登录后再评论");
        }
        
        String ip = getIpAddress(request);
        Long commentId = commentService.saveComment(commentDTO, userId, ip);
        return Result.success(commentId);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    /**
     * 审核评论
     */
    @PutMapping("/audit/{id}")
    public Result<Void> auditComment(@PathVariable Long id, @RequestParam Integer status) {
        commentService.auditComment(id, status);
        return Result.success();
    }

    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/like/{id}")
    public Result<Boolean> likeComment(@PathVariable Long id, HttpServletRequest request) {
        // 尝试获取用户ID（可能未登录）
        Long userId = null;
        try {
            userId = jwtUtils.getUserIdFromRequest(request);
        } catch (Exception e) {
            // 游客点赞
        }
        
        String ip = getIpAddress(request);
        boolean liked = commentService.likeComment(id, userId, ip);
        return Result.success(liked);
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


