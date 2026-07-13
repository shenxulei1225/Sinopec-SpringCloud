# platform 资源库：编译，以及通过根目录 start-microservices.ps1 启动/停止（58098）
param(
    [Parameter(Position = 0)]
    [ValidateSet('build', 'start', 'stop', 'restart', 'status')]
    [string]$Action = 'build'
)

$ErrorActionPreference = 'Stop'
$ModuleRoot = $PSScriptRoot
$RepoRoot = Join-Path $ModuleRoot '..\..'
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
        Set-Location $ModuleRoot
        Write-Host "Building cheers-module-platform-resource-server ..." -ForegroundColor Cyan
        mvn clean install --% -DskipTests
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
