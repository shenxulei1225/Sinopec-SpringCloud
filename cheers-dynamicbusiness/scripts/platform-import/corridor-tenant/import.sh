#!/usr/bin/env bash
# 智慧管廊租户（默认 tenant 2 · 账号 zhgl）全量 seed
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGDATABASE="${PGDATABASE:-sinopec}"
PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-Coolhomer}"

CORRIDOR_TENANT_ID="${CORRIDOR_TENANT_ID:-2}"

run() {
  echo ">> psql -f $(basename "$1")"
  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 \
    -v corridor_tenant_id="$CORRIDOR_TENANT_ID" \
    -f "$1"
}

echo "corridor tenant_id=${CORRIDOR_TENANT_ID} (login: zhgl / 密码 123456，与 tenant 1 admin 相同)"

run "${SCRIPT_DIR}/seed/00_repair_sequences.sql"
run "${SCRIPT_DIR}/seed/system_tenant_zhgl.sql"
if [[ -f "${SCRIPT_DIR}/evict_zhgl_role_cache.sh" ]]; then
  bash "${SCRIPT_DIR}/evict_zhgl_role_cache.sh" || true
fi
run "${SCRIPT_DIR}/seed/dynamic_corridor_spatial_entity_types.sql"
run "${SCRIPT_DIR}/seed/dynamic_corridor_structure.sql"
run "${SCRIPT_DIR}/seed/dynamic_corridor_zone_structure_split.sql"
run "${SCRIPT_DIR}/seed/dynamic_corridor_field_library.sql"
run "${SCRIPT_DIR}/seed/dynamic_corridor_model_fields.sql"
run "${SCRIPT_DIR}/seed/dynamic_corridor_base_field_labels.sql"

echo ">> export corridor spatial entities from zhgl_import_temp"
EXPORT_PY="${SCRIPT_DIR}/.venv/bin/python"
if [[ ! -x "$EXPORT_PY" ]]; then
  EXPORT_PY="python3"
fi
CORRIDOR_TENANT_ID="${CORRIDOR_TENANT_ID}" "$EXPORT_PY" "${SCRIPT_DIR}/export_corridor_spatial_from_zhgl.py"
run "${SCRIPT_DIR}/seed/dynamic_corridor_spatial_entities.generated.sql"

if [[ -f "${SCRIPT_DIR}/../system/evict_category_tree_cache.sh" ]]; then
  bash "${SCRIPT_DIR}/../system/evict_category_tree_cache.sh" || true
fi

echo "done: corridor-tenant (facility + zone spatial; structure type optional)"
