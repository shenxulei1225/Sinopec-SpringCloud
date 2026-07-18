# cheers-module-business-middle-platform 聚合模块：编译全平台子模块，以及通过根目录脚本启动/停止 platform 微服务（58098）
# 旧根目录 cheers-module-business-middle-platform-resource/build.ps1 已废弃，请使用本脚本或
# cheers-module-business-middle-platform-resource-server/build.ps1
param(
    [Parameter(Position = 0)]
    [ValidateSet('build', 'start', 'stop', 'restart', 'status')]
    [string]$Action = 'build'
)

$ErrorActionPreference = 'Stop'
$ModuleRoot = $PSScriptRoot
$RepoRoot = Join-Path $ModuleRoot '..'
$StartScript = Join-Path $RepoRoot 'start-microservices.ps1'
$ServiceName = 'platform'

function Invoke-StartScript {
    param([string[]]$Args)
    if (-not (Test-Path $StartScript)) {
        Write-Error "未找到启动脚本: $StartScript"
        exit 1
    }
    & $StartScript @Args
    if ($null -ne $LASTEXITCODE -and $LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

switch ($Action) {
    'build' {
        Set-Location $RepoRoot
        Write-Host "Building cheers-module-business-middle-platform (from repo root)..." -ForegroundColor Cyan
        mvn install --% -pl cheers-module-business-middle-platform -am -DskipTests
        if ($LASTEXITCODE -ne 0) {
            Set-Location $ModuleRoot
            Write-Host "Retry: mvn install from cheers-module-business-middle-platform/ ..." -ForegroundColor Yellow
            mvn install --% -DskipTests
        }
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        Write-Host "BUILD SUCCESS" -ForegroundColor Green
    }
    'start' {
        Invoke-StartScript @($ServiceName)
    }
    'stop' {
        Invoke-StartScript @('stop', $ServiceName)
    }
    'restart' {
        Invoke-StartScript @('stop', $ServiceName)
        Start-Sleep -Seconds 2
        Invoke-StartScript @($ServiceName)
    }
    'status' {
        Invoke-StartScript @('status')
    }
}
