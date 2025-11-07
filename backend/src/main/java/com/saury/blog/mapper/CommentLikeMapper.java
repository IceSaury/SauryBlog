package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论点赞Mapper
 *
 * @author Saury
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
}

