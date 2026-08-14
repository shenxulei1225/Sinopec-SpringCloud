#!/usr/bin/env bash
# 任务执行步骤 + 巡检执行记录入口 · platform-import
# 前置：Flyway V54；task_excution_record 已存在
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

run "${SCRIPT_DIR}/01_fields.sql"
run "${SCRIPT_DIR}/02_entity_types.sql"
run "${SCRIPT_DIR}/03_base_fields.sql"
run "${SCRIPT_DIR}/04_models.sql"
run "${SCRIPT_DIR}/05_patrol_execution_entry.sql"

echo "done: task-execution step + patrol entry"
