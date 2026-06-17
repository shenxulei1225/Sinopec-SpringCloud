#!/bin/bash

# ============================================================================
# ZHGL 微服务启动脚本
# ============================================================================
# 使用方法:
#   ./start-microservices.sh                    # 显示所有可用服务
#   ./start-microservices.sh all                # 启动所有核心服务
#   ./start-microservices.sh <服务名>            # 启动单个服务
#   ./start-microservices.sh status             # 查看服务状态
#   ./start-microservices.sh stop <服务名>       # 停止服务
#   ./start-microservices.sh stop-all           # 停止所有服务
#   ./start-microservices.sh nacos              # 启动 Nacos
#   ./start-microservices.sh nacos status       # 查看 Nacos 状态
#   ./start-microservices.sh nacos stop         # 停止 Nacos
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 设置 Java 17 环境
if [ -f "setup-java17.sh" ]; then
    source setup-java17.sh 2>/dev/null
fi

# Nacos 配置（Mac 默认 ~/nacos，可通过 NACOS_HOME 环境变量覆盖）
NACOS_HOME="${NACOS_HOME:-$HOME/nacos}"
NACOS_PORT="8848"

# 检查 Nacos 是否运行，未运行则自动启动
check_nacos() {
    if curl -s "http://localhost:${NACOS_PORT}/nacos/" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Nacos 运行正常${NC} (${BLUE}${NACOS_HOME}${NC})"
        return 0
    fi

    echo -e "${YELLOW}⚠️  警告: Nacos 未运行${NC}"
    echo -e "${BLUE}   Nacos 路径: ${NACOS_HOME}${NC}"
    echo -e "${BLUE}   正在尝试启动 Nacos...${NC}"

    if [ -f "$SCRIPT_DIR/start-nacos.sh" ]; then
        bash "$SCRIPT_DIR/start-nacos.sh" || exit 1
        return 0
    fi

    local nacos_startup="$NACOS_HOME/bin/startup.sh"
    if [ ! -f "$nacos_startup" ] && [ -f "$NACOS_HOME/bin/startup-java17.sh" ]; then
        nacos_startup="$NACOS_HOME/bin/startup-java17.sh"
    fi

    if [ ! -f "$nacos_startup" ]; then
        echo -e "${RED}❌ 未找到 Nacos 启动脚本${NC}"
        echo -e "${BLUE}   期望路径: ${NACOS_HOME}/bin/startup.sh${NC}"
        echo -e "${BLUE}   可通过 NACOS_HOME 指定安装目录，或运行: ./start-nacos.sh${NC}"
        echo ""
        echo -e "${BLUE}   安装步骤:${NC}"
        echo "   1. 访问 https://github.com/alibaba/nacos/releases"
        echo "   2. 下载 nacos-server-*.tar.gz 并解压到 ~/nacos"
        exit 1
    fi

    local nacos_log="$SCRIPT_DIR/logs/nacos-startup.log"
    mkdir -p "$SCRIPT_DIR/logs"
    cd "$NACOS_HOME/bin"
    nohup bash "$(basename "$nacos_startup")" -m standalone > "$nacos_log" 2>&1 &
    cd "$SCRIPT_DIR"

    echo -e "${BLUE}   启动脚本: ${nacos_startup}${NC}"
    echo -e "${BLUE}   等待 Nacos 启动（最多 60 秒）...${NC}"

    local max_wait=60
    local waited=0
    local check_interval=3
    while [ $waited -lt $max_wait ]; do
        if curl -s "http://localhost:${NACOS_PORT}/nacos/" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Nacos 启动成功（耗时 ${waited} 秒）${NC}"
            return 0
        fi
        sleep $check_interval
        waited=$((waited + check_interval))
    done

    echo -e "${RED}❌ Nacos 启动超时（已等待 ${max_wait} 秒）${NC}"
    echo -e "${BLUE}   查看启动日志: tail -50 ${nacos_log}${NC}"
    exit 1
}

# 检查 Redis 是否运行（可选）
check_redis() {
    if command -v redis-cli > /dev/null 2>&1; then
        if redis-cli ping > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Redis 运行正常${NC}"
        else
            echo -e "${YELLOW}⚠️  警告: Redis 未运行（可选,某些功能可能需要）${NC}"
        fi
    fi
}

# 获取服务路径
get_service_path() {
    case "$1" in
        gateway) echo "yudao-gateway" ;;
        system) echo "yudao-module-system/yudao-module-system-server" ;;
        infra) echo "yudao-module-infra/yudao-module-infra-server" ;;
        member) echo "yudao-module-member/yudao-module-member-server" ;;
        bpm) echo "yudao-module-bpm/yudao-module-bpm-server" ;;
        pay) echo "yudao-module-pay/yudao-module-pay-server" ;;
        report) echo "yudao-module-report/yudao-module-report-server" ;;
        mp) echo "yudao-module-mp/yudao-module-mp-server" ;;
        product) echo "yudao-module-mall/yudao-module-product-server" ;;
        promotion) echo "yudao-module-mall/yudao-module-promotion-server" ;;
        trade) echo "yudao-module-mall/yudao-module-trade-server" ;;
        statistics) echo "yudao-module-mall/yudao-module-statistics-server" ;;
        crm) echo "yudao-module-crm/yudao-module-crm-server" ;;
        erp) echo "yudao-module-erp/yudao-module-erp-server" ;;
        ai) echo "yudao-module-ai/yudao-module-ai-server" ;;
        iot) echo "yudao-module-iot/yudao-module-iot-server" ;;
        alarm) echo "yudao-module-alarm/yudao-module-alarm-biz" ;;
        facility) echo "yudao-module-facility-management/yudao-module-facility-management-server" ;;
        scene) echo "yudao-module-scene-platform/yudao-module-scene-platform-server" ;;
        twin) echo "yudao-module-twin/yudao-module-twin-biz" ;;
        inspection) echo "yudao-module-inspection-task/yudao-module-inspection-task-server" ;;
        dynamic) echo "cheers-module-dynamicbusiness/cheers-module-dynamicbusiness-server" ;;
        platform|resource) echo "cheers-module-platform-resource/cheers-module-platform-resource-server" ;;
        *) echo "" ;;
    esac
}

# 获取服务端口
get_service_port() {
    case "$1" in
        gateway) echo "58080" ;;
        system) echo "58081" ;;
        infra) echo "58082" ;;
        member) echo "58087" ;;
        bpm) echo "58083" ;;
        pay) echo "58085" ;;
        report) echo "58084" ;;
        mp) echo "58086" ;;
        product) echo "58100" ;;
        promotion) echo "58101" ;;
        trade) echo "58102" ;;
        statistics) echo "58103" ;;
        crm) echo "58089" ;;
        erp) echo "58088" ;;
        ai) echo "58090" ;;
        iot) echo "58091" ;;
        alarm) echo "58097" ;;
        facility) echo "58092" ;;
        scene) echo "58093" ;;
        twin) echo "58094" ;;
        inspection) echo "58095" ;;
        dynamic) echo "58096" ;;
        platform|resource) echo "58098" ;;
        *) echo "" ;;
    esac
}

# 检查服务是否运行
is_service_running() {
    local service_name=$1
    local port=$(get_service_port "$service_name")
    
    if [ -z "$port" ]; then
        return 1
    fi
    
    if lsof -ti:$port > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# 启动服务
start_service() {
    local service_name=$1
    local show_logs=${2:-false}  # 第二个参数控制是否显示日志
    local service_path=$(get_service_path "$service_name")
    
    if [ -z "$service_path" ]; then
        echo -e "${RED}❌ 错误: 未知的服务 '$service_name'${NC}"
        echo ""
        show_services
        exit 1
    fi
    
    # 检查服务是否已运行
    if is_service_running "$service_name"; then
        local port=$(get_service_port "$service_name")
        local log_file="$SCRIPT_DIR/logs/${service_name}-server.log"
        echo -e "${YELLOW}⚠️  服务 $service_name 已在运行 (端口: $port)${NC}"
        echo -e "   ${YELLOW}📋 查看实时日志:${NC}"
        echo -e "      ${BLUE}./start-microservices.sh logs $service_name${NC}"
        echo -e "      或: ${BLUE}tail -f $log_file${NC}"
        echo -e "   ${YELLOW}📚 API 文档:${NC}"
        echo -e "      Swagger UI: ${BLUE}http://localhost:$port/swagger-ui${NC}"
        echo -e "      Knife4j:    ${BLUE}http://localhost:$port/doc.html${NC}"
        echo -e "      API Docs:   ${BLUE}http://localhost:$port/v3/api-docs${NC}"
        return 0
    fi
    
    # 检查服务目录是否存在
    if [ ! -d "$service_path" ]; then
        echo -e "${RED}❌ 错误: 服务路径不存在: $service_path${NC}"
        exit 1
    fi
    
    echo -e "${BLUE}🚀 启动服务: $service_name${NC}"
    echo -e "   路径: $service_path"
    echo ""
    
    # 确保日志目录存在
    mkdir -p "$SCRIPT_DIR/logs"
    
    local log_file="$SCRIPT_DIR/logs/${service_name}-server.log"
    local port=$(get_service_port "$service_name")
    local working_dir="$service_path"
    local maven_cmd="mvn spring-boot:run -Dspring-boot.run.profiles=local"

    cd "$working_dir"
    
    # 使用 Maven 启动（后台运行）
    nohup bash -lc "$maven_cmd" > "$log_file" 2>&1 &
    local pid=$!
    
    echo -e "${BLUE}   进程 ID: $pid${NC}"
    echo -e "${BLUE}   日志文件: $log_file${NC}"
    echo -e "${BLUE}   等待服务启动（最多 60 秒）...${NC}"
    echo ""
    
    # 如果要求显示日志,则实时显示
    if [ "$show_logs" = "true" ]; then
        echo -e "${BLUE}📋 实时日志（按 Ctrl+C 停止查看日志,服务将继续运行）:${NC}"
        echo "----------------------------------------------------------------"
        # 等待日志文件创建
        sleep 2
        # 实时显示日志,但服务继续在后台运行
        tail -f "$log_file" &
        local tail_pid=$!
    fi
    
    # 等待服务启动
    local max_wait=60
    local waited=0
    local check_interval=2
    
    while [ $waited -lt $max_wait ]; do
        if is_service_running "$service_name"; then
            # 如果正在显示日志,停止 tail
            if [ "$show_logs" = "true" ] && [ -n "$tail_pid" ]; then
                kill $tail_pid 2>/dev/null
            fi
            echo ""
            echo -e "${GREEN}✅ 服务 $service_name 启动成功！${NC}"
            echo -e "   端口: ${BLUE}$port${NC}"
            echo -e "   进程 ID: ${BLUE}$pid${NC}"
            echo -e "   日志文件: ${BLUE}$log_file${NC}"
            echo -e "   ${YELLOW}📋 查看实时日志:${NC}"
            echo -e "      ${BLUE}./start-microservices.sh logs $service_name${NC}"
            echo -e "      或: ${BLUE}tail -f $log_file${NC}"
            echo -e "   健康检查: ${BLUE}curl http://localhost:$port/actuator/health${NC}"
            
            # 显示 Swagger 文档地址
            echo -e "   ${YELLOW}📚 API 文档:${NC}"
            echo -e "      Swagger UI: ${BLUE}http://localhost:$port/swagger-ui${NC}"
            echo -e "      Knife4j:    ${BLUE}http://localhost:$port/doc.html${NC}"
            echo -e "      API Docs:   ${BLUE}http://localhost:$port/v3/api-docs${NC}"
            
            cd "$SCRIPT_DIR"
            return 0
        fi
        sleep $check_interval
        waited=$((waited + check_interval))
        # 显示进度
        if [ $((waited % 6)) -eq 0 ]; then
            echo -e "${BLUE}   已等待 ${waited} 秒...${NC}"
        fi
    done
    
    # 如果正在显示日志,停止 tail
    if [ "$show_logs" = "true" ] && [ -n "$tail_pid" ]; then
        kill $tail_pid 2>/dev/null
    fi
    
    echo ""
    echo -e "${YELLOW}⚠️  服务启动超时（已等待 ${max_wait} 秒）${NC}"
    echo -e "   请检查日志: ${BLUE}$log_file${NC}"
    echo ""
    echo -e "${BLUE}   最后 30 行日志:${NC}"
    echo "----------------------------------------------------------------"
    tail -30 "$log_file" 2>/dev/null || echo "   日志文件不存在或为空"
    echo "----------------------------------------------------------------"
    
    cd "$SCRIPT_DIR"
    return 1
}

# 停止服务
stop_service() {
    local service_name=$1
    local port=$(get_service_port "$service_name")
    
    if [ -z "$port" ]; then
        echo -e "${RED}❌ 错误: 未知的服务 '$service_name'${NC}"
        exit 1
    fi
    
    if ! is_service_running "$service_name"; then
        echo -e "${YELLOW}⚠️  服务 $service_name 未运行${NC}"
        return 0
    fi
    
    echo -e "${BLUE}🛑 停止服务: $service_name${NC}"
    
    # 查找占用端口的进程（Spring Boot 应用进程）
    local app_pids=$(lsof -ti:$port)
    
    if [ -n "$app_pids" ]; then
        for pid in $app_pids; do
            # 验证进程确实在监听目标端口（防止误杀）
            local pid_port=$(lsof -ti:$port 2>/dev/null | grep "^${pid}$")
            if [ -z "$pid_port" ]; then
                continue
            fi
            
            # 获取进程的父进程（Maven 进程）
            local ppid=$(ps -o ppid= -p $pid 2>/dev/null | tr -d ' ')
            
            echo -e "   终止进程: $pid (应用进程)"
            
            # 先尝试优雅终止（SIGTERM）,只终止当前进程,不影响进程组
            kill -TERM $pid 2>/dev/null
            sleep 1
            
            # 如果还在运行,强制终止
            if ps -p $pid > /dev/null 2>&1; then
                kill -9 $pid 2>/dev/null
            fi
            
            # 如果存在父进程（Maven）,也终止它
            if [ -n "$ppid" ] && [ "$ppid" != "1" ]; then
                # 检查父进程是否是 Maven 进程,并且不是其他服务的进程
                local parent_cmd=$(ps -o comm= -p $ppid 2>/dev/null)
                local parent_cmdline=$(ps -o command= -p $ppid 2>/dev/null)
                
                # 验证父进程确实是当前服务的 Maven 进程
                local service_path=$(get_service_path "$service_name")
                if echo "$parent_cmdline" | grep -q "java.*maven\|mvn" && echo "$parent_cmdline" | grep -q "$service_path"; then
                    echo -e "   终止父进程: $ppid (Maven 进程)"
                    kill -TERM $ppid 2>/dev/null
                    sleep 1
                    if ps -p $ppid > /dev/null 2>&1; then
                        kill -9 $ppid 2>/dev/null
                    fi
                fi
            fi
        done
        sleep 2
        
        if is_service_running "$service_name"; then
            echo -e "${RED}❌ 服务 $service_name 停止失败${NC}"
            return 1
        else
            echo -e "${GREEN}✅ 服务 $service_name 已停止${NC}"
            return 0
        fi
    else
        echo -e "${YELLOW}⚠️  未找到运行中的进程${NC}"
        return 1
    fi
}

# 显示服务状态
show_status() {
    echo -e "${BLUE}📊 服务状态:${NC}"
    echo ""
    printf "%-15s %-10s %-10s %-20s %-40s\n" "服务名" "端口" "状态" "PID" "访问链接"
    echo "----------------------------------------------------------------------------------------------------"

    for svc in gateway system infra member bpm pay report mp product promotion trade statistics crm erp ai iot alarm dynamic platform facility scene twin inspection; do
        local port=$(get_service_port "$svc")
        if [ -n "$port" ]; then
            if is_service_running "$svc"; then
                local pid=$(lsof -ti:$port 2>/dev/null | head -1)
                local url="http://localhost:$port"
                printf "%-15s %-10s ${GREEN}%-10s${NC} %-20s ${BLUE}%-40s${NC}\n" "$svc" "$port" "运行中" "$pid" "$url"
            else
                printf "%-15s %-10s ${RED}%-10s${NC} %-20s %-40s\n" "$svc" "$port" "未运行" "-" "-"
            fi
        fi
    done

    echo ""
    echo -e "${BLUE}📋 基础设施状态:${NC}"

    # 检查 Nacos
    if curl -s "http://localhost:${NACOS_PORT}/nacos/" > /dev/null 2>&1; then
        echo -e "   Nacos (8848): ${GREEN}运行中${NC} - ${BLUE}http://localhost:${NACOS_PORT}/nacos${NC} (${NACOS_HOME})"
    else
        echo -e "   Nacos (8848): ${RED}未运行${NC} - 路径: ${NACOS_HOME}"
    fi

    # 检查 Redis
    if command -v redis-cli > /dev/null 2>&1; then
        if redis-cli ping > /dev/null 2>&1; then
            echo -e "   Redis (6379): ${GREEN}运行中${NC}"
        else
            echo -e "   Redis (6379): ${RED}未运行${NC}"
        fi
    fi

    echo ""
    echo -e "${BLUE}🔗 快捷访问链接:${NC}"
    echo -e "   ${YELLOW}网关入口:${NC} ${BLUE}http://localhost:58080${NC}"
    echo -e "   ${YELLOW}系统管理:${NC} ${BLUE}http://localhost:58080/admin-ui/${NC}"
    echo -e "   ${YELLOW}Nacos控制台:${NC} ${BLUE}http://localhost:8848/nacos${NC}"
    echo ""
    echo -e "${BLUE}📚 API 文档链接:${NC}"

    # 显示运行中服务的 Swagger 链接
    local has_running_services=false
    for svc in gateway system infra member bpm pay report mp product promotion trade statistics crm erp ai iot alarm dynamic platform facility scene twin inspection; do
        if is_service_running "$svc"; then
            local port=$(get_service_port "$svc")
            if [ -n "$port" ]; then
                has_running_services=true
                echo -e "   ${YELLOW}$svc 服务:${NC}"
                echo -e "      Swagger UI:  ${BLUE}http://localhost:$port/swagger-ui${NC}"
                echo -e "      Knife4j:     ${BLUE}http://localhost:$port/doc.html${NC}"
                echo -e "      API Docs:    ${BLUE}http://localhost:$port/v3/api-docs${NC}"
            fi
        fi
    done

    if [ "$has_running_services" = "false" ]; then
        echo -e "   ${YELLOW}暂无运行中的服务${NC}"
    fi
}

# 显示所有服务
show_services() {
    echo -e "${BLUE}📋 可用微服务列表:${NC}"
    echo ""
    printf "%-15s %-50s %-10s\n" "服务名" "模块路径" "端口"
    echo "----------------------------------------------------------------------------------------"
    for svc in gateway system infra member bpm pay report mp product promotion trade statistics crm erp ai iot alarm dynamic platform facility scene twin inspection; do
        local path=$(get_service_path "$svc")
        local port=$(get_service_port "$svc")
        if [ -n "$path" ]; then
            printf "%-15s %-50s %-10s\n" "$svc" "$path" "$port"
        fi
    done
    echo ""
    echo -e "${BLUE}使用方法:${NC}"
    echo "  ./start-microservices.sh                    # 显示所有可用服务"
    echo "  ./start-microservices.sh all                # 启动所有核心服务（后台运行）"
    echo "  ./start-microservices.sh all -f            # 启动所有核心服务（显示日志）"
    echo "  ./start-microservices.sh all-services       # 启动所有服务包括业务服务（后台运行）"
    echo "  ./start-microservices.sh all-services -f    # 启动所有服务包括业务服务（显示日志）"
    echo "  ./start-microservices.sh <服务名>            # 启动单个服务（后台运行）"
    echo "  ./start-microservices.sh <服务名> -f         # 启动单个服务（显示日志）"
    echo "  ./start-microservices.sh status             # 查看服务状态"
    echo "  ./start-microservices.sh logs               # 查看所有运行中服务的日志"
    echo "  ./start-microservices.sh logs <服务名>       # 实时查看指定服务的日志"
    echo "  ./start-microservices.sh stop <服务名>       # 停止服务"
    echo "  ./start-microservices.sh stop-all           # 停止所有服务"
    echo "  ./start-microservices.sh nacos                # 启动 Nacos"
    echo "  ./start-microservices.sh nacos status         # 查看 Nacos 状态"
    echo "  ./start-microservices.sh nacos stop           # 停止 Nacos"
    echo ""
    echo -e "${BLUE}说明:${NC}"
    echo "  - Nacos 默认路径: $NACOS_HOME（可通过 NACOS_HOME 环境变量覆盖）"
    echo "  - 默认后台运行,日志保存到 logs/ 目录"
    echo "  - 使用 -f 参数可以实时查看启动日志"
    echo ""
    echo -e "${BLUE}核心服务（推荐启动顺序）:${NC}"
    echo "  1. infra     - 基础设施服务（必需,提供日志、文件等服务）"
    echo "  2. system    - 系统服务（必需,依赖 infra 的日志服务）"
    echo "  3. gateway   - 网关服务（必需）"
    echo "  4. bpm       - 工作流服务（必需）"
    echo "  5. alarm     - 告警管理服务（必需）"
    echo "  6. dynamic    - 动态业务服务（facility 等模块依赖）"
    echo "  7. platform   - 平台资源库（组件/视图/页面，别名 resource，端口 58098）"
    echo ""
    echo -e "${BLUE}业务服务（按需启动）:${NC}"
    echo "  - member     - 会员服务"
    echo "  - bpm        - 工作流服务"
    echo "  - pay        - 支付服务"
    echo "  - crm        - CRM 服务"
    echo "  - erp        - ERP 服务"
    echo "  - ai         - AI 服务"
    echo "  - iot        - IoT 服务"
}

# 启动所有核心服务
start_all_core() {
    local show_logs=${1:-false}  # 是否显示日志
    echo -e "${BLUE}🚀 启动所有核心服务...${NC}"
    echo ""
    
    check_nacos
    check_redis
    echo ""
    
    # 按顺序启动核心服务
    # 注意：infra 必须在 system 之前启动,因为 system 启动时会调用 infra 的日志服务
    local services=("infra" "system" "gateway" "bpm" "alarm" "dynamic" "platform" "facility" "scene" "twin" "inspection")
    local failed_services=()
    
    for svc in "${services[@]}"; do
        if ! start_service "$svc" "$show_logs"; then
            failed_services+=("$svc")
        fi
        if [ "$show_logs" = "true" ]; then
            echo ""
            echo -e "${BLUE}按 Enter 继续启动下一个服务...${NC}"
            read -r
        else
            sleep 3
        fi
    done
    
    echo ""
    if [ ${#failed_services[@]} -eq 0 ]; then
        echo -e "${GREEN}✅ 所有核心服务启动完成${NC}"
    else
        echo -e "${YELLOW}⚠️  部分服务启动失败: ${failed_services[*]}${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}📝 提示:${NC}"
    echo "  - 查看服务状态: ./start-microservices.sh status"
    echo "  - 查看服务日志: tail -f logs/<服务名>-server.log"
    echo "  - 停止所有服务: ./start-microservices.sh stop-all"
}

# 启动所有服务（包括业务服务）
start_all_services() {
    local show_logs=${1:-false}  # 是否显示日志
    echo -e "${BLUE}🚀 启动所有服务（包括业务服务）...${NC}"
    echo ""
    
    check_nacos
    check_redis
    echo ""
    
    # 按顺序启动所有服务：先核心服务,再业务服务
    local services=("system" "infra" "gateway" "member" "bpm" "pay" "report" "mp" "product" "promotion" "trade" "statistics" "crm" "erp" "ai" "iot" "alarm" "dynamic" "platform" "facility" "scene" "twin" "inspection")
    local failed_services=()
    
    for svc in "${services[@]}"; do
        if ! start_service "$svc" "$show_logs"; then
            failed_services+=("$svc")
        fi
        if [ "$show_logs" = "true" ]; then
            echo ""
            echo -e "${BLUE}按 Enter 继续启动下一个服务...${NC}"
            read -r
        else
            sleep 3
        fi
    done
    
    echo ""
    if [ ${#failed_services[@]} -eq 0 ]; then
        echo -e "${GREEN}✅ 所有服务启动完成${NC}"
    else
        echo -e "${YELLOW}⚠️  部分服务启动失败: ${failed_services[*]}${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}📝 提示:${NC}"
    echo "  - 查看服务状态: ./start-microservices.sh status"
    echo "  - 查看服务日志: tail -f logs/<服务名>-server.log"
    echo "  - 停止所有服务: ./start-microservices.sh stop-all"
}

# 查看服务日志
show_logs() {
    local service_name=$1
    
    if [ -z "$service_name" ]; then
        echo -e "${BLUE}📋 查看所有运行中服务的日志（按 Ctrl+C 退出）:${NC}"
        echo ""
        
        local running_services=()
        for svc in gateway system infra member bpm pay report mp product promotion trade statistics crm erp ai iot alarm dynamic platform facility scene twin inspection; do
            if is_service_running "$svc"; then
                running_services+=("$svc")
            fi
        done
        
        if [ ${#running_services[@]} -eq 0 ]; then
            echo -e "${YELLOW}⚠️  没有运行中的服务${NC}"
            return 1
        fi
        
        echo -e "${BLUE}运行中的服务: ${running_services[*]}${NC}"
        echo ""
        echo -e "${YELLOW}提示: 使用 'logs <服务名>' 查看单个服务的日志${NC}"
        echo ""
        
        # 显示所有运行中服务的日志（使用 tail -f 分别显示）
        for svc in "${running_services[@]}"; do
            local log_file="$SCRIPT_DIR/logs/${svc}-server.log"
            if [ -f "$log_file" ]; then
                echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
                echo -e "${GREEN}📋 $svc 服务日志:${NC}"
                echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
                tail -20 "$log_file"
                echo ""
            fi
        done
        
        echo -e "${BLUE}实时查看日志请使用: tail -f logs/<服务名>-server.log${NC}"
        return 0
    fi
    
    # 查看单个服务的日志
    local service_path=$(get_service_path "$service_name")
    if [ -z "$service_path" ]; then
        echo -e "${RED}❌ 错误: 未知的服务 '$service_name'${NC}"
        echo ""
        show_services
        exit 1
    fi
    
    local log_file="$SCRIPT_DIR/logs/${service_name}-server.log"
    
    if [ ! -f "$log_file" ]; then
        echo -e "${YELLOW}⚠️  日志文件不存在: $log_file${NC}"
        echo -e "${BLUE}   服务可能还未启动或日志文件尚未创建${NC}"
        return 1
    fi
    
    if ! is_service_running "$service_name"; then
        echo -e "${YELLOW}⚠️  服务 $service_name 未运行${NC}"
        echo -e "${BLUE}   显示最后 50 行日志:${NC}"
        echo "----------------------------------------------------------------"
        tail -50 "$log_file"
        return 0
    fi
    
    local port=$(get_service_port "$service_name")
    echo -e "${BLUE}📋 查看服务 $service_name 的日志（端口: $port）${NC}"
    echo -e "${BLUE}   日志文件: $log_file${NC}"
    echo -e "${YELLOW}   按 Ctrl+C 退出${NC}"
    echo "----------------------------------------------------------------"
    tail -f "$log_file"
}

# 停止所有服务
stop_all_services() {
    echo -e "${BLUE}🛑 停止所有服务...${NC}"
    echo ""
    
    local services=("gateway" "infra" "system" "member" "bpm" "pay" "report" "mp" "product" "promotion" "trade" "statistics" "crm" "erp" "ai" "iot" "alarm" "dynamic" "platform" "facility" "scene" "twin" "inspection")
    local stopped_count=0
    
    for svc in "${services[@]}"; do
        if is_service_running "$svc"; then
            if stop_service "$svc"; then
                stopped_count=$((stopped_count + 1))
            fi
        fi
    done
    
    echo ""
    if [ $stopped_count -gt 0 ]; then
        echo -e "${GREEN}✅ 已停止 $stopped_count 个服务${NC}"
    else
        echo -e "${YELLOW}⚠️  没有运行中的服务${NC}"
    fi
}

# 主逻辑
main() {
    # 创建日志目录
    mkdir -p logs
    
    # 检查是否显示日志
    local show_logs=false
    if [ "$2" = "-f" ] || [ "$3" = "-f" ]; then
        show_logs=true
    fi
    
    case "$1" in
        "")
            show_services
            ;;
        "all")
            start_all_core "$show_logs"
            ;;
        "all-services")
            start_all_services "$show_logs"
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs "$2"
            ;;
        "stop")
            if [ -z "$2" ]; then
                echo -e "${RED}❌ 错误: 请指定要停止的服务名${NC}"
                echo "   使用方法: ./start-microservices.sh stop <服务名>"
                exit 1
            fi
            stop_service "$2"
            ;;
        "stop-all")
            stop_all_services
            ;;
        "nacos")
            if [ -f "$SCRIPT_DIR/start-nacos.sh" ]; then
                bash "$SCRIPT_DIR/start-nacos.sh" "${2:-}"
            else
                check_nacos
            fi
            ;;
        *)
            check_nacos
            check_redis
            echo ""
            start_service "$1" "$show_logs"
            ;;
    esac
}

main "$@"

