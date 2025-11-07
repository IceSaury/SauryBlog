# 📡 Saury Blog API 接口文档

## 📋 目录

- [1. 基础说明](#1-基础说明)
- [2. 认证授权](#2-认证授权)
- [3. 用户管理](#3-用户管理)
- [4. 文章管理](#4-文章管理)
- [5. 分类管理](#5-分类管理)
- [6. 标签管理](#6-标签管理)
- [7. 评论管理](#7-评论管理)
- [8. 项目管理](#8-项目管理)
- [9. 留言管理](#9-留言管理)
- [10. AI 聊天](#10-ai-聊天)
- [11. 文件上传](#11-文件上传)
- [12. 仪表盘](#12-仪表盘)
- [13. 网站配置](#13-网站配置)

---

## 1. 基础说明

### 1.1 接口基础信息

- **Base URL**: `http://localhost:8080/api`
- **请求格式**: `application/json`
- **响应格式**: `application/json`
- **字符编码**: `UTF-8`

### 1.2 统一响应格式

所有接口统一返回以下格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**响应字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200 表示成功 |
| message | string | 响应消息 |
| data | object | 响应数据 |

### 1.3 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 1.4 认证方式

除登录、注册接口外，其他需要认证的接口需要在请求头中携带 JWT Token：

```
Authorization: Bearer <token>
```

---

## 2. 认证授权

### 2.1 用户注册

**接口地址**: `POST /auth/register`

**请求参数**:

```json
{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名，3-20 位 |
| password | string | 是 | 密码，6-20 位 |
| nickname | string | 否 | 昵称 |
| email | string | 否 | 邮箱 |

**响应示例**:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

### 2.2 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "Saury",
      "avatar": "https://example.com/avatar.jpg",
      "email": "admin@saury.com",
      "role": 0
    }
  }
}
```

### 2.3 退出登录

**接口地址**: `POST /auth/logout`

**请求头**: 需要携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

### 2.4 获取当前用户信息

**接口地址**: `GET /auth/info`

**请求头**: 需要携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "Saury",
    "avatar": "https://example.com/avatar.jpg",
    "email": "admin@saury.com",
    "github": "https://github.com/username",
    "csdn": "https://blog.csdn.net/username",
    "intro": "Java开发工程师",
    "role": 0
  }
}
```

---

## 3. 用户管理

### 3.1 获取用户列表

**接口地址**: `GET /user/list`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| keyword | string | 否 | 搜索关键词 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "Saury",
        "avatar": "https://example.com/avatar.jpg",
        "email": "admin@saury.com",
        "role": 0,
        "status": 1,
        "createTime": "2025-01-01 00:00:00"
      }
    ]
  }
}
```

### 3.2 更新用户信息

**接口地址**: `PUT /user/{id}`

**请求头**: 需要携带 Token

**请求参数**:

```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/new-avatar.jpg",
  "email": "new@example.com",
  "github": "https://github.com/username",
  "csdn": "https://blog.csdn.net/username",
  "intro": "个人简介"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 3.3 修改密码

**接口地址**: `PUT /user/password`

**请求头**: 需要携带 Token

**请求参数**:

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

### 3.4 删除用户

**接口地址**: `DELETE /user/{id}`

**请求头**: 需要携带 Token（管理员）

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 4. 文章管理

### 4.1 获取文章列表

**接口地址**: `GET /article/list`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| categoryId | long | 否 | 分类 ID |
| tagId | long | 否 | 标签 ID |
| keyword | string | 否 | 搜索关键词 |
| status | int | 否 | 状态：0-草稿 1-已发布 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "title": "Spring Boot 3.0 新特性",
        "summary": "本文介绍 Spring Boot 3.0 的新特性...",
        "cover": "https://example.com/cover.jpg",
        "categoryId": 2,
        "categoryName": "Spring",
        "tags": [
          {
            "id": 2,
            "name": "Spring Boot",
            "color": "#FF006E"
          }
        ],
        "viewCount": 1000,
        "likeCount": 50,
        "commentCount": 10,
        "isTop": 0,
        "isFeatured": 1,
        "status": 1,
        "createTime": "2025-01-01 00:00:00",
        "updateTime": "2025-01-02 00:00:00"
      }
    ]
  }
}
```

### 4.2 获取文章详情

**接口地址**: `GET /article/{id}`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Spring Boot 3.0 新特性",
    "summary": "本文介绍 Spring Boot 3.0 的新特性...",
    "cover": "https://example.com/cover.jpg",
    "content": "# Spring Boot 3.0 新特性\n\n...",
    "categoryId": 2,
    "categoryName": "Spring",
    "tags": [
      {
        "id": 2,
        "name": "Spring Boot",
        "color": "#FF006E"
      }
    ],
    "author": {
      "id": 1,
      "nickname": "Saury",
      "avatar": "https://example.com/avatar.jpg"
    },
    "viewCount": 1000,
    "likeCount": 50,
    "commentCount": 10,
    "createTime": "2025-01-01 00:00:00",
    "updateTime": "2025-01-02 00:00:00"
  }
}
```

### 4.3 创建文章

**接口地址**: `POST /article`

**请求头**: 需要携带 Token

**请求参数**:

```json
{
  "title": "文章标题",
  "summary": "文章摘要",
  "cover": "https://example.com/cover.jpg",
  "content": "# 文章内容\n\nMarkdown 格式",
  "categoryId": 2,
  "tagIds": [1, 2, 3],
  "isTop": 0,
  "isFeatured": 0,
  "status": 1
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 文章标题 |
| summary | string | 否 | 文章摘要 |
| cover | string | 否 | 文章封面 |
| content | string | 是 | 文章内容（Markdown） |
| categoryId | long | 否 | 分类 ID |
| tagIds | array | 否 | 标签 ID 数组 |
| isTop | int | 否 | 是否置顶：0-否 1-是 |
| isFeatured | int | 否 | 是否推荐：0-否 1-是 |
| status | int | 否 | 状态：0-草稿 1-已发布 |

**响应示例**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

### 4.4 更新文章

**接口地址**: `PUT /article/{id}`

**请求头**: 需要携带 Token

**请求参数**: 同创建文章

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 4.5 删除文章

**接口地址**: `DELETE /article/{id}`

**请求头**: 需要携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 4.6 文章点赞

**接口地址**: `POST /article/{id}/like`

**请求头**: 需要携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": null
}
```

---

## 5. 分类管理

### 5.1 获取分类列表

**接口地址**: `GET /category/list`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 6,
    "list": [
      {
        "id": 1,
        "name": "Java",
        "description": "Java技术相关文章",
        "icon": "☕",
        "sort": 1,
        "articleCount": 10,
        "createTime": "2025-01-01 00:00:00"
      }
    ]
  }
}
```

### 5.2 获取所有分类（不分页）

**接口地址**: `GET /category/all`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Java",
      "description": "Java技术相关文章",
      "icon": "☕",
      "sort": 1,
      "articleCount": 10
    }
  ]
}
```

### 5.3 创建分类

**接口地址**: `POST /category`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

```json
{
  "name": "分类名称",
  "description": "分类描述",
  "icon": "🎨",
  "sort": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

### 5.4 更新分类

**接口地址**: `PUT /category/{id}`

**请求头**: 需要携带 Token（管理员）

**请求参数**: 同创建分类

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 5.5 删除分类

**接口地址**: `DELETE /category/{id}`

**请求头**: 需要携带 Token（管理员）

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 6. 标签管理

### 6.1 获取标签列表

**接口地址**: `GET /tag/list`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 10,
    "list": [
      {
        "id": 1,
        "name": "Java",
        "color": "#00F0FF",
        "articleCount": 15,
        "createTime": "2025-01-01 00:00:00"
      }
    ]
  }
}
```

### 6.2 获取所有标签（不分页）

**接口地址**: `GET /tag/all`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Java",
      "color": "#00F0FF",
      "articleCount": 15
    }
  ]
}
```

### 6.3 创建标签

**接口地址**: `POST /tag`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

```json
{
  "name": "标签名称",
  "color": "#00F0FF"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

### 6.4 更新标签

**接口地址**: `PUT /tag/{id}`

**请求头**: 需要携带 Token（管理员）

**请求参数**: 同创建标签

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 6.5 删除标签

**接口地址**: `DELETE /tag/{id}`

**请求头**: 需要携带 Token（管理员）

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 7. 评论管理

### 7.1 获取文章评论列表

**接口地址**: `GET /comment/list`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "list": [
      {
        "id": 1,
        "articleId": 1,
        "userId": 1,
        "parentId": null,
        "replyUserId": null,
        "content": "写得不错！",
        "nickname": "张三",
        "avatar": "https://example.com/avatar.jpg",
        "likeCount": 5,
        "createTime": "2025-01-01 00:00:00",
        "replies": [
          {
            "id": 2,
            "parentId": 1,
            "replyUserId": 1,
            "content": "谢谢！",
            "nickname": "Saury",
            "avatar": "https://example.com/avatar2.jpg",
            "likeCount": 2,
            "createTime": "2025-01-01 01:00:00"
          }
        ]
      }
    ]
  }
}
```

### 7.2 发表评论

**接口地址**: `POST /comment`

**请求头**: 可选携带 Token（未登录可匿名评论）

**请求参数**:

```json
{
  "articleId": 1,
  "parentId": null,
  "replyUserId": null,
  "content": "评论内容",
  "nickname": "匿名用户",
  "email": "anonymous@example.com"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | long | 是 | 文章 ID |
| parentId | long | 否 | 父评论 ID（回复时填写） |
| replyUserId | long | 否 | 回复用户 ID（回复时填写） |
| content | string | 是 | 评论内容 |
| nickname | string | 否 | 昵称（未登录时必填） |
| email | string | 否 | 邮箱（未登录时必填） |

**响应示例**:

```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "id": 1
  }
}
```

### 7.3 删除评论

**接口地址**: `DELETE /comment/{id}`

**请求头**: 需要携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 7.4 评论点赞

**接口地址**: `POST /comment/{id}/like`

**请求头**: 可选携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": null
}
```

---

## 8. 项目管理

### 8.1 获取项目列表

**接口地址**: `GET /project/list`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |
| type | int | 否 | 项目类型：0-企业 1-个人 2-开源 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "name": "Saury Blog",
        "description": "赛博朋克风格的个人博客系统",
        "cover": "https://example.com/project-cover.jpg",
        "images": [
          "https://example.com/image1.jpg",
          "https://example.com/image2.jpg"
        ],
        "techStack": ["Java", "Spring Boot", "Vue", "MySQL"],
        "githubUrl": "https://github.com/username/project",
        "demoUrl": "https://demo.example.com",
        "highlights": [
          "赛博朋克设计风格",
          "AI 智能助手",
          "Live2D 看板娘"
        ],
        "type": 1,
        "viewCount": 500,
        "createTime": "2025-01-01 00:00:00"
      }
    ]
  }
}
```

### 8.2 获取项目详情

**接口地址**: `GET /project/{id}`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Saury Blog",
    "description": "赛博朋克风格的个人博客系统...",
    "cover": "https://example.com/project-cover.jpg",
    "images": ["https://example.com/image1.jpg"],
    "techStack": ["Java", "Spring Boot", "Vue"],
    "githubUrl": "https://github.com/username/project",
    "demoUrl": "https://demo.example.com",
    "highlights": ["赛博朋克设计风格"],
    "type": 1,
    "viewCount": 500,
    "createTime": "2025-01-01 00:00:00"
  }
}
```

### 8.3 创建项目

**接口地址**: `POST /project`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

```json
{
  "name": "项目名称",
  "description": "项目描述",
  "cover": "https://example.com/cover.jpg",
  "images": ["https://example.com/image1.jpg"],
  "techStack": ["Java", "Spring Boot"],
  "githubUrl": "https://github.com/username/project",
  "demoUrl": "https://demo.example.com",
  "highlights": ["项目亮点1", "项目亮点2"],
  "type": 1,
  "sort": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

### 8.4 更新项目

**接口地址**: `PUT /project/{id}`

**请求头**: 需要携带 Token（管理员）

**请求参数**: 同创建项目

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 8.5 删除项目

**接口地址**: `DELETE /project/{id}`

**请求头**: 需要携带 Token（管理员）

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 9. 留言管理

### 9.1 获取留言列表

**接口地址**: `GET /message/list`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 10 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 30,
    "list": [
      {
        "id": 1,
        "userId": 1,
        "content": "网站做得很棒！",
        "nickname": "张三",
        "avatar": "https://example.com/avatar.jpg",
        "email": "user@example.com",
        "address": "浙江省杭州市",
        "status": 1,
        "createTime": "2025-01-01 00:00:00"
      }
    ]
  }
}
```

### 9.2 发表留言

**接口地址**: `POST /message`

**请求头**: 可选携带 Token（未登录可匿名留言）

**请求参数**:

```json
{
  "content": "留言内容",
  "nickname": "匿名用户",
  "email": "anonymous@example.com"
}
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | string | 是 | 留言内容 |
| nickname | string | 否 | 昵称（未登录时必填） |
| email | string | 否 | 邮箱（未登录时必填） |

**响应示例**:

```json
{
  "code": 200,
  "message": "留言成功",
  "data": {
    "id": 1
  }
}
```

### 9.3 删除留言

**接口地址**: `DELETE /message/{id}`

**请求头**: 需要携带 Token（管理员）

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 9.4 审核留言

**接口地址**: `PUT /message/{id}/status`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

```json
{
  "status": 1
}
```

**参数说明**:

- status: 0-待审核 1-已通过 2-已拒绝

**响应示例**:

```json
{
  "code": 200,
  "message": "审核成功",
  "data": null
}
```

---

## 10. AI 聊天

### 10.1 发送消息

**接口地址**: `POST /chat/send`

**请求头**: 可选携带 Token

**请求参数**:

```json
{
  "message": "你好，请介绍一下 Spring Boot"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "reply": "你好！Spring Boot 是一个基于 Spring 框架的开源 Java 开发框架...",
    "timestamp": "2025-01-01 00:00:00"
  }
}
```

### 10.2 获取聊天历史

**接口地址**: `GET /chat/history`

**请求头**: 需要携带 Token

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页数量，默认 20 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "role": "user",
        "content": "你好",
        "createTime": "2025-01-01 00:00:00"
      },
      {
        "id": 2,
        "role": "assistant",
        "content": "你好！有什么我可以帮助你的吗？",
        "createTime": "2025-01-01 00:00:01"
      }
    ]
  }
}
```

### 10.3 清空聊天历史

**接口地址**: `DELETE /chat/history`

**请求头**: 需要携带 Token

**响应示例**:

```json
{
  "code": 200,
  "message": "清空成功",
  "data": null
}
```

---

## 11. 文件上传

### 11.1 上传图片

**接口地址**: `POST /upload/image`

**请求头**: 需要携带 Token

**请求参数**: multipart/form-data

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件 |

**响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.oss-cn-hangzhou.aliyuncs.com/images/xxx.jpg"
  }
}
```

### 11.2 上传文件

**接口地址**: `POST /upload/file`

**请求头**: 需要携带 Token

**请求参数**: multipart/form-data

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 文件 |

**响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://example.oss-cn-hangzhou.aliyuncs.com/files/xxx.pdf",
    "filename": "xxx.pdf",
    "size": 1024000
  }
}
```

---

## 12. 仪表盘

### 12.1 获取统计数据

**接口地址**: `GET /dashboard/stats`

**请求头**: 需要携带 Token（管理员）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleCount": 50,
    "categoryCount": 6,
    "tagCount": 15,
    "commentCount": 200,
    "viewCount": 10000,
    "userCount": 100,
    "todayViewCount": 150,
    "todayCommentCount": 10
  }
}
```

### 12.2 获取访问趋势

**接口地址**: `GET /dashboard/trend`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| days | int | 否 | 天数，默认 7 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dates": ["2025-01-01", "2025-01-02", "2025-01-03"],
    "views": [100, 150, 200],
    "comments": [5, 8, 10]
  }
}
```

### 12.3 获取文章排行

**接口地址**: `GET /dashboard/article-rank`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 否 | 排行类型：view-浏览 like-点赞 comment-评论 |
| limit | int | 否 | 数量，默认 10 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "Spring Boot 3.0 新特性",
      "viewCount": 1000,
      "likeCount": 50,
      "commentCount": 10
    }
  ]
}
```

---

## 13. 网站配置

### 13.1 获取网站配置

**接口地址**: `GET /config`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "siteName": "Saury Blog",
    "siteDesc": "一个赛博朋克风格的技术博客",
    "siteKeywords": "Java,Spring Boot,Vue,技术博客",
    "siteAuthor": "Saury",
    "siteRecord": "",
    "siteFooter": "© 2025 Saury Blog. All Rights Reserved.",
    "messageLoginRequired": false,
    "commentLoginRequired": false
  }
}
```

### 13.2 更新网站配置

**接口地址**: `PUT /config`

**请求头**: 需要携带 Token（管理员）

**请求参数**:

```json
{
  "siteName": "Saury Blog",
  "siteDesc": "一个赛博朋克风格的技术博客",
  "siteKeywords": "Java,Spring Boot,Vue",
  "siteAuthor": "Saury",
  "siteRecord": "ICP备案号",
  "siteFooter": "© 2025 Saury Blog",
  "messageLoginRequired": false,
  "commentLoginRequired": false
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

## 📚 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 40001 | 用户名已存在 | 更换用户名 |
| 40002 | 用户名或密码错误 | 检查登录信息 |
| 40003 | 参数校验失败 | 检查请求参数 |
| 40101 | Token 已过期 | 重新登录 |
| 40102 | Token 无效 | 重新登录 |
| 40103 | 未登录 | 请先登录 |
| 40301 | 权限不足 | 需要管理员权限 |
| 40401 | 文章不存在 | 检查文章 ID |
| 40402 | 分类不存在 | 检查分类 ID |
| 40403 | 标签不存在 | 检查标签 ID |
| 40404 | 资源不存在 | 检查资源 ID |
| 50001 | 文件上传失败 | 检查文件格式和大小 |
| 50002 | OSS 服务异常 | 检查 OSS 配置 |
| 50003 | AI 服务异常 | 检查 OpenAI 配置 |

---

## 🔔 注意事项

1. 所有需要认证的接口都需要在请求头中携带 `Authorization: Bearer <token>`
2. 文件上传接口需要使用 `multipart/form-data` 格式
3. 分页参数 `pageNum` 从 1 开始
4. 时间格式统一为 `yyyy-MM-dd HH:mm:ss`
5. 图片上传支持格式：jpg、jpeg、png、gif，单个文件不超过 5MB
6. AI 聊天功能需要配置 OpenAI API Key
7. 文件上传功能需要配置阿里云 OSS

---

## 📞 技术支持

如有接口使用问题，请联系：

- 📧 Email: 3254905724@qq.com
- 🐙 GitHub: [@IceSaury](https://github.com/IceSaury)

---

<div align="center">

**© 2025 Saury Blog. All Rights Reserved.**

</div>

