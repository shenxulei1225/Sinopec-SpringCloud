# ============================================================================
# ZHGL 微服务启动脚本 (Windows PowerShell 版本)
# 与 start-microservices.sh（Mac）保持同一套路径与套件命令。
# ============================================================================
# 使用方法:
#   .\start-microservices.ps1                    # 显示所有可用服务
#   .\start-microservices.ps1 all                # 启动所有核心服务
#   .\start-microservices.ps1 <服务名>            # 启动单个服务
#   .\start-microservices.ps1 status             # 查看服务状态
#   .\start-microservices.ps1 stop <服务名>       # 停止服务
#   .\start-microservices.ps1 stop-all           # 停止所有服务
#   .\start-microservices.ps1 logs <服务名>       # 查看服务日志
#   .\start-microservices.ps1 nacos              # 启动 Nacos
#   .\start-microservices.ps1 nacos status       # 查看 Nacos 状态
#   .\start-microservices.ps1 nacos stop         # 停止 Nacos
#   .\start-microservices.ps1 bmp                # 推荐：过程引擎 + 路径规划
#   .\start-microservices.ps1 bmp-process        # 仅过程引擎（platform 套件）
#   .\start-microservices.ps1 bmp-path           # 仅路径规划
#   .\start-microservices.ps1 bmp-scene-3d       # 三维 scene-3d
#   .\start-microservices.ps1 bmp-gis            # GIS
#   .\start-microservices.ps1 bmp-alarm          # 告警
#   .\start-microservices.ps1 bmp-work-order     # 工单
#   .\start-microservices.ps1 twin-dev           # 孪生联调：dynamic + scene-3d + twin
#   .\start-microservices.ps1 stop-twin-dev      # 停止 twin-dev
#   .\start-microservices.ps1 stop-bmp           # 停止 bmp（process+path）
#   .\start-microservices.ps1 platform-all       # [弃用] 等同 bmp
#
# 业务中台父工程：cheers-business-middle-platform
# bmp = bmp-process + bmp-path（不含 scene-3d / gis / twin）
# twin = cheers-twin（整合层；联调请用 twin-dev）
# dynamic = cheers-dynamicbusiness\cheers-dynamicbusiness-server（勿用旧 cheers-module-dynamicbusiness）
# ============================================================================

param(
    [Parameter(Position=0)]
    [string]$Command = "",
    
    [Parameter(Position=1)]
    [string]$ServiceName = "",
    
    [Parameter()]
    [switch]$f  # 显示日志标志
)

$ErrorActionPreference = "Continue"

# 设置控制台编码为 UTF-8，解决中文乱码问题
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

# 创建日志目录
$LogDir = Join-Path $ScriptDir "logs"
if (-not (Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
}

# 服务配置映射（与 start-microservices.sh 的 get_service_path 保持一致；勿再引用已迁走的 yudao-module-*）
$ServiceConfig = @{
    "gateway"    = @{ Path = "cheers-gateway"; Port = 58080 }
    "system"     = @{ Path = "cheers-system\cheers-system-server"; Port = 58081 }
    "infra"      = @{ Path = "cheers-infra\cheers-infra-server"; Port = 58082 }
    "member"     = @{ Path = "cheers-member\cheers-member-server"; Port = 58087 }
    "bpm"        = @{ Path = "cheers-bpm\cheers-bpm-server"; Port = 58083 }
    "pay"        = @{ Path = "cheers-pay\cheers-pay-server"; Port = 58085 }
    "report"     = @{ Path = "cheers-report\cheers-report-server"; Port = 58084 }
    "mp"         = @{ Path = "cheers-mp\cheers-mp-server"; Port = 58086 }
    "product"    = @{ Path = "cheers-mall\cheers-product-server"; Port = 58100 }
    "promotion"  = @{ Path = "cheers-mall\cheers-promotion-server"; Port = 58101 }
    "trade"      = @{ Path = "cheers-mall\cheers-trade-server"; Port = 58102 }
    "statistics" = @{ Path = "cheers-mall\cheers-statistics-server"; Port = 58103 }
    "crm"        = @{ Path = "cheers-crm\cheers-crm-server"; Port = 58089 }
    "erp"        = @{ Path = "cheers-erp\cheers-erp-server"; Port = 58088 }
    "ai"         = @{ Path = "cheers-ai\cheers-ai-server"; Port = 58090 }
    "iot"        = @{ Path = "cheers-iot\cheers-iot-server"; Port = 58091 }
    "alarm"      = @{ Path = "cheers-business-middle-platform\cheers-alarm-server"; Port = 58097 }
    "bmp-alarm"  = @{ Path = "cheers-business-middle-platform\cheers-alarm-server"; Port = 58097 }
    "work-order" = @{ Path = "cheers-business-middle-platform\cheers-work-order-server"; Port = 58098 }
    "bmp-work-order" = @{ Path = "cheers-business-middle-platform\cheers-work-order-server"; Port = 58098 }
    "scene"      = @{ Path = "cheers-business-middle-platform\cheers-scene-3d-server"; Port = 58093 }
    "scene-3d"   = @{ Path = "cheers-business-middle-platform\cheers-scene-3d-server"; Port = 58093 }
    "gis"        = @{ Path = "cheers-business-middle-platform\cheers-gis-server"; Port = 58109 }
    "bmp-gis"    = @{ Path = "cheers-business-middle-platform\cheers-gis-server"; Port = 58109 }
    "twin"       = @{ Path = "cheers-twin\cheers-twin-server"; Port = 58094 }
    "cheers-twin"= @{ Path = "cheers-twin\cheers-twin-server"; Port = 58094 }
    "inspection" = @{ Path = "cheers-inspection-task\cheers-inspection-task-server"; Port = 58095 }
    "dynamic"    = @{ Path = "cheers-dynamicbusiness\cheers-dynamicbusiness-server"; Port = 58096 }
    # platform / resource：组件库、视图库
    "platform"   = @{ Path = "cheers-business-middle-platform\cheers-resource-server"; Port = 58098 }
    "resource"   = @{ Path = "cheers-business-middle-platform\cheers-resource-server"; Port = 58098 }
    "bmp-resource" = @{ Path = "cheers-business-middle-platform\cheers-resource-server"; Port = 58098 }
    "platform-runtime" = @{ Path = "cheers-business-middle-platform\cheers-runtime-server"; Port = 58099 }
    "runtime-l4" = @{ Path = "cheers-business-middle-platform\cheers-runtime-server"; Port = 58099 }
    "bmp-runtime" = @{ Path = "cheers-business-middle-platform\cheers-runtime-server"; Port = 58099 }
    "platform-orchestration" = @{ Path = "cheers-business-middle-platform\cheers-orchestration-server"; Port = 58104 }
    "orchestration" = @{ Path = "cheers-business-middle-platform\cheers-orchestration-server"; Port = 58104 }
    "bmp-orchestration" = @{ Path = "cheers-business-middle-platform\cheers-orchestration-server"; Port = 58104 }
    "platform-policy" = @{ Path = "cheers-business-middle-platform\cheers-policy-server"; Port = 58105 }
    "policy"     = @{ Path = "cheers-business-middle-platform\cheers-policy-server"; Port = 58105 }
    "bmp-policy" = @{ Path = "cheers-business-middle-platform\cheers-policy-server"; Port = 58105 }
    "platform-capability" = @{ Path = "cheers-business-middle-platform\cheers-capability-server"; Port = 58106 }
    "capability" = @{ Path = "cheers-business-middle-platform\cheers-capability-server"; Port = 58106 }
    "bmp-capability" = @{ Path = "cheers-business-middle-platform\cheers-capability-server"; Port = 58106 }
    "platform-topology" = @{ Path = "cheers-business-middle-platform\cheers-topology-server"; Port = 58107 }
    "topology"   = @{ Path = "cheers-business-middle-platform\cheers-topology-server"; Port = 58107 }
    "bmp-topology" = @{ Path = "cheers-business-middle-platform\cheers-topology-server"; Port = 58107 }
    "platform-routing" = @{ Path = "cheers-business-middle-platform\cheers-routing-server"; Port = 58108 }
    "routing"    = @{ Path = "cheers-business-middle-platform\cheers-routing-server"; Port = 58108 }
    "bmp-routing" = @{ Path = "cheers-business-middle-platform\cheers-routing-server"; Port = 58108 }
}

# 与 start-microservices.sh 保持一致
$KnownServices = @(
    "gateway", "system", "infra", "member", "bpm", "pay", "report", "mp", "product", "promotion", "trade", "statistics",
    "crm", "erp", "ai", "iot", "alarm", "work-order", "dynamic",
    "platform", "platform-runtime", "platform-orchestration", "platform-policy", "platform-capability",
    "platform-topology", "platform-routing",
    "scene", "gis", "twin", "inspection"
)

$CoreServices = @(
    "infra", "system", "gateway", "bpm", "alarm", "dynamic",
    "platform", "platform-runtime", "platform-orchestration", "platform-policy", "platform-capability",
    "platform-topology", "platform-routing",
    "scene", "gis", "twin", "inspection"
)

$AllServices = @(
    "system", "infra", "gateway", "member", "bpm", "pay", "report", "mp", "product", "promotion", "trade", "statistics",
    "crm", "erp", "ai", "iot", "alarm", "work-order", "dynamic",
    "platform", "platform-runtime", "platform-orchestration", "platform-policy", "platform-capability",
    "platform-topology", "platform-routing",
    "scene", "gis", "twin", "inspection"
)

$StopServices = @(
    "gateway", "infra", "system", "member", "bpm", "pay", "report", "mp", "product", "promotion", "trade", "statistics",
    "crm", "erp", "ai", "iot", "alarm", "work-order", "dynamic",
    "platform-routing", "platform-topology",
    "platform-orchestration", "platform-runtime", "platform-policy", "platform-capability", "platform",
    "scene", "gis", "twin", "inspection"
)

# 与 Mac start-microservices.sh 套件一致
$BmpProcessServices = @(
    "platform", "platform-policy", "platform-capability", "platform-runtime", "platform-orchestration"
)
$BmpPathServices = @("platform-topology", "platform-routing")
$BmpCoreServices = $BmpProcessServices + $BmpPathServices
$BmpScene3dServices = @("scene-3d")
$BmpGisServices = @("gis")
$BmpAlarmServices = @("alarm")
$BmpWorkOrderServices = @("work-order")
$TwinDevServices = @("dynamic", "scene-3d", "twin")
# [弃用别名] platform-all ≡ bmp
$PlatformAllServices = $BmpCoreServices

# 颜色输出函数
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success { param([string]$Message) Write-ColorOutput "[OK] $Message" "Green" }
function Write-Error { param([string]$Message) Write-ColorOutput "[ERR] $Message" "Red" }
function Write-Warning { param([string]$Message) Write-ColorOutput "[WARN] $Message" "Yellow" }
function Write-Info { param([string]$Message) Write-ColorOutput "[INFO] $Message" "Cyan" }

function Test-InCmd {
    return -not [string]::IsNullOrEmpty($env:ComSpec) -and $env:ComSpec.ToLower().EndsWith("\\cmd.exe") -and -not [string]::IsNullOrEmpty($env:PROMPT)
}

# 检查端口是否被占用
function Test-PortInUse {
    param([int]$Port)
    # 只判断监听状态，避免 TIME_WAIT/CLOSE_WAIT 等导致误判，也更快
    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($null -ne $connection) {
        if ($connection -is [Array]) { return $connection.Count -gt 0 }
        return $true
    }
    # 非管理员 / Get-NetTCPConnection 不可用时的兜底，避免误判「未运行」后又抢端口失败
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $iar = $client.BeginConnect('127.0.0.1', $Port, $null, $null)
        $connected = $iar.AsyncWaitHandle.WaitOne(300)
        if ($connected) {
            try { $client.EndConnect($iar) } catch { }
            $client.Close()
            return $true
        }
        $client.Close()
    } catch {
        # ignore
    }
    return $false
}

# 获取占用端口(监听)的进程ID
function Get-PortProcess {
    param([int]$Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($connection -and $connection.OwningProcess -and $connection.OwningProcess -ne 0) {
        return $connection.OwningProcess
    }
    return $null
}

# 检查服务是否运行
function Test-ServiceRunning {
    param([string]$ServiceName)
    
    if (-not $ServiceConfig.ContainsKey($ServiceName)) {
        return $false
    }
    
    $port = $ServiceConfig[$ServiceName].Port
    return Test-PortInUse -Port $port
}


# 检查 Nacos 是否运行（与 Mac 脚本一致：8848 /nacos/）
function Test-NacosRunning {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8848/nacos/" -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop
        return $response.StatusCode -eq 200
    } catch {
        $connection = Get-NetTCPConnection -LocalPort 8848 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
        return $null -ne $connection
    }
}

# Nacos 安装路径（可通过 NACOS_HOME 覆盖，默认 H:\nacos）
$NacosPath = if ($env:NACOS_HOME) { $env:NACOS_HOME } else { "H:\nacos" }

# 启动 Nacos
function Start-Nacos {
    Write-Info "检查 Nacos 状态..."
    
    if (Test-NacosRunning) {
        Write-Success "Nacos 运行正常"
        return $true
    }
    
    Write-Warning "Nacos 未运行"
    Write-Info "正在尝试启动 Nacos..."
    
    # 尝试查找 Nacos 启动脚本
    $nacosStartupScript = Join-Path $NacosPath "bin\startup.cmd"
    
    if (-not (Test-Path $nacosStartupScript)) {
        Write-Error "未找到 Nacos 启动脚本: $nacosStartupScript"
        Write-ColorOutput "   请手动启动 Nacos 或修改脚本中的 `$NacosPath 变量" "Yellow"
        Write-ColorOutput "   当前配置路径: $NacosPath" "Cyan"
        Write-ColorOutput "   下载地址: https://github.com/alibaba/nacos/releases" "Cyan"
        return $false
    }
    
    # 启动 Nacos（单机模式）
    $nacosLogFile = Join-Path $LogDir "nacos-startup.log"
    $nacosBinDir = Join-Path $NacosPath "bin"
    
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "startup.cmd -m standalone" -WorkingDirectory $nacosBinDir -WindowStyle Minimized
    
    # 等待 Nacos 启动
    Write-Info "等待 Nacos 启动（最多 60 秒）..."
    $maxWait = 60
    $waited = 0
    $checkInterval = 3
    
    while ($waited -lt $maxWait) {
        Start-Sleep -Seconds $checkInterval
        $waited += $checkInterval
        
        if (Test-NacosRunning) {
            Write-Success "Nacos 启动成功（耗时 $waited 秒）"
            return $true
        }
        
        if ($waited % 10 -eq 0) {
            Write-Info ("已等待 {0} 秒..." -f $waited)
        }
    }
    
    Write-Error "Nacos 启动超时（已等待 $maxWait 秒）"
    return $false
}

# Redis 安装路径与密码(如有密码请在此配置,例如 "your-password")
$RedisPath = "E:\Program Files\Redis"
$RedisPassword = "123456"

# 检查 Redis 是否运行
function Test-RedisRunning {
    # 快速端口检测，避免 redis-cli 不可用导致误判
    if (Test-PortInUse -Port 6379) {
        return $true
    }

    # 兼容非管理员权限下 Get-NetTCPConnection 不可用的场景
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $iar = $client.BeginConnect('127.0.0.1', 6379, $null, $null)
        if ($iar.AsyncWaitHandle.WaitOne(500)) {
            $client.EndConnect($iar)
            $client.Close()
            return $true
        }
        $client.Close()
    } catch {
        # ignore
    }

    # 优先使用配置目录下的 redis-cli.exe, 若不存在再尝试 PATH 中的 redis-cli
    $redisCliCandidates = @()

    if ($RedisPath -and (Test-Path $RedisPath)) {
        $redisCliFromConfig = Join-Path $RedisPath "redis-cli.exe"
        if (Test-Path $redisCliFromConfig) {
            $redisCliCandidates += $redisCliFromConfig
        }
    }

    return $false
}

# 启动 Redis
function Start-Redis {
    Write-Info "检查 Redis 状态..."
    
    if (Test-RedisRunning) {
        Write-Success "Redis 运行正常"
        return $true
    }
    
    Write-Warning "Redis 未运行"
    Write-Info "正在尝试启动 Redis..."
    
    # 检查 Redis 路径
    if (-not (Test-Path $RedisPath)) {
        Write-Warning "Redis 路径不存在: $RedisPath"
        Write-ColorOutput "   Redis 是可选的,某些功能可能需要" "Yellow"
        return $false
    }
    
    $redisServerExe = Join-Path $RedisPath "redis-server.exe"
    if (-not (Test-Path $redisServerExe)) {
        Write-Warning "redis-server.exe 不存在: $redisServerExe"
        Write-ColorOutput "   Redis 是可选的,某些功能可能需要" "Yellow"
        return $false
    }
    
    # 启动 Redis（后台运行，使用配置文件）
    Write-Info "启动 Redis..."
    $redisConfigFile = Join-Path $RedisPath "redis.windows.conf"
    if (Test-Path $redisConfigFile) {
        Start-Process -FilePath $redisServerExe -ArgumentList "`"$redisConfigFile`"" -WorkingDirectory $RedisPath -WindowStyle Minimized
    } else {
        Start-Process -FilePath $redisServerExe -WorkingDirectory $RedisPath -WindowStyle Minimized
    }
    
    # 等待 Redis 启动
    Write-Info "等待 Redis 启动（最多 10 秒）..."
    $maxWait = 10
    $waited = 0
    $checkInterval = 1
    
    while ($waited -lt $maxWait) {
        Start-Sleep -Seconds $checkInterval
        $waited += $checkInterval
        
        if (Test-RedisRunning) {
            Write-Success "Redis 启动成功"
            return $true
        }
    }
    
    Write-Warning "Redis 启动超时（已等待 $maxWait 秒）"
    Write-ColorOutput "   Redis 是可选的,某些功能可能需要" "Yellow"
    return $false
}

function Check-Redis {
    Start-Redis | Out-Null
}

# 启动单个服务
function Start-SingleService {
    param(
        [string]$ServiceName,
        [bool]$ShowLogs = $false
    )
    
    if (-not $ServiceConfig.ContainsKey($ServiceName)) {
        Write-Error "未知的服务: $ServiceName"
        Show-Services
        return $false
    }
    
    $config = $ServiceConfig[$ServiceName]
    $servicePath = Join-Path $ScriptDir $config.Path
    $port = $config.Port
    
    # 检查服务是否已运行
    if (Test-ServiceRunning -ServiceName $ServiceName) {
        Write-Warning "服务 $ServiceName 已在运行 (端口: $port)"
        $logFile = Join-Path $LogDir "$ServiceName-server.log"
        Write-ColorOutput "   [LOG] 查看实时日志:" "Yellow"
        Write-ColorOutput "      .\start-microservices.ps1 logs $ServiceName" "Cyan"
        Write-ColorOutput "   [DOC] API 文档:" "Yellow"
        Write-ColorOutput "      Swagger UI: http://localhost:$port/swagger-ui" "Cyan"
        Write-ColorOutput "      Knife4j:    http://localhost:$port/doc.html" "Cyan"
        return $true
    }
    
    # 检查服务目录是否存在
    if (-not (Test-Path $servicePath)) {
        Write-Error "服务路径不存在: $servicePath"
        return $false
    }
    
    Write-ColorOutput "[START] 启动服务: $ServiceName" "Cyan"
    Write-ColorOutput "   路径: $servicePath" "White"
    Write-ColorOutput "" "White"

    # topology/routing 依赖本仓 SNAPSHOT API，首次启动前先 install 到本地仓库，避免 Unable to find instance 实为编译失败未起来
    if ($ServiceName -in @("topology", "platform-topology", "routing", "platform-routing")) {
        $platformRoot = Join-Path $ScriptDir "cheers-business-middle-platform"
        $artifactId = Split-Path $config.Path -Leaf
        Write-Info "安装 $artifactId 及依赖到本地 Maven（-am install -DskipTests）..."
        Push-Location $platformRoot
        try {
            & mvn -pl $artifactId -am install -DskipTests -q
            if ($LASTEXITCODE -ne 0) {
                Write-Warning "依赖安装失败（exit=$LASTEXITCODE），仍尝试启动；若失败请查看日志"
            } else {
                Write-Success "依赖已就绪"
            }
        } finally {
            Pop-Location
        }
        Write-ColorOutput "" "White"
    }
    
    $logFile = Join-Path $LogDir "$ServiceName-server.log"
    $mavenCommand = "mvn spring-boot:run `"-Dspring-boot.run.profiles=local`" `"-Dfile.encoding=UTF-8`" `"-Dsun.jnu.encoding=UTF-8`""
    $workingDirectory = $servicePath

    # 启动前校验 pom，避免落到已迁走/空壳目录时只看到健康检查超时
    $pomFile = Join-Path $workingDirectory "pom.xml"
    if (-not (Test-Path $pomFile)) {
        Write-Error "服务目录缺少 pom.xml: $pomFile"
        Write-ColorOutput "   请确认路径已与 start-microservices.sh 对齐（cheers-*），勿使用旧的 yudao-module-*" "Yellow"
        return $false
    }
    
    if ($ShowLogs) {
        Write-Info "前台启动（实时输出）: $mavenCommand"
        Write-Info "按 Ctrl+C 可停止该服务"
        Write-ColorOutput "" "White"

        Push-Location $workingDirectory
        try {
            Invoke-Expression $mavenCommand
        } finally {
            Pop-Location
        }

        return $true
    }

    # 使用 Maven 启动（后台运行，输出重定向到日志文件）
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = "cmd.exe"
    $startInfo.Arguments = "/c $mavenCommand > `"$logFile`" 2>&1"
    $startInfo.WorkingDirectory = $workingDirectory
    $startInfo.UseShellExecute = $true
    $startInfo.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
    
    [void][System.Diagnostics.Process]::Start($startInfo)
    
    Write-Info "已在后台发起启动，等待服务通过健康检查..."
    Write-Info "日志文件: $logFile"
    Write-Info "等待服务启动（最多 90 秒）..."
    Write-ColorOutput "" "White"
    
    # 等待服务启动
    $maxWait = 90
    $waited = 0
    $checkInterval = 3
    
    while ($waited -lt $maxWait) {
        Start-Sleep -Seconds $checkInterval
        $waited += $checkInterval
        
        if (Test-ServiceRunning -ServiceName $ServiceName) {
            Write-ColorOutput "" "White"
            Write-Success "服务 $ServiceName 启动成功！"
            Write-ColorOutput "   端口: $port" "Cyan"
            Write-ColorOutput "   日志文件: $logFile" "Cyan"
            Write-ColorOutput "   [LOG] 查看实时日志:" "Yellow"
            Write-ColorOutput "      .\start-microservices.ps1 logs $ServiceName" "Cyan"
            Write-ColorOutput "   健康检查: curl http://localhost:$port/actuator/health" "Cyan"
            Write-ColorOutput "   [DOC] API 文档:" "Yellow"
            Write-ColorOutput "      Swagger UI: http://localhost:$port/swagger-ui" "Cyan"
            Write-ColorOutput "      Knife4j:    http://localhost:$port/doc.html" "Cyan"
            return $true
        }
        
        if ($waited % 10 -eq 0) {
            Write-Info "已等待 $waited 秒..."
        }
    }
    
    Write-ColorOutput "" "White"
    Write-Warning "服务启动超时（已等待 $maxWait 秒）"
    Write-ColorOutput "   请检查日志: $logFile" "Cyan"
    
    if (Test-Path $logFile) {
        Write-ColorOutput "" "White"
        Write-ColorOutput "   最后 30 行日志:" "Cyan"
        Write-ColorOutput "----------------------------------------------------------------" "White"
        Get-Content $logFile -Tail 30 -Encoding UTF8
        Write-ColorOutput "----------------------------------------------------------------" "White"
    }
    
    return $false
}


# 停止单个服务
function Stop-SingleService {
    param([string]$ServiceName)

    if (-not $ServiceConfig.ContainsKey($ServiceName)) {
        Write-Error "未知的服务: $ServiceName"
        return $false
    }

    $port = $ServiceConfig[$ServiceName].Port

    if (-not (Test-ServiceRunning -ServiceName $ServiceName)) {
        Write-Warning "服务 $ServiceName 未运行"
        return $true
    }

    Write-ColorOutput "[STOP] 停止服务: $ServiceName" "Cyan"

    # 优先按端口获取 PID（更准确），再做二次确认与轮询等待端口释放
    $procId = Get-PortProcess -Port $port
    if (-not $procId) {
        Write-Warning "未找到运行中的进程"
        return $false
    }

    Write-Info "终止进程: $procId"

    # 1) 先尝试优雅停止
    try {
        Stop-Process -Id $procId -ErrorAction SilentlyContinue
    } catch {
        # ignore
    }

    # 2) 短暂等待（给 JVM / Spring 一点退出时间）
    Start-Sleep -Seconds 2

    # 3) 若端口仍占用，强制杀死，并包含子进程（mvn / java 可能有子进程树）
    if (Test-PortInUse -Port $port) {
        try {
            Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
        } catch {
            # ignore
        }

        # 兜底：使用 taskkill /T 确保杀掉整个树（某些情况下 Stop-Process 不会杀子进程）
        try {
            & taskkill.exe /PID $procId /T /F 2>$null | Out-Null
        } catch {
            # ignore
        }
    }

    # 4) 轮询等待端口释放，避免“刚 kill 但端口还没回收”导致误判失败
    $maxWaitSeconds = 20
    $intervalSeconds = 1
    $waited = 0

    while ($waited -lt $maxWaitSeconds) {
        if (-not (Test-PortInUse -Port $port)) {
            Write-Success "服务 $ServiceName 已停止"
            return $true
        }
        Start-Sleep -Seconds $intervalSeconds
        $waited += $intervalSeconds
    }

    # 5) 超时仍占用：打印当前占用 PID 便于排查（可能是 PID 复用或端口被其他进程占用）
    $currentPid = Get-PortProcess -Port $port
    if ($currentPid) {
        Write-Error ("服务 {0} 停止失败（端口 {1} 仍被 PID {2} 占用）" -f $ServiceName, $port, $currentPid)
    } else {
        Write-Error ("服务 {0} 停止失败（端口 {1} 仍被占用）" -f $ServiceName, $port)
    }

    return $false
}

# 显示服务状态
function Show-Status {
    Write-ColorOutput "[STATUS] 服务状态 (快速扫描模式):" "Cyan"
    Write-ColorOutput "" "White"
    
    # 优化：一次性获取所有监听状态的 TCP 连接，极大提升 Windows 下的扫描速度
    $allConnections = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue
    # 建立端口到 PID 的快速映射表
    $portMap = @{}
    foreach ($conn in $allConnections) {
        $portMap[[int]$conn.LocalPort] = $conn.OwningProcess
    }

    $format = "{0,-15} {1,-10} {2,-10} {3,-15} {4,-40}"
    Write-ColorOutput ($format -f "服务名", "端口", "状态", "PID", "访问链接") "White"
    Write-ColorOutput ("-" * 100) "White"
    
    foreach ($svc in $KnownServices) {
        if (-not $ServiceConfig.ContainsKey($svc)) { continue }
        $config = $ServiceConfig[$svc]
        $port = $config.Port
        
        if ($portMap.ContainsKey($port)) {
            $procId = $portMap[$port]
            $url = "http://localhost:$port"
            Write-Host ($format -f $svc, $port, "运行中", $procId, $url) -ForegroundColor Green
        } else {
            Write-Host ($format -f $svc, $port, "未运行", "-", "-") -ForegroundColor Red
        }
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "[INFRA] 基础设施状态:" "Cyan"
    
    if (Test-NacosRunning) {
        Write-Host "   Nacos (8848): 运行中 - http://localhost:8848/nacos ($NacosPath)" -ForegroundColor Green
    } else {
        Write-Host "   Nacos (8848): 未运行 - 路径: $NacosPath" -ForegroundColor Red
    }
    
    if ($portMap.ContainsKey(6379)) {
        Write-Host "   Redis (6379): 运行中" -ForegroundColor Green
    } else {
        Write-Host "   Redis (6379): 未运行" -ForegroundColor Yellow
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "[LINK] 快捷访问链接:" "Cyan"
    Write-ColorOutput "   网关入口: http://localhost:58080" "Yellow"
    Write-ColorOutput "   系统管理: http://localhost:58080/admin-ui/" "Yellow"
    Write-ColorOutput "   应急管理: http://localhost:58080/emergency-admin/" "Yellow"
    Write-ColorOutput "   Nacos控制台: http://localhost:8848/nacos" "Yellow"
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "[DOC] API 文档链接:" "Cyan"
    
    $hasRunning = $false
    foreach ($svc in $KnownServices) {
        if (-not $ServiceConfig.ContainsKey($svc)) { continue }
        if ($portMap.ContainsKey($ServiceConfig[$svc].Port)) {
            $hasRunning = $true
            $port = $ServiceConfig[$svc].Port
            Write-ColorOutput "   $svc 服务:" "Yellow"
            Write-ColorOutput "      Swagger UI:  http://localhost:$port/swagger-ui" "Cyan"
            Write-ColorOutput "      Knife4j:     http://localhost:$port/doc.html" "Cyan"
        }
    }
    
    if (-not $hasRunning) {
        Write-ColorOutput "   暂无运行中的服务" "Yellow"
    }
}

# 显示所有服务
function Show-Services {
    Write-ColorOutput "[LIST] 可用微服务列表:" "Cyan"
    Write-ColorOutput "" "White"
    
    $format = "{0,-15} {1,-55} {2,-10}"
    Write-ColorOutput ($format -f "服务名", "模块路径", "端口") "White"
    Write-ColorOutput ("-" * 80) "White"
    
    foreach ($svc in $KnownServices) {
        $config = $ServiceConfig[$svc]
        Write-ColorOutput ($format -f $svc, $config.Path, $config.Port) "White"
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "使用方法:" "Cyan"
    Write-ColorOutput "  .\start-microservices.ps1                    # 显示所有可用服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 all                # 启动所有核心服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 all -f             # 启动所有核心服务（显示日志）" "White"
    Write-ColorOutput "  .\start-microservices.ps1 all-services       # 启动所有服务包括业务服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 bmp                # 过程引擎 + 路径规划（推荐）" "White"
    Write-ColorOutput "  .\start-microservices.ps1 bmp-process        # 仅过程引擎（platform 五件套）" "White"
    Write-ColorOutput "  .\start-microservices.ps1 twin-dev           # 孪生联调：dynamic + scene-3d + twin" "White"
    Write-ColorOutput "  .\start-microservices.ps1 platform-all       # [弃用] 等同 bmp" "White"
    Write-ColorOutput "  .\start-microservices.ps1 <服务名>            # 启动单个服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 status             # 查看服务状态" "White"
    Write-ColorOutput "  .\start-microservices.ps1 logs <服务名>       # 查看服务日志" "White"
    Write-ColorOutput "  .\start-microservices.ps1 stop <服务名>       # 停止服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 stop-all           # 停止所有服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 stop-twin-dev      # 停止 twin-dev" "White"
    Write-ColorOutput "  .\start-microservices.ps1 stop-bmp           # 停止 bmp" "White"
    Write-ColorOutput "  .\start-microservices.ps1 nacos              # 启动 Nacos" "White"
    Write-ColorOutput "  .\start-microservices.ps1 nacos status       # 查看 Nacos 状态" "White"
    Write-ColorOutput "  .\start-microservices.ps1 nacos stop         # 停止 Nacos" "White"
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "说明:" "Cyan"
    Write-ColorOutput "  - Nacos 默认路径: $NacosPath（可通过 NACOS_HOME 覆盖）" "White"
    Write-ColorOutput "  - 默认后台运行,日志保存到 logs/ 目录" "White"
    Write-ColorOutput "  - 使用 -f 参数可以实时查看启动日志" "White"
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "核心服务（推荐启动顺序）:" "Cyan"
    Write-ColorOutput "  1. infra     - 基础设施服务（必需,提供日志、文件等服务）" "White"
    Write-ColorOutput "  2. system    - 系统服务（必需,依赖 infra 的日志服务）" "White"
    Write-ColorOutput "  3. gateway   - 网关服务（必需）" "White"
    Write-ColorOutput "  4. bpm       - 工作流服务（必需）" "White"
    Write-ColorOutput "  5. alarm     - 告警管理服务（必需）" "White"
    Write-ColorOutput "  6. dynamic   - 动态业务（cheers-dynamicbusiness\cheers-dynamicbusiness-server，58096）" "White"
    Write-ColorOutput "  7. platform  - 平台资源库（cheers-resource-server，别名 resource，58098）" "White"
    Write-ColorOutput "     路径: cheers-business-middle-platform\cheers-resource-server" "Gray"
    Write-ColorOutput "     套件: .\start-microservices.ps1 bmp / twin-dev（与 Mac .sh 一致）" "Gray"
    Write-ColorOutput "  8. platform-policy - 平台策略（58105）" "White"
    Write-ColorOutput "  9. platform-capability - 平台能力映射（58106）" "White"
    Write-ColorOutput "  10. platform-runtime - 平台 L4 运行时（58099）" "White"
    Write-ColorOutput "  11. platform-orchestration - 平台编排/排程 run（58104，依赖 runtime）" "White"
    Write-ColorOutput "  12. platform-topology - 站场拓扑/路网（58107，路径规划必需）" "White"
    Write-ColorOutput "  13. platform-routing - 路径规划引擎（58108，试走/算路必需）" "White"
    Write-ColorOutput "     （.\start-microservices.ps1 all / platform-all 已按 7->13 顺序启动 platform 套件）" "Gray"
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "业务服务（按需启动）:" "Cyan"
    Write-ColorOutput "  - member     - 会员服务" "White"
    Write-ColorOutput "  - pay        - 支付服务" "White"
    Write-ColorOutput "  - crm        - CRM 服务" "White"
    Write-ColorOutput "  - erp        - ERP 服务" "White"
    Write-ColorOutput "  - ai         - AI 服务" "White"
    Write-ColorOutput "  - iot        - IoT 服务" "White"
}


# 启动所有核心服务
function Start-AllCore {
    param([bool]$ShowLogs = $false)
    
    Write-ColorOutput "[START] 启动所有核心服务..." "Cyan"
    Write-ColorOutput "" "White"
    
    if (-not (Start-Nacos)) {
        Write-Error "Nacos 启动失败,无法继续"
        return
    }
    Check-Redis
    Write-ColorOutput "" "White"
    
    $failedServices = @()
    
    foreach ($svc in $CoreServices) {
        if (-not (Start-SingleService -ServiceName $svc -ShowLogs $ShowLogs)) {
            $failedServices += $svc
        }
        
        if ($ShowLogs) {
            Write-ColorOutput "" "White"
            Write-ColorOutput "按 Enter 继续启动下一个服务..." "Cyan"
            Read-Host
        } else {
            Start-Sleep -Seconds 3
        }
    }
    
    Write-ColorOutput "" "White"
    if ($failedServices.Count -eq 0) {
        Write-Success "所有核心服务启动完成"
    } else {
        Write-Warning "部分服务启动失败: $($failedServices -join ', ')"
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "[TIP] 提示:" "Cyan"
    Write-ColorOutput "  - 查看服务状态: .\start-microservices.ps1 status" "White"
    Write-ColorOutput "  - 查看服务日志: Get-Content logs\<服务名>-server.log -Tail 50" "White"
    Write-ColorOutput "  - 停止所有服务: .\start-microservices.ps1 stop-all" "White"
}

# 启动所有服务
function Start-AllServices {
    param([bool]$ShowLogs = $false)
    
    Write-ColorOutput "[START] 启动所有服务（包括业务服务）..." "Cyan"
    Write-ColorOutput "" "White"
    
    # 检查 Nacos,如果未运行则尝试启动（但不阻止继续）
    if (-not (Test-NacosRunning)) {
        Write-Warning "Nacos 未运行,尝试启动..."
        Start-Nacos | Out-Null
    } else {
        Write-Success "Nacos 运行正常"
    }
    Check-Redis
    Write-ColorOutput "" "White"
    
    $failedServices = @()
    
    foreach ($svc in $AllServices) {
        if (-not (Start-SingleService -ServiceName $svc -ShowLogs $ShowLogs)) {
            $failedServices += $svc
        }
        
        if ($ShowLogs) {
            Write-ColorOutput "" "White"
            Write-ColorOutput "按 Enter 继续启动下一个服务..." "Cyan"
            Read-Host
        } else {
            Start-Sleep -Seconds 3
        }
    }
    
    Write-ColorOutput "" "White"
    if ($failedServices.Count -eq 0) {
        Write-Success "所有服务启动完成"
    } else {
        Write-Warning "部分服务启动失败: $($failedServices -join ', ')"
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "[TIP] 提示:" "Cyan"
    Write-ColorOutput "  - 查看服务状态: .\start-microservices.ps1 status" "White"
    Write-ColorOutput "  - 查看服务日志: Get-Content logs\<服务名>-server.log -Tail 50" "White"
    Write-ColorOutput "  - 停止所有服务: .\start-microservices.ps1 stop-all" "White"
}

# 停止所有服务
function Stop-AllServices {
    Write-ColorOutput "[STOP] 停止所有服务 (快速模式)..." "Cyan"
    Write-ColorOutput "" "White"

    # 优化：一次性获取所有监听端口 -> PID 的映射，避免对每个服务重复调用 Get-NetTCPConnection
    $allConnections = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue
    $portMap = @{}
    foreach ($conn in $allConnections) {
        $portMap[[int]$conn.LocalPort] = $conn.OwningProcess
    }

    $stoppedCount = 0
    $failed = @()

    foreach ($svc in $StopServices) {
        if (-not $ServiceConfig.ContainsKey($svc)) { continue }
        $port = $ServiceConfig[$svc].Port
        if (-not $portMap.ContainsKey($port)) {
            continue
        }

        $procId = $portMap[$port]
        Write-Info "停止服务: $svc (端口: $port, PID: $procId)"

        # 快速停止：优先 taskkill /T 结束整棵进程树（mvn/cmd/java 常见父子进程关系）
        try {
            & taskkill.exe /PID $procId /T /F 2>$null | Out-Null
        } catch {
            # ignore
        }

        # 短暂等待端口释放（快速模式缩短等待时间）
        $maxWaitSeconds = 5
        $intervalSeconds = 1
        $waited = 0
        while ($waited -lt $maxWaitSeconds) {
            if (-not (Test-PortInUse -Port $port)) {
                break
            }
            Start-Sleep -Seconds $intervalSeconds
            $waited += $intervalSeconds
        }

        if (-not (Test-PortInUse -Port $port)) {
            $stoppedCount++
        } else {
            $failed += $svc
        }
    }

    Write-ColorOutput "" "White"
    if ($stoppedCount -gt 0) {
        Write-Success "已停止 $stoppedCount 个服务"
    } else {
        Write-Warning "没有运行中的服务"
    }

    if ($failed.Count -gt 0) {
        Write-Warning "部分服务停止可能未完全成功(端口仍占用): $($failed -join ', ')"
    }
}

# 查看服务日志
function Show-Logs {
    param([string]$ServiceName)
    
    if ([string]::IsNullOrEmpty($ServiceName)) {
        Write-ColorOutput "[LOG] 查看所有运行中服务的日志:" "Cyan"
        Write-ColorOutput "" "White"
        
        $runningServices = @()
        foreach ($svc in $KnownServices) {
            if (Test-ServiceRunning -ServiceName $svc) {
                $runningServices += $svc
            }
        }
        
        if ($runningServices.Count -eq 0) {
            Write-Warning "没有运行中的服务"
            return
        }
        
        Write-ColorOutput "运行中的服务: $($runningServices -join ', ')" "Cyan"
        Write-ColorOutput "" "White"
        Write-ColorOutput "提示: 使用 'logs <服务名>' 查看单个服务的日志" "Yellow"
        Write-ColorOutput "" "White"
        
        foreach ($svc in $runningServices) {
            $logFile = Join-Path $LogDir "$svc-server.log"
            if (Test-Path $logFile) {
                Write-ColorOutput ("━" * 50) "Cyan"
                Write-ColorOutput "[LOG] $svc 服务日志:" "Green"
                Write-ColorOutput ("━" * 50) "Cyan"
                Get-Content $logFile -Tail 20 -Encoding UTF8
                Write-ColorOutput "" "White"
            }
        }
        
        Write-ColorOutput "实时查看日志请使用: Get-Content logs\<服务名>-server.log -Wait -Tail 50" "Cyan"
        return
    }
    
    # 查看单个服务的日志
    if (-not $ServiceConfig.ContainsKey($ServiceName)) {
        Write-Error "未知的服务: $ServiceName"
        Show-Services
        return
    }
    
    $logFile = Join-Path $LogDir "$ServiceName-server.log"
    
    if (-not (Test-Path $logFile)) {
        Write-Warning "日志文件不存在: $logFile"
        Write-Info "服务可能还未启动或日志文件尚未创建"
        return
    }
    
    $port = $ServiceConfig[$ServiceName].Port
    
    if (-not (Test-ServiceRunning -ServiceName $ServiceName)) {
        Write-Warning "服务 $ServiceName 未运行"
        Write-Info "显示最后 50 行日志:"
        Write-ColorOutput "----------------------------------------------------------------" "White"
        Get-Content $logFile -Tail 50 -Encoding UTF8
        return
    }
    
    Write-ColorOutput "[LOG] 查看服务 $ServiceName 的日志（端口: $port）" "Cyan"
    Write-ColorOutput "   日志文件: $logFile" "Cyan"
    Write-ColorOutput "   按 Ctrl+C 退出" "Yellow"
    Write-ColorOutput "----------------------------------------------------------------" "White"
    Get-Content $logFile -Wait -Tail 50 -Encoding UTF8
}

function Start-SuiteServices {
    param(
        [string[]]$Services,
        [bool]$ShowLogs = $false
    )
    foreach ($svc in $Services) {
        Start-SingleService -ServiceName $svc -ShowLogs $ShowLogs | Out-Null
        if (-not $ShowLogs) {
            Start-Sleep -Seconds 3
        }
    }
}

function Stop-SuiteServices {
    param([string[]]$Services)
    $stoppedCount = 0
    foreach ($svc in $Services) {
        if (Test-ServiceRunning -ServiceName $svc) {
            if (Stop-SingleService -ServiceName $svc) {
                $stoppedCount++
            }
        }
    }
    Write-ColorOutput "" "White"
    if ($stoppedCount -gt 0) {
        Write-Success "套件已停止 $stoppedCount 个服务"
    } else {
        Write-Warning "套件内没有运行中的服务"
    }
}

function Start-BmpSuite {
    param(
        [bool]$ShowLogs = $false,
        [string]$Label = "业务中台核心套件 (bmp = process + path)"
    )
    if (-not (Start-Nacos)) {
        Write-Error "Nacos 启动失败,无法继续"
        return
    }
    Check-Redis
    Write-ColorOutput "" "White"
    Write-ColorOutput "[START] $Label，共 $($BmpCoreServices.Count) 个" "Cyan"
    Write-ColorOutput "" "White"
    Start-SuiteServices -Services $BmpCoreServices -ShowLogs $ShowLogs
}

function Start-PlatformAll {
    param([bool]$ShowLogs = $false)
    Write-Warning "platform-all 已弃用：请改用 .\start-microservices.ps1 bmp"
    Start-BmpSuite -ShowLogs $ShowLogs
}

function Invoke-NacosCommand {
    param([string]$SubCommand = "")

    $nacosScript = Join-Path $ScriptDir "start-nacos.ps1"
    if (Test-Path $nacosScript) {
        if ([string]::IsNullOrEmpty($SubCommand)) {
            & $nacosScript
        } else {
            & $nacosScript $SubCommand
        }
        return
    }

    switch ($SubCommand.ToLower()) {
        "status" {
            if (Test-NacosRunning) { Write-Success "Nacos 运行正常 ($NacosPath)" }
            else { Write-Warning "Nacos 未运行 ($NacosPath)" }
        }
        "stop" {
            $nacosPid = Get-NetTCPConnection -LocalPort 8848 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess
            if ($nacosPid) { & taskkill.exe /PID $nacosPid /T /F 2>$null | Out-Null }
        }
        default { Start-Nacos | Out-Null }
    }
}

# 主逻辑
switch ($Command.ToLower()) {
    "" {
        Show-Services
    }
    "all" {
        Start-AllCore -ShowLogs $f.IsPresent | Out-Null
    }
    "all-services" {
        Start-AllServices -ShowLogs $f.IsPresent | Out-Null
    }
    "bmp" {
        Start-BmpSuite -ShowLogs $f.IsPresent | Out-Null
    }
    "bmp-all" {
        Start-BmpSuite -ShowLogs $f.IsPresent | Out-Null
    }
    "platform-all" {
        Start-PlatformAll -ShowLogs $f.IsPresent | Out-Null
    }
    "bmp-process" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 业务中台 · 过程引擎套件 (bmp-process)" "Cyan"
        Start-SuiteServices -Services $BmpProcessServices -ShowLogs $f.IsPresent
    }
    "bmp-path" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 业务中台 · 路径规划套件 (bmp-path)" "Cyan"
        Start-SuiteServices -Services $BmpPathServices -ShowLogs $f.IsPresent
    }
    "bmp-scene-3d" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 业务中台 · 三维服务 (bmp-scene-3d)" "Cyan"
        Start-SuiteServices -Services $BmpScene3dServices -ShowLogs $f.IsPresent
    }
    "bmp-gis" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 业务中台 · GIS (bmp-gis)" "Cyan"
        Start-SuiteServices -Services $BmpGisServices -ShowLogs $f.IsPresent
    }
    "bmp-alarm" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 业务中台 · 告警标准服务 (bmp-alarm)" "Cyan"
        Start-SuiteServices -Services $BmpAlarmServices -ShowLogs $f.IsPresent
    }
    "bmp-work-order" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 业务中台 · 工单标准服务 (bmp-work-order)" "Cyan"
        Start-SuiteServices -Services $BmpWorkOrderServices -ShowLogs $f.IsPresent
    }
    "bmp-station-dev" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 站场联调：bmp-path + bmp-scene-3d" "Cyan"
        Start-SuiteServices -Services ($BmpPathServices + $BmpScene3dServices) -ShowLogs $f.IsPresent
    }
    "twin-dev" {
        if (-not (Start-Nacos)) { Write-Error "Nacos 启动失败,无法继续"; exit 1 }
        Check-Redis
        Write-ColorOutput "[START] 孪生整合联调 (twin-dev)：dynamic + scene-3d + twin" "Cyan"
        Start-SuiteServices -Services $TwinDevServices -ShowLogs $f.IsPresent
    }
    "stop-bmp" {
        Write-ColorOutput "[STOP] 停止 bmp（process + path）" "Cyan"
        Stop-SuiteServices -Services $BmpCoreServices
    }
    "stop-bmp-all" {
        Write-ColorOutput "[STOP] 停止 bmp（process + path）" "Cyan"
        Stop-SuiteServices -Services $BmpCoreServices
    }
    "stop-bmp-process" {
        Write-ColorOutput "[STOP] 停止 bmp-process" "Cyan"
        Stop-SuiteServices -Services $BmpProcessServices
    }
    "stop-bmp-path" {
        Write-ColorOutput "[STOP] 停止 bmp-path" "Cyan"
        Stop-SuiteServices -Services $BmpPathServices
    }
    "stop-bmp-scene-3d" {
        Write-ColorOutput "[STOP] 停止 bmp-scene-3d" "Cyan"
        Stop-SuiteServices -Services $BmpScene3dServices
    }
    "stop-bmp-gis" {
        Write-ColorOutput "[STOP] 停止 bmp-gis" "Cyan"
        Stop-SuiteServices -Services $BmpGisServices
    }
    "stop-bmp-alarm" {
        Write-ColorOutput "[STOP] 停止 bmp-alarm" "Cyan"
        Stop-SuiteServices -Services $BmpAlarmServices
    }
    "stop-bmp-work-order" {
        Write-ColorOutput "[STOP] 停止 bmp-work-order" "Cyan"
        Stop-SuiteServices -Services $BmpWorkOrderServices
    }
    "stop-twin-dev" {
        Write-ColorOutput "[STOP] 停止 twin-dev" "Cyan"
        Stop-SuiteServices -Services $TwinDevServices
    }
    "status" {
        Show-Status
    }
    "logs" {
        Show-Logs -ServiceName $ServiceName
    }
    "stop" {
        if ([string]::IsNullOrEmpty($ServiceName)) {
            Write-Error "请指定要停止的服务名"
            Write-ColorOutput "   使用方法: .\start-microservices.ps1 stop <服务名>" "White"
            exit 1
        }
        Stop-SingleService -ServiceName $ServiceName | Out-Null
    }
    "stop-all" {
        Stop-AllServices | Out-Null
    }
    "nacos" {
        Invoke-NacosCommand -SubCommand $ServiceName
    }
    default {
        # 尝试作为服务名启动
        if ($ServiceConfig.ContainsKey($Command)) {
            if (-not (Start-Nacos)) {
                Write-Error "Nacos 启动失败,无法继续"
                exit 1
            }
            Check-Redis
            Write-ColorOutput "" "White"
            Start-SingleService -ServiceName $Command -ShowLogs $f.IsPresent | Out-Null
        } else {
            Write-Error "未知的命令或服务: $Command"
            Show-Services
        }
    }
}

