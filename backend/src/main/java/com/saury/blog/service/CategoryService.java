package com.saury.blog.service;

import com.saury.blog.dto.CategoryDTO;
import com.saury.blog.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author Saury
 */
public interface CategoryService {

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    List<CategoryVO> getAllCategories();

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 保存分类（新增或编辑）
     *
     * @param categoryDTO 分类DTO
     * @return 分类ID
     */
    Long saveCategory(CategoryDTO categoryDTO);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}


