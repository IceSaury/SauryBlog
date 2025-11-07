package com.saury.blog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saury.blog.common.Result;
import com.saury.blog.service.ChatService;
import com.saury.blog.utils.JwtUtils;
import com.saury.blog.vo.ChatHistoryVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * AI聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    private final JwtUtils jwtUtils;
    
    /**
     * 发送消息 - 流式响应
     */
    @PostMapping(value = "/send/stream", produces = "text/html;charset=UTF-8")
    public Flux<String> sendMessageStream(
            @RequestParam("message") String message,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(httpRequest);
        return chatService.chatStream(message, sessionId, userId);
    }
    
    /**
     * 获取聊天历史
     */
    @GetMapping("/history/{sessionId}")
    public Result<ChatHistoryVO> getChatHistory(@PathVariable String sessionId,
                                                HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(httpRequest);
        ChatHistoryVO history = chatService.getChatHistory(sessionId, userId);
        return Result.success(history);
    }
    
    /**
     * 清除聊天历史
     */
    @DeleteMapping("/history/{sessionId}")
    public Result<Void> clearChatHistory(@PathVariable String sessionId,
                                        HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(httpRequest);
        chatService.clearChatHistory(sessionId, userId);
        return Result.success();
    }
    
    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/sessions")
    public Result<List<String>> getUserSessions(HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(httpRequest);
        List<String> sessions = chatService.getUserSessions(userId);
        return Result.success(sessions);
    }
}

