#!/usr/bin/env bash
# 标准检查项 seed（检查项分类/挂接/型号适用；不含 inspection_method）
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGDATABASE="${PGDATABASE:-sinopec}"
PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-Coolhomer}"

run_py() {
  echo ">> python $(basename "$1")"
  python "$1"
}

echo "== inspection-item: video monitoring leaf =="
run_py "${SCRIPT_DIR}/16_fix_video_monitoring_leaf_items.py"

echo "== inspection-item: dedupe + suffix =="
run_py "${SCRIPT_DIR}/17_dedupe_and_suffix_inspection_items.py"

echo "== inspection-item: security specialty =="
run_py "${SCRIPT_DIR}/18_fix_security_specialty_leaf_items.py"

echo "== inspection-item: remaining security =="
run_py "${SCRIPT_DIR}/19_fix_remaining_security_leaf_items.py"

echo "== inspection-item: instrumentation =="
run_py "${SCRIPT_DIR}/20_fix_instrumentation_leaf_items.py"

echo "== inspection-item: fill remaining equipment leaves =="
run_py "${SCRIPT_DIR}/21_fill_remaining_equipment_leaf_items.py"

echo "== inspection-item: sync inspection category links =="
run_py "${SCRIPT_DIR}/22_sync_inspection_category_links_from_cascade.py"

echo "== inspection-item: model applicability from category links =="
run_py "${SCRIPT_DIR}/09_apply_equipment_inspection_packages.py"

echo "== inspection-item: retire DOMAIN entries =="
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 -f "${SCRIPT_DIR}/12_retire_inspection_item_domains.sql"

echo "done: inspection-item seed (SOP 见 ../sop/import.sh)"
