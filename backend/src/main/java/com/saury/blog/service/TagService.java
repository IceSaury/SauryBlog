package com.saury.blog.service;

import com.saury.blog.dto.TagDTO;
import com.saury.blog.vo.TagVO;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author Saury
 */
public interface TagService {

    /**
     * 查询所有标签
     *
     * @return 标签列表
     */
    List<TagVO> getAllTags();

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签信息
     */
    TagVO getTagById(Long id);

    /**
     * 保存标签（新增或编辑）
     *
     * @param tagDTO 标签DTO
     * @return 标签ID
     */
    Long saveTag(TagDTO tagDTO);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void deleteTag(Long id);

    /**
     * 根据文章ID查询标签列表
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<TagVO> getTagsByArticleId(Long articleId);
}


