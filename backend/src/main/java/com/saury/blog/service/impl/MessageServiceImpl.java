package com.saury.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saury.blog.dto.MessageDTO;
import com.saury.blog.entity.Message;
import com.saury.blog.entity.User;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.mapper.MessageMapper;
import com.saury.blog.mapper.UserMapper;
import com.saury.blog.service.MessageService;
import com.saury.blog.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言服务实现
 *
 * @author Saury
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public Page<MessageVO> getMessagePage(Integer page, Integer size, Integer status) {
        Page<Message> messagePage = new Page<>(page, size);
        
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Message::getStatus, status)
               .orderByDesc(Message::getCreateTime)  // 按创建时间降序，最新的在最前面
               .orderByDesc(Message::getId);         // ID作为次要排序

        Page<Message> result = messageMapper.selectPage(messagePage, wrapper);
        
        // 转换为VO
        Page<MessageVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<MessageVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public Long saveMessage(MessageDTO messageDTO, Long userId, String ip) {
        Message message = BeanUtil.copyProperties(messageDTO, Message.class);
        message.setUserId(userId);
        message.setIp(ip);
        
        // 如果是登录用户，获取用户信息
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                message.setNickname(user.getNickname());
                message.setEmail(user.getEmail());
                message.setAvatar(user.getAvatar());
            }
        }
        
        // 初始化默认值
        message.setStatus(1); // 默认通过（可根据需求改为待审核）
        
        messageMapper.insert(message);
        
        return message.getId();
    }

    @Override
    public void deleteMessage(Long id) {
        messageMapper.deleteById(id);
    }

    @Override
    public void auditMessage(Long id, Integer status) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException("留言不存在");
        }
        
        message.setStatus(status);
        messageMapper.updateById(message);
    }

    /**
     * 转换为VO
     */
    private MessageVO convertToVO(Message message) {
        return BeanUtil.copyProperties(message, MessageVO.class);
    }
}


