#!/usr/bin/env bash
# 五维编排 bundle seed（P0 平台目录 + P1 facility）
# 前置：Flyway V63；system seed 已导入 entity_type
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGDATABASE="${PGDATABASE:-sinopec}"
PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-Coolhomer}"

run() {
  echo ">> psql -f $(basename "$1")"
  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 -f "$1"
}

echo "== five-w-orchestration: helpers =="
run "${SCRIPT_DIR}/00_helpers.sql"

echo "== five-w-orchestration: P0 (equipment / region / inspection_item / inspection_content) =="
run "${SCRIPT_DIR}/01_p0_orchestration.sql"

echo "== five-w-orchestration: P1 (facility) =="
run "${SCRIPT_DIR}/02_p1_facility.sql"

echo "== five-w-orchestration: P1 (zone / Constructure) =="
run "${SCRIPT_DIR}/03_p1_zone_constructure.sql"

echo "== five-w-orchestration: P1 (inspection_method / patrol / task) =="
run "${SCRIPT_DIR}/04_p1_inspection_patrol.sql"

echo "== five-w-orchestration: P2 (backfill missing entity types) =="
run "${SCRIPT_DIR}/05_p2_ledger_backfill.sql"

echo "== five-w-orchestration: inspection_item Who entity layout =="
run "${SCRIPT_DIR}/06_inspection_item_who_entity_layout.sql"

echo "== five-w-orchestration: stamp columnSection=OBJECT on unstamped model/entity (migrate off workspaceBand) =="
run "${SCRIPT_DIR}/07_stamp_column_section_object.sql"

echo "done: five-w-orchestration seed"
