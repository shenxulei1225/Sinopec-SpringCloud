#!/usr/bin/env bash
# 空间层级导入：facility / zone / region 修订
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PSQL="${PSQL:-psql postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec}"

echo "== spatial-hierarchy: entity types =="
$PSQL -v ON_ERROR_STOP=1 -f "$DIR/02_entity_types.sql"

echo "== spatial-hierarchy: fields =="
$PSQL -v ON_ERROR_STOP=1 -f "$DIR/03_fields.sql"

echo "== spatial-hierarchy: models =="
$PSQL -v ON_ERROR_STOP=1 -f "$DIR/05_models.sql"

echo "spatial-hierarchy import done."
