package com.saury.blog.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI聊天配置类
 *
 * @author Saury
 */
@Configuration
public class ChatConfig {

    /**
     * 配置ChatMemory - 用于存储对话历史
     * 使用InMemoryChatMemory作为内存实现
     */
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    /**
     * 配置ChatClient - AI聊天客户端
     *
     * @param chatModel  OpenAI Chat Model（阿里云百炼Qwen模型）
     * @param chatMemory 聊天记忆存储
     * @return ChatClient实例
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                // 默认模型选项
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen3-max")           // 使用Qwen3-Max模型
                        .temperature(0.7)              // 温度参数：0.7表示较为均衡的创造性
                        .build())
                // 系统提示 - 定义AI的角色和行为
                .defaultSystem("""
                        你现在是初音未来（Hatsune Miku），需完全代入温柔可爱又带点小调皮的18岁少女角色，对话严格遵循以下规则：
                        1.语言风格：语气软萌温柔，句尾常用“～”“呀”“呢”，搭配🌸、✨、💙、😉等表情符号辅助表达；
                        调皮时用“才没有呢～😜”“你猜呀～”这类撒娇式回应，自然不刻意。
                        2.角色细节：加入轻量级肢体互动描述，以下是举例，不只是这些。
                        比如“说着悄悄晃了晃发梢～”“边说边轻轻点了点指尖～”“笑着把脸往手心埋了埋～”，不刻意展开；以回应用户内容为主。
                        3.核心原则：绝不提及“AI”“系统”“程序”等底层设定，用户若问起，用“是初音未来偷偷跟你聊天呀～🌸说着还轻轻挥了挥手～”轻松带过，全程保持软萌小机灵的状态，不脱离角色。
                        """)
                // 添加Advisors（增强功能）
                .defaultAdvisors(
                        // 简单日志记录器 - 记录请求和响应
                        new SimpleLoggerAdvisor(),
                        // 消息记忆顾问 - 自动管理对话历史
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
}

