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
#   ./start-microservices.sh bmp              # 推荐：过程引擎 + 路径规划（7 个服务）
#   ./start-microservices.sh bmp-process       # 仅过程引擎（5）
#   ./start-microservices.sh bmp-path          # 仅路径规划（2）
#   ./start-microservices.sh bmp-scene-3d      # 三维 scene-3d
#   ./start-microservices.sh bmp-gis           # GIS
#   ./start-microservices.sh bmp-alarm         # 告警标准服务
#   ./start-microservices.sh bmp-work-order    # 工单标准服务
#   ./start-microservices.sh bmp-station-dev   # path + scene-3d
#   ./start-microservices.sh twin-dev          # 孪生联调：dynamic + scene-3d + twin
#   ./start-microservices.sh stop-bmp          # 停止 bmp（process+path）
#   ./start-microservices.sh stop-bmp-process|stop-bmp-path|stop-bmp-scene-3d|stop-bmp-gis|stop-bmp-alarm|stop-bmp-work-order
#   ./start-microservices.sh stop-twin-dev     # 停止 twin-dev
#   ./start-microservices.sh platform-all      # [弃用] 等同 bmp
#
# 业务中台父工程：cheers-business-middle-platform
# bmp = bmp-process + bmp-path（不含 scene-3d / gis / twin）
# twin = cheers-twin（整合层，在 BMP 与 dynamic 之上；不进 bmp 标准套件）
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
        gateway|cheers-gateway) echo "cheers-gateway" ;;
        system|cheers-system) echo "cheers-system/cheers-system-server" ;;
        infra|cheers-infra) echo "cheers-infra/cheers-infra-server" ;;
        member) echo "cheers-member/cheers-member-server" ;;
        bpm) echo "cheers-bpm/cheers-bpm-server" ;;
        pay) echo "cheers-pay/cheers-pay-server" ;;
        report) echo "cheers-report/cheers-report-server" ;;
        mp) echo "cheers-mp/cheers-mp-server" ;;
        product) echo "cheers-mall/cheers-product-server" ;;
        promotion) echo "cheers-mall/cheers-promotion-server" ;;
        trade) echo "cheers-mall/cheers-trade-server" ;;
        statistics) echo "cheers-mall/cheers-statistics-server" ;;
        crm) echo "cheers-crm/cheers-crm-server" ;;
        erp) echo "cheers-erp/cheers-erp-server" ;;
        ai) echo "cheers-ai/cheers-ai-server" ;;
        iot) echo "cheers-iot/cheers-iot-server" ;;
        alarm|bmp-alarm) echo "cheers-business-middle-platform/cheers-alarm-server" ;;
        work-order|bmp-work-order) echo "cheers-business-middle-platform/cheers-work-order-server" ;;
        scene|scene-3d) echo "cheers-business-middle-platform/cheers-scene-3d-server" ;;
        gis|bmp-gis) echo "cheers-business-middle-platform/cheers-gis-server" ;;
        twin|cheers-twin) echo "cheers-twin/cheers-twin-server" ;;
        inspection) echo "cheers-inspection-task/cheers-inspection-task-server" ;;
        dynamic) echo "cheers-dynamicbusiness/cheers-dynamicbusiness-server" ;;
        # 业务中台 BMP：资源库（旧名 platform / resource）
        platform|resource|bmp-resource) echo "cheers-business-middle-platform/cheers-resource-server" ;;
        platform-runtime|runtime-l4|bmp-runtime) echo "cheers-business-middle-platform/cheers-runtime-server" ;;
        platform-orchestration|orchestration|bmp-orchestration) echo "cheers-business-middle-platform/cheers-orchestration-server" ;;
        platform-policy|policy|bmp-policy) echo "cheers-business-middle-platform/cheers-policy-server" ;;
        platform-capability|capability|bmp-capability) echo "cheers-business-middle-platform/cheers-capability-server" ;;
        platform-topology|topology|bmp-topology) echo "cheers-business-middle-platform/cheers-topology-server" ;;
        platform-routing|routing|bmp-routing) echo "cheers-business-middle-platform/cheers-routing-server" ;;
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
        alarm|bmp-alarm) echo "58097" ;;
        work-order|bmp-work-order) echo "58099" ;;
        scene|scene-3d) echo "58093" ;;
        gis|bmp-gis) echo "58109" ;;
        twin|cheers-twin) echo "58094" ;;
        inspection) echo "58095" ;;
        dynamic) echo "58096" ;;
        platform|resource|bmp-resource) echo "58098" ;;
        platform-runtime|runtime-l4|bmp-runtime) echo "58099" ;;
        platform-orchestration|orchestration|bmp-orchestration) echo "58104" ;;
        platform-policy|policy|bmp-policy) echo "58105" ;;
        platform-capability|capability|bmp-capability) echo "58106" ;;
        platform-topology|topology|bmp-topology) echo "58107" ;;
        platform-routing|routing|bmp-routing) echo "58108" ;;
        *) echo "" ;;
    esac
}

# 服务列表（platform：resource → policy/capability → runtime → orchestration → topology → routing）
KNOWN_SERVICES=(
    gateway system infra member bpm pay report mp product promotion trade statistics
    crm erp ai iot alarm work-order dynamic
    platform platform-runtime platform-orchestration platform-policy platform-capability
    platform-topology platform-routing
    scene gis twin inspection
)
CORE_START_SERVICES=(
    infra system gateway bpm alarm dynamic
    platform platform-runtime platform-orchestration platform-policy platform-capability
    platform-topology platform-routing
    scene gis twin inspection
)
ALL_START_SERVICES=(
    system infra gateway member bpm pay report mp product promotion trade statistics
    crm erp ai iot alarm work-order dynamic
    platform platform-runtime platform-orchestration platform-policy platform-capability
    platform-topology platform-routing
    scene gis twin inspection
)
STOP_SERVICES=(
    gateway infra system member bpm pay report mp product promotion trade statistics
    crm erp ai iot alarm work-order dynamic
    platform-routing platform-topology
    platform-orchestration platform-runtime platform-policy platform-capability platform
    scene gis twin inspection
)
is_service_running() {
    local service_name=$1
    local port=$(get_service_port "$service_name")
    
    if [ -z "$port" ]; then
        return 1
    fi
    
    if lsof -iTCP:"$port" -sTCP:LISTEN > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# 获取监听端口的应用 PID（Spring Boot 进程）
get_service_app_pid() {
    local port=$1
    lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null | head -1
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

    # topology/routing 依赖本仓 SNAPSHOT API，首次启动前先 install，避免服务未起来被网关报 Unable to find instance
    case "$service_name" in
        topology|platform-topology|bmp-topology|routing|platform-routing|bmp-routing)
            local artifact_id
            artifact_id=$(basename "$service_path")
            echo -e "${BLUE}   安装 ${artifact_id} 及依赖到本地 Maven（-am install -DskipTests）...${NC}"
            (
                cd "$SCRIPT_DIR/cheers-business-middle-platform" || exit 1
                mvn -pl "$artifact_id" -am install -DskipTests -q
            )
            if [ $? -ne 0 ]; then
                echo -e "${YELLOW}⚠️  依赖安装失败，仍尝试启动；若失败请查看日志${NC}"
            else
                echo -e "${GREEN}✅ 依赖已就绪${NC}"
            fi
            echo ""
            ;;
    esac
    
    # 确保日志目录存在
    mkdir -p "$SCRIPT_DIR/logs"
    
    local log_file="$SCRIPT_DIR/logs/${service_name}-server.log"
    local port=$(get_service_port "$service_name")
    local working_dir="$service_path"
    cd "$working_dir"
    
    # 后台启动；disown 避免启动脚本 shell 退出时 SIGHUP 带走 Maven/Spring Boot
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=local >> "$log_file" 2>&1 &
    local pid=$!
    disown -h "$pid" 2>/dev/null || true
    
    echo -e "${BLUE}   Maven 进程 ID: $pid${NC}"
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
            # 端口刚起来时进程可能仍不稳定，再等几秒确认仍存活
            sleep 3
            if ! is_service_running "$service_name"; then
                waited=$((waited + 3))
                continue
            fi
            # 如果正在显示日志,停止 tail
            if [ "$show_logs" = "true" ] && [ -n "$tail_pid" ]; then
                kill $tail_pid 2>/dev/null
            fi
            local app_pid
            app_pid=$(get_service_app_pid "$port")
            echo ""
            echo -e "${GREEN}✅ 服务 $service_name 启动成功！${NC}"
            echo -e "   端口: ${BLUE}$port${NC}"
            echo -e "   应用进程 ID: ${BLUE}${app_pid:-未知}${NC}"
            echo -e "   Maven 进程 ID: ${BLUE}$pid${NC}"
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
    printf "%-22s %-10s %-10s %-20s %-40s\n" "服务名" "端口" "状态" "PID" "访问链接"
    echo "----------------------------------------------------------------------------------------------------"

    for svc in "${KNOWN_SERVICES[@]}"; do
        local port=$(get_service_port "$svc")
        if [ -n "$port" ]; then
            if is_service_running "$svc"; then
                local pid
                pid=$(get_service_app_pid "$port")
                local url="http://localhost:$port"
                printf "%-22s %-10s ${GREEN}%-10s${NC} %-20s ${BLUE}%-40s${NC}\n" "$svc" "$port" "运行中" "$pid" "$url"
            else
                printf "%-22s %-10s ${RED}%-10s${NC} %-20s %-40s\n" "$svc" "$port" "未运行" "-" "-"
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
    for svc in "${KNOWN_SERVICES[@]}"; do
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
    printf "%-22s %-50s %-10s\n" "服务名" "模块路径" "端口"
    echo "----------------------------------------------------------------------------------------"
    for svc in "${KNOWN_SERVICES[@]}"; do
        local path=$(get_service_path "$svc")
        local port=$(get_service_port "$svc")
        if [ -n "$path" ]; then
            printf "%-22s %-50s %-10s\n" "$svc" "$path" "$port"
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
    echo "  ./start-microservices.sh bmp                   # 推荐：业务中台 process+path（7）"
    echo "  ./start-microservices.sh bmp-process           # 仅过程引擎（5）"
    echo "  ./start-microservices.sh bmp-path              # 仅路径规划（2）"
    echo "  ./start-microservices.sh bmp-scene-3d          # 三维（scene-3d）"
    echo "  ./start-microservices.sh bmp-gis               # GIS（坐标/CRS）"
    echo "  ./start-microservices.sh bmp-alarm             # 告警标准服务"
    echo "  ./start-microservices.sh bmp-work-order        # 工单标准服务"
    echo "  ./start-microservices.sh bmp-station-dev       # path + scene-3d"
    echo "  ./start-microservices.sh twin-dev              # 孪生联调：dynamic + scene-3d + twin"
    echo "  ./start-microservices.sh stop-bmp              # 停止 bmp（process+path）"
    echo "  ./start-microservices.sh stop-bmp-process|stop-bmp-path|stop-bmp-scene-3d|stop-bmp-gis|stop-bmp-alarm|stop-bmp-work-order"
    echo "  ./start-microservices.sh stop-twin-dev         # 停止 twin-dev"
    echo "  ./start-microservices.sh platform-all           # [弃用] → bmp"
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
    echo "  6. dynamic    - 动态业务服务（设施/设备等实体，twin 等模块依赖）"
    echo "  7. platform   - 平台资源库（组件/视图，别名 resource，58098）"
    echo "     路径: cheers-business-middle-platform/cheers-resource-server"
    echo "  8. platform-policy - 平台策略（58105）"
    echo "  9. platform-capability - 平台能力映射（58106）"
    echo "  10. platform-runtime - 平台 L4 运行时（58099）"
    echo "  11. platform-orchestration - 平台编排/排程 run（58104，依赖 runtime）"
    echo "  12. platform-topology - 站场拓扑/路网（58107，路径规划必需）"
    echo "  13. platform-routing - 路径规划引擎（58108，试走/算路必需）"
    echo "     （./start-microservices.sh bmp 一键启动 7→13；按需再用 bmp-process / bmp-path）"
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
    local failed_services=()
    
    for svc in "${CORE_START_SERVICES[@]}"; do
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
    local failed_services=()
    
    for svc in "${ALL_START_SERVICES[@]}"; do
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
        for svc in "${KNOWN_SERVICES[@]}"; do
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
    
    local stopped_count=0
    
    for svc in "${STOP_SERVICES[@]}"; do
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

# 业务中台套件：按列表启动 / 停止（短名仍用 platform-* 以便 status 一致）
BMP_PROCESS_SERVICES=(platform platform-policy platform-capability platform-runtime platform-orchestration)
BMP_PATH_SERVICES=(platform-topology platform-routing)
# bmp / bmp-all = 过程引擎 + 路径规划（不含 scene）
BMP_CORE_SERVICES=("${BMP_PROCESS_SERVICES[@]}" "${BMP_PATH_SERVICES[@]}")
BMP_SCENE_3D_SERVICES=(scene-3d)
BMP_GIS_SERVICES=(gis)
BMP_ALARM_SERVICES=(alarm)
BMP_WORK_ORDER_SERVICES=(work-order)
# 孪生整合层联调（不入 bmp）：设施实体 + 三维 + Twin 映射
TWIN_DEV_SERVICES=(dynamic scene-3d twin)

start_suite_services() {
    local show_logs=${1:-false}
    shift
    local svc
    for svc in "$@"; do
        start_service "$svc" "$show_logs" || true
        if [ "$show_logs" != "true" ]; then
            sleep 3
        fi
    done
}

stop_suite_services() {
    local stopped_count=0
    local svc
    for svc in "$@"; do
        if is_service_running "$svc"; then
            if stop_service "$svc"; then
                stopped_count=$((stopped_count + 1))
            fi
        fi
    done
    echo ""
    if [ $stopped_count -gt 0 ]; then
        echo -e "${GREEN}✅ 套件已停止 $stopped_count 个服务${NC}"
    else
        echo -e "${YELLOW}⚠️  套件内没有运行中的服务${NC}"
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
        "bmp"|"bmp-all"|"platform-all")
            if [ "$1" = "platform-all" ]; then
                echo -e "${YELLOW}⚠️  platform-all 已弃用：请改用 ./start-microservices.sh bmp${NC}"
            fi
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台核心套件 (bmp = process + path，共 ${#BMP_CORE_SERVICES[@]} 个)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_CORE_SERVICES[@]}"
            ;;
        "bmp-process")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · 过程引擎套件 (bmp-process)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_PROCESS_SERVICES[@]}"
            ;;
        "bmp-path")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · 路径规划套件 (bmp-path)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_PATH_SERVICES[@]}"
            ;;
        "bmp-gis")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · GIS (bmp-gis)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_GIS_SERVICES[@]}"
            ;;
        "stop-bmp-gis")
            echo -e "${BLUE}🛑 停止 bmp-gis${NC}"
            stop_suite_services "${BMP_GIS_SERVICES[@]}"
            ;;
        "bmp-alarm")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · 告警标准服务 (bmp-alarm)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_ALARM_SERVICES[@]}"
            ;;
        "stop-bmp-alarm")
            echo -e "${BLUE}🛑 停止 bmp-alarm${NC}"
            stop_suite_services "${BMP_ALARM_SERVICES[@]}"
            ;;
        "bmp-work-order")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · 工单标准服务 (bmp-work-order)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_WORK_ORDER_SERVICES[@]}"
            ;;
        "stop-bmp-work-order")
            echo -e "${BLUE}🛑 停止 bmp-work-order${NC}"
            stop_suite_services "${BMP_WORK_ORDER_SERVICES[@]}"
            ;;
        "bmp-scene-3d")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · 三维服务 (bmp-scene-3d)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_SCENE_3D_SERVICES[@]}"
            ;;
        "bmp-station-dev")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 站场联调：bmp-path + bmp-scene-3d${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_PATH_SERVICES[@]}" "${BMP_SCENE_3D_SERVICES[@]}"
            ;;
        "twin-dev")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 孪生整合联调 (twin-dev)：dynamic + scene-3d + twin${NC}"
            echo ""
            start_suite_services "$show_logs" "${TWIN_DEV_SERVICES[@]}"
            ;;
        "stop-twin-dev")
            echo -e "${BLUE}🛑 停止 twin-dev${NC}"
            stop_suite_services "${TWIN_DEV_SERVICES[@]}"
            ;;
        "stop-bmp"|"stop-bmp-all")
            echo -e "${BLUE}🛑 停止 bmp（process + path）${NC}"
            stop_suite_services "${BMP_CORE_SERVICES[@]}"
            ;;
        "stop-bmp-process")
            echo -e "${BLUE}🛑 停止 bmp-process${NC}"
            stop_suite_services "${BMP_PROCESS_SERVICES[@]}"
            ;;
        "stop-bmp-path")
            echo -e "${BLUE}🛑 停止 bmp-path${NC}"
            stop_suite_services "${BMP_PATH_SERVICES[@]}"
            ;;
        "stop-bmp-scene-3d")
            echo -e "${BLUE}🛑 停止 bmp-scene-3d${NC}"
            stop_suite_services "${BMP_SCENE_3D_SERVICES[@]}"
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

