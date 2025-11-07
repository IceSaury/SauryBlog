<template>
  <div class="about-page">
    <div class="page-header">
      <h1 class="page-title glitch" data-text="ABOUT ME">ABOUT ME</h1>
      <p class="page-subtitle">Java开发工程师 | 全栈开发者 | 技术爱好者</p>
    </div>

    <!-- 个人简介 -->
    <CyberCard class="about-card intro-card">
      <div class="card-header">
        <h2 class="section-title">
          <span class="title-icon">👨‍💻</span>
          个人简介
        </h2>
      </div>
      <div class="intro-content">
        <p class="intro-text">
          我是一名充满热情的 Java 开发工程师，拥有扎实的计算机基础和丰富的项目开发经验。
          专注于后端开发、微服务架构设计与性能优化，致力于编写高质量、可维护的代码。
        </p>
        <p class="intro-text">
          在技术领域，我始终保持学习的态度，紧跟行业前沿技术发展。擅长使用 Spring 生态系统构建企业级应用，
          熟悉分布式系统设计，对高并发、高可用系统有深入理解。同时也热衷于技术分享，乐于与团队成员交流技术心得。
        </p>
        <div class="highlights">
          <div class="highlight-item">
            <span class="highlight-number">3+</span>
            <span class="highlight-label">年开发经验</span>
          </div>
          <div class="highlight-item">
            <span class="highlight-number">10+</span>
            <span class="highlight-label">项目经验</span>
          </div>
          <div class="highlight-item">
            <span class="highlight-number">20+</span>
            <span class="highlight-label">技术栈掌握</span>
          </div>
        </div>
      </div>
    </CyberCard>

    <!-- 核心技能 -->
    <CyberCard class="about-card skills-card">
      <div class="card-header">
        <h2 class="section-title">
          <span class="title-icon">⚡</span>
          核心技能
        </h2>
      </div>
      <div class="skills-content">
        <div class="skill-category">
          <h3 class="category-name">后端开发</h3>
          <div class="tech-tags">
            <CyberTag color="cyan">Java 17+</CyberTag>
            <CyberTag color="magenta">Spring Boot</CyberTag>
            <CyberTag color="purple">Spring Cloud</CyberTag>
            <CyberTag color="cyan">MyBatis Plus</CyberTag>
            <CyberTag color="magenta">Spring AI</CyberTag>
          </div>
        </div>

        <div class="skill-category">
          <h3 class="category-name">数据库 & 缓存</h3>
          <div class="tech-tags">
            <CyberTag color="yellow">MySQL</CyberTag>
            <CyberTag color="purple">Redis</CyberTag>
            <CyberTag color="cyan">MongoDB</CyberTag>
            <CyberTag color="magenta">Elasticsearch</CyberTag>
          </div>
        </div>

        <div class="skill-category">
          <h3 class="category-name">中间件 & 工具</h3>
          <div class="tech-tags">
            <CyberTag color="green">RabbitMQ</CyberTag>
            <CyberTag color="yellow">Kafka</CyberTag>
            <CyberTag color="purple">Nginx</CyberTag>
            <CyberTag color="cyan">Docker</CyberTag>
            <CyberTag color="magenta">Git</CyberTag>
          </div>
        </div>

        <div class="skill-category">
          <h3 class="category-name">前端技术</h3>
          <div class="tech-tags">
            <CyberTag color="green">Vue.js</CyberTag>
            <CyberTag color="cyan">TypeScript</CyberTag>
            <CyberTag color="magenta">Element Plus</CyberTag>
            <CyberTag color="purple">Vite</CyberTag>
          </div>
        </div>
      </div>
    </CyberCard>

    <!-- 专业能力 -->
    <CyberCard class="about-card abilities-card">
      <div class="card-header">
        <h2 class="section-title">
          <span class="title-icon">🎯</span>
          专业能力
        </h2>
      </div>
      <div class="abilities-grid">
        <div class="ability-item">
          <div class="ability-icon">🏗️</div>
          <h3 class="ability-title">架构设计</h3>
          <p class="ability-desc">
            熟悉微服务架构设计模式，能够根据业务需求设计合理的系统架构，
            包括服务拆分、API 设计、数据库设计等
          </p>
        </div>
        <div class="ability-item">
          <div class="ability-icon">⚡</div>
          <h3 class="ability-title">性能优化</h3>
          <p class="ability-desc">
            擅长 SQL 优化、缓存策略设计、代码性能调优，
            能够有效解决系统性能瓶颈，提升系统响应速度
          </p>
        </div>
        <div class="ability-item">
          <div class="ability-icon">🔒</div>
          <h3 class="ability-title">安全防护</h3>
          <p class="ability-desc">
            熟悉常见的 Web 安全问题及防护方案，包括 SQL 注入、
            XSS、CSRF 等，能够构建安全可靠的系统
          </p>
        </div>
        <div class="ability-item">
          <div class="ability-icon">🚀</div>
          <h3 class="ability-title">DevOps</h3>
          <p class="ability-desc">
            熟悉 CI/CD 流程，使用 Docker 容器化部署，
            能够搭建自动化测试和部署流程
          </p>
        </div>
      </div>
    </CyberCard>

    <!-- 项目经验 -->
    <CyberCard class="about-card projects-card">
      <div class="card-header">
        <h2 class="section-title">
          <span class="title-icon">💼</span>
          项目经验
        </h2>
      </div>
      <div class="project-list">
        <div class="project-item">
          <div class="project-header">
            <h3 class="project-name">Saury 个人博客系统</h3>
            <span class="project-tag">个人项目</span>
          </div>
          <p class="project-desc">
            一个基于 Spring Boot + Vue3 的全栈博客系统，采用赛博朋克风格设计。
            实现了文章管理、评论系统、用户权限、阿里云 OSS 图片上传等功能。
          </p>
          <div class="project-tech">
            <span>Spring Boot</span>
            <span>MyBatis Plus</span>
            <span>Redis</span>
            <span>JWT</span>
            <span>Vue3</span>
            <span>TypeScript</span>
          </div>
        </div>

        <div class="project-item">
          <div class="project-header">
            <h3 class="project-name">企业级微服务架构项目</h3>
            <span class="project-tag">商业项目</span>
          </div>
          <p class="project-desc">
            参与开发大型电商平台后端系统，负责订单服务、支付服务的设计与开发。
            使用 Spring Cloud 微服务架构，实现了高并发下的稳定运行。
          </p>
          <div class="project-tech">
            <span>Spring Cloud</span>
            <span>Nacos</span>
            <span>Feign</span>
            <span>RabbitMQ</span>
            <span>MySQL</span>
            <span>Redis</span>
          </div>
        </div>

        <div class="project-item">
          <div class="project-header">
            <h3 class="project-name">智能数据分析平台</h3>
            <span class="project-tag">企业项目</span>
          </div>
          <p class="project-desc">
            开发数据采集与分析系统，实现了海量数据的实时处理和可视化展示。
            运用 Elasticsearch 进行全文检索，Kafka 进行消息队列处理。
          </p>
          <div class="project-tech">
            <span>Spring Boot</span>
            <span>Elasticsearch</span>
            <span>Kafka</span>
            <span>MongoDB</span>
            <span>ECharts</span>
          </div>
        </div>
      </div>
    </CyberCard>

    <!-- 教育背景 & 证书 -->
    <div class="two-column-section">
      <CyberCard class="about-card education-card">
        <div class="card-header">
          <h2 class="section-title">
            <span class="title-icon">🎓</span>
            教育背景
          </h2>
        </div>
        <div class="education-content">
          <div class="education-item">
            <h3 class="education-school">江苏 大学</h3>
            <p class="education-major">智能科学与技术 | 本科</p>
            <p class="education-time">2023 - 2027</p>
          </div>
        </div>
      </CyberCard>

      <CyberCard class="about-card certificates-card">
        <div class="card-header">
          <h2 class="section-title">
            <span class="title-icon">🏆</span>
            技能证书
          </h2>
        </div>
        <div class="certificates-content">
          <ul class="certificate-list">
            <li>阿里云开发者认证</li>
            <li>软件设计师（中级）</li>
            <li>CET-4 英语四级</li>
          </ul>
        </div>
      </CyberCard>
    </div>

    <!-- 联系方式 -->
    <CyberCard class="about-card contact-card">
      <div class="card-header">
        <h2 class="section-title">
          <span class="title-icon">📮</span>
          联系方式
        </h2>
      </div>
      <div class="contact-content">
        <div class="contact-grid">
          <a href="mailto:3254905724@qq.com" class="contact-item">
            <div class="contact-icon">📧</div>
            <div class="contact-info">
              <div class="contact-label">Email</div>
              <div class="contact-value">3254905724@qq.com</div>
            </div>
          </a>
          <a href="https://github.com/IceSaury" target="_blank" class="contact-item">
            <div class="contact-icon">🔗</div>
            <div class="contact-info">
              <div class="contact-label">GitHub</div>
              <div class="contact-value">github.com/IceSaury</div>
            </div>
          </a>
        </div>
        <div class="contact-footer">
          <p>期待与您交流技术，共同成长！</p>
        </div>
      </div>
    </CyberCard>
  </div>
</template>

<script setup lang="ts">
import CyberCard from '@/components/CyberCard.vue'
import CyberTag from '@/components/CyberTag.vue'
</script>

<style lang="scss" scoped>
.about-page {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}

// 页面头部
.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-title {
  font-size: 56px;
  font-weight: 700;
  color: $neon-cyan;
  text-shadow: $shadow-neon-cyan;
  margin-bottom: 16px;
  position: relative;
  display: inline-block;

  &.glitch {
    &::before,
    &::after {
      content: attr(data-text);
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
    }

    &:hover::before {
      left: 2px;
      text-shadow: -2px 0 $neon-magenta;
      animation: glitch-anim 0.3s;
    }

    &:hover::after {
      left: -2px;
      text-shadow: 2px 0 $neon-purple;
      animation: glitch-anim 0.3s reverse;
    }
  }
}

.page-subtitle {
  font-size: 20px;
  color: $text-secondary;
  font-weight: 500;
}

// 通用卡片样式
.about-card {
  margin-bottom: 32px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba($neon-cyan, 0.15);
  }
}

.card-header {
  margin-bottom: 24px;
}

.section-title {
  font-size: 28px;
  color: $neon-cyan;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;

  .title-icon {
    font-size: 32px;
  }
}

// 个人简介卡片
.intro-content {
  .intro-text {
    color: $text-secondary;
    font-size: 16px;
    line-height: 1.8;
    margin-bottom: 20px;
    text-align: justify;
  }

  .highlights {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    margin-top: 32px;
    padding-top: 32px;
    border-top: 1px solid $border-secondary;

    .highlight-item {
      text-align: center;

      .highlight-number {
        display: block;
        font-size: 40px;
        font-weight: 700;
        color: $neon-cyan;
        text-shadow: $shadow-neon-cyan;
        margin-bottom: 8px;
      }

      .highlight-label {
        display: block;
        color: $text-secondary;
        font-size: 14px;
      }
    }
  }
}

// 核心技能卡片
.skills-content {
  .skill-category {
    margin-bottom: 28px;

    &:last-child {
      margin-bottom: 0;
    }

    .category-name {
      font-size: 18px;
      color: $neon-magenta;
      font-weight: 600;
      margin-bottom: 12px;
      padding-left: 12px;
      border-left: 3px solid $neon-magenta;
    }

    .tech-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      padding-left: 12px;
    }
  }
}

// 专业能力卡片
.abilities-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;

  .ability-item {
    padding: 24px;
    background: rgba($neon-cyan, 0.03);
    border: 1px solid rgba($neon-cyan, 0.1);
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      background: rgba($neon-cyan, 0.05);
      border-color: rgba($neon-cyan, 0.3);
      transform: translateY(-2px);
    }

    .ability-icon {
      font-size: 36px;
      margin-bottom: 12px;
    }

    .ability-title {
      font-size: 18px;
      color: $neon-cyan;
      font-weight: 600;
      margin-bottom: 12px;
    }

    .ability-desc {
      color: $text-secondary;
      font-size: 14px;
      line-height: 1.6;
    }
  }
}

// 项目经验卡片
.project-list {
  .project-item {
    padding: 24px;
    background: rgba($neon-purple, 0.03);
    border: 1px solid rgba($neon-purple, 0.1);
    border-radius: 8px;
    margin-bottom: 20px;
    transition: all 0.3s ease;

    &:last-child {
      margin-bottom: 0;
    }

    &:hover {
      background: rgba($neon-purple, 0.05);
      border-color: rgba($neon-purple, 0.3);
      transform: translateX(4px);
    }

    .project-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;

      .project-name {
        font-size: 20px;
        color: $neon-cyan;
        font-weight: 600;
      }

      .project-tag {
        padding: 4px 12px;
        background: rgba($neon-magenta, 0.2);
        color: $neon-magenta;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;
      }
    }

    .project-desc {
      color: $text-secondary;
      font-size: 15px;
      line-height: 1.7;
      margin-bottom: 16px;
    }

    .project-tech {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      span {
        padding: 4px 10px;
        background: rgba($neon-cyan, 0.1);
        color: $neon-cyan;
        border: 1px solid rgba($neon-cyan, 0.2);
        border-radius: 4px;
        font-size: 12px;
      }
    }
  }
}

// 两列布局区域
.two-column-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

// 教育背景卡片
.education-content {
  .education-item {
    .education-school {
      font-size: 20px;
      color: $neon-cyan;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .education-major {
      color: $text-secondary;
      font-size: 16px;
      margin-bottom: 6px;
    }

    .education-time {
      color: $text-disabled;
      font-size: 14px;
    }
  }
}

// 证书卡片
.certificates-content {
  .certificate-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      color: $text-secondary;
      font-size: 15px;
      padding: 10px 0;
      padding-left: 24px;
      position: relative;

      &::before {
        content: '🏅';
        position: absolute;
        left: 0;
        top: 10px;
      }

      &:not(:last-child) {
        border-bottom: 1px solid rgba($border-secondary, 0.3);
      }
    }
  }
}

// 联系方式卡片
.contact-content {
  .contact-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    .contact-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: rgba($neon-cyan, 0.03);
      border: 1px solid rgba($neon-cyan, 0.1);
      border-radius: 8px;
      text-decoration: none;
      transition: all 0.3s ease;

      &:hover {
        background: rgba($neon-cyan, 0.08);
        border-color: rgba($neon-cyan, 0.3);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba($neon-cyan, 0.2);
      }

      .contact-icon {
        font-size: 32px;
      }

      .contact-info {
        flex: 1;

        .contact-label {
          font-size: 12px;
          color: $text-disabled;
          margin-bottom: 4px;
        }

        .contact-value {
          font-size: 15px;
          color: $neon-cyan;
          font-weight: 500;
        }
      }
    }
  }

  .contact-footer {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid $border-secondary;

    p {
      color: $text-secondary;
      font-size: 16px;
      font-style: italic;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-title {
    font-size: 40px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .intro-content .highlights {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .abilities-grid {
    grid-template-columns: 1fr;
  }

  .two-column-section {
    grid-template-columns: 1fr;
  }

  .contact-grid {
    grid-template-columns: 1fr !important;
  }
}

// 故障动画
@keyframes glitch-anim {
  0% { clip-path: inset(40% 0 61% 0); }
  20% { clip-path: inset(92% 0 1% 0); }
  40% { clip-path: inset(43% 0 1% 0); }
  60% { clip-path: inset(25% 0 58% 0); }
  80% { clip-path: inset(54% 0 7% 0); }
  100% { clip-path: inset(58% 0 43% 0); }
}
</style>

