package com.saury.blog.controller;

import com.saury.blog.common.Result;
import com.saury.blog.entity.SiteConfig;
import com.saury.blog.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 网站配置控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    /**
     * 获取所有配置
     */
    @GetMapping("/list")
    public Result<List<SiteConfig>> getAllConfigs() {
        List<SiteConfig> configs = siteConfigService.getAllConfigs();
        return Result.success(configs);
    }

    /**
     * 获取所有配置（以Map形式返回）
     */
    @GetMapping("/map")
    public Result<Map<String, String>> getAllConfigsAsMap() {
        Map<String, String> configs = siteConfigService.getAllConfigsAsMap();
        return Result.success(configs);
    }

    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/{configKey}")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        String value = siteConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    /**
     * 保存或更新单个配置
     */
    @PutMapping("/{configKey}")
    public Result<Void> saveOrUpdateConfig(
            @PathVariable String configKey,
            @RequestParam String configValue) {
        siteConfigService.saveOrUpdateConfig(configKey, configValue);
        return Result.success();
    }

    /**
     * 批量保存或更新配置
     */
    @PutMapping("/batch")
    public Result<Void> batchSaveOrUpdate(@RequestBody Map<String, String> configs) {
        siteConfigService.batchSaveOrUpdate(configs);
        return Result.success();
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{configKey}")
    public Result<Void> deleteConfig(@PathVariable String configKey) {
        siteConfigService.deleteConfig(configKey);
        return Result.success();
    }

    /**
     * 判断留言是否需要登录
     */
    @GetMapping("/message/login-required")
    public Result<Boolean> isMessageLoginRequired() {
        boolean required = siteConfigService.isMessageLoginRequired();
        return Result.success(required);
    }

    /**
     * 判断评论是否需要登录
     */
    @GetMapping("/comment/login-required")
    public Result<Boolean> isCommentLoginRequired() {
        boolean required = siteConfigService.isCommentLoginRequired();
        return Result.success(required);
    }
}

