package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章Mapper
 *
 * @author Saury
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}

