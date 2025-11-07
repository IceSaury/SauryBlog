# 🤝 贡献指南

感谢您对 Saury Blog 项目的关注！我们欢迎任何形式的贡献，包括但不限于：

- 🐛 报告 Bug
- 💡 提出新功能建议
- 📝 改进文档
- 🔧 提交代码修复或新功能

---

## 📋 行为准则

参与本项目即表示您同意遵守以下准则：

- 尊重所有贡献者
- 使用友好和包容的语言
- 接受建设性的批评
- 关注对社区最有利的事情

---

## 🐛 如何报告 Bug

在提交 Bug 之前，请先搜索 [Issues](https://github.com/IceSaury/SauryBlog/issues) 确认该问题是否已被报告。

### Bug 报告应包含：

1. **清晰的标题** - 简洁描述问题
2. **重现步骤** - 详细的操作步骤
3. **预期行为** - 你期望发生什么
4. **实际行为** - 实际发生了什么
5. **环境信息** - 操作系统、浏览器、版本等
6. **截图或日志** - 如果可能，提供截图或错误日志

### Bug 报告模板：

```markdown
**Bug 描述**
简短描述这个 Bug

**重现步骤**
1. 进入 '...'
2. 点击 '....'
3. 滚动到 '....'
4. 看到错误

**预期行为**
清晰描述你期望发生什么

**实际行为**
清晰描述实际发生了什么

**截图**
如果可能，添加截图帮助解释问题

**环境信息**
- 操作系统: [例如 Windows 11]
- 浏览器: [例如 Chrome 120]
- 版本: [例如 1.0.0]

**附加信息**
其他相关信息
```

---

## 💡 如何提出功能建议

我们欢迎新功能建议！请先查看 [Issues](https://github.com/IceSaury/SauryBlog/issues) 确认该功能是否已被提出。

### 功能建议应包含：

1. **功能描述** - 清晰描述你想要的功能
2. **使用场景** - 为什么需要这个功能
3. **解决方案** - 你认为应该如何实现
4. **替代方案** - 是否有其他实现方式

### 功能建议模板：

```markdown
**功能描述**
清晰简洁地描述你想要的功能

**使用场景**
为什么需要这个功能？它解决了什么问题？

**建议的解决方案**
你认为应该如何实现这个功能

**替代方案**
你考虑过的其他实现方式

**附加信息**
其他相关信息、截图、示例等
```

---

## 🔧 如何贡献代码

### 开发流程

1. **Fork 项目**

点击右上角的 Fork 按钮，将项目 Fork 到你的 GitHub 账户。

2. **克隆到本地**

```bash
git clone https://github.com/IceSaury/SauryBlog.git
cd SauryBlog
```

3. **创建分支**

```bash
# 创建并切换到新分支
git checkout -b feature/your-feature-name

# 或修复 Bug
git checkout -b fix/your-bug-fix
```

分支命名规范：
- `feature/xxx` - 新功能
- `fix/xxx` - Bug 修复
- `docs/xxx` - 文档更新
- `style/xxx` - 代码格式调整
- `refactor/xxx` - 重构代码
- `test/xxx` - 测试相关
- `chore/xxx` - 构建/工具相关

4. **配置开发环境**

```bash
# 后端
cd backend
mvn clean install

# 前端
cd frontend
npm install
```

5. **进行开发**

请遵循项目的代码规范：

**Java 代码规范**：
- 使用 4 空格缩进
- 类名使用大驼峰命名（PascalCase）
- 方法名和变量名使用小驼峰命名（camelCase）
- 常量使用全大写加下划线（UPPER_SNAKE_CASE）
- 添加必要的注释
- 每个类和方法都应有清晰的职责

**JavaScript/TypeScript 代码规范**：
- 使用 2 空格缩进
- 使用 ES6+ 语法
- 组件名使用大驼峰命名
- 方法和变量名使用小驼峰命名
- 使用 TypeScript 类型注解
- 添加必要的注释

6. **提交代码**

```bash
git add .
git commit -m "feat: 添加用户头像上传功能"
```

提交信息规范（遵循 [Conventional Commits](https://www.conventionalcommits.org/)）：

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整（不影响功能）
- `refactor`: 重构代码
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具相关

示例：
```
feat: 添加用户头像上传功能
fix: 修复文章列表分页错误
docs: 更新 API 文档
style: 格式化代码
refactor: 重构用户服务层
perf: 优化文章查询性能
test: 添加用户注册单元测试
chore: 更新依赖版本
```

7. **推送到远程**

```bash
git push origin feature/your-feature-name
```

8. **创建 Pull Request**

- 进入你 Fork 的仓库
- 点击 "New Pull Request"
- 选择你的分支
- 填写 PR 描述
- 等待代码审查

### Pull Request 规范

**标题**：清晰描述你的改动

**描述应包含**：
- 改动内容说明
- 相关 Issue 编号（如果有）
- 测试说明
- 截图（如果有 UI 改动）

**PR 模板**：

```markdown
## 改动说明
简要描述你的改动

## 相关 Issue
Closes #issue_number

## 改动类型
- [ ] Bug 修复
- [ ] 新功能
- [ ] 重构
- [ ] 文档更新
- [ ] 其他

## 测试
描述你如何测试这些改动

## 截图（如果有 UI 改动）
添加截图展示改动

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 已添加必要的注释
- [ ] 已更新相关文档
- [ ] 已进行本地测试
- [ ] 没有产生新的警告
```

---

## 📝 如何改进文档

文档改进同样重要！如果你发现文档中的错误或不清楚的地方，请：

1. Fork 项目
2. 修改相关文档
3. 提交 Pull Request

文档包括：
- `README.md` - 项目介绍
- `API.md` - API 接口文档
- `DEPLOY.md` - 部署指南
- 代码注释

---

## ✅ 代码审查流程

提交 PR 后，维护者会进行代码审查：

1. **自动检查** - CI/CD 会自动运行测试
2. **人工审查** - 维护者会审查代码
3. **修改建议** - 可能会提出修改建议
4. **合并代码** - 审查通过后合并到主分支

**审查时间**：通常在 1-3 个工作日内

---

## 🎯 开发技巧

### 后端开发

1. **启动后端**
```bash
cd backend
mvn spring-boot:run
```

2. **运行测试**
```bash
mvn test
```

3. **打包**
```bash
mvn clean package -DskipTests
```

### 前端开发

1. **启动开发服务器**
```bash
cd frontend
npm run dev
```

2. **构建生产版本**
```bash
npm run build
```

3. **代码检查**
```bash
npm run lint
```

### 数据库

开发环境使用的数据库脚本位于：
```
backend/src/main/resources/db/schema.sql
```

---

## 🌟 成为贡献者

当你的 PR 被合并后，你将成为项目贡献者！我们会：

1. 在 README.md 的贡献者列表中添加你的信息
2. 感谢你的贡献

---

## 📞 联系方式

如有任何问题，欢迎通过以下方式联系：

- 📧 Email: your.email@example.com
- 💬 GitHub Issues: [提交 Issue](https://github.com/IceSaury/SauryBlog/issues)
- 🐙 GitHub Discussions: [参与讨论](https://github.com/IceSaury/SauryBlog/discussions)

---

## 🙏 感谢

感谢所有为项目做出贡献的开发者！

---

<div align="center">

**期待你的贡献！🎉**

</div>

