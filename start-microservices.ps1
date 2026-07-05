# ============================================================================
# ZHGL 微服务启动脚本 (Windows PowerShell 版本)
# ============================================================================
# 使用方法:
#   .\start-microservices.ps1                    # 显示所有可用服务
#   .\start-microservices.ps1 all                # 启动所有核心服务
#   .\start-microservices.ps1 <服务名>            # 启动单个服务
#   .\start-microservices.ps1 status             # 查看服务状态
#   .\start-microservices.ps1 stop <服务名>       # 停止服务
#   .\start-microservices.ps1 stop-all           # 停止所有服务
#   .\start-microservices.ps1 logs <服务名>       # 查看服务日志
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

# 服务配置映射
$ServiceConfig = @{
    "gateway"    = @{ Path = "yudao-gateway"; Port = 58080 }
    "system"     = @{ Path = "yudao-module-system\yudao-module-system-server"; Port = 58081 }
    "infra"      = @{ Path = "yudao-module-infra\yudao-module-infra-server"; Port = 58082 }
    "member"     = @{ Path = "yudao-module-member\yudao-module-member-server"; Port = 58087 }
    "bpm"        = @{ Path = "yudao-module-bpm\yudao-module-bpm-server"; Port = 58083 }
    "pay"        = @{ Path = "yudao-module-pay\yudao-module-pay-server"; Port = 58085 }
    "report"     = @{ Path = "yudao-module-report\yudao-module-report-server"; Port = 58084 }
    "mp"         = @{ Path = "yudao-module-mp\yudao-module-mp-server"; Port = 58086 }
    "product"    = @{ Path = "yudao-module-mall\yudao-module-product-server"; Port = 58100 }
    "promotion"  = @{ Path = "yudao-module-mall\yudao-module-promotion-server"; Port = 58101 }
    "trade"      = @{ Path = "yudao-module-mall\yudao-module-trade-server"; Port = 58102 }
    "statistics" = @{ Path = "yudao-module-mall\yudao-module-statistics-server"; Port = 58103 }
    "crm"        = @{ Path = "yudao-module-crm\yudao-module-crm-server"; Port = 58089 }
    "erp"        = @{ Path = "yudao-module-erp\yudao-module-erp-server"; Port = 58088 }
    "ai"         = @{ Path = "yudao-module-ai\yudao-module-ai-server"; Port = 58090 }
    "iot"        = @{ Path = "yudao-module-iot\yudao-module-iot-server"; Port = 58091 }
    "alarm"      = @{ Path = "yudao-module-alarm\yudao-module-alarm-biz"; Port = 58097 }
    "facility"   = @{ Path = "yudao-module-facility-management\yudao-module-facility-management-server"; Port = 58092 }
    "scene"      = @{ Path = "yudao-module-scene-platform\yudao-module-scene-platform-server"; Port = 58093 }
    "twin"       = @{ Path = "yudao-module-twin\yudao-module-twin-biz"; Port = 58094 }
    "inspection" = @{ Path = "yudao-module-inspection-task\yudao-module-inspection-task-server"; Port = 58095 }
    "dynamic"    = @{ Path = "cheers-module-dynamicbusiness\cheers-module-dynamicbusiness-server"; Port = 58096 }
    "platform"   = @{ Path = "cheers-module-platform\cheers-module-platform-resource-server"; Port = 58098 }
    "resource"   = @{ Path = "cheers-module-platform\cheers-module-platform-resource-server"; Port = 58098 }  # platform 别名
}


# 核心服务列表（按启动顺序）
$CoreServices = @(
    "infra",
    "system",
    "gateway",
    "bpm",
    "alarm",
    "dynamic",
    "platform",
    "facility",
    "scene",
    "twin",
    "inspection"
)

# 所有服务列表
$AllServices = @(
    "infra",
    "system",
    "gateway",
    "member",
    "bpm",
    "pay",
    "report",
    "mp",
    "product",
    "promotion",
    "trade",
    "statistics",
    "crm",
    "erp",
    "ai",
    "iot",
    "alarm",
    "dynamic",
    "platform",
    "facility",
    "scene",
    "twin",
    "inspection"
)

# 颜色输出函数
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success { param([string]$Message) Write-ColorOutput "✅ $Message" "Green" }
function Write-Error { param([string]$Message) Write-ColorOutput "❌ $Message" "Red" }
function Write-Warning { param([string]$Message) Write-ColorOutput "⚠️  $Message" "Yellow" }
function Write-Info { param([string]$Message) Write-ColorOutput "🔹 $Message" "Cyan" }

function Test-InCmd {
    return -not [string]::IsNullOrEmpty($env:ComSpec) -and $env:ComSpec.ToLower().EndsWith("\\cmd.exe") -and -not [string]::IsNullOrEmpty($env:PROMPT)
}

# 检查端口是否被占用
function Test-PortInUse {
    param([int]$Port)
    # 只判断监听状态，避免 TIME_WAIT/CLOSE_WAIT 等导致误判，也更快
    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($null -eq $connection) { return $false }
    if ($connection -is [Array]) { return $connection.Count -gt 0 }
    return $true
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


# 检查 Nacos 是否运行
function Test-NacosRunning {
    # 方法1：检查端口是否被占用（最可靠）
    try {
        $connection = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
        if ($null -ne $connection) {
            return $true
        }
    } catch {
        # 继续尝试其他方法
    }
    
    # 方法2：检查 8848 端口
    try {
        $connection = Get-NetTCPConnection -LocalPort 8848 -ErrorAction SilentlyContinue
        if ($null -ne $connection) {
            return $true
        }
    } catch {
        # 继续尝试其他方法
    }
    
    # 方法3：尝试 HTTP 请求（作为最后手段）
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/" -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

# Nacos 安装路径（可根据实际情况修改）
$NacosPath = "H:\nacos"

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
        Write-ColorOutput "   📋 查看实时日志:" "Yellow"
        Write-ColorOutput "      .\start-microservices.ps1 logs $ServiceName" "Cyan"
        Write-ColorOutput "   📚 API 文档:" "Yellow"
        Write-ColorOutput "      Swagger UI: http://localhost:$port/swagger-ui" "Cyan"
        Write-ColorOutput "      Knife4j:    http://localhost:$port/doc.html" "Cyan"
        return $true
    }
    
    # 检查服务目录是否存在
    if (-not (Test-Path $servicePath)) {
        Write-Error "服务路径不存在: $servicePath"
        return $false
    }
    
    Write-ColorOutput "🚀 启动服务: $ServiceName" "Cyan"
    Write-ColorOutput "   路径: $servicePath" "White"
    Write-ColorOutput "" "White"
    
    $logFile = Join-Path $LogDir "$ServiceName-server.log"
    $mavenCommand = "mvn spring-boot:run `"-Dspring-boot.run.profiles=local`" `"-Dfile.encoding=UTF-8`" `"-Dsun.jnu.encoding=UTF-8`""
    $workingDirectory = $servicePath

    if ($ServiceName -eq "alarm") {
        $workingDirectory = $ScriptDir
        $mavenCommand = "mvn -f yudao-module-alarm\yudao-module-alarm-biz\pom.xml org.springframework.boot:spring-boot-maven-plugin:3.5.9:run `"-Dspring-boot.run.mainClass=cn.iocoder.yudao.module.alarm.AlarmServerApplication`" `"-Dspring-boot.run.profiles=local`" `"-Dfile.encoding=UTF-8`" `"-Dsun.jnu.encoding=UTF-8`""
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
            Write-ColorOutput "   📋 查看实时日志:" "Yellow"
            Write-ColorOutput "      .\start-microservices.ps1 logs $ServiceName" "Cyan"
            Write-ColorOutput "   健康检查: curl http://localhost:$port/actuator/health" "Cyan"
            Write-ColorOutput "   📚 API 文档:" "Yellow"
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

    Write-ColorOutput "🛑 停止服务: $ServiceName" "Cyan"

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
    Write-ColorOutput "📊 服务状态 (快速扫描模式):" "Cyan"
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
    
    foreach ($svc in $AllServices) {
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
    Write-ColorOutput "📋 基础设施状态:" "Cyan"
    
    # 使用内存表检查 Nacos 常用端口
    if ($portMap.ContainsKey(8848) -or $portMap.ContainsKey(8080)) {
        Write-Host "   Nacos (8848): 运行中 - http://localhost:8848/nacos" -ForegroundColor Green
    } else {
        Write-Host "   Nacos (8848): 未运行" -ForegroundColor Red
    }
    
    if ($portMap.ContainsKey(6379)) {
        Write-Host "   Redis (6379): 运行中" -ForegroundColor Green
    } else {
        Write-Host "   Redis (6379): 未运行" -ForegroundColor Yellow
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "🔗 快捷访问链接:" "Cyan"
    Write-ColorOutput "   网关入口: http://localhost:58080" "Yellow"
    Write-ColorOutput "   系统管理: http://localhost:58080/admin-ui/" "Yellow"
    Write-ColorOutput "   应急管理: http://localhost:58080/emergency-admin/" "Yellow"
    Write-ColorOutput "   Nacos控制台: http://localhost:8848/nacos" "Yellow"
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "📚 API 文档链接:" "Cyan"
    
    $hasRunning = $false
    foreach ($svc in $AllServices) {
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
    Write-ColorOutput "📋 可用微服务列表:" "Cyan"
    Write-ColorOutput "" "White"
    
    $format = "{0,-15} {1,-55} {2,-10}"
    Write-ColorOutput ($format -f "服务名", "模块路径", "端口") "White"
    Write-ColorOutput ("-" * 80) "White"
    
    foreach ($svc in $AllServices) {
        $config = $ServiceConfig[$svc]
        Write-ColorOutput ($format -f $svc, $config.Path, $config.Port) "White"
    }
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "使用方法:" "Cyan"
    Write-ColorOutput "  .\start-microservices.ps1                    # 显示所有可用服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 all                # 启动所有核心服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 all -f             # 启动所有核心服务（显示日志）" "White"
    Write-ColorOutput "  .\start-microservices.ps1 all-services       # 启动所有服务包括业务服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 <服务名>            # 启动单个服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 status             # 查看服务状态" "White"
    Write-ColorOutput "  .\start-microservices.ps1 logs <服务名>       # 查看服务日志" "White"
    Write-ColorOutput "  .\start-microservices.ps1 stop <服务名>       # 停止服务" "White"
    Write-ColorOutput "  .\start-microservices.ps1 stop-all           # 停止所有服务" "White"
    
    Write-ColorOutput "" "White"
    Write-ColorOutput "核心服务（推荐启动顺序）:" "Cyan"
    Write-ColorOutput "  1. infra     - 基础设施服务（必需,提供日志、文件等服务）" "White"
    Write-ColorOutput "  2. system    - 系统服务（必需,依赖 infra 的日志服务）" "White"
    Write-ColorOutput "  3. gateway   - 网关服务（必需）" "White"
    Write-ColorOutput "  4. bpm       - 工作流服务（必需）" "White"
    Write-ColorOutput "  5. emergency - 应急管理服务（必需）" "White"
    Write-ColorOutput "  6. alarm     - 告警管理服务（必需）" "White"
    Write-ColorOutput "  7. dynamic   - 动态业务服务（facility 等模块依赖）" "White"
    Write-ColorOutput "  8. platform  - 平台资源库（组件/视图/页面，端口 58098）" "White"
    Write-ColorOutput "     (别名 resource) 网关: /admin-api/platformresource/**" "Gray"
    
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
    
    Write-ColorOutput "🚀 启动所有核心服务..." "Cyan"
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
    Write-ColorOutput "📝 提示:" "Cyan"
    Write-ColorOutput "  - 查看服务状态: .\start-microservices.ps1 status" "White"
    Write-ColorOutput "  - 查看服务日志: Get-Content logs\<服务名>-server.log -Tail 50" "White"
    Write-ColorOutput "  - 停止所有服务: .\start-microservices.ps1 stop-all" "White"
}

# 启动所有服务
function Start-AllServices {
    param([bool]$ShowLogs = $false)
    
    Write-ColorOutput "🚀 启动所有服务（包括业务服务）..." "Cyan"
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
    Write-ColorOutput "📝 提示:" "Cyan"
    Write-ColorOutput "  - 查看服务状态: .\start-microservices.ps1 status" "White"
    Write-ColorOutput "  - 查看服务日志: Get-Content logs\<服务名>-server.log -Tail 50" "White"
    Write-ColorOutput "  - 停止所有服务: .\start-microservices.ps1 stop-all" "White"
}

# 停止所有服务
function Stop-AllServices {
    Write-ColorOutput "🛑 停止所有服务 (快速模式)..." "Cyan"
    Write-ColorOutput "" "White"

    # 优化：一次性获取所有监听端口 -> PID 的映射，避免对每个服务重复调用 Get-NetTCPConnection
    $allConnections = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue
    $portMap = @{}
    foreach ($conn in $allConnections) {
        $portMap[[int]$conn.LocalPort] = $conn.OwningProcess
    }

    $stoppedCount = 0
    $failed = @()

    foreach ($svc in $AllServices) {
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
        Write-ColorOutput "📋 查看所有运行中服务的日志:" "Cyan"
        Write-ColorOutput "" "White"
        
        $runningServices = @()
        foreach ($svc in $AllServices) {
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
                Write-ColorOutput "📋 $svc 服务日志:" "Green"
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
    
    Write-ColorOutput "📋 查看服务 $ServiceName 的日志（端口: $port）" "Cyan"
    Write-ColorOutput "   日志文件: $logFile" "Cyan"
    Write-ColorOutput "   按 Ctrl+C 退出" "Yellow"
    Write-ColorOutput "----------------------------------------------------------------" "White"
    Get-Content $logFile -Wait -Tail 50 -Encoding UTF8
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

