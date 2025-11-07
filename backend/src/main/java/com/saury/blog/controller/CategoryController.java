package com.saury.blog.controller;

import com.saury.blog.common.Result;
import com.saury.blog.dto.CategoryDTO;
import com.saury.blog.service.CategoryService;
import com.saury.blog.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result<List<CategoryVO>> getAllCategories() {
        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 查询分类详情
     */
    @GetMapping("/detail/{id}")
    public Result<CategoryVO> getCategoryDetail(@PathVariable Long id) {
        CategoryVO category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 保存分类（新增或编辑）
     */
    @PostMapping("/save")
    public Result<Long> saveCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Long categoryId = categoryService.saveCategory(categoryDTO);
        return Result.success(categoryId);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}


