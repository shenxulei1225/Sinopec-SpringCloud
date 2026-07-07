#!/usr/bin/env bash
# 开发联调：system + corridor + station（站场包不存在时跳过）
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash "${SCRIPT_DIR}/system/import.sh"
bash "${SCRIPT_DIR}/smart-corridor/import.sh"
if [[ -f "${SCRIPT_DIR}/smart-station/import.sh" ]]; then
  bash "${SCRIPT_DIR}/smart-station/import.sh"
else
  echo "skip: smart-station/import.sh not found"
fi
echo "done: dev all"
