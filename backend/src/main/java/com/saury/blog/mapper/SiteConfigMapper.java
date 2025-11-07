package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.SiteConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网站配置Mapper
 *
 * @author Saury
 */
@Mapper
public interface SiteConfigMapper extends BaseMapper<SiteConfig> {
}

