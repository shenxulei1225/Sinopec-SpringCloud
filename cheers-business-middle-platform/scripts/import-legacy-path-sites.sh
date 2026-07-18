#!/usr/bin/env bash
# 金桥 / 洛阳：老 MySQL dump → platform_path_network + ent_point（巡检点）
# 默认读取 /Users/kevin/Sinopec/洛阳数据/pre-dev-full-*.sql.gz
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
DUMP="${1:-/Users/kevin/Sinopec/洛阳数据/pre-dev-full-20260711_114237.sql.gz}"

# facilityId 以本机 seed 为准（FAC-JINQIAO=44, FAC-LUOYANG-SHENGRUI=45）；其它环境请改参数
python3 "${ROOT}/import_legacy_path_sites.py" \
  --dump "${DUMP}" \
  --site 121212:44:FAC-JINQIAO \
  --site 121240:45:FAC-LUOYANG-SHENGRUI \
  --publish
