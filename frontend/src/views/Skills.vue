<template>
  <div class="skills-page">
    <div class="page-header">
      <h1 class="page-title glitch" data-text="SKILL TREE">SKILL TREE</h1>
      <p class="page-subtitle">技术栈 & 能力矩阵</p>
    </div>

    <!-- 技能概览统计 -->
    <div class="skills-stats">
      <div class="stat-item">
        <div class="stat-icon">💻</div>
        <div class="stat-info">
          <div class="stat-number">{{ totalSkills }}</div>
          <div class="stat-label">掌握技能</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon">🏆</div>
        <div class="stat-info">
          <div class="stat-number">{{ expertSkills }}</div>
          <div class="stat-label">精通技能</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <div class="stat-number">{{ learningSkills }}</div>
          <div class="stat-label">学习中</div>
        </div>
      </div>
    </div>

    <!-- 技能分类展示 -->
    <div class="skills-categories">
      <CyberCard 
        v-for="category in skillCategories" 
        :key="category.name" 
        class="category-card"
      >
        <div class="category-header">
          <div class="category-title">
            <span class="category-icon">{{ category.icon }}</span>
            <h2>{{ category.name }}</h2>
          </div>
          <div class="category-badge">{{ category.skills.length }} 项</div>
        </div>
        
        <div class="skills-list">
          <div 
            v-for="skill in category.skills" 
            :key="skill.name"
            class="skill-item"
          >
            <div class="skill-info">
              <div class="skill-name-row">
                <span class="skill-name">{{ skill.name }}</span>
                <span :class="['skill-level-badge', `level-${skill.levelLabel}`]">
                  {{ skill.levelLabel }}
                </span>
              </div>
              <div class="skill-description">{{ skill.description }}</div>
            </div>
            
            <div class="skill-progress">
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ 
                    width: `${skill.level}%`,
                    background: skill.color || category.color
                  }"
                >
                  <span class="progress-percentage">{{ skill.level }}%</span>
                </div>
              </div>
            </div>

            <div v-if="skill.tags" class="skill-tags">
              <span v-for="tag in skill.tags" :key="tag" class="skill-tag">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </CyberCard>
    </div>

    <!-- 学习路线图 -->
    <CyberCard class="learning-roadmap">
      <div class="roadmap-header">
        <h2 class="section-title">
          <span class="title-icon">🎯</span>
          学习路线图
        </h2>
      </div>
      
      <div class="roadmap-timeline">
        <div 
          v-for="(phase, index) in learningRoadmap" 
          :key="index"
          class="timeline-item"
        >
          <div class="timeline-marker">
            <span class="marker-number">{{ index + 1 }}</span>
          </div>
          <div class="timeline-content">
            <h3 class="phase-title">{{ phase.title }}</h3>
            <p class="phase-desc">{{ phase.description }}</p>
            <div class="phase-skills">
              <span v-for="skill in phase.skills" :key="skill" class="phase-skill">
                {{ skill }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </CyberCard>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import CyberCard from '@/components/CyberCard.vue'

// 技能分类数据
const skillCategories = ref([
  {
    name: '后端开发',
    icon: '⚙️',
    color: 'linear-gradient(135deg, #00F0FF 0%, #0080FF 100%)',
    skills: [
      { 
        name: 'Java', 
        level: 90, 
        levelLabel: '精通',
        description: '核心编程语言，深入理解 JVM、并发编程、设计模式',
        tags: ['JDK17+', '并发编程', 'JVM调优'],
        color: 'linear-gradient(135deg, #00F0FF 0%, #00A0FF 100%)'
      },
      { 
        name: 'Spring Boot', 
        level: 88, 
        levelLabel: '精通',
        description: '企业级应用开发框架，熟悉自动配置、starter 开发',
        tags: ['Spring Boot 3', 'AOP', '自动配置'],
        color: 'linear-gradient(135deg, #00F0FF 0%, #00A0FF 100%)'
      },
      { 
        name: 'Spring Cloud', 
        level: 82, 
        levelLabel: '熟练',
        description: '微服务架构，熟悉服务注册、配置中心、网关',
        tags: ['Nacos', 'Gateway', 'Feign'],
        color: 'linear-gradient(135deg, #00D0FF 0%, #0090FF 100%)'
      },
      { 
        name: 'MyBatis Plus', 
        level: 85, 
        levelLabel: '精通',
        description: 'ORM 框架，精通 SQL 优化、动态 SQL、分页插件',
        tags: ['代码生成', 'SQL优化', '分页'],
        color: 'linear-gradient(135deg, #00D0FF 0%, #0090FF 100%)'
      },
      { 
        name: 'Spring AI', 
        level: 70, 
        levelLabel: '熟悉',
        description: 'AI 集成框架，探索 LLM 应用开发',
        tags: ['LLM', 'RAG', 'Vector DB'],
        color: 'linear-gradient(135deg, #00C0FF 0%, #0080FF 100%)'
      }
    ]
  },
  {
    name: '数据库 & 缓存',
    icon: '💾',
    color: 'linear-gradient(135deg, #A200FF 0%, #FF00FF 100%)',
    skills: [
      { 
        name: 'MySQL', 
        level: 85, 
        levelLabel: '精通',
        description: '关系型数据库，熟悉索引优化、事务处理、主从复制',
        tags: ['索引优化', '事务', '主从复制'],
        color: 'linear-gradient(135deg, #A200FF 0%, #C000FF 100%)'
      },
      { 
        name: 'Redis', 
        level: 83, 
        levelLabel: '精通',
        description: '缓存与消息中间件，熟悉缓存策略、分布式锁',
        tags: ['缓存策略', '分布式锁', 'Pub/Sub'],
        color: 'linear-gradient(135deg, #A200FF 0%, #C000FF 100%)'
      },
      { 
        name: 'MongoDB', 
        level: 72, 
        levelLabel: '熟悉',
        description: 'NoSQL 文档数据库，适用于非结构化数据存储',
        tags: ['文档存储', '聚合查询'],
        color: 'linear-gradient(135deg, #9000FF 0%, #B000FF 100%)'
      },
      { 
        name: 'Elasticsearch', 
        level: 75, 
        levelLabel: '熟悉',
        description: '搜索引擎，熟悉全文检索、数据分析',
        tags: ['全文检索', '聚合分析', 'ELK'],
        color: 'linear-gradient(135deg, #9000FF 0%, #B000FF 100%)'
      }
    ]
  },
  {
    name: '中间件 & DevOps',
    icon: '🚀',
    color: 'linear-gradient(135deg, #FF66B2 0%, #FF0080 100%)',
    skills: [
      { 
        name: 'Docker', 
        level: 80, 
        levelLabel: '熟练',
        description: '容器化技术，熟悉镜像构建、容器编排',
        tags: ['容器化', 'Docker Compose'],
        color: 'linear-gradient(135deg, #FF66B2 0%, #FF3399 100%)'
      },
      { 
        name: 'RabbitMQ', 
        level: 78, 
        levelLabel: '熟练',
        description: '消息队列，熟悉消息可靠性、延迟队列',
        tags: ['消息队列', '死信队列'],
        color: 'linear-gradient(135deg, #FF66B2 0%, #FF3399 100%)'
      },
      { 
        name: 'Kafka', 
        level: 70, 
        levelLabel: '熟悉',
        description: '分布式流处理平台，高吞吐量消息系统',
        tags: ['流处理', '高吞吐'],
        color: 'linear-gradient(135deg, #FF4499 0%, #FF1177 100%)'
      },
      { 
        name: 'Nginx', 
        level: 76, 
        levelLabel: '熟练',
        description: 'Web 服务器与反向代理，负载均衡配置',
        tags: ['反向代理', '负载均衡'],
        color: 'linear-gradient(135deg, #FF4499 0%, #FF1177 100%)'
      },
      { 
        name: 'Git', 
        level: 88, 
        levelLabel: '精通',
        description: '版本控制系统，熟悉分支管理、代码合并策略',
        tags: ['版本控制', 'Git Flow'],
        color: 'linear-gradient(135deg, #FF66B2 0%, #FF3399 100%)'
      }
    ]
  },
  {
    name: '前端技术',
    icon: '🎨',
    color: 'linear-gradient(135deg, #00FF9F 0%, #00D080 100%)',
    skills: [
      { 
        name: 'Vue.js 3', 
        level: 82, 
        levelLabel: '熟练',
        description: '现代前端框架，Composition API、响应式系统',
        tags: ['Composition API', 'Pinia', 'Vue Router'],
        color: 'linear-gradient(135deg, #00FF9F 0%, #00DD88 100%)'
      },
      { 
        name: 'TypeScript', 
        level: 80, 
        levelLabel: '熟练',
        description: '类型安全的 JavaScript 超集，提升代码质量',
        tags: ['类型系统', '接口定义'],
        color: 'linear-gradient(135deg, #00FF9F 0%, #00DD88 100%)'
      },
      { 
        name: 'Element Plus', 
        level: 85, 
        levelLabel: '精通',
        description: 'Vue 3 组件库，快速构建企业级应用',
        tags: ['UI组件', '主题定制'],
        color: 'linear-gradient(135deg, #00FF9F 0%, #00DD88 100%)'
      },
      { 
        name: 'Vite', 
        level: 78, 
        levelLabel: '熟练',
        description: '下一代前端构建工具，快速的开发体验',
        tags: ['构建工具', 'HMR'],
        color: 'linear-gradient(135deg, #00EE90 0%, #00CC70 100%)'
      },
      { 
        name: 'HTML/CSS/SCSS', 
        level: 83, 
        levelLabel: '熟练',
        description: '前端基础，响应式设计、CSS 预处理器',
        tags: ['响应式', 'Flexbox', 'Grid'],
        color: 'linear-gradient(135deg, #00EE90 0%, #00CC70 100%)'
      }
    ]
  },
  {
    name: '其他技能',
    icon: '🛠️',
    color: 'linear-gradient(135deg, #FFED00 0%, #FFB000 100%)',
    skills: [
      { 
        name: '算法与数据结构', 
        level: 75, 
        levelLabel: '熟悉',
        description: '扎实的计算机基础，常用算法实现',
        tags: ['排序', '查找', '动态规划'],
        color: 'linear-gradient(135deg, #FFED00 0%, #FFC000 100%)'
      },
      { 
        name: '设计模式', 
        level: 80, 
        levelLabel: '熟练',
        description: '23 种设计模式，提升代码可维护性',
        tags: ['单例', '工厂', '代理'],
        color: 'linear-gradient(135deg, #FFED00 0%, #FFC000 100%)'
      },
      { 
        name: 'Linux', 
        level: 76, 
        levelLabel: '熟练',
        description: '服务器运维，Shell 脚本编写',
        tags: ['命令行', 'Shell', '权限管理'],
        color: 'linear-gradient(135deg, #FFED00 0%, #FFC000 100%)'
      },
      { 
        name: 'RESTful API', 
        level: 88, 
        levelLabel: '精通',
        description: 'API 设计规范，接口文档编写',
        tags: ['接口设计', 'Swagger', 'Postman'],
        color: 'linear-gradient(135deg, #FFE000 0%, #FFB000 100%)'
      }
    ]
  }
])

// 学习路线图
const learningRoadmap = ref([
  {
    title: '✅ 已完成 - Java 基础',
    description: '掌握 Java 核心语法、面向对象编程、集合框架',
    skills: ['Java SE', 'OOP', '集合框架', '多线程']
  },
  {
    title: '✅ 已完成 - Spring 生态',
    description: '深入学习 Spring Boot、Spring MVC、Spring Data',
    skills: ['Spring Boot', 'Spring MVC', 'Spring Data', 'AOP']
  },
  {
    title: '🔄 进行中 - 微服务架构',
    description: '学习 Spring Cloud、分布式系统设计',
    skills: ['Spring Cloud', 'Nacos', 'Gateway', 'Sentinel']
  },
  {
    title: '📝 计划中 - AI 应用开发',
    description: '探索 AI 技术在实际项目中的应用',
    skills: ['Spring AI', 'LangChain', 'Vector Database', 'RAG']
  },
  {
    title: '📝 计划中 - 云原生技术',
    description: '学习 Kubernetes、Service Mesh 等云原生技术',
    skills: ['Kubernetes', 'Istio', 'Helm', 'CI/CD']
  }
])

// 计算技能统计
const totalSkills = computed(() => {
  return skillCategories.value.reduce((sum, cat) => sum + cat.skills.length, 0)
})

const expertSkills = computed(() => {
  let count = 0
  skillCategories.value.forEach(cat => {
    count += cat.skills.filter(s => s.level >= 85).length
  })
  return count
})

const learningSkills = computed(() => {
  return learningRoadmap.value.filter(r => r.title.includes('进行中') || r.title.includes('计划中')).length
})
</script>

<style lang="scss" scoped>
.skills-page {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}

// 页面头部
.page-header {
  text-align: center;
  margin-bottom: 48px;
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

// 技能统计
.skills-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 48px;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 24px;
    background: rgba($neon-cyan, 0.05);
    border: 1px solid rgba($neon-cyan, 0.2);
    border-radius: 12px;
    transition: all 0.3s ease;

    &:hover {
      background: rgba($neon-cyan, 0.08);
      border-color: rgba($neon-cyan, 0.4);
      transform: translateY(-4px);
      box-shadow: 0 8px 20px rgba($neon-cyan, 0.2);
    }

    .stat-icon {
      font-size: 48px;
    }

    .stat-info {
      flex: 1;

      .stat-number {
        font-size: 36px;
        font-weight: 700;
        color: $neon-cyan;
        line-height: 1;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
}

// 技能分类
.skills-categories {
  display: grid;
  gap: 24px;
  margin-bottom: 32px;
}

.category-card {
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba($neon-cyan, 0.15);
  }
}

.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid rgba($neon-cyan, 0.2);

  .category-title {
    display: flex;
    align-items: center;
    gap: 12px;

    .category-icon {
      font-size: 32px;
    }

    h2 {
      font-size: 24px;
      color: $neon-cyan;
      font-weight: 600;
      margin: 0;
    }
  }

  .category-badge {
    padding: 6px 16px;
    background: rgba($neon-cyan, 0.15);
    color: $neon-cyan;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 600;
  }
}

.skills-list {
  display: grid;
  gap: 20px;
}

.skill-item {
  padding: 20px;
  background: rgba($bg-secondary, 0.5);
  border: 1px solid rgba($neon-cyan, 0.1);
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: rgba($bg-secondary, 0.8);
    border-color: rgba($neon-cyan, 0.3);
    transform: translateX(4px);
  }
}

.skill-info {
  margin-bottom: 12px;

  .skill-name-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;

    .skill-name {
      font-size: 18px;
      font-weight: 600;
      color: $text-primary;
    }

    .skill-level-badge {
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;

      &.level-精通 {
        background: rgba($neon-cyan, 0.2);
        color: $neon-cyan;
        border: 1px solid rgba($neon-cyan, 0.4);
      }

      &.level-熟练 {
        background: rgba($neon-purple, 0.2);
        color: $neon-purple;
        border: 1px solid rgba($neon-purple, 0.4);
      }

      &.level-熟悉 {
        background: rgba($neon-green, 0.2);
        color: $neon-green;
        border: 1px solid rgba($neon-green, 0.4);
      }
    }
  }

  .skill-description {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.6;
  }
}

.skill-progress {
  margin-bottom: 12px;

  .progress-bar {
    height: 28px;
    background: rgba(0, 0, 0, 0.3);
    border-radius: 14px;
    overflow: hidden;
    position: relative;
    border: 1px solid rgba($neon-cyan, 0.2);

    .progress-fill {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      padding: 0 12px;
      border-radius: 14px;
      transition: width 1s ease;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(90deg, 
          transparent 0%, 
          rgba(255, 255, 255, 0.1) 50%, 
          transparent 100%);
        animation: shimmer 2s infinite;
      }

      .progress-percentage {
        font-size: 12px;
        font-weight: 700;
        color: white;
        text-shadow: 0 0 4px rgba(0, 0, 0, 0.5);
        position: relative;
        z-index: 1;
      }
    }
  }
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .skill-tag {
    padding: 4px 10px;
    background: rgba($neon-cyan, 0.08);
    color: $text-secondary;
    border: 1px solid rgba($neon-cyan, 0.15);
    border-radius: 6px;
    font-size: 12px;
    transition: all 0.2s ease;

    &:hover {
      background: rgba($neon-cyan, 0.15);
      border-color: rgba($neon-cyan, 0.3);
      color: $neon-cyan;
    }
  }
}

// 学习路线图
.learning-roadmap {
  margin-top: 32px;
}

.roadmap-header {
  margin-bottom: 32px;

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
}

.roadmap-timeline {
  position: relative;
  padding-left: 40px;

  &::before {
    content: '';
    position: absolute;
    left: 15px;
    top: 0;
    bottom: 0;
    width: 2px;
    background: linear-gradient(180deg, 
      $neon-cyan 0%, 
      $neon-purple 50%, 
      $neon-magenta 100%);
  }

  .timeline-item {
    position: relative;
    margin-bottom: 32px;

    &:last-child {
      margin-bottom: 0;
    }

    .timeline-marker {
      position: absolute;
      left: -40px;
      top: 0;
      width: 32px;
      height: 32px;
      background: $bg-primary;
      border: 3px solid $neon-cyan;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 0 12px rgba($neon-cyan, 0.6);

      .marker-number {
        font-size: 14px;
        font-weight: 700;
        color: $neon-cyan;
      }
    }

    .timeline-content {
      padding: 20px;
      background: rgba($bg-secondary, 0.5);
      border: 1px solid rgba($neon-cyan, 0.2);
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        background: rgba($bg-secondary, 0.8);
        border-color: rgba($neon-cyan, 0.4);
        transform: translateX(4px);
      }

      .phase-title {
        font-size: 18px;
        font-weight: 600;
        color: $neon-cyan;
        margin-bottom: 8px;
      }

      .phase-desc {
        font-size: 14px;
        color: $text-secondary;
        line-height: 1.6;
        margin-bottom: 12px;
      }

      .phase-skills {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .phase-skill {
          padding: 4px 10px;
          background: rgba($neon-purple, 0.1);
          color: $neon-purple;
          border: 1px solid rgba($neon-purple, 0.3);
          border-radius: 6px;
          font-size: 12px;
          font-weight: 500;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-title {
    font-size: 40px;
  }

  .skills-stats {
    grid-template-columns: 1fr;
  }

  .stat-item .stat-icon {
    font-size: 36px;
  }

  .stat-item .stat-info .stat-number {
    font-size: 28px;
  }
}

// 动画
@keyframes glitch-anim {
  0% { clip-path: inset(40% 0 61% 0); }
  20% { clip-path: inset(92% 0 1% 0); }
  40% { clip-path: inset(43% 0 1% 0); }
  60% { clip-path: inset(25% 0 58% 0); }
  80% { clip-path: inset(54% 0 7% 0); }
  100% { clip-path: inset(58% 0 43% 0); }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style>

