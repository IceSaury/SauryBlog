package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.CommentDTO;
import com.saury.blog.entity.Article;
import com.saury.blog.entity.Comment;
import com.saury.blog.entity.CommentLike;
import com.saury.blog.entity.User;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.ArticleMapper;
import com.saury.blog.mapper.CommentMapper;
import com.saury.blog.mapper.CommentLikeMapper;
import com.saury.blog.mapper.UserMapper;
import com.saury.blog.service.CommentService;
import com.saury.blog.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final CommentLikeMapper commentLikeMapper;

    @Override
    public List<CommentVO> getCommentsByArticleId(Long articleId) {
        // 查询所有已通过的评论
        List<Comment> comments = commentMapper.selectList(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime)
        );
        
        List<CommentVO> commentVOs = comments.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        // 构建树形结构
        return buildCommentTree(commentVOs);
    }

    @Override
    public Page<CommentVO> getCommentPage(Integer page, Integer size, Long articleId, Integer status) {
        Page<Comment> commentPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(articleId != null, Comment::getArticleId, articleId)
               .eq(status != null, Comment::getStatus, status)
               .orderByDesc(Comment::getCreateTime);

        Page<Comment> result = commentMapper.selectPage(commentPage, wrapper);
        
        // 转换为VO
        Page<CommentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<CommentVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveComment(CommentDTO commentDTO, Long userId, String ip) {
        Comment comment = BeanUtil.copyProperties(commentDTO, Comment.class);
        comment.setUserId(userId);
        comment.setIp(ip);
        
        // 如果是登录用户，获取用户信息
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                comment.setNickname(user.getNickname());
                comment.setEmail(user.getEmail());
                comment.setAvatar(user.getAvatar());
            }
        }
        
        // 初始化默认值
        comment.setLikeCount(0);
        comment.setStatus(1); // 默认通过（可根据需求改为待审核）
        
        commentMapper.insert(comment);
        
        // 更新文章评论数
        updateArticleCommentCount(commentDTO.getArticleId());
        
        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id) {
        // 获取评论信息（用于更新文章评论数）
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        Long articleId = comment.getArticleId();
        
        // 将评论状态设置为已删除（status=0）
        comment.setStatus(0);
        commentMapper.updateById(comment);
        
        // 删除子评论（将子评论状态也设置为已删除）
        List<Comment> childComments = commentMapper.selectList(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, id)
        );
        for (Comment child : childComments) {
            child.setStatus(0);
            commentMapper.updateById(child);
        }
        
        // 更新文章评论数
        updateArticleCommentCount(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditComment(Long id, Integer status) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setStatus(status);
        commentMapper.updateById(comment);
        
        // 更新文章评论数（审核状态改变会影响评论数）
        updateArticleCommentCount(comment.getArticleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeComment(Long commentId, Long userId, String ip) {
        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 构建查询条件
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId);
        
        // 登录用户通过userId查询，游客通过IP查询
        if (userId != null) {
            wrapper.eq(CommentLike::getUserId, userId);
        } else {
            wrapper.eq(CommentLike::getIp, ip);
        }

        // 查询是否已点赞
        CommentLike existingLike = commentLikeMapper.selectOne(wrapper);

        if (existingLike != null) {
            // 已点赞，取消点赞
            commentLikeMapper.deleteById(existingLike.getId());
            
            // 减少点赞数
            int newLikeCount = Math.max(0, comment.getLikeCount() - 1);
            comment.setLikeCount(newLikeCount);
            commentMapper.updateById(comment);
            
            return false; // 返回false表示取消点赞
        } else {
            // 未点赞，添加点赞记录
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);
            commentLike.setIp(ip);
            commentLikeMapper.insert(commentLike);
            
            // 增加点赞数
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentMapper.updateById(comment);
            
            return true; // 返回true表示点赞成功
        }
    }

    /**
     * 更新文章评论数
     */
    private void updateArticleCommentCount(Long articleId) {
        // 统计文章的评论数（只统计已通过的评论）
        Long count = commentMapper.selectCount(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
        );
        
        // 更新文章的评论数
        Article article = articleMapper.selectById(articleId);
        if (article != null) {
            article.setCommentCount(count.intValue());
            articleMapper.updateById(article);
        }
    }

    /**
     * 构建评论树
     */
    private List<CommentVO> buildCommentTree(List<CommentVO> comments) {
        List<CommentVO> rootComments = new ArrayList<>();
        
        // 分组：父评论ID -> 子评论列表
        Map<Long, List<CommentVO>> childrenMap = comments.stream()
            .filter(comment -> comment.getParentId() != null)
            .collect(Collectors.groupingBy(CommentVO::getParentId));
        
        // 找出所有根评论并设置子评论
        for (CommentVO comment : comments) {
            if (comment.getParentId() == null) {
                comment.setChildren(childrenMap.getOrDefault(comment.getId(), new ArrayList<>()));
                rootComments.add(comment);
            }
        }
        
        return rootComments;
    }

    /**
     * 转换为VO
     */
    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = BeanUtil.copyProperties(comment, CommentVO.class);
        
        // 设置回复用户昵称
        if (comment.getReplyUserId() != null) {
            User replyUser = userMapper.selectById(comment.getReplyUserId());
            if (replyUser != null) {
                vo.setReplyUserNickname(replyUser.getNickname());
            }
        }
        
        return vo;
    }
}


