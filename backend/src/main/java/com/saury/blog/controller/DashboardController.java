package com.saury.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saury.blog.common.Result;
import com.saury.blog.service.DashboardService;
import com.saury.blog.vo.DashboardStatsVO;

import lombok.RequiredArgsConstructor;

/**
 * 仪表盘控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getDashboardStats() {
        return Result.success(dashboardService.getDashboardStats());
    }
}

