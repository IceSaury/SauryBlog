package com.saury.blog.controller;

import com.saury.blog.common.Result;
import com.saury.blog.dto.TagDTO;
import com.saury.blog.service.TagService;
import com.saury.blog.vo.TagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 查询所有标签
     */
    @GetMapping("/list")
    public Result<List<TagVO>> getAllTags() {
        List<TagVO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 查询标签详情
     */
    @GetMapping("/detail/{id}")
    public Result<TagVO> getTagDetail(@PathVariable Long id) {
        TagVO tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 保存标签（新增或编辑）
     */
    @PostMapping("/save")
    public Result<Long> saveTag(@Valid @RequestBody TagDTO tagDTO) {
        Long tagId = tagService.saveTag(tagDTO);
        return Result.success(tagId);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}


