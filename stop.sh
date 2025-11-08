#!/bin/bash

# ================================
# SauryBlog Docker 停止脚本
# ================================

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# 停止服务
print_info "正在停止 SauryBlog 服务..."

if docker compose version &> /dev/null; then
    docker compose down
else
    docker-compose down
fi

print_success "服务已停止"

# 询问是否删除数据
echo ""
read -p "是否删除所有数据（包括数据库）？[y/N] " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_warning "正在删除数据..."
    
    if docker compose version &> /dev/null; then
        docker compose down -v
    else
        docker-compose down -v
    fi
    
    print_success "数据已删除"
else
    print_info "数据已保留，下次启动时将继续使用"
fi

echo ""
print_info "如需重新启动，请运行: ./deploy.sh"

