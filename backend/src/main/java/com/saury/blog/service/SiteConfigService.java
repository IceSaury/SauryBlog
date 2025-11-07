package com.saury.blog.service;

import com.saury.blog.entity.SiteConfig;

import java.util.List;
import java.util.Map;

/**
 * 网站配置服务接口
 *
 * @author Saury
 */
public interface SiteConfigService {

    /**
     * 获取所有配置
     *
     * @return 配置列表
     */
    List<SiteConfig> getAllConfigs();

    /**
     * 获取所有配置（以Map形式返回）
     *
     * @return 配置Map（key-value）
     */
    Map<String, String> getAllConfigsAsMap();

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 保存或更新配置
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    void saveOrUpdateConfig(String configKey, String configValue);

    /**
     * 批量保存或更新配置
     *
     * @param configs 配置Map
     */
    void batchSaveOrUpdate(Map<String, String> configs);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     */
    void deleteConfig(String configKey);

    /**
     * 判断留言是否需要登录
     *
     * @return true-需要登录，false-允许匿名
     */
    boolean isMessageLoginRequired();

    /**
     * 判断评论是否需要登录
     *
     * @return true-需要登录，false-允许匿名
     */
    boolean isCommentLoginRequired();
}

