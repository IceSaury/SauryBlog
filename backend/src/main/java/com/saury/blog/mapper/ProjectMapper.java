package com.saury.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saury.blog.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目Mapper
 *
 * @author Saury
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}

