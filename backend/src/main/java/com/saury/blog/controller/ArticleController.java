package com.saury.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.common.Result;
import com.saury.blog.dto.ArticleDTO;
import com.saury.blog.service.ArticleService;
import com.saury.blog.utils.JwtUtils;
import com.saury.blog.vo.ArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtils jwtUtils;

    /**
     * 分页查询文章列表
     */
    @GetMapping("/list")
    public Result<Page<ArticleVO>> getArticlePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        // 前台只查询已发布的文章
        if (status == null) {
            status = 1;
        }
        
        Page<ArticleVO> result = articleService.getArticlePage(page, size, categoryId, tagId, keyword, status);
        return Result.success(result);
    }

    /**
     * 查询文章详情
     */
    @GetMapping("/detail/{id}")
    public Result<ArticleVO> getArticleDetail(@PathVariable Long id) {
        ArticleVO article = articleService.getArticleById(id);
        // 增加浏览量
        articleService.incrementViewCount(id);
        return Result.success(article);
    }

    /**
     * 保存文章（新增或编辑）
     */
    @PostMapping("/save")
    public Result<Long> saveArticle(@Valid @RequestBody ArticleDTO articleDTO, HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        Long articleId = articleService.saveArticle(articleDTO, userId);
        return Result.success(articleId);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    /**
     * 下架文章
     */
    @PutMapping("/unlist/{id}")
    public Result<Void> unlistArticle(@PathVariable Long id) {
        articleService.unlistArticle(id);
        return Result.success();
    }
}


