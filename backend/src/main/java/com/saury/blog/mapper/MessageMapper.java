package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言Mapper
 *
 * @author Saury
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}


