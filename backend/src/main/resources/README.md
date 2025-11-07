# 配置文件说明

## 📁 文件列表

- **application.yml** - 主配置文件，使用变量占位符（可提交Git）
- **application-dev.yml** - 开发环境配置，包含敏感信息（不提交Git）⚠️
- **application-dev.yml.example** - 配置模板（可提交Git）

## 🚀 快速开始

### 首次使用

1. 复制配置模板：
   ```bash
   cp application-dev.yml.example application-dev.yml
   ```

2. 编辑 `application-dev.yml`，填写真实配置：
   - MySQL 密码
   - Redis 密码
   - 阿里云 OSS 配置（AccessKeyId、AccessKeySecret等）

3. 启动项目测试

## ⚠️ 重要提示

- `application-dev.yml` 包含敏感信息，已添加到 `.gitignore`
- **切勿将 `application-dev.yml` 提交到 Git 仓库**
- 如需共享配置，请使用安全的方式（加密邮件、密码管理工具等）

## 📖 详细文档

查看项目根目录的 `CONFIG_SECURITY_GUIDE.md` 获取完整的配置安全管理指南。

