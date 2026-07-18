#!/usr/bin/env bash
# 按磁盘 migration SQL 重算 flyway 历史 checksum（改已执行 V*.sql 或 regenerate V1 后必跑）
# 通用说明见 .cursor/rules/flyway-migration.mdc
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SERVER="${ROOT}/cheers-dynamicbusiness-server"

PGHOST="${PGHOST:-127.0.0.1}"
PGPORT="${PGPORT:-5432}"
PGDATABASE="${PGDATABASE:-sinopec}"
PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-Coolhomer}"

echo ">> flyway:repair (${PGDATABASE} / dynamicbusiness)"
cd "${SERVER}"
mvn -q flyway:repair \
  -Dflyway.url="jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}" \
  -Dflyway.user="${PGUSER}" \
  -Dflyway.password="${PGPASSWORD}"

echo ">> current history:"
psql -h "${PGHOST}" -p "${PGPORT}" -U "${PGUSER}" -d "${PGDATABASE}" -c \
  "SELECT version, script, checksum, success
   FROM dynamicbusiness.flyway_schema_history_dynamicbusiness
   WHERE version IS NOT NULL
   ORDER BY installed_rank;"

echo "done: repair complete — restart dynamic service to verify"
