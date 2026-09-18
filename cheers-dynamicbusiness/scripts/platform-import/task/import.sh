#!/usr/bin/env bash
# 任务底座打开步骤树（混挂检查项 + 动作）
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGDATABASE="${PGDATABASE:-sinopec}"
PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-Coolhomer}"

echo ">> psql -f 01_enable_step_tree.sql"
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 \
  -f "${SCRIPT_DIR}/01_enable_step_tree.sql"

echo ">> psql -f 02_sample_visible_task.sql"
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 \
  -f "${SCRIPT_DIR}/02_sample_visible_task.sql"

echo ">> psql -f 03_assign_jinqiao_facility_to_orphan_tasks.sql"
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 \
  -f "${SCRIPT_DIR}/03_assign_jinqiao_facility_to_orphan_tasks.sql"

echo "done: task step tree + sample + jinqiao facility"
