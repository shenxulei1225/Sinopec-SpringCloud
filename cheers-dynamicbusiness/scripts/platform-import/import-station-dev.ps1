# 开发联调：Flyway 已由应用执行后，导入 system + smart-station（不含管廊）
# 用法: .\import-station-dev.ps1
param(
    [switch]$SkipVerify
)

$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
try { chcp 65001 | Out-Null } catch { }

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$SeedDir = Join-Path $ScriptDir 'system\seed'
$StationDir = Join-Path $ScriptDir 'smart-station'

$env:PGPASSWORD = if ($env:PGPASSWORD) { $env:PGPASSWORD } else { 'Coolhomer' }
$env:PGCLIENTENCODING = 'UTF8'
$PgHost = if ($env:PGHOST) { $env:PGHOST } else { '127.0.0.1' }
$PgPort = if ($env:PGPORT) { $env:PGPORT } else { '5432' }
$PgDb = if ($env:PGDATABASE) { $env:PGDATABASE } else { 'sinopec' }
$PgUser = if ($env:PGUSER) { $env:PGUSER } else { 'postgres' }

function Invoke-SeedSql([string]$Path) {
    Write-Host ">> $(Split-Path $Path -Leaf)" -ForegroundColor Cyan
    if (-not (Test-Path -LiteralPath $Path)) {
        throw "SQL file not found: $Path"
    }
    # Windows 上 psql -f 会按系统 ANSI(GBK) 读文件，UTF-8 seed 须从 stdin 以 UTF-8 喂入
    $sql = [System.IO.File]::ReadAllText($Path, [System.Text.UTF8Encoding]::new($false))
    $sql | & psql -h $PgHost -p $PgPort -U $PgUser -d $PgDb -v ON_ERROR_STOP=1 --no-psqlrc
    if ($LASTEXITCODE -ne 0) { throw "psql failed: $Path" }
}

Write-Host '=== system seed ===' -ForegroundColor Green
@(
    'dynamic_entity_type', 'dynamic_entity_type_config', 'dynamic_entity_type_relation',
    'dynamic_entity_type_base_field', 'dynamic_field', 'dynamic_group', 'dynamic_group_relation',
    'dynamic_model', 'dynamic_category_equipment.generated', 'dynamic_model_equipment.generated',
    'dynamic_model_category_equipment.generated', 'dynamic_category', 'dynamic_category_type',
    'dynamic_business', 'dynamic_business_entry',
    'business_capability', 'dynamic_entity_equipment_dev_sample'
) | ForEach-Object { Invoke-SeedSql (Join-Path $SeedDir "$_.sql") }

Write-Host '=== legacy CAT -> EQCAT merge (idempotent if no CAT-*) ===' -ForegroundColor Green
python (Join-Path $ScriptDir 'migrate_legacy_cat_to_eqcat.py') --apply
if ($LASTEXITCODE -ne 0) { throw 'migrate_legacy_cat_to_eqcat.py failed' }

Write-Host '=== smart-station ===' -ForegroundColor Green
@('04_extension_fields', '05_models', '06_model_field_assignments') | ForEach-Object {
    Invoke-SeedSql (Join-Path $StationDir "$_.sql")
}

if (-not $SkipVerify) {
    Write-Host '=== verify V3 code columns ===' -ForegroundColor Green
    Invoke-SeedSql (Join-Path $ScriptDir 'verify-v3-seed.sql')
}

Write-Host 'done: system + smart-station' -ForegroundColor Green
