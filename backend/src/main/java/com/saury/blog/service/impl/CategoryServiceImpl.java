package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.saury.blog.dto.CategoryDTO;
import com.saury.blog.entity.Article;
import com.saury.blog.entity.Category;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.ArticleMapper;
import com.saury.blog.mapper.CategoryMapper;
import com.saury.blog.service.CategoryService;
import com.saury.blog.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort)
        );
        
        return categories.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return convertToVO(category);
    }

    @Override
    public Long saveCategory(CategoryDTO categoryDTO) {
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        
        // 初始化默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }
        
        if (category.getId() != null) {
            categoryMapper.updateById(category);
        } else {
            categoryMapper.insert(category);
        }
        
        return category.getId();
    }

    @Override
    public void deleteCategory(Long id) {
        // 检查是否有文章使用该分类
        Long count = articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, id)
        );
        
        if (count > 0) {
            throw new BusinessException("该分类下存在文章，无法删除");
        }
        
        categoryMapper.deleteById(id);
    }

    /**
     * 转换为VO
     */
    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = BeanUtil.copyProperties(category, CategoryVO.class);
        
        // 统计文章数量
        Long articleCount = articleMapper.selectCount(
            new LambdaQueryWrapper<Article>()
                .eq(Article::getCategoryId, category.getId())
                .eq(Article::getStatus, 1) // 已发布
        );
        vo.setArticleCount(articleCount.intValue());
        
        return vo;
    }
}


