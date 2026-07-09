#!/usr/bin/env bash
# 系统共用包 · 仅 seed（建表由 Flyway V1→V2→V3 负责，勿在此重复执行 DDL）
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

run "${SCRIPT_DIR}/seed/dynamic_entity_type.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_type_config.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_type_relation.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_type_base_field.sql"
run "${SCRIPT_DIR}/seed/dynamic_field.sql"
run "${SCRIPT_DIR}/seed/dynamic_group.sql"
run "${SCRIPT_DIR}/seed/dynamic_group_relation.sql"
run "${SCRIPT_DIR}/seed/dynamic_model.sql"
run "${SCRIPT_DIR}/seed/dynamic_category.sql"
run "${SCRIPT_DIR}/seed/dynamic_business.sql"
run "${SCRIPT_DIR}/seed/dynamic_business_entry.sql"
run "${SCRIPT_DIR}/seed/business_capability.sql"

echo "done: 系统共用 seed（前置：Flyway V1→V2→V3 已执行）"
