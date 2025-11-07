package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.ArticleDTO;
import com.saury.blog.entity.Article;
import com.saury.blog.entity.ArticleTag;
import com.saury.blog.entity.Category;
import com.saury.blog.entity.User;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.ArticleMapper;
import com.saury.blog.mapper.ArticleTagMapper;
import com.saury.blog.mapper.CategoryMapper;
import com.saury.blog.mapper.UserMapper;
import com.saury.blog.service.ArticleService;
import com.saury.blog.service.TagService;
import com.saury.blog.service.ViewStatsService;
import com.saury.blog.vo.ArticleVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final TagService tagService;
    private final ViewStatsService viewStatsService;

    @Override
    public Page<ArticleVO> getArticlePage(Integer page, Integer size, Long categoryId, Long tagId, String keyword, Integer status) {
        Page<Article> articlePage = new Page<>(page, size);
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Article::getCategoryId, categoryId)
               .eq(status != null, Article::getStatus, status)
               .and(StringUtils.isNotBlank(keyword), w -> w
                   .like(Article::getTitle, keyword)
                   .or()
                   .like(Article::getSummary, keyword))
               .orderByDesc(Article::getIsTop)
               .orderByDesc(Article::getCreateTime);

        // 如果有标签ID，需要关联查询
        if (tagId != null) {
            List<Long> articleIds = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tagId)
            ).stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
            
            if (articleIds.isEmpty()) {
                return new Page<>(page, size, 0);
            }
            wrapper.in(Article::getId, articleIds);
        }

        Page<Article> result = articleMapper.selectPage(articlePage, wrapper);
        
        // 转换为VO
        Page<ArticleVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ArticleVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        return convertToVO(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveArticle(ArticleDTO articleDTO, Long userId) {
        Article article = BeanUtil.copyProperties(articleDTO, Article.class);
        article.setUserId(userId);
        
        // 初始化默认值
        if (article.getId() == null) {
            article.setViewCount(0);
            article.setLikeCount(0);
            article.setCommentCount(0);
            article.setCollectCount(0);
            article.setIsTop(article.getIsTop() != null ? article.getIsTop() : 0);
            article.setIsFeatured(article.getIsFeatured() != null ? article.getIsFeatured() : 0);
        }
        
        // 保存文章
        if (article.getId() != null) {
            articleMapper.updateById(article);
        } else {
            articleMapper.insert(article);
        }
        
        Long articleId = article.getId();
        
        // 处理标签关联
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            // 删除旧的标签关联
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
                .eq(ArticleTag::getArticleId, articleId));
            
            // 添加新的标签关联
            for (Long tagId : articleDTO.getTagIds()) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                articleTagMapper.insert(articleTag);
            }
        }
        
        return articleId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        // 删除文章
        articleMapper.deleteById(id);
        
        // 删除文章标签关联
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>()
            .eq(ArticleTag::getArticleId, id));
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
            
            // 记录每日访问量到Redis
            viewStatsService.recordDailyView();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlistArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 只有已发布的文章才能下架
        if (article.getStatus() != 1) {
            throw new BusinessException("只有已发布的文章才能下架");
        }
        
        // 将状态更新为已下架（2）
        article.setStatus(2);
        articleMapper.updateById(article);
    }

    /**
     * 转换为VO
     */
    private ArticleVO convertToVO(Article article) {
        ArticleVO vo = BeanUtil.copyProperties(article, ArticleVO.class);
        
        // 设置分类名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 设置作者信息
        if (article.getUserId() != null) {
            User user = userMapper.selectById(article.getUserId());
            if (user != null) {
                vo.setAuthorNickname(user.getNickname());
                vo.setAuthorAvatar(user.getAvatar());
            }
        }
        
        // 设置标签列表
        vo.setTags(tagService.getTagsByArticleId(article.getId()));
        
        return vo;
    }
}


