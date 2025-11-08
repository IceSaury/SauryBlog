# 🐳 SauryBlog Docker 部署文档

## 📋 目录

- [系统要求](#系统要求)
- [快速开始](#快速开始)
- [详细部署步骤](#详细部署步骤)
- [配置说明](#配置说明)
- [常用命令](#常用命令)
- [故障排查](#故障排查)
- [生产环境优化](#生产环境优化)
- [更新升级](#更新升级)

---

## 🖥️ 系统要求

### 硬件要求（最低配置）

- **CPU**: 2核心
- **内存**: 4GB RAM（推荐 8GB）
- **磁盘**: 20GB 可用空间

### 软件要求

- **操作系统**: Linux (Ubuntu 20.04+, CentOS 7+, Debian 10+)
- **Docker**: 20.10+
- **Docker Compose**: 2.0+ 或 docker-compose 1.29+

---

## 🚀 快速开始

### 一键部署（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/IceSaury/SauryBlog.git
cd SauryBlog

# 2. 创建环境配置文件
cat > .env << 'EOF'
# 端口配置
MYSQL_PORT=3306
REDIS_PORT=6379
BACKEND_PORT=8088
FRONTEND_PORT=80

# MySQL 配置
MYSQL_ROOT_PASSWORD=YourStrongPassword123!
MYSQL_DATABASE=saury_blog

# Redis 配置
REDIS_PASSWORD=YourRedisPassword123!

# JWT 配置
JWT_SECRET=SauryBlogCyberpunkStylePersonalWebsiteSecretKey2024ChangeThis

# OpenAI 配置（可选）
OPENAI_API_KEY=your_openai_api_key_here

# 阿里云 OSS 配置（可选）
ALIYUN_OSS_ACCESS_KEY_ID=your_access_key_id
ALIYUN_OSS_ACCESS_KEY_SECRET=your_access_key_secret
ALIYUN_OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
ALIYUN_OSS_BUCKET_NAME=your_bucket_name
ALIYUN_OSS_PREFIX=saury-blog/
EOF

# 3. 修改 .env 文件中的密码等敏感信息
vim .env  # 或使用 nano .env

# 4. 添加执行权限并运行部署脚本
chmod +x deploy.sh stop.sh
./deploy.sh
```

部署完成后，访问 `http://your-server-ip` 即可看到博客首页。

---

## 📝 详细部署步骤

### 步骤 1: 安装 Docker 和 Docker Compose

#### Ubuntu/Debian

```bash
# 更新包索引
sudo apt update

# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到 docker 组（可选）
sudo usermod -aG docker $USER
```

#### CentOS/RHEL

```bash
# 安装 Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker
```

#### 安装 Docker Compose

```bash
# Docker Compose V2（推荐，已内置在 Docker 中）
docker compose version

# 如果上面的命令失败，安装 V1 版本
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 步骤 2: 克隆项目

```bash
# 克隆项目到服务器
git clone https://github.com/IceSaury/SauryBlog.git
cd SauryBlog
```

### 步骤 3: 配置环境变量

创建 `.env` 文件：

```bash
vim .env
```

填入以下内容（根据实际情况修改）：

```env
# ============ 端口配置 ============
MYSQL_PORT=3306
REDIS_PORT=6379
BACKEND_PORT=8088
FRONTEND_PORT=80

# ============ MySQL 配置 ============
MYSQL_ROOT_PASSWORD=YourStrongPassword123!
MYSQL_DATABASE=saury_blog

# ============ Redis 配置 ============
REDIS_PASSWORD=YourRedisPassword123!

# ============ JWT 配置 ============
JWT_SECRET=SauryBlogCyberpunkStylePersonalWebsiteSecretKey2024ChangeThis

# ============ OpenAI 配置（可选）============
OPENAI_API_KEY=your_openai_api_key_here

# ============ 阿里云 OSS 配置（可选）============
ALIYUN_OSS_ACCESS_KEY_ID=your_access_key_id
ALIYUN_OSS_ACCESS_KEY_SECRET=your_access_key_secret
ALIYUN_OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
ALIYUN_OSS_BUCKET_NAME=your_bucket_name
ALIYUN_OSS_PREFIX=saury-blog/
```

**⚠️ 重要提示：**
- 必须修改 `MYSQL_ROOT_PASSWORD` 为强密码
- 必须修改 `REDIS_PASSWORD` 为强密码
- 必须修改 `JWT_SECRET` 为复杂的随机字符串
- 如果不使用 AI 聊天功能，`OPENAI_API_KEY` 可以保持默认
- 如果不使用图片上传功能，OSS 配置可以保持默认

### 步骤 4: 运行部署脚本

```bash
# 添加执行权限
chmod +x deploy.sh stop.sh

# 运行部署
./deploy.sh
```

部署过程大约需要 3-5 分钟，脚本会自动完成以下操作：
1. 检查 Docker 环境
2. 创建必要的目录
3. 构建 Docker 镜像
4. 启动所有服务
5. 等待服务就绪
6. 显示访问地址

### 步骤 5: 验证部署

部署完成后，访问以下地址验证：

- **前端页面**: `http://your-server-ip`
- **后端 API**: `http://your-server-ip:8088/api`

**默认登录账号：**
- 用户名: `admin`
- 密码: `123456`

⚠️ **请立即修改默认密码！**

---

## ⚙️ 配置说明

### 端口说明

| 服务 | 容器端口 | 宿主机端口 | 说明 |
|------|---------|-----------|------|
| Frontend | 80 | 80 | 前端服务，可修改 |
| Backend | 8088 | 8088 | 后端 API，可修改 |
| MySQL | 3306 | 3306 | MySQL 数据库 |
| Redis | 6379 | 6379 | Redis 缓存 |

如果端口被占用，可在 `.env` 文件中修改宿主机映射端口。

### 数据持久化

所有重要数据都通过 Docker Volume 持久化存储：

- `mysql_data`: MySQL 数据库文件
- `redis_data`: Redis 持久化文件
- `backend_logs`: 后端日志文件

即使删除容器，数据也不会丢失。

### 可选功能配置

#### AI 聊天功能

如需启用 AI 聊天功能，需要配置 OpenAI API Key：

```env
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxx
```

支持的 API 提供商：
- OpenAI 官方
- 阿里云通义千问（默认）
- 其他兼容 OpenAI 接口的服务

#### 图片上传功能

如需启用图片上传到云存储，需要配置阿里云 OSS：

```env
ALIYUN_OSS_ACCESS_KEY_ID=your_access_key_id
ALIYUN_OSS_ACCESS_KEY_SECRET=your_access_key_secret
ALIYUN_OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
ALIYUN_OSS_BUCKET_NAME=your_bucket_name
```

如果不配置，图片上传功能将无法使用，但不影响其他功能。

---

## 🎮 常用命令

### 服务管理

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启所有服务
docker-compose restart

# 重启单个服务
docker-compose restart backend

# 查看服务状态
docker-compose ps

# 停止并删除所有数据（危险操作）
docker-compose down -v
```

### 日志查看

```bash
# 查看所有服务日志
docker-compose logs

# 实时查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend

# 查看最近 100 行日志
docker-compose logs --tail=100 backend
```

### 进入容器

```bash
# 进入后端容器
docker exec -it saury-backend sh

# 进入 MySQL 容器
docker exec -it saury-mysql mysql -uroot -p

# 进入 Redis 容器
docker exec -it saury-redis redis-cli -a your_redis_password
```

### 数据库操作

```bash
# 导出数据库
docker exec saury-mysql mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog > backup.sql

# 导入数据库
docker exec -i saury-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog < backup.sql

# 直接进入 MySQL
docker exec -it saury-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog
```

### 镜像管理

```bash
# 重新构建镜像
docker-compose build --no-cache

# 查看镜像大小
docker images | grep saury

# 清理无用镜像
docker image prune -a
```

---

## 🔧 故障排查

### 常见问题

#### 1. 端口被占用

**错误信息：**
```
Error: bind: address already in use
```

**解决方案：**
```bash
# 查看端口占用
sudo netstat -tulnp | grep :80
sudo netstat -tulnp | grep :8088

# 修改 .env 文件中的端口
vim .env
# 修改后重新部署
docker-compose down && docker-compose up -d
```

#### 2. 后端启动失败

**检查步骤：**
```bash
# 查看后端日志
docker-compose logs backend

# 常见原因：
# - 数据库连接失败：检查 MySQL 是否正常运行
# - 环境变量未配置：检查 .env 文件
# - 内存不足：检查服务器内存
```

#### 3. 数据库连接失败

```bash
# 检查 MySQL 是否运行
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 测试数据库连接
docker exec -it saury-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD}
```

#### 4. 前端无法访问后端

**检查步骤：**
```bash
# 1. 检查后端是否运行
curl http://localhost:8088/api/actuator/health

# 2. 检查 nginx 配置
docker exec -it saury-frontend cat /etc/nginx/conf.d/default.conf

# 3. 查看前端日志
docker-compose logs frontend
```

#### 5. 内存不足

```bash
# 查看容器资源占用
docker stats

# 如果内存不足，可以调整 JVM 参数
# 编辑 docker-compose.yml，修改 JAVA_OPTS
JAVA_OPTS: -Xms256m -Xmx512m -XX:+UseG1GC
```

### 性能监控

```bash
# 查看容器资源使用情况
docker stats

# 查看磁盘使用
docker system df

# 查看网络连接
docker network inspect saury-network
```

---

## 🚀 生产环境优化

### 1. 安全加固

#### 修改默认密码

```bash
# 立即修改管理员密码
# 登录后台 -> 个人资料 -> 修改密码
```

#### 配置防火墙

```bash
# 使用 ufw（Ubuntu）
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# 使用 firewalld（CentOS）
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

#### 限制数据库访问

编辑 `docker-compose.yml`，注释掉 MySQL 端口映射：

```yaml
mysql:
  # ports:
  #   - "${MYSQL_PORT}:3306"  # 注释掉这一行
```

### 2. 配置 HTTPS

#### 使用 Nginx 反向代理

```bash
# 安装 certbot
sudo apt install certbot python3-certbot-nginx

# 获取 SSL 证书
sudo certbot --nginx -d yourdomain.com

# 配置 Nginx
sudo vim /etc/nginx/sites-available/sauryblog
```

示例 Nginx 配置：

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 3. 性能优化

#### 调整 MySQL 配置

编辑 `mysql/conf/my.cnf`：

```ini
[mysqld]
# 根据服务器内存调整
innodb_buffer_pool_size=1G  # 设置为物理内存的 50-70%
max_connections=500
```

#### 调整 Redis 配置

编辑 `redis/conf/redis.conf`：

```conf
# 根据需求调整内存限制
maxmemory 512mb
maxmemory-policy allkeys-lru
```

#### 调整 JVM 参数

编辑 `docker-compose.yml`：

```yaml
backend:
  environment:
    JAVA_OPTS: -Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### 4. 备份策略

#### 自动备份脚本

创建 `backup.sh`：

```bash
#!/bin/bash
BACKUP_DIR="/path/to/backup"
DATE=$(date +%Y%m%d_%H%M%S)

# 备份数据库
docker exec saury-mysql mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog > ${BACKUP_DIR}/db_${DATE}.sql

# 备份 Redis
docker exec saury-redis redis-cli -a ${REDIS_PASSWORD} save
docker cp saury-redis:/data/dump.rdb ${BACKUP_DIR}/redis_${DATE}.rdb

# 删除 7 天前的备份
find ${BACKUP_DIR} -name "*.sql" -mtime +7 -delete
find ${BACKUP_DIR} -name "*.rdb" -mtime +7 -delete
```

设置定时任务：

```bash
# 编辑 crontab
crontab -e

# 每天凌晨 2 点执行备份
0 2 * * * /path/to/backup.sh
```

---

## 📦 更新升级

### 更新代码

```bash
# 1. 拉取最新代码
cd SauryBlog
git pull

# 2. 停止服务
docker-compose down

# 3. 重新构建镜像
docker-compose build --no-cache

# 4. 启动服务
docker-compose up -d

# 5. 查看日志确认启动成功
docker-compose logs -f
```

### 数据库迁移

如果新版本包含数据库变更：

```bash
# 1. 备份数据库
docker exec saury-mysql mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog > backup_before_upgrade.sql

# 2. 执行迁移脚本（如果有）
docker exec -i saury-mysql mysql -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog < migration.sql
```

---

## 📞 技术支持

如遇到问题，请通过以下方式获取帮助：

- 📧 Email: 3254905724@qq.com
- 🐙 GitHub Issues: [https://github.com/IceSaury/SauryBlog/issues](https://github.com/IceSaury/SauryBlog/issues)

---

## 📄 许可证

本项目采用 MIT 许可证开源。

---

<div align="center">

**Made with ❤️ by Saury**

⭐ 如果这个项目对你有帮助，请给个 Star！

</div>

