package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper
 *
 * @author Saury
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}

