# 🚀 部署指南

本文档详细介绍如何将 Saury Blog 部署到生产环境。

---

## 📋 目录

- [1. 服务器要求](#1-服务器要求)
- [2. 环境准备](#2-环境准备)
- [3. 数据库部署](#3-数据库部署)
- [4. Redis 部署](#4-redis-部署)
- [5. 后端部署](#5-后端部署)
- [6. 前端部署](#6-前端部署)
- [7. Nginx 配置](#7-nginx-配置)
- [8. HTTPS 配置](#8-https-配置)
- [9. Docker 部署](#9-docker-部署)
- [10. 常见问题](#10-常见问题)

---

## 1. 服务器要求

### 推荐配置

- **CPU**: 2核及以上
- **内存**: 4GB 及以上
- **硬盘**: 40GB 及以上
- **带宽**: 3Mbps 及以上
- **操作系统**: CentOS 7+ / Ubuntu 18.04+ / Debian 9+

### 最低配置

- **CPU**: 1核
- **内存**: 2GB
- **硬盘**: 20GB
- **带宽**: 1Mbps

---

## 2. 环境准备

### 2.1 更新系统

```bash
# CentOS
sudo yum update -y

# Ubuntu/Debian
sudo apt update && sudo apt upgrade -y
```

### 2.2 安装 Java 21

```bash
# 下载 OpenJDK 21
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz

# 解压
sudo mkdir -p /usr/local/java
sudo tar -zxvf jdk-21_linux-x64_bin.tar.gz -C /usr/local/java/

# 配置环境变量
sudo vim /etc/profile

# 添加以下内容
export JAVA_HOME=/usr/local/java/jdk-21
export PATH=$JAVA_HOME/bin:$PATH

# 使配置生效
source /etc/profile

# 验证
java -version
```

### 2.3 安装 Node.js

```bash
# 使用 nvm 安装
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18

# 验证
node -v
npm -v
```

### 2.4 安装 Nginx

```bash
# CentOS
sudo yum install nginx -y

# Ubuntu/Debian
sudo apt install nginx -y

# 启动 Nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# 验证
nginx -v
```

---

## 3. 数据库部署

### 3.1 安装 MySQL 8.0

```bash
# CentOS
wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
sudo rpm -ivh mysql80-community-release-el7-3.noarch.rpm
sudo yum install mysql-server -y

# Ubuntu/Debian
sudo apt install mysql-server -y

# 启动 MySQL
sudo systemctl start mysqld
sudo systemctl enable mysqld
```

### 3.2 初始化数据库

```bash
# 获取临时密码（CentOS）
sudo grep 'temporary password' /var/log/mysqld.log

# 登录 MySQL
mysql -u root -p

# 修改密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';

# 创建数据库
CREATE DATABASE saury_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户
CREATE USER 'saury'@'localhost' IDENTIFIED BY 'YourPassword123!';
GRANT ALL PRIVILEGES ON saury_blog.* TO 'saury'@'localhost';
FLUSH PRIVILEGES;

# 退出
exit;
```

### 3.3 导入数据库脚本

```bash
# 上传 schema.sql 到服务器
scp backend/src/main/resources/db/schema.sql user@server:/tmp/

# 导入数据库
mysql -u saury -p saury_blog < /tmp/schema.sql
```

---

## 4. Redis 部署

### 4.1 安装 Redis

```bash
# CentOS
sudo yum install redis -y

# Ubuntu/Debian
sudo apt install redis-server -y

# 启动 Redis
sudo systemctl start redis
sudo systemctl enable redis

# 验证
redis-cli ping
```

### 4.2 配置 Redis

```bash
# 编辑配置文件
sudo vim /etc/redis/redis.conf

# 修改以下配置
bind 127.0.0.1
requirepass YourRedisPassword
maxmemory 512mb
maxmemory-policy allkeys-lru

# 重启 Redis
sudo systemctl restart redis
```

---

## 5. 后端部署

### 5.1 上传后端代码

```bash
# 在本地打包
cd backend
mvn clean package -DskipTests

# 上传到服务器
scp target/saury-blog-1.0.0.jar user@server:/home/user/app/
scp src/main/resources/application.yml user@server:/home/user/app/
scp src/main/resources/application-prod.yml user@server:/home/user/app/
```

### 5.2 配置生产环境

创建 `application-prod.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/saury_blog?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: saury
    password: YourPassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  data:
    redis:
      host: localhost
      port: 6379
      password: YourRedisPassword
      database: 0
      timeout: 3000
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0

jwt:
  secret: your-production-jwt-secret-key-min-256-bits
  expire: 604800

# 阿里云 OSS 配置
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name

# OpenAI 配置
spring:
  ai:
    openai:
      api-key: your-openai-api-key
      base-url: https://api.openai.com
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7

# 日志配置
logging:
  level:
    root: INFO
    com.saury.blog: INFO
  file:
    name: /home/user/app/logs/saury-blog.log
    max-size: 100MB
    max-history: 30
```

### 5.3 创建启动脚本

创建 `start.sh`：

```bash
#!/bin/bash

APP_NAME=saury-blog-1.0.0.jar
APP_HOME=/home/user/app

# 停止旧进程
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "Stopping application (PID: $PID)..."
    kill -9 $PID
    sleep 2
fi

# 启动应用
echo "Starting application..."
cd $APP_HOME
nohup java -jar \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    -Dspring.profiles.active=prod \
    $APP_NAME > /dev/null 2>&1 &

echo "Application started successfully!"
```

赋予执行权限：

```bash
chmod +x start.sh
```

### 5.4 使用 systemd 管理服务

创建服务文件 `/etc/systemd/system/saury-blog.service`：

```ini
[Unit]
Description=Saury Blog Application
After=network.target mysql.service redis.service

[Service]
Type=forking
User=user
WorkingDirectory=/home/user/app
ExecStart=/bin/bash /home/user/app/start.sh
ExecStop=/bin/kill -9 $MAINPID
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start saury-blog
sudo systemctl enable saury-blog

# 查看状态
sudo systemctl status saury-blog

# 查看日志
tail -f /home/user/app/logs/saury-blog.log
```

---

## 6. 前端部署

### 6.1 本地构建

```bash
cd frontend

# 安装依赖
npm install

# 修改 API 地址
# 编辑 src/utils/request.ts
# 将 baseURL 改为生产环境地址
# baseURL: 'https://your-domain.com/api'

# 构建生产版本
npm run build
```

### 6.2 上传到服务器

```bash
# 压缩 dist 目录
tar -czvf dist.tar.gz dist/

# 上传到服务器
scp dist.tar.gz user@server:/home/user/

# 在服务器上解压
cd /home/user/
tar -xzvf dist.tar.gz
sudo mv dist /usr/share/nginx/html/saury-blog
```

---

## 7. Nginx 配置

### 7.1 配置反向代理

创建配置文件 `/etc/nginx/conf.d/saury-blog.conf`：

```nginx
# 后端 API 服务器
upstream backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /usr/share/nginx/html/saury-blog;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端 API 代理
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # 上传文件大小限制
        client_max_body_size 10M;
    }
    
    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        root /usr/share/nginx/html/saury-blog;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
    
    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/json application/javascript application/xml+rss application/rss+xml font/truetype font/opentype application/vnd.ms-fontobject image/svg+xml;
}
```

### 7.2 测试并重启 Nginx

```bash
# 测试配置
sudo nginx -t

# 重启 Nginx
sudo systemctl restart nginx
```

---

## 8. HTTPS 配置

### 8.1 使用 Let's Encrypt 免费证书

```bash
# 安装 Certbot
# CentOS
sudo yum install certbot python3-certbot-nginx -y

# Ubuntu/Debian
sudo apt install certbot python3-certbot-nginx -y

# 申请证书
sudo certbot --nginx -d your-domain.com
```

### 8.2 更新 Nginx 配置

Certbot 会自动更新配置，或者手动修改：

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # 其他配置同上...
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

### 8.3 自动续期

```bash
# 测试续期
sudo certbot renew --dry-run

# Certbot 会自动添加 cron 任务
```

---

## 9. Docker 部署

### 9.1 后端 Dockerfile

创建 `backend/Dockerfile`：

```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/saury-blog-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "-XX:+UseG1GC", "app.jar"]
```

### 9.2 前端 Dockerfile

创建 `frontend/Dockerfile`：

```dockerfile
FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 9.3 Docker Compose

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: saury-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: saury_blog
      MYSQL_USER: saury
      MYSQL_PASSWORD: saury123
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./backend/src/main/resources/db/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    networks:
      - saury-network

  redis:
    image: redis:7-alpine
    container_name: saury-redis
    command: redis-server --requirepass redis123
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - saury-network

  backend:
    build: ./backend
    container_name: saury-backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/saury_blog
      SPRING_DATASOURCE_USERNAME: saury
      SPRING_DATASOURCE_PASSWORD: saury123
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PASSWORD: redis123
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    networks:
      - saury-network

  frontend:
    build: ./frontend
    container_name: saury-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - saury-network

volumes:
  mysql-data:
  redis-data:

networks:
  saury-network:
    driver: bridge
```

### 9.4 启动服务

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 10. 常见问题

### 10.1 后端启动失败

**问题**: 连接数据库失败

**解决方案**:
1. 检查数据库是否启动
2. 检查数据库用户名和密码
3. 检查防火墙是否开放 3306 端口
4. 检查 MySQL 是否允许远程连接

### 10.2 前端访问 404

**问题**: 刷新页面出现 404

**解决方案**:
在 Nginx 配置中添加 `try_files $uri $uri/ /index.html;`

### 10.3 跨域问题

**问题**: API 请求被 CORS 阻止

**解决方案**:
1. 确认后端已配置 CORS（CorsConfig.java）
2. 使用 Nginx 反向代理，避免跨域

### 10.4 文件上传失败

**问题**: 上传文件超过大小限制

**解决方案**:
在 Nginx 配置中添加：
```nginx
client_max_body_size 10M;
```

在 Spring Boot 配置中添加：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

### 10.5 性能优化

**优化建议**:
1. 启用 Redis 缓存
2. 开启 Gzip 压缩
3. 配置静态资源缓存
4. 使用 CDN 加速
5. 数据库索引优化
6. 图片懒加载
7. 代码分割（前端）

---

## 📞 技术支持

如有部署问题，请联系：

- 📧 Email: your.email@example.com
- 🐙 GitHub Issues: https://github.com/yourusername/SauryBlog/issues

---

<div align="center">

**祝您部署顺利！🎉**

</div>

