package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper
 *
 * @author Saury
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}


