#!/usr/bin/env bash
# 检查方法（inspection_method）增量 seed
# 前置：Flyway V43（ent_inspection_method）已执行；建议先跑 system/import.sh
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

echo "== inspection-method: fields =="
run "${SCRIPT_DIR}/01_fields.sql"

echo "== inspection-method: entity type =="
run "${SCRIPT_DIR}/02_entity_type.sql"

echo "== inspection-method: base fields =="
run "${SCRIPT_DIR}/03_base_fields.sql"

echo "== inspection-method: model =="
run "${SCRIPT_DIR}/04_model.sql"

echo "== inspection-method: patch inspection_item method_template_id =="
run "${SCRIPT_DIR}/05_patch_inspection_item_method_ref.sql"

echo "== inspection-method: sample bind (optional) =="
run "${SCRIPT_DIR}/06_sample_bind.sql"

echo "== inspection-method: sample templates library (optional) =="
run "${SCRIPT_DIR}/07_sample_templates.sql"

echo "== inspection-method: bind inspection items to method templates (optional) =="
run "${SCRIPT_DIR}/08_bind_inspection_items.sql"

echo "== inspection-method: equipment inspection packages (optional, python) =="
python "${SCRIPT_DIR}/09_apply_equipment_inspection_packages.py"

echo "== inspection-method: DOMAIN equipment inspection entry =="
run "${SCRIPT_DIR}/10_domain_equipment_inspection_item.sql"

echo "done: inspection-method seed"
