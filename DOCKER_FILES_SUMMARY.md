# 📋 Docker 配置文件总览

本文档列出了 SauryBlog Docker 部署所需的所有文件及其作用。

## 📁 文件结构

```
SauryBlog/
├── docker-compose.yml              # Docker Compose 编排文件（主配置）
├── .env                            # 环境变量配置文件（需手动创建）
├── deploy.sh                       # 一键部署脚本
├── stop.sh                         # 停止服务脚本
├── DOCKER_DEPLOY.md                # Docker 部署完整文档
│
├── backend/
│   ├── Dockerfile                  # 后端 Docker 镜像构建文件
│   ├── .dockerignore               # Docker 构建忽略文件
│   └── src/main/resources/
│       └── application-prod.yml    # 生产环境配置
│
├── frontend/
│   ├── Dockerfile                  # 前端 Docker 镜像构建文件
│   ├── .dockerignore               # Docker 构建忽略文件
│   └── nginx.conf                  # Nginx 配置文件
│
├── mysql/
│   └── conf/
│       └── my.cnf                  # MySQL 配置文件
│
└── redis/
    └── conf/
        └── redis.conf              # Redis 配置文件
```

## 📝 文件说明

### 核心配置文件

#### 1. `docker-compose.yml`
**作用**: Docker Compose 主配置文件，定义所有服务的编排关系

**包含的服务**:
- MySQL 8.0
- Redis 7
- Spring Boot 后端
- Vue3 + Nginx 前端

**特性**:
- 服务健康检查
- 数据持久化
- 网络隔离
- 环境变量注入

---

#### 2. `.env` (需手动创建)
**作用**: 存储敏感配置信息和环境变量

**创建方法**:
```bash
cat > .env << 'EOF'
MYSQL_PORT=3306
REDIS_PORT=6379
BACKEND_PORT=8088
FRONTEND_PORT=80
MYSQL_ROOT_PASSWORD=YourStrongPassword123!
MYSQL_DATABASE=saury_blog
REDIS_PASSWORD=YourRedisPassword123!
JWT_SECRET=YourJWTSecretKey
OPENAI_API_KEY=your_api_key
ALIYUN_OSS_ACCESS_KEY_ID=your_key_id
ALIYUN_OSS_ACCESS_KEY_SECRET=your_key_secret
ALIYUN_OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
ALIYUN_OSS_BUCKET_NAME=your_bucket
ALIYUN_OSS_PREFIX=saury-blog/
EOF
```

**⚠️ 重要**: 此文件包含敏感信息，已添加到 `.gitignore`，不会被提交到代码仓库。

---

### 部署脚本

#### 3. `deploy.sh`
**作用**: 一键部署脚本，自动化部署流程

**功能**:
- ✅ 检查 Docker 环境
- ✅ 验证配置文件
- ✅ 创建必要目录
- ✅ 构建镜像
- ✅ 启动服务
- ✅ 健康检查
- ✅ 显示访问地址

**使用方法**:
```bash
chmod +x deploy.sh
./deploy.sh
```

---

#### 4. `stop.sh`
**作用**: 停止服务脚本

**功能**:
- 停止所有容器
- 可选择是否删除数据
- 友好的交互提示

**使用方法**:
```bash
chmod +x stop.sh
./stop.sh
```

---

### 后端配置

#### 5. `backend/Dockerfile`
**作用**: 后端 Docker 镜像构建文件

**特点**:
- 多阶段构建（减小镜像体积）
- 基于 Eclipse Temurin JRE 21
- 时区设置为 Asia/Shanghai
- JVM 参数优化
- 健康检查

**镜像大小**: 约 300MB

---

#### 6. `backend/.dockerignore`
**作用**: 排除不需要打包进镜像的文件

**排除内容**:
- target/ (已编译文件)
- IDE 配置文件
- 日志文件
- 文档文件

---

#### 7. `backend/src/main/resources/application-prod.yml`
**作用**: 生产环境 Spring Boot 配置

**特点**:
- 通过环境变量注入配置
- 优化的日志级别
- 关闭 SQL 日志输出
- 滚动日志文件

---

### 前端配置

#### 8. `frontend/Dockerfile`
**作用**: 前端 Docker 镜像构建文件

**特点**:
- 多阶段构建
- Node.js 构建 + Nginx 运行
- 时区设置
- 健康检查

**镜像大小**: 约 50MB

---

#### 9. `frontend/.dockerignore`
**作用**: 排除不需要打包进镜像的文件

**排除内容**:
- node_modules/
- dist/
- IDE 配置文件
- 文档文件

---

#### 10. `frontend/nginx.conf`
**作用**: Nginx 服务器配置

**功能**:
- 静态文件服务
- Gzip 压缩
- 反向代理后端 API
- Vue Router History 模式支持
- 缓存策略
- 安全头部

---

### 数据库配置

#### 11. `mysql/conf/my.cnf`
**作用**: MySQL 数据库优化配置

**优化项**:
- 字符集：UTF-8MB4
- 最大连接数：1000
- 慢查询日志
- InnoDB 优化
- 时区设置

---

#### 12. `redis/conf/redis.conf`
**作用**: Redis 缓存服务配置

**配置项**:
- RDB + AOF 持久化
- 内存限制：256MB
- LRU 淘汰策略
- 慢查询日志
- 连接超时设置

---

### 文档

#### 13. `DOCKER_DEPLOY.md`
**作用**: 完整的 Docker 部署文档

**内容包括**:
- 📋 系统要求
- 🚀 快速开始
- 📝 详细部署步骤
- ⚙️ 配置说明
- 🎮 常用命令
- 🔧 故障排查
- 🚀 生产环境优化
- 📦 更新升级

---

## 🚀 快速部署流程

### 1️⃣ 首次部署

```bash
# 克隆项目
git clone https://github.com/IceSaury/SauryBlog.git
cd SauryBlog

# 创建环境配置
cat > .env << 'EOF'
MYSQL_PORT=3306
REDIS_PORT=6379
BACKEND_PORT=8088
FRONTEND_PORT=80
MYSQL_ROOT_PASSWORD=YourPassword123!
MYSQL_DATABASE=saury_blog
REDIS_PASSWORD=YourRedisPass123!
JWT_SECRET=YourJWTSecretKey
OPENAI_API_KEY=your_api_key
ALIYUN_OSS_ACCESS_KEY_ID=your_key
ALIYUN_OSS_ACCESS_KEY_SECRET=your_secret
ALIYUN_OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
ALIYUN_OSS_BUCKET_NAME=your_bucket
ALIYUN_OSS_PREFIX=saury-blog/
EOF

# 修改配置（重要！）
vim .env

# 一键部署
chmod +x deploy.sh
./deploy.sh
```

### 2️⃣ 访问应用

部署成功后：
- 前端: `http://your-server-ip`
- 后端: `http://your-server-ip:8088/api`
- 默认账号: `admin` / `123456`

### 3️⃣ 日常维护

```bash
# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
./stop.sh

# 备份数据库
docker exec saury-mysql mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog > backup.sql
```

---

## 🔐 安全建议

1. **必须修改的配置**:
   - ✅ `MYSQL_ROOT_PASSWORD` - 使用强密码
   - ✅ `REDIS_PASSWORD` - 使用强密码
   - ✅ `JWT_SECRET` - 使用复杂随机字符串
   - ✅ 首次登录后立即修改 `admin` 密码

2. **可选配置**:
   - `OPENAI_API_KEY` - 如不使用 AI 功能可保留默认
   - `ALIYUN_OSS_*` - 如不使用图片上传可保留默认

3. **生产环境建议**:
   - 配置防火墙
   - 启用 HTTPS
   - 定期备份数据
   - 监控服务状态

---

## 📊 资源占用

### 最低配置
- CPU: 2核
- 内存: 4GB
- 磁盘: 20GB

### 推荐配置
- CPU: 4核
- 内存: 8GB
- 磁盘: 50GB

### 容器资源占用（参考）
- MySQL: ~200MB
- Redis: ~50MB
- Backend: ~500MB
- Frontend (Nginx): ~10MB
- **总计**: ~800MB

---

## 🆘 常见问题

### Q1: 端口被占用怎么办？
**A**: 修改 `.env` 文件中的端口配置，然后重新部署。

### Q2: 如何查看详细日志？
**A**: `docker-compose logs -f [服务名]`

### Q3: 如何进入容器调试？
**A**: `docker exec -it [容器名] sh`

### Q4: 如何备份数据？
**A**: 
```bash
# 备份 MySQL
docker exec saury-mysql mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} saury_blog > backup.sql

# 备份 Redis
docker exec saury-redis redis-cli -a ${REDIS_PASSWORD} save
docker cp saury-redis:/data/dump.rdb ./backup.rdb
```

### Q5: 如何更新应用？
**A**: 
```bash
git pull
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

## 📞 获取帮助

如遇到问题：
1. 查看 [DOCKER_DEPLOY.md](./DOCKER_DEPLOY.md) 完整文档
2. 查看 [故障排查](./DOCKER_DEPLOY.md#故障排查) 章节
3. 提交 [GitHub Issue](https://github.com/IceSaury/SauryBlog/issues)
4. 发送邮件: 3254905724@qq.com

---

<div align="center">

**🎉 祝你部署顺利！**

Made with ❤️ by Saury

</div>

