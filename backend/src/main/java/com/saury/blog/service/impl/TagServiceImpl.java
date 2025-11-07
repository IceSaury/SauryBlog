package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saury.blog.dto.TagDTO;
import com.saury.blog.entity.ArticleTag;
import com.saury.blog.entity.Tag;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.ArticleTagMapper;
import com.saury.blog.mapper.TagMapper;
import com.saury.blog.service.TagService;
import com.saury.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Override
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagMapper.selectList(null);
        return tags.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public TagVO getTagById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return convertToVO(tag);
    }

    @Override
    public Long saveTag(TagDTO tagDTO) {
        Tag tag = BeanUtil.copyProperties(tagDTO, Tag.class);
        
        if (tag.getId() != null) {
            tagMapper.updateById(tag);
        } else {
            tagMapper.insert(tag);
        }
        
        return tag.getId();
    }

    @Override
    public void deleteTag(Long id) {
        // 检查是否有文章使用该标签
        Long count = articleTagMapper.selectCount(
            new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, id)
        );
        
        if (count > 0) {
            throw new BusinessException("该标签下存在文章，无法删除");
        }
        
        tagMapper.deleteById(id);
    }

    @Override
    public List<TagVO> getTagsByArticleId(Long articleId) {
        // 查询文章标签关联
        List<Long> tagIds = articleTagMapper.selectList(
            new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId)
        ).stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return List.of();
        }
        
        // 查询标签信息
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private TagVO convertToVO(Tag tag) {
        TagVO vo = BeanUtil.copyProperties(tag, TagVO.class);
        
        // 统计文章数量
        Long articleCount = articleTagMapper.selectCount(
            new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tag.getId())
        );
        vo.setArticleCount(articleCount.intValue());
        
        return vo;
    }
}


