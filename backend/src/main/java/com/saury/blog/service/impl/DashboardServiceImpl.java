package com.saury.blog.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saury.blog.entity.Article;
import com.saury.blog.entity.ArticleTag;
import com.saury.blog.entity.Category;
import com.saury.blog.entity.Comment;
import com.saury.blog.entity.Project;
import com.saury.blog.entity.Tag;
import com.saury.blog.mapper.ArticleMapper;
import com.saury.blog.mapper.ArticleTagMapper;
import com.saury.blog.mapper.CategoryMapper;
import com.saury.blog.mapper.CommentMapper;
import com.saury.blog.mapper.MessageMapper;
import com.saury.blog.mapper.ProjectMapper;
import com.saury.blog.mapper.TagMapper;
import com.saury.blog.mapper.UserMapper;
import com.saury.blog.service.DashboardService;
import com.saury.blog.service.ViewStatsService;
import com.saury.blog.vo.DashboardStatsVO;

import lombok.RequiredArgsConstructor;

/**
 * 仪表盘服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ArticleMapper articleMapper;
    private final ProjectMapper projectMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ViewStatsService viewStatsService;

    @Override
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 基础统计
        stats.setArticleCount(articleMapper.selectCount(null));
        stats.setProjectCount(projectMapper.selectCount(null));
        stats.setCommentCount(commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 1)));
        stats.setUserCount(userMapper.selectCount(null));
        stats.setMessageCount(messageMapper.selectCount(null));

        // 总访问量（所有文章浏览量之和）
        List<Article> articles = articleMapper.selectList(null);
        Long totalViews = articles.stream()
                .mapToLong(article -> article.getViewCount() != null ? article.getViewCount() : 0L)
                .sum();
        stats.setTotalViews(totalViews);

        // 计算趋势（本周 vs 上周）
        stats.setArticleTrend(calculateArticleTrend(7));
        stats.setProjectTrend(calculateProjectTrend(7));
        stats.setCommentTrend(calculateCommentTrend(7));
        stats.setViewTrend(calculateViewTrend(7));

        // 分类统计
        stats.setCategoryStats(getCategoryStats());

        // 标签统计
        stats.setTagStats(getTagStats());

        // 文章浏览量排行
        stats.setTopViewedArticles(getTopViewedArticles(10));

        // 最近7天访问量统计
        stats.setDailyViewStats(getDailyViewStats(7));

        // 文章状态统计
        stats.setArticleStatusStats(getArticleStatusStats());

        // 评论状态统计
        stats.setCommentStatusStats(getCommentStatusStats());

        return stats;
    }

    /**
     * 计算文章增长趋势
     */
    private Double calculateArticleTrend(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(days);
        LocalDateTime twoWeeksAgo = now.minusDays(days * 2);

        Long currentCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .ge(Article::getCreateTime, weekAgo)
        );

        Long previousCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .ge(Article::getCreateTime, twoWeeksAgo)
                        .lt(Article::getCreateTime, weekAgo)
        );

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        return ((currentCount - previousCount) * 100.0) / previousCount;
    }

    /**
     * 计算项目增长趋势
     */
    private Double calculateProjectTrend(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(days);
        LocalDateTime twoWeeksAgo = now.minusDays(days * 2);

        Long currentCount = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>()
                        .ge(Project::getCreateTime, weekAgo)
        );

        Long previousCount = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>()
                        .ge(Project::getCreateTime, twoWeeksAgo)
                        .lt(Project::getCreateTime, weekAgo)
        );

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        return ((currentCount - previousCount) * 100.0) / previousCount;
    }

    /**
     * 计算评论增长趋势
     */
    private Double calculateCommentTrend(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(days);
        LocalDateTime twoWeeksAgo = now.minusDays(days * 2);

        Long currentCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>()
                        .ge(Comment::getCreateTime, weekAgo)
                        .eq(Comment::getStatus, 1)
        );

        Long previousCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>()
                        .ge(Comment::getCreateTime, twoWeeksAgo)
                        .lt(Comment::getCreateTime, weekAgo)
                        .eq(Comment::getStatus, 1)
        );

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        return ((currentCount - previousCount) * 100.0) / previousCount;
    }

    /**
     * 计算访问量增长趋势（模拟数据，实际应该从访问日志统计）
     */
    private Double calculateViewTrend(int days) {
        // 这里返回模拟数据，实际应该从Redis或数据库日志中统计
        return 25.0;
    }

    /**
     * 获取分类统计
     */
    private List<DashboardStatsVO.CategoryStats> getCategoryStats() {
        List<Category> categories = categoryMapper.selectList(null);
        return categories.stream()
                .map(category -> {
                    Long count = articleMapper.selectCount(
                            new LambdaQueryWrapper<Article>()
                                    .eq(Article::getCategoryId, category.getId())
                                    .eq(Article::getStatus, 1)
                    );
                    return DashboardStatsVO.CategoryStats.builder()
                            .name(category.getName())
                            .count(count)
                            .build();
                })
                .filter(stats -> stats.getCount() > 0)
                .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
                .collect(Collectors.toList());
    }

    /**
     * 获取标签统计
     */
    private List<DashboardStatsVO.TagStats> getTagStats() {
        List<Tag> tags = tagMapper.selectList(null);
        return tags.stream()
                .map(tag -> {
                    Long count = articleTagMapper.selectCount(
                            new LambdaQueryWrapper<ArticleTag>()
                                    .eq(ArticleTag::getTagId, tag.getId())
                    );
                    return DashboardStatsVO.TagStats.builder()
                            .name(tag.getName())
                            .count(count)
                            .build();
                })
                .filter(stats -> stats.getCount() > 0)
                .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
                .limit(10) // 只取前10个
                .collect(Collectors.toList());
    }

    /**
     * 获取文章浏览量排行
     */
    private List<DashboardStatsVO.ArticleViewStats> getTopViewedArticles(int limit) {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getViewCount)
                        .last("LIMIT " + limit)
        );

        return articles.stream()
                .map(article -> DashboardStatsVO.ArticleViewStats.builder()
                        .title(article.getTitle())
                        .viewCount(article.getViewCount() != null ? article.getViewCount().longValue() : 0L)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取最近N天的访问量统计
     */
    private List<DashboardStatsVO.DailyStats> getDailyViewStats(int days) {
        // 从Redis获取真实的访问量统计数据
        return viewStatsService.getDailyViewStats(days);
    }

    /**
     * 获取文章状态统计
     */
    private Map<String, Long> getArticleStatusStats() {
        Map<String, Long> stats = new HashMap<>();

        // 状态: 0-草稿 1-已发布
        stats.put("草稿", articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, 0)));
        stats.put("已发布", articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)));

        return stats;
    }

    /**
     * 获取评论状态统计
     */
    private Map<String, Long> getCommentStatusStats() {
        Map<String, Long> stats = new HashMap<>();

        // 统计已发布的评论（status=1）
        stats.put("已通过", commentMapper.selectCount(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getStatus, 1)
        ));
        
        // 统计已删除的评论（status=0）
        stats.put("已删除", commentMapper.selectCount(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getStatus, 0)
        ));

        return stats;
    }
}

