-- 创建数据库
CREATE DATABASE IF NOT EXISTS `saury_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `saury_blog`;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `github` varchar(255) DEFAULT NULL COMMENT 'GitHub地址',
  `csdn` varchar(255) DEFAULT NULL COMMENT 'CSDN地址',
  `intro` text COMMENT '个人简介',
  `role` tinyint DEFAULT 1 COMMENT '角色：0-管理员 1-普通用户',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 文章分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `sort` int DEFAULT 0 COMMENT '排序',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- 文章标签表
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- 文章表
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
  `cover` varchar(255) DEFAULT NULL COMMENT '文章封面',
  `content` longtext COMMENT '文章内容（Markdown）',
  `view_count` int DEFAULT 0 COMMENT '浏览量',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `comment_count` int DEFAULT 0 COMMENT '评论数',
  `collect_count` int DEFAULT 0 COMMENT '收藏数',
  `is_top` tinyint DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
  `is_featured` tinyint DEFAULT 0 COMMENT '是否推荐：0-否 1-是',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已下架',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章标签关联表
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 项目表
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `description` text COMMENT '项目描述',
  `cover` varchar(255) DEFAULT NULL COMMENT '项目封面',
  `images` text COMMENT '项目图片（JSON数组）',
  `tech_stack` varchar(500) DEFAULT NULL COMMENT '技术栈（JSON数组）',
  `github_url` varchar(255) DEFAULT NULL COMMENT 'GitHub地址',
  `demo_url` varchar(255) DEFAULT NULL COMMENT '演示地址',
  `highlights` text COMMENT '项目亮点（JSON数组）',
  `type` tinyint DEFAULT 0 COMMENT '项目类型：0-企业项目 1-个人项目 2-开源项目',
  `sort` int DEFAULT 0 COMMENT '排序',
  `view_count` int DEFAULT 0 COMMENT '浏览量',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-隐藏 1-显示',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 评论表
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `reply_user_id` bigint DEFAULT NULL COMMENT '回复用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称（游客）',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱（游客）',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像（游客）',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `address` varchar(100) DEFAULT NULL COMMENT '归属地',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-已删除 1-已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 评论点赞表
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（登录用户）',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址（游客）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
  UNIQUE KEY `uk_comment_ip` (`comment_id`, `ip`),
  KEY `idx_comment_id` (`comment_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- 留言表
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `content` text NOT NULL COMMENT '留言内容',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称（游客）',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱（游客）',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像（游客）',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `address` varchar(100) DEFAULT NULL COMMENT '归属地',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 访问日志表
DROP TABLE IF EXISTS `visit_log`;
CREATE TABLE `visit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `url` varchar(255) DEFAULT NULL COMMENT '访问URL',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `address` varchar(100) DEFAULT NULL COMMENT '归属地',
  `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) DEFAULT NULL COMMENT '操作系统',
  `device` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问日志表';

-- 网站配置表
DROP TABLE IF EXISTS `site_config`;
CREATE TABLE `site_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站配置表';

-- 插入默认管理员账号（密码：admin123，使用BCrypt加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `intro`, `role`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Saury', 'admin@saury.com', 'Java开发工程师，热爱技术，热爱分享。', 0, 1);

-- 插入默认分类
INSERT INTO `category` (`name`, `description`, `icon`, `sort`) VALUES
('Java', 'Java技术相关文章', '☕', 1),
('Spring', 'Spring全家桶', '🍃', 2),
('数据库', '数据库相关技术', '💾', 3),
('前端', '前端技术分享', '🎨', 4),
('微服务', '微服务架构', '🔧', 5),
('其他', '其他技术文章', '📝', 6);

-- 插入默认标签
INSERT INTO `tag` (`name`, `color`) VALUES
('Java', '#00F0FF'),
('Spring Boot', '#FF006E'),
('Vue', '#A200FF'),
('MySQL', '#FFED00'),
('Redis', '#00F0FF'),
('Docker', '#FF006E');

-- 插入网站配置
INSERT INTO `site_config` (`config_key`, `config_value`, `description`) VALUES
('site_name', 'Saury Blog', '网站名称'),
('site_desc', '一个赛博朋克风格的技术博客', '网站描述'),
('site_keywords', 'Java,Spring Boot,Vue,技术博客', '网站关键词'),
('site_author', 'Saury', '网站作者'),
('site_record', '', '网站备案号'),
('site_footer', '© 2025 Saury Blog. All Rights Reserved.', '网站页脚'),
('message_login_required', 'false', '留言是否需要登录（true-需要登录，false-允许匿名）'),
('comment_login_required', 'false', '评论是否需要登录（true-需要登录，false-允许匿名）');

