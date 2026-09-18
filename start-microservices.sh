#!/bin/bash

# ============================================================================
# 本地微服务启停（按职责分套件，不按旧名 platform-* 使用）
# ============================================================================
# 先看这四档：
#   ./start-microservices.sh base      # 底盘：登录、网关、文件日志
#   ./start-microservices.sh biz       # 动态业务 + 巡检 + 孪生 + 协议网关
#   ./start-microservices.sh bmp       # 中台全部（配置/过程/路网/告警工单手册/三维地图）
#   ./start-microservices.sh all       # 底盘 + 工作流 + 动态业务 + 中台
# 只改组件配置时：
#   ./start-microservices.sh stop resource && ./start-microservices.sh resource
#
# 本地端口 15xxx（网关 15080；协议网关 8095，设备 WebSocket 直连）。前端业务页只连网关。
# 中台工程：cheers-business-middle-platform
# 动态业务：cheers-dynamicbusiness/cheers-dynamicbusiness-server
# Maven 本地仓库：默认 ~/MavenRepositoy，可用 MAVEN_REPO_LOCAL 覆盖。
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

# 统一本地仓库，避免 spring-boot:run 落到 ~/.m2 而找不到本仓 SNAPSHOT
# 注意：nohup 无法调用 shell 函数，启动命令须直接写 mvn -Dmaven.repo.local=...
MAVEN_REPO_LOCAL="${MAVEN_REPO_LOCAL:-$HOME/MavenRepositoy}"
mkdir -p "$MAVEN_REPO_LOCAL"

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
        maintenance|bmp-maintenance) echo "cheers-business-middle-platform/cheers-maintenance-server" ;;
        scene|scene-3d|bmp-scene-3d) echo "cheers-business-middle-platform/cheers-scene-3d-server" ;;
        gis|bmp-gis) echo "cheers-business-middle-platform/cheers-gis-server" ;;
        twin|cheers-twin) echo "cheers-twin/cheers-twin-server" ;;
        inspection) echo "cheers-inspection-task/cheers-inspection-task-server" ;;
        protocol|device-protocol|protocol-gateway|cheers-device-protocol-gateway) echo "cheers-device-protocol-gateway/cheers-device-protocol-gateway-server" ;;
        dynamic) echo "cheers-dynamicbusiness/cheers-dynamicbusiness-server" ;;
        resource|platform|bmp-resource) echo "cheers-business-middle-platform/cheers-resource-server" ;;
        runtime|platform-runtime|runtime-l4|bmp-runtime) echo "cheers-business-middle-platform/cheers-runtime-server" ;;
        orchestration|platform-orchestration|bmp-orchestration) echo "cheers-business-middle-platform/cheers-orchestration-server" ;;
        policy|platform-policy|bmp-policy) echo "cheers-business-middle-platform/cheers-policy-server" ;;
        capability|platform-capability|bmp-capability) echo "cheers-business-middle-platform/cheers-capability-server" ;;
        topology|platform-topology|bmp-topology) echo "cheers-business-middle-platform/cheers-topology-server" ;;
        routing|platform-routing|bmp-routing) echo "cheers-business-middle-platform/cheers-routing-server" ;;
        *) echo "" ;;
    esac
}

# 获取服务端口
get_service_port() {
    case "$1" in
        gateway) echo "15080" ;;
        system) echo "15081" ;;
        infra) echo "15082" ;;
        member) echo "15087" ;;
        bpm) echo "15083" ;;
        pay) echo "15085" ;;
        report) echo "15084" ;;
        mp) echo "15086" ;;
        product) echo "15100" ;;
        promotion) echo "15101" ;;
        trade) echo "15102" ;;
        statistics) echo "15103" ;;
        crm) echo "15089" ;;
        erp) echo "15088" ;;
        ai) echo "15090" ;;
        iot) echo "15091" ;;
        alarm|bmp-alarm) echo "15097" ;;
        work-order|bmp-work-order) echo "15110" ;;
        maintenance|bmp-maintenance) echo "15111" ;;
        scene|scene-3d|bmp-scene-3d) echo "15093" ;;
        gis|bmp-gis) echo "15109" ;;
        twin|cheers-twin) echo "15094" ;;
        inspection) echo "15095" ;;
        protocol|device-protocol|protocol-gateway|cheers-device-protocol-gateway) echo "8095" ;;
        dynamic) echo "15096" ;;
        resource|platform|bmp-resource) echo "15098" ;;
        runtime|platform-runtime|runtime-l4|bmp-runtime) echo "15099" ;;
        orchestration|platform-orchestration|bmp-orchestration) echo "15104" ;;
        policy|platform-policy|bmp-policy) echo "15105" ;;
        capability|platform-capability|bmp-capability) echo "15106" ;;
        topology|platform-topology|bmp-topology) echo "15107" ;;
        routing|platform-routing|bmp-routing) echo "15108" ;;
        *) echo "" ;;
    esac
}

# 旧名收到正名，日志和 status 只认正名
canonical_service_name() {
    case "$1" in
        gateway|cheers-gateway) echo "gateway" ;;
        system|cheers-system) echo "system" ;;
        infra|cheers-infra) echo "infra" ;;
        bpm) echo "bpm" ;;
        alarm|bmp-alarm) echo "alarm" ;;
        work-order|bmp-work-order) echo "work-order" ;;
        maintenance|bmp-maintenance) echo "maintenance" ;;
        scene|scene-3d|bmp-scene-3d) echo "scene-3d" ;;
        gis|bmp-gis) echo "gis" ;;
        twin|cheers-twin) echo "twin" ;;
        inspection) echo "inspection" ;;
        protocol|device-protocol|protocol-gateway|cheers-device-protocol-gateway) echo "protocol" ;;
        dynamic) echo "dynamic" ;;
        resource|platform|bmp-resource) echo "resource" ;;
        runtime|platform-runtime|runtime-l4|bmp-runtime) echo "runtime" ;;
        orchestration|platform-orchestration|bmp-orchestration) echo "orchestration" ;;
        policy|platform-policy|bmp-policy) echo "policy" ;;
        capability|platform-capability|bmp-capability) echo "capability" ;;
        topology|platform-topology|bmp-topology) echo "topology" ;;
        routing|platform-routing|bmp-routing) echo "routing" ;;
        *) echo "$1" ;;
    esac
}

# 按职责套件（正名）。商城/演示类不进默认列表，路径仍保留可手启。
BASE_SERVICES=(infra system gateway)
WORKFLOW_SERVICES=(bpm)
BIZ_SERVICES=(dynamic inspection twin protocol)
BMP_SERVICES=(
    resource
    policy capability runtime orchestration
    topology routing
    alarm work-order maintenance
    scene-3d gis
)
BMP_PROCESS_SERVICES=(resource policy capability runtime orchestration)
BMP_PATH_SERVICES=(topology routing)
ALL_DUTY_SERVICES=("${BASE_SERVICES[@]}" "${WORKFLOW_SERVICES[@]}" "${BIZ_SERVICES[@]}" "${BMP_SERVICES[@]}")
KNOWN_SERVICES=("${ALL_DUTY_SERVICES[@]}")
CORE_START_SERVICES=("${ALL_DUTY_SERVICES[@]}")
ALL_START_SERVICES=("${ALL_DUTY_SERVICES[@]}")
STOP_SERVICES=(
    gateway infra system bpm
    dynamic inspection twin protocol
    routing topology orchestration runtime policy capability resource
    alarm work-order maintenance scene-3d gis
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
    local raw_name=$1
    local service_name
    service_name=$(canonical_service_name "$raw_name")
    local show_logs=${2:-false}  # 第二个参数控制是否显示日志
    if [ "$raw_name" != "$service_name" ]; then
        echo -e "${YELLOW}   旧名 ${raw_name} → 正名 ${service_name}${NC}"
    fi
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

    # 本仓 SNAPSHOT API 需先 install 到 ~/MavenRepositoy；否则 spring-boot:run 会去远程找 jar
    # 一律从仓库根 -pl <相对路径> -am，才能拉到跨父工程依赖（如 orchestration → emergency-api）
    case "$service_name" in
        resource|runtime|orchestration|policy|capability|topology|routing|\
        alarm|work-order|maintenance|scene-3d|gis|twin|dynamic|inspection|protocol|bpm|system)
            echo -e "${BLUE}   安装 ${service_path} 及依赖到 ${MAVEN_REPO_LOCAL}（-am install -DskipTests）...${NC}"
            (
                cd "$SCRIPT_DIR" || exit 1
                mvn -Dmaven.repo.local="$MAVEN_REPO_LOCAL" -pl "$service_path" -am install -DskipTests -q
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
    
    # 每次启动覆盖写日志，只保留本次运行；避免历史 ERROR 堆积干扰排查
    # 后台启动；disown 避免启动脚本 shell 退出时 SIGHUP 带走 Maven/Spring Boot
    # nohup 不能调用 shell 函数，须直接写 mvn + -Dmaven.repo.local（所有服务共用）
    # maven.test.skip：本地启动不编/不跑测试，避免测试缺类挡住 spring-boot:run
    nohup mvn -Dmaven.repo.local="$MAVEN_REPO_LOCAL" spring-boot:run -Dspring-boot.run.profiles=local -Dmaven.test.skip=true > "$log_file" 2>&1 &
    local pid=$!
    disown -h "$pid" 2>/dev/null || true
    
    # Mac 上 InetUtils 解析主机名可能占 30s+，Maven 编译 + 冷启动常 >60s
    local max_wait=120
    local waited=0
    local check_interval=2
    
    echo -e "${BLUE}   Maven 进程 ID: $pid${NC}"
    echo -e "${BLUE}   日志文件: $log_file${NC}"
    echo -e "${BLUE}   等待服务启动（最多 ${max_wait} 秒）...${NC}"
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
    local service_name
    service_name=$(canonical_service_name "$1")
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
    echo -e "   ${YELLOW}网关入口:${NC} ${BLUE}http://localhost:15080${NC}"
    echo -e "   ${YELLOW}系统管理:${NC} ${BLUE}http://localhost:15080/admin-ui/${NC}"
    echo -e "   ${YELLOW}应急管理:${NC} ${BLUE}http://localhost:15080/emergency-admin/${NC}"
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
    echo -e "${BLUE}按职责启停（先看这里）${NC}"
    echo ""
    echo "  ./start-microservices.sh base       # 底盘：登录、网关、文件日志"
    echo "  ./start-microservices.sh biz        # 动态业务 + 巡检 + 孪生 + 协议网关"
    echo "  ./start-microservices.sh bmp        # 中台全部"
    echo "  ./start-microservices.sh all        # 底盘 + 工作流 + 动态业务 + 中台"
    echo "  ./start-microservices.sh resource   # 只开组件配置（改配置表用这个）"
    echo "  ./start-microservices.sh stop-base|stop-biz|stop-bmp|stop-all"
    echo "  ./start-microservices.sh status"
    echo "  ./start-microservices.sh logs resource"
    echo ""
    echo -e "${BLUE}中台里只开一块${NC}"
    echo "  bmp-process   组件配置 + 过程（策略/能力/运行时/编排）"
    echo "  bmp-path      路网（拓扑 + 算路）"
    echo "  alarm / work-order / maintenance / scene-3d / gis"
    echo ""
    echo -e "${BLUE}各进程正名${NC}"
    echo ""
    printf "%-16s %-58s %-8s\n" "正名" "管什么 / 路径" "端口"
    echo "----------------------------------------------------------------------------------------"
    printf "%-16s %-58s %-8s\n" "infra" "底盘·文件日志  cheers-infra/cheers-infra-server" "15082"
    printf "%-16s %-58s %-8s\n" "system" "底盘·账号权限  cheers-system/cheers-system-server" "15081"
    printf "%-16s %-58s %-8s\n" "gateway" "底盘·网关      cheers-gateway" "15080"
    printf "%-16s %-58s %-8s\n" "bpm" "工作流          cheers-bpm/cheers-bpm-server" "15083"
    printf "%-16s %-58s %-8s\n" "dynamic" "动态业务台账    cheers-dynamicbusiness/...-server" "15096"
    printf "%-16s %-58s %-8s\n" "inspection" "巡检任务" "15095"
    printf "%-16s %-58s %-8s\n" "twin" "孪生" "15094"
    printf "%-16s %-58s %-8s\n" "protocol" "设备协议网关    cheers-device-protocol-gateway-server" "8095"
    printf "%-16s %-58s %-8s\n" "resource" "中台·组件配置   cheers-resource-server" "15098"
    printf "%-16s %-58s %-8s\n" "policy" "中台·过程策略" "15105"
    printf "%-16s %-58s %-8s\n" "capability" "中台·过程能力" "15106"
    printf "%-16s %-58s %-8s\n" "runtime" "中台·过程运行" "15099"
    printf "%-16s %-58s %-8s\n" "orchestration" "中台·过程编排" "15104"
    printf "%-16s %-58s %-8s\n" "topology" "中台·路网拓扑" "15107"
    printf "%-16s %-58s %-8s\n" "routing" "中台·算路" "15108"
    printf "%-16s %-58s %-8s\n" "alarm" "中台·告警" "15097"
    printf "%-16s %-58s %-8s\n" "work-order" "中台·工单" "15110"
    printf "%-16s %-58s %-8s\n" "maintenance" "中台·维护手册" "15111"
    printf "%-16s %-58s %-8s\n" "scene-3d" "中台·三维" "15093"
    printf "%-16s %-58s %-8s\n" "gis" "中台·地图" "15109"
    echo ""
    echo "旧名 platform / platform-* 仍能敲，会收到上表正名。不要用 platform-all。"
    echo "Nacos: $NACOS_HOME   日志: logs/<正名>-server.log"
}

# 启动所有核心服务
start_all_core() {
    local show_logs=${1:-false}  # 是否显示日志
    echo -e "${BLUE}🚀 全开：底盘 + 工作流 + 动态业务 + 中台...${NC}"
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
    service_name=$(canonical_service_name "$service_name")
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

BMP_SCENE_3D_SERVICES=(scene-3d)
BMP_GIS_SERVICES=(gis)
BMP_ALARM_SERVICES=(alarm)
BMP_WORK_ORDER_SERVICES=(work-order)
BMP_MAINTENANCE_SERVICES=(maintenance)
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
        "all"|"all-services")
            echo -e "${BLUE}🚀 全开：底盘 + 工作流 + 动态业务 + 中台${NC}"
            start_all_core "$show_logs"
            ;;
        "base")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 底盘（登录 / 网关 / 文件日志）${NC}"
            echo ""
            start_suite_services "$show_logs" "${BASE_SERVICES[@]}"
            ;;
        "stop-base")
            echo -e "${BLUE}🛑 停止底盘${NC}"
            stop_suite_services "${BASE_SERVICES[@]}"
            ;;
        "biz")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 动态业务 + 巡检 + 孪生 + 协议网关${NC}"
            echo ""
            start_suite_services "$show_logs" "${BIZ_SERVICES[@]}"
            ;;
        "stop-biz")
            echo -e "${BLUE}🛑 停止动态业务套件${NC}"
            stop_suite_services "${BIZ_SERVICES[@]}"
            ;;
        "bmp"|"bmp-all"|"platform-all")
            if [ "$1" = "platform-all" ]; then
                echo -e "${YELLOW}⚠️  platform-all 已弃用：请改用 ./start-microservices.sh bmp${NC}"
            fi
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 中台全部（共 ${#BMP_SERVICES[@]} 个）${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_SERVICES[@]}"
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
        "bmp-maintenance")
            check_nacos
            check_redis
            echo -e "${BLUE}🚀 业务中台 · 维护手册服务 (bmp-maintenance)${NC}"
            echo ""
            start_suite_services "$show_logs" "${BMP_MAINTENANCE_SERVICES[@]}"
            ;;
        "stop-bmp-maintenance")
            echo -e "${BLUE}🛑 停止 bmp-maintenance${NC}"
            stop_suite_services "${BMP_MAINTENANCE_SERVICES[@]}"
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
            echo -e "${BLUE}🛑 停止中台全部${NC}"
            stop_suite_services "${BMP_SERVICES[@]}"
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

