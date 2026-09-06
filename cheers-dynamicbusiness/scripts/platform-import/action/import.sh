#!/usr/bin/env bash
# 动作库（entityTypeCode=action）seed
# 前置：Flyway 已执行至 V76（ent_action*）、V77（dynamic_action_enablement）
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

echo "== action: fields =="
run "${SCRIPT_DIR}/01_fields.sql"
echo "== action: entity type =="
run "${SCRIPT_DIR}/02_entity_type.sql"
echo "== action: base fields =="
run "${SCRIPT_DIR}/03_base_fields.sql"
echo "== action: model + categories =="
run "${SCRIPT_DIR}/04_model.sql"
echo "== action: param models (before sample actions) =="
run "${SCRIPT_DIR}/09_action_param_models.sql"
echo "== action: sample actions =="
run "${SCRIPT_DIR}/05_sample_actions.sql"
echo "== action: ensure data layout =="
run "${SCRIPT_DIR}/06_ensure_data_layout.sql"
echo "== action: ensure catalog orchestration =="
run "${SCRIPT_DIR}/07_ensure_catalog_orchestration.sql"
echo "== action: ensure layout props =="
run "${SCRIPT_DIR}/08_ensure_layout_props.sql"
echo "== action: ensure model-tab list props =="
run "${SCRIPT_DIR}/11_ensure_model_tab_list_props.sql"
echo "== action: robot atomic actions =="
run "${SCRIPT_DIR}/10_robot_atomic_actions.sql"
echo "== action: retire param models + drop execution_means from entity fields =="
run "${SCRIPT_DIR}/12_retire_param_models_and_execution_means.sql"

echo "done: action seed"
