# Seed work-process demo data and verify gateway APIs.
# Usage: powershell -File script/seed-work-process-demo.ps1

$ErrorActionPreference = 'Stop'
$env:NO_PROXY = '127.0.0.1,localhost'
$env:no_proxy = '127.0.0.1,localhost'
if (-not $env:PGPASSWORD) { $env:PGPASSWORD = 'Coolhomer' }

$SqlFile = Join-Path $PSScriptRoot 'seed-work-process-demo.sql'
if (-not (Test-Path $SqlFile)) { throw "SQL not found: $SqlFile" }

Write-Host "==> run $SqlFile"
& psql -h 127.0.0.1 -U postgres -d sinopec -v ON_ERROR_STOP=1 -f $SqlFile
if ($LASTEXITCODE -ne 0) { throw "psql failed, exit=$LASTEXITCODE" }

Write-Host '==> login and verify APIs'
$loginBody = '{"username":"admin","password":"123456"}'
$login = Invoke-RestMethod -Uri 'http://127.0.0.1:58080/admin-api/system/auth/login' `
    -Method Post -Body $loginBody -ContentType 'application/json; charset=utf-8' `
    -Headers @{ 'tenant-id' = '1' } -TimeoutSec 15
if ($login.code -ne 0 -and $login.code -ne 200) {
    throw "login failed: $($login.msg)"
}
$token = $login.data.accessToken
$headers = @{
    Authorization = "Bearer $token"
    'tenant-id'   = '1'
}

function Show-Page([string]$name, [string]$url) {
    $r = Invoke-RestMethod -Uri $url -Headers $headers -TimeoutSec 15
    Write-Host ("  {0}: code={1} total={2}" -f $name, $r.code, $r.data.total)
    foreach ($row in @($r.data.list | Select-Object -First 5)) {
        $label = if ($row.woNo) { $row.woNo } elseif ($row.caseNo) { $row.caseNo } elseif ($row.code) { $row.code } elseif ($row.title) { $row.title } else { $row.id }
        Write-Host ("    - id={0} {1} status={2}" -f $row.id, $label, $row.status)
    }
}

Show-Page 'work-order' 'http://127.0.0.1:58080/admin-api/work-order/orders/page?pageNo=1&pageSize=20'
Show-Page 'handbooks' 'http://127.0.0.1:58080/admin-api/maintenance/handbooks/page?pageNo=1&pageSize=20'
Show-Page 'binding' 'http://127.0.0.1:58080/admin-api/maintenance/binding-rules/page?pageNo=1&pageSize=20'
Show-Page 'calendar' 'http://127.0.0.1:58080/admin-api/maintenance/calendar/page?pageNo=1&pageSize=20'
Show-Page 'corrective' 'http://127.0.0.1:58080/admin-api/maintenance/corrective-cases/page?pageNo=1&pageSize=20'

Write-Host ''
Write-Host 'Open these routes after login (admin / 123456, tenant 1):'
Write-Host '  /work-order'
Write-Host '  /work-order/<id of WO-DEMO-0002>  (IN_PROGRESS: check steps / complete)'
Write-Host '  /work-order/<id of WO-DEMO-0001>  (DISPATCHED: start then steps)'
Write-Host '  /maintenance/handbooks'
Write-Host '  /maintenance/binding-rules'
Write-Host '  /maintenance/calendar'
Write-Host '  /maintenance/corrective'
