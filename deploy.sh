#!/bin/bash

# ================================
# SauryBlog Docker 一键部署脚本
# ================================

set -e  # 遇到错误立即退出

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的信息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 打印 Banner
print_banner() {
    echo -e "${BLUE}"
    echo "╔════════════════════════════════════════╗"
    echo "║     🚀 SauryBlog Docker 部署工具     ║"
    echo "║      Cyberpunk Style Blog System      ║"
    echo "╚════════════════════════════════════════╝"
    echo -e "${NC}"
}

# 检查 Docker 是否安装
check_docker() {
    print_info "检查 Docker 环境..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    print_success "Docker 环境检查通过"
}

# 检查 .env 文件
check_env() {
    print_info "检查环境配置文件..."
    
    if [ ! -f .env ]; then
        print_warning ".env 文件不存在，正在创建..."
        cp .env.example .env
        print_warning "请编辑 .env 文件并配置你的环境变量"
        print_warning "配置完成后，请重新运行此脚本"
        exit 1
    fi
    
    print_success "环境配置文件检查通过"
}

# 创建必要的目录
create_dirs() {
    print_info "创建必要的目录..."
    
    mkdir -p mysql/conf
    mkdir -p redis/conf
    mkdir -p logs
    
    print_success "目录创建完成"
}

# 停止并删除旧容器
cleanup_old() {
    print_info "清理旧的容器和镜像..."
    
    # 尝试停止容器（如果存在）
    docker-compose down 2>/dev/null || docker compose down 2>/dev/null || true
    
    print_success "清理完成"
}

# 构建并启动服务
start_services() {
    print_info "开始构建并启动服务（这可能需要几分钟）..."
    
    # 使用 docker compose 或 docker-compose
    if docker compose version &> /dev/null; then
        COMPOSE_CMD="docker compose"
    else
        COMPOSE_CMD="docker-compose"
    fi
    
    print_info "构建镜像..."
    $COMPOSE_CMD build --no-cache
    
    print_info "启动服务..."
    $COMPOSE_CMD up -d
    
    print_success "服务启动完成"
}

# 等待服务就绪
wait_for_services() {
    print_info "等待服务启动..."
    
    # 等待 MySQL
    print_info "等待 MySQL 启动..."
    for i in {1..30}; do
        if docker exec saury-mysql mysqladmin ping -h localhost -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent 2>/dev/null; then
            print_success "MySQL 已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "MySQL 启动超时"
            exit 1
        fi
        echo -n "."
        sleep 2
    done
    
    # 等待 Redis
    print_info "等待 Redis 启动..."
    sleep 5
    print_success "Redis 已就绪"
    
    # 等待后端
    print_info "等待后端服务启动（可能需要 1-2 分钟）..."
    for i in {1..60}; do
        if curl -s http://localhost:${BACKEND_PORT:-8088}/api/actuator/health > /dev/null 2>&1; then
            print_success "后端服务已就绪"
            break
        fi
        if [ $i -eq 60 ]; then
            print_warning "后端服务启动超时，请检查日志"
        fi
        echo -n "."
        sleep 3
    done
}

# 显示服务状态
show_status() {
    print_info "服务状态："
    echo ""
    
    if docker compose version &> /dev/null; then
        docker compose ps
    else
        docker-compose ps
    fi
    
    echo ""
    print_success "=========================================="
    print_success "部署完成！🎉"
    print_success "=========================================="
    echo ""
    print_info "访问地址："
    print_info "  前端页面: http://localhost:${FRONTEND_PORT:-80}"
    print_info "  后端 API: http://localhost:${BACKEND_PORT:-8088}/api"
    echo ""
    print_info "默认账号："
    print_info "  用户名: admin"
    print_info "  密码: 123456"
    echo ""
    print_warning "⚠️  请及时修改默认密码！"
    echo ""
    print_info "常用命令："
    print_info "  查看日志: docker-compose logs -f [服务名]"
    print_info "  停止服务: docker-compose down"
    print_info "  重启服务: docker-compose restart"
    print_info "  进入容器: docker exec -it [容器名] sh"
    echo ""
}

# 主函数
main() {
    print_banner
    
    # 加载 .env 文件
    if [ -f .env ]; then
        set -a
        source .env
        set +a
    fi
    
    check_docker
    check_env
    create_dirs
    cleanup_old
    start_services
    wait_for_services
    show_status
}

# 运行主函数
main

