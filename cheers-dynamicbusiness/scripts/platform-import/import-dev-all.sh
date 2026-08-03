#!/usr/bin/env bash
# 开发联调：Flyway 已由应用执行后，导入 system + corridor + station + inspection-method + five-w-orchestration seed
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash "${SCRIPT_DIR}/system/import.sh"
bash "${SCRIPT_DIR}/smart-corridor/import.sh"
if [[ -f "${SCRIPT_DIR}/smart-station/import.sh" ]]; then
  bash "${SCRIPT_DIR}/smart-station/import.sh"
else
  echo "skip: smart-station/import.sh not found"
fi
if [[ -f "${SCRIPT_DIR}/inspection-method/import.sh" ]]; then
  bash "${SCRIPT_DIR}/inspection-method/import.sh"
else
  echo "skip: inspection-method/import.sh not found"
fi
if [[ -f "${SCRIPT_DIR}/five-w-orchestration/import.sh" ]]; then
  bash "${SCRIPT_DIR}/five-w-orchestration/import.sh"
else
  echo "skip: five-w-orchestration/import.sh not found"
fi
echo "done: dev all"
