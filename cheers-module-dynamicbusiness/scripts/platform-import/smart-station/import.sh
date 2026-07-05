#!/usr/bin/env bash
# 智慧站场产品包（需先导入 system/）
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

run "${SCRIPT_DIR}/05_models.sql"
run "${SCRIPT_DIR}/06_categories.sql"
run "${SCRIPT_DIR}/07_entities.sql"

echo "done: 智慧站场产品包（需先导入 system/）"
