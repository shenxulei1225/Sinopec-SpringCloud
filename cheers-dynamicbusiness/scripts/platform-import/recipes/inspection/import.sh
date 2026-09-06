#!/usr/bin/env bash
# 可选检查演示配方：前置自检 → How 挂 sopHow → 样例方法选用
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
: "${DATABASE_URL:?请设置 DATABASE_URL，例如 postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec}"

psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/01_ensure_inspection_prereqs.sql"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/02_patch_inspection_item_sop_how.sql"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/03_sample_method_bindings.sql"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/04_demo_tank_sop_bindings.sql"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/05_demo_tank_route_bindings.sql"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/06_patrol_target_layout_default_identities.sql"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -f "$ROOT/07_what_workface_props.sql"
echo "recipes/inspection: import done"
