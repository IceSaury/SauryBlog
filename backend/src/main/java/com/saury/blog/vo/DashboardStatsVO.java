package com.saury.blog.vo;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仪表盘统计数据VO
 *
 * @author Saury
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {

    /**
     * 文章总数
     */
    private Long articleCount;

    /**
     * 项目总数
     */
    private Long projectCount;

    /**
     * 总访问量
     */
    private Long totalViews;

    /**
     * 评论总数
     */
    private Long commentCount;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 留言总数
     */
    private Long messageCount;

    /**
     * 文章趋势（本周增长百分比）
     */
    private Double articleTrend;

    /**
     * 项目趋势（本周增长百分比）
     */
    private Double projectTrend;

    /**
     * 访问量趋势（本周增长百分比）
     */
    private Double viewTrend;

    /**
     * 评论趋势（本周增长百分比）
     */
    private Double commentTrend;

    /**
     * 分类统计（分类名 -> 文章数量）
     */
    private List<CategoryStats> categoryStats;

    /**
     * 标签统计（标签名 -> 文章数量）
     */
    private List<TagStats> tagStats;

    /**
     * 文章浏览量排行（Top 10）
     */
    private List<ArticleViewStats> topViewedArticles;

    /**
     * 最近7天访问量统计
     */
    private List<DailyStats> dailyViewStats;

    /**
     * 文章状态统计
     */
    private Map<String, Long> articleStatusStats;

    /**
     * 评论状态统计
     */
    private Map<String, Long> commentStatusStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStats {
        private String name;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagStats {
        private String name;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleViewStats {
        private String title;
        private Long viewCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private String date;
        private Long count;
    }
}

