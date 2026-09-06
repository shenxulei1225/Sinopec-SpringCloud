#!/usr/bin/env bash
# 规范标准（entityTypeCode=standard）seed
# 前置：Flyway 已执行至 V90（ent_standard* 业务固定列）
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

echo "== standard: fields =="
run "${SCRIPT_DIR}/01_fields.sql"
echo "== standard: entity type =="
run "${SCRIPT_DIR}/02_entity_type.sql"
echo "== standard: base fields =="
run "${SCRIPT_DIR}/03_base_fields.sql"
echo "== standard: model =="
run "${SCRIPT_DIR}/04_model.sql"
echo "== standard: categories =="
run "${SCRIPT_DIR}/05_categories.sql"
echo "== standard: sample entities =="
run "${SCRIPT_DIR}/06_sample_standards.sql"
echo "== standard: library layout (SINGLE · 分类|实体|详情) =="
run "${SCRIPT_DIR}/07_library_layout.sql"

echo "done: standard seed"
