package com.saury.blog.service;

import java.util.List;

import com.saury.blog.vo.DashboardStatsVO.DailyStats;

/**
 * 访问量统计服务
 *
 * @author Saury
 */
public interface ViewStatsService {

    /**
     * 记录今日访问量
     */
    void recordDailyView();

    /**
     * 获取最近N天的访问量统计
     *
     * @param days 天数
     * @return 每日访问量统计列表
     */
    List<DailyStats> getDailyViewStats(int days);
}

