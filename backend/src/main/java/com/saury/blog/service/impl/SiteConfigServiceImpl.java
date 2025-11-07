package com.saury.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saury.blog.entity.SiteConfig;
import com.saury.blog.mapper.SiteConfigMapper;
import com.saury.blog.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网站配置服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    @Override
    @Cacheable(value = "siteConfig", key = "'all'")
    public List<SiteConfig> getAllConfigs() {
        return siteConfigMapper.selectList(null);
    }

    @Override
    @Cacheable(value = "siteConfig", key = "'map'")
    public Map<String, String> getAllConfigsAsMap() {
        List<SiteConfig> configs = getAllConfigs();
        return configs.stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue));
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    @Cacheable(value = "siteConfig", key = "#configKey")
    public String getConfigValue(String configKey, String defaultValue) {
        LambdaQueryWrapper<SiteConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteConfig::getConfigKey, configKey);
        SiteConfig config = siteConfigMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : defaultValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "siteConfig", allEntries = true)
    public void saveOrUpdateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<SiteConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteConfig::getConfigKey, configKey);
        SiteConfig existConfig = siteConfigMapper.selectOne(wrapper);

        if (existConfig != null) {
            // 更新
            existConfig.setConfigValue(configValue);
            siteConfigMapper.updateById(existConfig);
        } else {
            // 新增
            SiteConfig newConfig = new SiteConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            siteConfigMapper.insert(newConfig);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "siteConfig", allEntries = true)
    public void batchSaveOrUpdate(Map<String, String> configs) {
        configs.forEach(this::saveOrUpdateConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "siteConfig", allEntries = true)
    public void deleteConfig(String configKey) {
        LambdaQueryWrapper<SiteConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteConfig::getConfigKey, configKey);
        siteConfigMapper.delete(wrapper);
    }

    @Override
    public boolean isMessageLoginRequired() {
        String value = getConfigValue("message_login_required", "false");
        return "true".equalsIgnoreCase(value);
    }

    @Override
    public boolean isCommentLoginRequired() {
        String value = getConfigValue("comment_login_required", "false");
        return "true".equalsIgnoreCase(value);
    }
}

