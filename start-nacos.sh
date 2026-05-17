#!/bin/bash

# ============================================================================
# Nacos 启动脚本
# ============================================================================
# 使用方法:
#   ./start-nacos.sh                    # 启动 Nacos（后台运行）
#   ./start-nacos.sh -f                 # 启动 Nacos（显示日志）
#   ./start-nacos.sh status             # 查看 Nacos 状态
#   ./start-nacos.sh stop               # 停止 Nacos
#   ./start-nacos.sh logs               # 查看 Nacos 日志
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Nacos 配置
NACOS_HOME="$HOME/nacos"
NACOS_BIN="$NACOS_HOME/bin"
NACOS_PORT="8848"
NACOS_STARTUP_SCRIPT="$NACOS_BIN/startup.sh"
NACOS_SHUTDOWN_SCRIPT="$NACOS_BIN/shutdown.sh"
LOG_FILE="logs/nacos-startup.log"

# 检查 Nacos 是否运行
is_nacos_running() {
    # 检查端口是否被占用
    if lsof -ti:$NACOS_PORT > /dev/null 2>&1; then
        return 0
    fi
    
    # 检查 Nacos 进程是否存在
    if ps aux | grep -v grep | grep -q "[n]acos-server.jar"; then
        return 0
    fi
    
    # 检查 Nacos Web 界面是否可访问
    if curl -s http://localhost:$NACOS_PORT/nacos/ > /dev/null 2>&1; then
        return 0
    fi
    
    return 1
}

# 获取 Nacos 进程 ID
get_nacos_pid() {
    # 优先通过端口查找
    local pid=$(lsof -ti:$NACOS_PORT 2>/dev/null | head -1)
    if [ -n "$pid" ]; then
        echo "$pid"
        return 0
    fi
    
    # 通过进程名查找
    pid=$(ps aux | grep "[n]acos-server.jar" | awk '{print $2}' | head -1)
    if [ -n "$pid" ]; then
        echo "$pid"
        return 0
    fi
    
    return 1
}

# 启动 Nacos
start_nacos() {
    local show_logs=${1:-false}  # 是否显示日志
    
    # 检查 Nacos 是否已运行
    if is_nacos_running; then
        local pid=$(get_nacos_pid)
        echo -e "${YELLOW}⚠️  Nacos 已在运行 (端口: $NACOS_PORT)${NC}"
        if [ -n "$pid" ]; then
            echo -e "   进程 ID: ${BLUE}$pid${NC}"
        fi
        echo -e "   控制台地址: ${BLUE}http://localhost:$NACOS_PORT/nacos${NC}"
        echo -e "   默认账号: ${BLUE}nacos / nacos${NC}"
        echo -e "   查看日志: ${BLUE}tail -f $NACOS_HOME/logs/start.out${NC}"
        return 0
    fi
    
    # 检查 Nacos 目录是否存在
    if [ ! -d "$NACOS_BIN" ]; then
        echo -e "${RED}❌ 错误: Nacos 目录不存在: $NACOS_BIN${NC}"
        echo -e "${YELLOW}   请确保 Nacos 已安装在: $NACOS_HOME${NC}"
        echo ""
        echo -e "${BLUE}   安装步骤:${NC}"
        echo "   1. 访问 https://github.com/alibaba/nacos/releases"
        echo "   2. 下载最新版本的 nacos-server-*.tar.gz"
        echo "   3. 解压到 ~/nacos 目录"
        echo "   4. 确保 ~/nacos/bin/startup.sh 文件存在"
        exit 1
    fi
    
    # 检查启动脚本是否存在
    if [ ! -f "$NACOS_STARTUP_SCRIPT" ]; then
        echo -e "${RED}❌ 错误: Nacos 启动脚本不存在: $NACOS_STARTUP_SCRIPT${NC}"
        exit 1
    fi
    
    echo -e "${BLUE}🚀 启动 Nacos...${NC}"
    echo -e "   Nacos 路径: $NACOS_HOME"
    echo -e "   启动脚本: $NACOS_STARTUP_SCRIPT"
    echo -e "   端口: $NACOS_PORT"
    echo ""
    
    # 确保日志目录存在
    mkdir -p "$SCRIPT_DIR/logs"
    
    # 切换到 Nacos bin 目录
    cd "$NACOS_BIN"
    
    # 启动 Nacos（单机模式）
    # 使用 nohup 在后台运行,并将输出重定向到日志文件
    nohup bash startup.sh -m standalone > "$SCRIPT_DIR/$LOG_FILE" 2>&1 &
    local startup_pid=$!
    
    cd "$SCRIPT_DIR"
    
    echo -e "${BLUE}   启动进程 ID: $startup_pid${NC}"
    echo -e "${BLUE}   启动日志: $SCRIPT_DIR/$LOG_FILE${NC}"
    echo -e "${BLUE}   Nacos 日志: $NACOS_HOME/logs/start.out${NC}"
    echo -e "${BLUE}   等待 Nacos 启动（最多 60 秒）...${NC}"
    echo ""
    
    # 如果要求显示日志,则实时显示
    if [ "$show_logs" = "true" ]; then
        echo -e "${BLUE}📋 实时日志（按 Ctrl+C 停止查看日志,Nacos 将继续运行）:${NC}"
        echo "----------------------------------------------------------------"
        # 等待日志文件创建
        sleep 3
        # 实时显示日志,但 Nacos 继续在后台运行
        tail -f "$SCRIPT_DIR/$LOG_FILE" &
        local tail_pid=$!
    fi
    
    # 等待 Nacos 启动
    local max_wait=60
    local waited=0
    local check_interval=3
    
    while [ $waited -lt $max_wait ]; do
        if is_nacos_running; then
            # 如果正在显示日志,停止 tail
            if [ "$show_logs" = "true" ] && [ -n "$tail_pid" ]; then
                kill $tail_pid 2>/dev/null
            fi
            echo ""
            local pid=$(get_nacos_pid)
            echo -e "${GREEN}✅ Nacos 启动成功！${NC}"
            echo -e "   端口: ${BLUE}$NACOS_PORT${NC}"
            if [ -n "$pid" ]; then
                echo -e "   进程 ID: ${BLUE}$pid${NC}"
            fi
            echo -e "   控制台地址: ${BLUE}http://localhost:$NACOS_PORT/nacos${NC}"
            echo -e "   默认账号: ${BLUE}nacos / nacos${NC}"
            echo -e "   启动日志: ${BLUE}$SCRIPT_DIR/$LOG_FILE${NC}"
            echo -e "   Nacos 日志: ${BLUE}$NACOS_HOME/logs/start.out${NC}"
            echo -e "   查看实时日志: ${BLUE}tail -f $NACOS_HOME/logs/start.out${NC}"
            return 0
        fi
        
        sleep $check_interval
        waited=$((waited + check_interval))
        
        # 显示进度
        if [ $((waited % 9)) -eq 0 ]; then
            echo -e "${BLUE}   已等待 ${waited} 秒...${NC}"
        fi
    done
    
    # 如果正在显示日志,停止 tail
    if [ "$show_logs" = "true" ] && [ -n "$tail_pid" ]; then
        kill $tail_pid 2>/dev/null
    fi
    
    echo ""
    echo -e "${YELLOW}⚠️  Nacos 启动超时（已等待 ${max_wait} 秒）${NC}"
    echo -e "   请检查日志: ${BLUE}$SCRIPT_DIR/$LOG_FILE${NC}"
    echo ""
    echo -e "${BLUE}   最后 30 行启动日志:${NC}"
    echo "----------------------------------------------------------------"
    tail -30 "$SCRIPT_DIR/$LOG_FILE" 2>/dev/null || echo "   日志文件不存在或为空"
    echo "----------------------------------------------------------------"
    
    if [ -f "$NACOS_HOME/logs/start.out" ]; then
        echo ""
        echo -e "${BLUE}   最后 30 行 Nacos 日志:${NC}"
        echo "----------------------------------------------------------------"
        tail -30 "$NACOS_HOME/logs/start.out" 2>/dev/null || echo "   Nacos 日志文件不存在或为空"
        echo "----------------------------------------------------------------"
    fi
    
    return 1
}

# 停止 Nacos
stop_nacos() {
    if ! is_nacos_running; then
        echo -e "${YELLOW}⚠️  Nacos 未运行${NC}"
        return 0
    fi
    
    echo -e "${BLUE}🛑 停止 Nacos...${NC}"
    
    # 优先使用 shutdown.sh 脚本停止
    if [ -f "$NACOS_SHUTDOWN_SCRIPT" ]; then
        echo -e "${BLUE}   使用 shutdown.sh 脚本停止...${NC}"
        cd "$NACOS_BIN"
        bash shutdown.sh
        cd "$SCRIPT_DIR"
        sleep 3
    fi
    
    # 如果还在运行,通过进程 ID 停止
    if is_nacos_running; then
        local pid=$(get_nacos_pid)
        if [ -n "$pid" ]; then
            echo -e "${BLUE}   终止进程: $pid${NC}"
            kill -9 $pid 2>/dev/null
            sleep 2
        fi
        
        # 如果端口仍被占用,通过端口停止
        local port_pids=$(lsof -ti:$NACOS_PORT 2>/dev/null)
        if [ -n "$port_pids" ]; then
            for pid in $port_pids; do
                echo -e "${BLUE}   终止占用端口的进程: $pid${NC}"
                kill -9 $pid 2>/dev/null
            done
            sleep 2
        fi
    fi
    
    if is_nacos_running; then
        echo -e "${RED}❌ Nacos 停止失败${NC}"
        return 1
    else
        echo -e "${GREEN}✅ Nacos 已停止${NC}"
        return 0
    fi
}

# 显示 Nacos 状态
show_status() {
    echo -e "${BLUE}📊 Nacos 状态:${NC}"
    echo ""
    
    if is_nacos_running; then
        local pid=$(get_nacos_pid)
        echo -e "   服务名: ${GREEN}Nacos${NC}"
        echo -e "   端口: ${GREEN}$NACOS_PORT${NC}"
        echo -e "   状态: ${GREEN}运行中${NC}"
        if [ -n "$pid" ]; then
            echo -e "   进程 ID: ${GREEN}$pid${NC}"
        fi
        echo ""
        echo -e "${BLUE}   访问地址:${NC}"
        echo -e "   - 控制台: ${BLUE}http://localhost:$NACOS_PORT/nacos${NC}"
        echo -e "   - 默认账号: ${BLUE}nacos / nacos${NC}"
        echo ""
        echo -e "${BLUE}   日志文件:${NC}"
        echo -e "   - 启动日志: ${BLUE}$SCRIPT_DIR/$LOG_FILE${NC}"
        echo -e "   - Nacos 日志: ${BLUE}$NACOS_HOME/logs/start.out${NC}"
        echo ""
        
        # 检查 Nacos API 是否可访问
        echo -e "${BLUE}   健康检查:${NC}"
        local health=$(curl -s http://localhost:$NACOS_PORT/nacos/v1/console/health 2>/dev/null)
        if [ -n "$health" ]; then
            echo -e "   ${GREEN}API 可访问${NC}"
        else
            echo -e "   ${YELLOW}API 检查失败（可能仍在启动中）${NC}"
        fi
    else
        echo -e "   服务名: Nacos"
        echo -e "   端口: $NACOS_PORT"
        echo -e "   状态: ${RED}未运行${NC}"
        echo ""
        echo -e "${YELLOW}   使用以下命令启动:${NC}"
        echo -e "   ${BLUE}./start-nacos.sh${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}📋 Nacos 配置信息:${NC}"
    echo -e "   - Nacos 路径: $NACOS_HOME"
    echo -e "   - 启动脚本: $NACOS_STARTUP_SCRIPT"
    if [ -f "$NACOS_STARTUP_SCRIPT" ]; then
        echo -e "   - 启动脚本状态: ${GREEN}存在${NC}"
    else
        echo -e "   - 启动脚本状态: ${RED}不存在${NC}"
    fi
}

# 查看日志
show_logs() {
    echo -e "${BLUE}📋 Nacos 日志:${NC}"
    echo ""
    
    if [ -f "$NACOS_HOME/logs/start.out" ]; then
        echo -e "${BLUE}   实时日志（按 Ctrl+C 退出）:${NC}"
        echo -e "   日志文件: $NACOS_HOME/logs/start.out"
        echo "----------------------------------------------------------------"
        tail -f "$NACOS_HOME/logs/start.out"
    elif [ -f "$SCRIPT_DIR/$LOG_FILE" ]; then
        echo -e "${BLUE}   启动日志（按 Ctrl+C 退出）:${NC}"
        echo -e "   日志文件: $SCRIPT_DIR/$LOG_FILE"
        echo "----------------------------------------------------------------"
        tail -f "$SCRIPT_DIR/$LOG_FILE"
    else
        echo -e "${YELLOW}⚠️  未找到日志文件${NC}"
        echo -e "   尝试查找的路径:"
        echo -e "   - $NACOS_HOME/logs/start.out"
        echo -e "   - $SCRIPT_DIR/$LOG_FILE"
        echo ""
        echo -e "${BLUE}   如果 Nacos 正在运行,日志文件应该在:${NC}"
        echo -e "   ${BLUE}$NACOS_HOME/logs/start.out${NC}"
    fi
}

# 显示帮助信息
show_help() {
    echo -e "${BLUE}📋 Nacos 启动脚本${NC}"
    echo ""
    echo -e "${BLUE}使用方法:${NC}"
    echo "  ./start-nacos.sh                    # 启动 Nacos（后台运行）"
    echo "  ./start-nacos.sh -f                 # 启动 Nacos（显示日志）"
    echo "  ./start-nacos.sh status             # 查看 Nacos 状态"
    echo "  ./start-nacos.sh stop               # 停止 Nacos"
    echo "  ./start-nacos.sh logs               # 查看 Nacos 日志"
    echo ""
    echo -e "${BLUE}说明:${NC}"
    echo "  - Nacos 路径: $NACOS_HOME"
    echo "  - 默认端口: $NACOS_PORT"
    echo "  - 默认账号: nacos / nacos"
    echo "  - 默认后台运行,日志保存到 logs/ 目录"
    echo "  - 使用 -f 参数可以实时查看启动日志"
    echo ""
    echo -e "${BLUE}控制台访问:${NC}"
    echo "  - 地址: http://localhost:$NACOS_PORT/nacos"
    echo "  - 账号: nacos"
    echo "  - 密码: nacos"
}

# 主逻辑
main() {
    # 创建日志目录
    mkdir -p logs
    
    case "$1" in
        "")
            # 没有参数,启动 Nacos（后台运行）
            start_nacos false
            ;;
        "-f"|"--follow")
            # 显示日志模式
            start_nacos true
            ;;
        "status")
            show_status
            ;;
        "stop")
            stop_nacos
            ;;
        "logs")
            show_logs
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            echo -e "${RED}❌ 未知参数: $1${NC}"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

main "$@"









