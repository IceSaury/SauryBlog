package com.saury.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.saury.blog.dto.LoginDTO;
import com.saury.blog.dto.PasswordDTO;
import com.saury.blog.dto.RegisterDTO;
import com.saury.blog.dto.UserDTO;
import com.saury.blog.entity.User;
import com.saury.blog.vo.LoginVO;
import com.saury.blog.vo.UserVO;

/**
 * 用户Service
 *
 * @author Saury
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户ID
     */
    Long register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId  用户ID
     * @param userDTO 用户信息
     */
    void updateUserInfo(Long userId, UserDTO userDTO);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param passwordDTO 密码信息
     */
    void updatePassword(Long userId, PasswordDTO passwordDTO);
}

