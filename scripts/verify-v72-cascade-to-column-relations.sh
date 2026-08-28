#!/usr/bin/env bash
# V72 验收：layout cascade → 栏间 CATEGORY_CATEGORY filter/write
# 用法（本机 dev 默认）：
#   ./scripts/verify-v72-cascade-to-column-relations.sh
# 其它环境：
#   PGHOST=… PGPORT=5432 PGUSER=… PGPASSWORD=… PGDATABASE=… ./scripts/verify-v72-cascade-to-column-relations.sh

set -euo pipefail

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-postgres}"
PGDATABASE="${PGDATABASE:-sinopec}"
export PGPASSWORD="${PGPASSWORD:-Coolhomer}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="${SCRIPT_DIR}/verify-v72-cascade-to-column-relations.sql"

psql_base=(psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1)

echo "==> 目标: ${PGUSER}@${PGHOST}:${PGPORT}/${PGDATABASE} (schema: dynamicbusiness)"
echo

echo "--- Flyway history (version 72) ---"
flyway_row="$("${psql_base[@]}" -At -c \
  "SELECT COALESCE(version,'') || '|' || COALESCE(script,'') || '|' || COALESCE(success::text,'')
   FROM dynamicbusiness.flyway_schema_history_dynamicbusiness
   WHERE version = '72'
   ORDER BY installed_rank DESC
   LIMIT 1;")"

if [[ -z "$flyway_row" ]]; then
  echo "FAIL: flyway_schema_history 中无 version 72 — 请先部署后端让 Flyway 执行 V72"
  exit 1
fi

IFS='|' read -r fw_version fw_script fw_success <<< "$flyway_row"
echo "  version=$fw_version script=$fw_script success=$fw_success"
if [[ "$fw_success" != "t" && "$fw_success" != "true" ]]; then
  echo "FAIL: V72 未成功执行"
  exit 1
fi
echo

echo "--- 数据验收 (期望查询 1～3 均为 0 行) ---"
tmp="$(mktemp)"
"${psql_base[@]}" -f "$SQL_FILE" > "$tmp"

cat "$tmp"

fail=0
for n in 1 2 3; do
  block="$(awk -v n="$n" '
    $0 ~ "^--- " n "\\)" { show=1; next }
    show && /^--- [0-9]+\)/ { exit }
    show { print }
  ' "$tmp")"
  # 去掉 SET、表头、分隔线，只数数据行
  data_lines="$(echo "$block" | grep -E '^\s*[0-9]+' || true)"
  count="$(echo "$data_lines" | grep -c . || true)"
  if [[ "$count" -ne 0 ]]; then
    echo
    echo "FAIL: 查询 $n 返回 $count 行（期望 0）"
    fail=1
  fi
done

rm -f "$tmp"

if [[ "$fail" -ne 0 ]]; then
  exit 1
fi

echo
echo "PASS: V72 Flyway 已登记且数据验收 1～3 均为 0 行"
