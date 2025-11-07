package com.saury.blog.controller;

import com.saury.blog.common.Result;
import com.saury.blog.dto.PasswordDTO;
import com.saury.blog.dto.UserDTO;
import com.saury.blog.service.UserService;
import com.saury.blog.utils.JwtUtils;
import com.saury.blog.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author Saury
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> updateUserInfo(
            @Valid @RequestBody UserDTO userDTO,
            HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        userService.updateUserInfo(userId, userDTO);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @Valid @RequestBody PasswordDTO passwordDTO,
            HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        userService.updatePassword(userId, passwordDTO);
        return Result.success();
    }
}

