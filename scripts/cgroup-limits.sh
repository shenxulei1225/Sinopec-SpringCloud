# 操作系统级内存上限（cgroup v2）。
# 管什么：每个微服务（Maven 父进程 + 业务 JVM）一共能占多少物理内存。
# 不管什么：JVM 堆怎么回收、某一类请求能同时进几份。
# 禁止：在读路径或业务代码里再猜一份默认内存；缺 cgroup 时假装已经限住。
# Windows / 无 cgroup 的机器：函数直接返回，不改启动。

CGROUP_ROOT="/sys/fs/cgroup/sinopec.services"

linux_cgroup_ready() {
    [ -f /sys/fs/cgroup/cgroup.controllers ] || return 1
    grep -qw memory /sys/fs/cgroup/cgroup.controllers || return 1
    return 0
}

cgroup_bytes() {
    local spec=$1
    case "$spec" in
        *G|*g) echo $(( ${spec%[Gg]} * 1024 * 1024 * 1024 )) ;;
        *M|*m) echo $(( ${spec%[Mm]} * 1024 * 1024 )) ;;
        *) echo "$spec" ;;
    esac
}

# 进程总占用上限。要比 -Xmx 大：还要装下元空间、线程栈、直接内存、Maven 父进程。
# dynamic 堆硬上限 3GB，整组封 4GB。inspection / protocol 堆 1.5GB，整组封 3GB。其余堆 768MB，整组封 2GB。
app_cgroup_memory_max() {
    case "$1" in
        dynamic) echo "4G" ;;
        inspection|protocol) echo "3G" ;;
        nacos) echo "1536M" ;;
        *) echo "2G" ;;
    esac
}

app_cgroup_memory_high() {
    case "$1" in
        dynamic) echo "3500M" ;;
        inspection|protocol) echo "2500M" ;;
        nacos) echo "1280M" ;;
        *) echo "1600M" ;;
    esac
}

ensure_cgroup_root() {
    linux_cgroup_ready || return 1
    if [ ! -d "$CGROUP_ROOT" ]; then
        mkdir -p "$CGROUP_ROOT" || return 1
    fi
    if [ -f "$CGROUP_ROOT/cgroup.subtree_control" ]; then
        echo "+memory +pids" > "$CGROUP_ROOT/cgroup.subtree_control" 2>/dev/null || true
    fi
    return 0
}

# 从听端口的业务 JVM 往上走到 Maven 父进程，再收集整棵树。
# 只认 java / mvn，避免把 SSH 会话或无关父进程拖进同一组。
service_tree_root_pid() {
    local pid=$1
    local ppid comm
    while [ -n "$pid" ] && [ "$pid" -gt 1 ] 2>/dev/null; do
        ppid=$(ps -o ppid= -p "$pid" 2>/dev/null | tr -d ' ')
        comm=$(ps -o comm= -p "$ppid" 2>/dev/null | tr -d ' ')
        case "$comm" in
            java|mvn)
                pid=$ppid
                ;;
            *)
                echo "$pid"
                return 0
                ;;
        esac
    done
    echo "$pid"
}

collect_pid_tree() {
    local pid=$1
    local child
    echo "$pid"
    for child in $(pgrep -P "$pid" 2>/dev/null); do
        collect_pid_tree "$child"
    done
}

write_cgroup_limits() {
    local dir=$1
    local max_spec=$2
    local high_spec=$3
    mkdir -p "$dir" || return 1
    echo "$(cgroup_bytes "$max_spec")" > "$dir/memory.max" || return 1
    echo "$(cgroup_bytes "$high_spec")" > "$dir/memory.high" || return 1
    echo 0 > "$dir/memory.swap.max" 2>/dev/null || true
    echo 1 > "$dir/memory.oom.group" 2>/dev/null || true
}

move_pids_into_cgroup() {
    local dir=$1
    shift
    local pid
    for pid in "$@"; do
        [ -n "$pid" ] || continue
        [ -d "/proc/$pid" ] || continue
        echo "$pid" > "$dir/cgroup.procs" 2>/dev/null || return 1
    done
    return 0
}

# 组里进程此刻的 RSS。已在跑再迁进去时，内核不会把旧账迁到新组，memory.current 会偏小。
cgroup_rss_mb() {
    local dir=$1
    local pid rss total=0
    [ -f "$dir/cgroup.procs" ] || { echo 0; return; }
    while read -r pid; do
        rss=$(awk '/^VmRSS:/{print $2}' "/proc/${pid}/status" 2>/dev/null)
        total=$((total + ${rss:-0}))
    done < "$dir/cgroup.procs"
    echo $((total / 1024))
}

# 新进程必须在组里出生，上限才从第一字节生效。
# 调用方把命令原样传进来。成功或退回 nohup 后都在后台，调用方用 $! 取 pid。
launch_in_service_cgroup() {
    local service_name=$1
    local log_file=$2
    shift 2
    local dir="${CGROUP_ROOT}/${service_name}"
    local max_spec high_spec
    max_spec=$(app_cgroup_memory_max "$service_name")
    high_spec=$(app_cgroup_memory_high "$service_name")
    if linux_cgroup_ready && ensure_cgroup_root && write_cgroup_limits "$dir" "$max_spec" "$high_spec"; then
        echo -e "${BLUE}   操作系统上限: ${max_spec}（Maven+业务进程合计，软压 ${high_spec}）${NC}"
        (
            echo $$ > "$dir/cgroup.procs" || exit 1
            exec "$@"
        ) > "$log_file" 2>&1 < /dev/null &
        return 0
    fi
    nohup "$@" > "$log_file" 2>&1 < /dev/null &
}

# 把某个已在跑的服务整组（Maven + 业务 JVM + 子进程）迁进自己的内存组。
# 缺端口、缺进程、或缺 cgroup 时返回非 0，由调用方打印，不假装已限住。
apply_service_cgroup() {
    local service_name=$1
    local port pid root_pid dir max_spec high_spec
    if ! linux_cgroup_ready; then
        return 1
    fi
    if ! ensure_cgroup_root; then
        echo -e "${YELLOW}⚠️  建不成操作系统内存组 ${CGROUP_ROOT}${NC}"
        return 1
    fi
    port=$(get_service_port "$service_name")
    if [ -z "$port" ]; then
        return 1
    fi
    pid=$(get_service_app_pid "$port")
    if [ -z "$pid" ]; then
        return 1
    fi
    root_pid=$(service_tree_root_pid "$pid")
    dir="${CGROUP_ROOT}/${service_name}"
    max_spec=$(app_cgroup_memory_max "$service_name")
    high_spec=$(app_cgroup_memory_high "$service_name")
    if ! write_cgroup_limits "$dir" "$max_spec" "$high_spec"; then
        echo -e "${YELLOW}⚠️  写不进 ${service_name} 的内存上限${NC}"
        return 1
    fi
    # 先迁子进程，再迁根，避免迁到一半子进程又在旧组里出生。
    if ! move_pids_into_cgroup "$dir" $(collect_pid_tree "$root_pid" | tac); then
        echo -e "${YELLOW}⚠️  进程迁不进 ${service_name} 的内存组（root=${root_pid}）${NC}"
        return 1
    fi
    echo -e "   操作系统上限: ${BLUE}${max_spec}${NC}（软压 ${high_spec}，组内进程 RSS $(cgroup_rss_mb "$dir")MB）"
    return 0
}

apply_named_pid_cgroup() {
    local name=$1
    local pid=$2
    local dir max_spec high_spec
    if [ -z "$pid" ] || [ ! -d "/proc/$pid" ]; then
        return 1
    fi
    if ! ensure_cgroup_root; then
        return 1
    fi
    dir="${CGROUP_ROOT}/${name}"
    max_spec=$(app_cgroup_memory_max "$name")
    high_spec=$(app_cgroup_memory_high "$name")
    write_cgroup_limits "$dir" "$max_spec" "$high_spec" || return 1
    move_pids_into_cgroup "$dir" $(collect_pid_tree "$pid" | tac) || return 1
    echo -e "   ${name}: 上限 ${BLUE}${max_spec}${NC}，组内进程 RSS $(cgroup_rss_mb "$dir")MB"
}

apply_nacos_cgroup() {
    local pid
    pid=$(pgrep -f 'nacos.standalone=true' | head -1)
    if [ -z "$pid" ]; then
        return 0
    fi
    apply_named_pid_cgroup nacos "$pid"
}

# 给所有已在听端口的服务补上限。已经在组里的会重写上限并再迁一次，不会再起一份进程。
apply_all_running_cgroups() {
    local svc count=0
    if ! linux_cgroup_ready; then
        echo -e "${YELLOW}⚠️  这台机器没有 cgroup v2 内存控制器，操作系统上限加不上。${NC}"
        return 1
    fi
    echo -e "${BLUE}给已在跑的服务加上操作系统内存上限（不重启）${NC}"
    for svc in "${KNOWN_SERVICES[@]}"; do
        if is_service_running "$svc"; then
            echo -e " ${YELLOW}${svc}${NC}"
            if apply_service_cgroup "$svc"; then
                count=$((count + 1))
            fi
        fi
    done
    apply_nacos_cgroup || true
    echo -e "${GREEN}已给 ${count} 个业务服务加上操作系统上限${NC}"
}
