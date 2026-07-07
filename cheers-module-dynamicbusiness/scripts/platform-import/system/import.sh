#!/usr/bin/env bash
# 系统共用包
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

run "${SCRIPT_DIR}/01_schema.sql"
run "${SCRIPT_DIR}/02_business_types.sql"
run "${SCRIPT_DIR}/03_fields.sql"
run "${SCRIPT_DIR}/04_field_groups.sql"
run "${SCRIPT_DIR}/05_models.sql"
run "${SCRIPT_DIR}/06_categories.sql"
run "${SCRIPT_DIR}/07_business_portal.sql"
run "${SCRIPT_DIR}/08_capabilities.sql"

echo "done: 系统共用包"
