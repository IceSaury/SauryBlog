package com.saury.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Saury Blog 启动类
 * 赛博朋克风格的个人博客系统
 *
 * @author Saury
 * @date 2024
 */
@SpringBootApplication
@MapperScan("com.saury.blog.mapper")
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
        System.out.println("\n" +
                "  ____                            ____  _             \n" +
                " / ___|  __ _ _   _ _ __ _   _  | __ )| | ___   __ _ \n" +
                " \\___ \\ / _` | | | | '__| | | | |  _ \\| |/ _ \\ / _` |\n" +
                "  ___) | (_| | |_| | |  | |_| | | |_) | | (_) | (_| |\n" +
                " |____/ \\__,_|\\__,_|_|   \\__, | |____/|_|\\___/ \\__, |\n" +
                "                         |___/                 |___/ \n" +
                "\n🚀 Saury Blog 启动成功！\n" +
                "📍 接口文档：http://localhost:8088/api\n" +
                "🎨 赛博朋克风格，科技感满满！\n");
    }
}

