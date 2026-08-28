#!/usr/bin/env bash
# 标准作业流程SOP（entityTypeCode=sop）seed
# 前置：Flyway 已执行至 V75（ent_sop_step_template*、SOP 模板/实例列、设备检查绑定表）
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

echo "== sop: fields =="
run "${SCRIPT_DIR}/01_fields.sql"
echo "== sop: entity type =="
run "${SCRIPT_DIR}/02_entity_type.sql"
echo "== sop: base fields =="
run "${SCRIPT_DIR}/03_base_fields.sql"
echo "== sop: model =="
run "${SCRIPT_DIR}/04_model.sql"
echo "== sop: step template fields =="
run "${SCRIPT_DIR}/10_step_template_fields.sql"
echo "== sop: step template entity type =="
run "${SCRIPT_DIR}/11_step_template_entity_type.sql"
echo "== sop: template/instance fields =="
run "${SCRIPT_DIR}/12_sop_template_instance_fields.sql"
echo "== sop: categories + step templates + sample templates =="
run "${SCRIPT_DIR}/13_sop_categories_and_sample_templates.sql"
echo "== sop: library layout =="
run "${SCRIPT_DIR}/14_sop_library_layout.sql"
echo "== sop: library layout + relations fix =="
run "${SCRIPT_DIR}/15_sop_library_layout_relations_fix.sql"
echo "== sop: retire inspection_method metadata =="
run "${SCRIPT_DIR}/05_retire_inspection_method_metadata.sql"
echo "== sop: sample sops =="
run "${SCRIPT_DIR}/06_sample_sops.sql"
echo "== sop: leak + PPE SOPs =="
run "${SCRIPT_DIR}/07_seed_leak_and_ppe_sops.sql"
echo "== sop: five-w helpers (refresh signature) =="
run "${SCRIPT_DIR}/../five-w-orchestration/00_helpers.sql"
echo "== sop: five-w orchestration =="
run "${SCRIPT_DIR}/08_five_w_orchestration.sql"
echo "== sop: retire field_work_standard artifacts =="
run "${SCRIPT_DIR}/09_retire_field_work_standard_artifacts.sql"

echo "done: sop seed"
