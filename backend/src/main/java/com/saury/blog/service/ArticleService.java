package com.saury.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.ArticleDTO;
import com.saury.blog.vo.ArticleVO;

/**
 * 文章服务接口
 *
 * @author Saury
 */
public interface ArticleService {

    /**
     * 分页查询文章列表
     *
     * @param page       页码
     * @param size       每页数量
     * @param categoryId 分类ID
     * @param tagId      标签ID
     * @param keyword    关键词
     * @param status     状态
     * @return 文章分页列表
     */
    Page<ArticleVO> getArticlePage(Integer page, Integer size, Long categoryId, Long tagId, String keyword, Integer status);

    /**
     * 根据ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Long id);

    /**
     * 保存文章（新增或编辑）
     *
     * @param articleDTO 文章DTO
     * @param userId     用户ID
     * @return 文章ID
     */
    Long saveArticle(ArticleDTO articleDTO, Long userId);

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    void deleteArticle(Long id);

    /**
     * 增加浏览量
     *
     * @param id 文章ID
     */
    void incrementViewCount(Long id);

    /**
     * 下架文章
     *
     * @param id 文章ID
     */
    void unlistArticle(Long id);
}


