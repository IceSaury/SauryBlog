package com.saury.blog.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天历史VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryVO {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息列表
     */
    private List<ChatMessageVO> messages;
    
    /**
     * 聊天消息VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageVO {
        /**
         * 消息角色：user/assistant
         */
        private String role;
        
        /**
         * 消息内容
         */
        private String content;
        
        /**
         * 时间戳
         */
        private Long timestamp;
    }
}

