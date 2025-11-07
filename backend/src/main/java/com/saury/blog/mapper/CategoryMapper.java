package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper
 *
 * @author Saury
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

