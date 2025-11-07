package com.saury.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.CommentDTO;
import com.saury.blog.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author Saury
 */
public interface CommentService {

    /**
     * 根据文章ID查询评论列表（树形结构）
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    List<CommentVO> getCommentsByArticleId(Long articleId);

    /**
     * 分页查询评论列表（后台管理）
     *
     * @param page      页码
     * @param size      每页数量
     * @param articleId 文章ID
     * @param status    状态
     * @return 评论分页列表
     */
    Page<CommentVO> getCommentPage(Integer page, Integer size, Long articleId, Integer status);

    /**
     * 保存评论
     *
     * @param commentDTO 评论DTO
     * @param userId     用户ID（可为空）
     * @param ip         IP地址
     * @return 评论ID
     */
    Long saveComment(CommentDTO commentDTO, Long userId, String ip);

    /**
     * 删除评论
     *
     * @param id 评论ID
     */
    void deleteComment(Long id);

    /**
     * 审核评论
     *
     * @param id     评论ID
     * @param status 状态
     */
    void auditComment(Long id, Integer status);

    /**
     * 点赞/取消点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID（可为空）
     * @param ip        IP地址
     * @return true-点赞成功 false-取消点赞
     */
    boolean likeComment(Long commentId, Long userId, String ip);
}


