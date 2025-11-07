package com.saury.blog.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.saury.blog.service.ChatService;
import com.saury.blog.vo.ChatHistoryVO;

import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * AI聊天服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    
    private final ChatClient chatClient;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis key前缀
    private static final String CHAT_HISTORY_PREFIX = "chat:history:";
    private static final String USER_SESSIONS_PREFIX = "chat:sessions:";
    
    // 会话过期时间（7天）
    private static final long SESSION_EXPIRE_DAYS = 7;
    
    @Override
    public Flux<String> chatStream(String message, String sessionId, Long userId) {
        // 获取或创建会话ID
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString(true);
            addUserSession(userId, sessionId);
        }
        
        // 验证会话所有权
        validateSessionOwnership(userId, sessionId);
        
        // 构建会话键，用于ChatMemory隔离
        String conversationKey = userId + ":" + sessionId;
        final String finalSessionId = sessionId;
        
        log.info("开始流式对话，用户ID: {}, 会话ID: {}, 消息: {}", userId, sessionId, message);
        
        // 保存用户消息到Redis
        long timestamp = System.currentTimeMillis();
        saveChatMessage(userId, finalSessionId, "user", message, timestamp);
        
        // 累积完整回复（用于保存到Redis）
        StringBuilder fullReply = new StringBuilder();
        
        // 调用AI模型 - 流式响应
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationKey))
                .stream()
                .content()
                .doOnNext(content -> {
                    // 累积内容
                    fullReply.append(content);
                    log.debug("接收到AI流式内容: {}", content);
                })
                .doOnComplete(() -> {
                    // 流式响应完成，保存AI回复到Redis
                    saveChatMessage(userId, finalSessionId, "assistant", fullReply.toString(), timestamp);
                    log.info("流式对话完成，用户ID: {}, 会话ID: {}", userId, finalSessionId);
                })
                .doOnError(error -> {
                    log.error("AI流式响应失败，用户ID: {}, 会话ID: {}", userId, finalSessionId, error);
                });
    }
    
    @Override
    public ChatHistoryVO getChatHistory(String sessionId, Long userId) {
        // 验证会话所有权
        validateSessionOwnership(userId, sessionId);
        
        List<ChatHistoryVO.ChatMessageVO> messages = getChatHistoryFromRedis(userId, sessionId);
        
        return ChatHistoryVO.builder()
                .sessionId(sessionId)
                .messages(messages)
                .build();
    }
    
    @Override
    public void clearChatHistory(String sessionId, Long userId) {
        // 验证会话所有权
        validateSessionOwnership(userId, sessionId);
        
        String key = CHAT_HISTORY_PREFIX + userId + ":" + sessionId;
        redisTemplate.delete(key);
        
        // 从用户会话列表中移除
        String sessionsKey = USER_SESSIONS_PREFIX + userId;
        redisTemplate.opsForSet().remove(sessionsKey, sessionId);
        
        log.info("清除聊天历史，用户ID: {}, 会话ID: {}", userId, sessionId);
    }
    
    @Override
    public List<String> getUserSessions(Long userId) {
        String sessionsKey = USER_SESSIONS_PREFIX + userId;
        Set<Object> sessions = redisTemplate.opsForSet().members(sessionsKey);
        
        if (sessions == null || sessions.isEmpty()) {
            return new ArrayList<>();
        }
        
        return sessions.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
    
    /**
     * 从Redis获取聊天历史
     */
    private List<ChatHistoryVO.ChatMessageVO> getChatHistoryFromRedis(Long userId, String sessionId) {
        String key = CHAT_HISTORY_PREFIX + userId + ":" + sessionId;
        List<Object> historyData = redisTemplate.opsForList().range(key, 0, -1);
        
        if (historyData == null || historyData.isEmpty()) {
            return new ArrayList<>();
        }
        
        return historyData.stream()
                .map(obj -> JSON.parseObject(obj.toString(), ChatHistoryVO.ChatMessageVO.class))
                .collect(Collectors.toList());
    }
    
    /**
     * 保存聊天消息到Redis
     */
    private void saveChatMessage(Long userId, String sessionId, String role, String content, long timestamp) {
        String key = CHAT_HISTORY_PREFIX + userId + ":" + sessionId;
        
        ChatHistoryVO.ChatMessageVO message = ChatHistoryVO.ChatMessageVO.builder()
                .role(role)
                .content(content)
                .timestamp(timestamp)
                .build();
        
        redisTemplate.opsForList().rightPush(key, JSON.toJSONString(message));
        
        // 设置过期时间
        redisTemplate.expire(key, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
    }
    
    /**
     * 添加用户会话
     */
    private void addUserSession(Long userId, String sessionId) {
        String sessionsKey = USER_SESSIONS_PREFIX + userId;
        redisTemplate.opsForSet().add(sessionsKey, sessionId);
        redisTemplate.expire(sessionsKey, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
    }
    
    /**
     * 验证会话所有权
     */
    private void validateSessionOwnership(Long userId, String sessionId) {
        String sessionsKey = USER_SESSIONS_PREFIX + userId;
        Boolean isMember = redisTemplate.opsForSet().isMember(sessionsKey, sessionId);
        
        if (isMember == null || !isMember) {
            // 如果会话不存在，可能是新会话或已过期，添加到用户会话列表
            addUserSession(userId, sessionId);
        }
    }
}

