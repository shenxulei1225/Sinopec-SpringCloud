# ============================================================================
# Nacos 启动脚本 (Windows PowerShell)
# ============================================================================
# 使用方法:
#   .\start-nacos.ps1                 # 启动 Nacos（后台运行）
#   .\start-nacos.ps1 -f              # 启动 Nacos（显示日志）
#   .\start-nacos.ps1 status          # 查看 Nacos 状态
#   .\start-nacos.ps1 stop            # 停止 Nacos
#   .\start-nacos.ps1 logs            # 查看 Nacos 日志
# ============================================================================

param(
    [Parameter(Position = 0)]
    [string]$Command = "",

    [switch]$f
)

$ErrorActionPreference = "Continue"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

$NacosHome = if ($env:NACOS_HOME) { $env:NACOS_HOME } else { "H:\nacos" }
$NacosPort = 8848
$NacosBin = Join-Path $NacosHome "bin"
$NacosStartupScript = Join-Path $NacosBin "startup.cmd"
$NacosShutdownScript = Join-Path $NacosBin "shutdown.cmd"
$LogDir = Join-Path $ScriptDir "logs"
$StartupLogFile = Join-Path $LogDir "nacos-startup.log"

if (-not (Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
}

function Write-Info([string]$Message) { Write-Host $Message -ForegroundColor Cyan }
function Write-Ok([string]$Message) { Write-Host "✅ $Message" -ForegroundColor Green }
function Write-Warn([string]$Message) { Write-Host "⚠️  $Message" -ForegroundColor Yellow }
function Write-Err([string]$Message) { Write-Host "❌ $Message" -ForegroundColor Red }

function Test-NacosRunning {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$NacosPort/nacos/" -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop
        return $response.StatusCode -eq 200
    } catch {
        $conn = Get-NetTCPConnection -LocalPort $NacosPort -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
        return $null -ne $conn
    }
}

function Get-NacosProcessId {
    $conn = Get-NetTCPConnection -LocalPort $NacosPort -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($conn -and $conn.OwningProcess -and $conn.OwningProcess -ne 0) {
        return $conn.OwningProcess
    }
    return $null
}

function Start-NacosServer {
    param([bool]$ShowLogs = $false)

    if (Test-NacosRunning) {
        $nacosPid = Get-NacosProcessId
        Write-Warn "Nacos 已在运行 (端口: $NacosPort)"
        if ($nacosPid) { Write-Host "   进程 ID: $nacosPid" -ForegroundColor Cyan }
        Write-Host "   控制台地址: http://localhost:$NacosPort/nacos" -ForegroundColor Cyan
        Write-Host "   默认账号: nacos / nacos" -ForegroundColor Cyan
        return $true
    }

    if (-not (Test-Path $NacosStartupScript)) {
        Write-Err "Nacos 启动脚本不存在: $NacosStartupScript"
        Write-Host "   可通过 NACOS_HOME 指定安装目录，当前: $NacosHome" -ForegroundColor Cyan
        Write-Host "   下载地址: https://github.com/alibaba/nacos/releases" -ForegroundColor Cyan
        return $false
    }

    Write-Info "🚀 启动 Nacos..."
    Write-Host "   Nacos 路径: $NacosHome"
    Write-Host "   启动脚本: $NacosStartupScript"
    Write-Host "   端口: $NacosPort"
    Write-Host ""

    Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "startup.cmd -m standalone > `"$StartupLogFile`" 2>&1" -WorkingDirectory $NacosBin -WindowStyle Minimized

    if ($ShowLogs -and (Test-Path $StartupLogFile)) {
        Write-Info "📋 实时日志（按 Ctrl+C 停止查看，Nacos 将继续运行）:"
        Start-Sleep -Seconds 2
        Get-Content $StartupLogFile -Wait -Tail 30 -Encoding UTF8
        return $true
    }

    Write-Info "等待 Nacos 启动（最多 60 秒）..."
    $maxWait = 60
    $waited = 0
    while ($waited -lt $maxWait) {
        Start-Sleep -Seconds 3
        $waited += 3
        if (Test-NacosRunning) {
            $nacosPid = Get-NacosProcessId
            Write-Ok "Nacos 启动成功！"
            if ($nacosPid) { Write-Host "   进程 ID: $nacosPid" -ForegroundColor Cyan }
            Write-Host "   控制台地址: http://localhost:$NacosPort/nacos" -ForegroundColor Cyan
            Write-Host "   启动日志: $StartupLogFile" -ForegroundColor Cyan
            return $true
        }
        if ($waited % 9 -eq 0) {
            Write-Info "已等待 $waited 秒..."
        }
    }

    Write-Warn "Nacos 启动超时（已等待 $maxWait 秒）"
    if (Test-Path $StartupLogFile) {
        Get-Content $StartupLogFile -Tail 30 -Encoding UTF8
    }
    return $false
}

function Stop-NacosServer {
    if (-not (Test-NacosRunning)) {
        Write-Warn "Nacos 未运行"
        return $true
    }

    Write-Info "🛑 停止 Nacos..."

    if (Test-Path $NacosShutdownScript) {
        Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "shutdown.cmd" -WorkingDirectory $NacosBin -Wait -WindowStyle Hidden
        Start-Sleep -Seconds 3
    }

    if (Test-NacosRunning) {
        $nacosPid = Get-NacosProcessId
        if ($nacosPid) {
            & taskkill.exe /PID $nacosPid /T /F 2>$null | Out-Null
            Start-Sleep -Seconds 2
        }
    }

    if (Test-NacosRunning) {
        Write-Err "Nacos 停止失败"
        return $false
    }

    Write-Ok "Nacos 已停止"
    return $true
}

function Show-NacosStatus {
    Write-Info "📊 Nacos 状态:"
    Write-Host ""
    if (Test-NacosRunning) {
        $nacosPid = Get-NacosProcessId
        Write-Host "   状态: 运行中" -ForegroundColor Green
        Write-Host "   端口: $NacosPort"
        if ($nacosPid) { Write-Host "   进程 ID: $nacosPid" }
        Write-Host "   控制台: http://localhost:$NacosPort/nacos" -ForegroundColor Cyan
    } else {
        Write-Host "   状态: 未运行" -ForegroundColor Red
        Write-Host "   启动: .\start-nacos.ps1" -ForegroundColor Yellow
    }
    Write-Host ""
    Write-Host "   Nacos 路径: $NacosHome"
}

function Show-NacosLogs {
    $nacosOut = Join-Path $NacosHome "logs\start.out"
    if (Test-Path $nacosOut) {
        Get-Content $nacosOut -Wait -Tail 50 -Encoding UTF8
        return
    }
    if (Test-Path $StartupLogFile) {
        Get-Content $StartupLogFile -Wait -Tail 50 -Encoding UTF8
        return
    }
    Write-Warn "未找到 Nacos 日志文件"
}

switch ($Command.ToLower()) {
    { $_ -in @("", "-f", "--follow") } {
        Start-NacosServer -ShowLogs:($f.IsPresent -or $Command -in @("-f", "--follow")) | Out-Null
    }
    "status" { Show-NacosStatus }
    "stop" { Stop-NacosServer | Out-Null }
    "logs" { Show-NacosLogs }
    default {
        Write-Err "未知参数: $Command"
        Write-Host "用法: .\start-nacos.ps1 [status|stop|logs|-f]" -ForegroundColor Cyan
        exit 1
    }
}
