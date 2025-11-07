package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saury.blog.common.ResultCode;
import com.saury.blog.config.JwtProperties;
import com.saury.blog.dto.LoginDTO;
import com.saury.blog.dto.PasswordDTO;
import com.saury.blog.dto.RegisterDTO;
import com.saury.blog.dto.UserDTO;
import com.saury.blog.entity.User;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.UserMapper;
import com.saury.blog.service.UserService;
import com.saury.blog.utils.JwtUtils;
import com.saury.blog.vo.LoginVO;
import com.saury.blog.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现
 *
 * @author Saury
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Long register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 检查用户名是否已存在
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setRole(1); // 默认为普通用户
        user.setStatus(1); // 默认为正常状态
        
        // 设置默认头像
        user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");

        // 保存用户
        save(user);

        return user.getId();
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成token
        String token = JwtUtils.generateToken(
                user.getId().toString(),
                jwtProperties.getExpiration(),
                jwtProperties.getSecret()
        );

        // 返回登录信息
        return new LoginVO(
                jwtProperties.getPrefix() + " " + token,
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getRole()
        );
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    public void updateUserInfo(Long userId, UserDTO userDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        BeanUtil.copyProperties(userDTO, user, "id", "username", "password", "role", "status");
        updateById(user);
    }

    @Override
    public void updatePassword(Long userId, PasswordDTO passwordDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!BCrypt.checkpw(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(BCrypt.hashpw(passwordDTO.getNewPassword()));
        updateById(user);
    }
}

