#!/usr/bin/env bash
# 将老栈模型转为 ecs-react/public/scene-models 下的 GLB，并回写数据库。
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
# scripts is under cheers-module-platform/scripts → repo root is 3 levels up from scripts? 
# path: Sinopec-SpringCloud/cheers-module-platform/scripts → ../../.. = Sinopec-SpringCloud
# Prefer monorepo root:
MONOREPO="$(cd "$(dirname "$0")/../../../.." && pwd)"
DUMP="${DUMP:-/Users/kevin/Sinopec/洛阳数据/pre-dev-full-20260711_114237.sql.gz}"
MODELS="${MODELS:-$MONOREPO/Sinopec_ecs/public/static/models}"
OUT="${OUT:-$MONOREPO/ecs-react/public/scene-models}"

python3 "$(dirname "$0")/reconvert_legacy_models.py" \
  --dump "$DUMP" \
  --models-root "$MODELS" \
  --out-dir "$OUT" \
  --public-base /scene-models \
  "$@"
