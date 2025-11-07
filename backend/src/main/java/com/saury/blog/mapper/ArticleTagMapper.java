package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章标签关联Mapper
 *
 * @author Saury
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}


