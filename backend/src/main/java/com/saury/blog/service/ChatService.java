package com.saury.blog.service;

import com.saury.blog.vo.ChatHistoryVO;

import reactor.core.publisher.Flux;

/**
 * AI聊天服务接口
 */
public interface ChatService {
    
    /**
     * 发送消息并获取AI回复 - 流式响应
     * 
     * @param message 用户消息
     * @param sessionId 会话ID（可选）
     * @param userId 用户ID
     * @return 文本流
     */
    Flux<String> chatStream(String message, String sessionId, Long userId);
    
    /**
     * 获取用户的聊天历史
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 聊天历史
     */
    ChatHistoryVO getChatHistory(String sessionId, Long userId);
    
    /**
     * 清除用户的聊天历史
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void clearChatHistory(String sessionId, Long userId);
    
    /**
     * 获取用户的所有会话列表
     * 
     * @param userId 用户ID
     * @return 会话ID列表
     */
    java.util.List<String> getUserSessions(Long userId);
}

