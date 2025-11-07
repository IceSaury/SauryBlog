package com.saury.blog.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.saury.blog.service.ViewStatsService;
import com.saury.blog.vo.DashboardStatsVO.DailyStats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 访问量统计服务实现
 *
 * @author Saury
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewStatsServiceImpl implements ViewStatsService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DAILY_VIEW_PREFIX = "blog:stats:daily:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Override
    public void recordDailyView() {
        try {
            String today = LocalDate.now().format(DATE_FORMATTER);
            String key = DAILY_VIEW_PREFIX + today;
            
            // 增加今日访问量
            redisTemplate.opsForValue().increment(key, 1);
            
            // 设置过期时间为90天，避免数据无限增长
            redisTemplate.expire(key, 90, TimeUnit.DAYS);
            
            log.debug("记录每日访问量: key={}", key);
        } catch (Exception e) {
            log.error("记录每日访问量失败", e);
        }
    }

    @Override
    public List<DailyStats> getDailyViewStats(int days) {
        List<DailyStats> stats = new ArrayList<>();
        
        try {
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                String dateKey = date.format(DATE_FORMATTER);
                String displayDate = date.format(DISPLAY_FORMATTER);
                String key = DAILY_VIEW_PREFIX + dateKey;
                
                // 从Redis获取访问量
                Object value = redisTemplate.opsForValue().get(key);
                Long count = 0L;
                
                if (value != null) {
                    if (value instanceof Integer) {
                        count = ((Integer) value).longValue();
                    } else if (value instanceof Long) {
                        count = (Long) value;
                    } else if (value instanceof String) {
                        try {
                            count = Long.parseLong((String) value);
                        } catch (NumberFormatException e) {
                            log.warn("无法解析访问量数据: {}", value);
                        }
                    }
                }
                
                stats.add(DailyStats.builder()
                        .date(displayDate)
                        .count(count)
                        .build());
            }
        } catch (Exception e) {
            log.error("获取每日访问量统计失败", e);
        }
        
        return stats;
    }
}

