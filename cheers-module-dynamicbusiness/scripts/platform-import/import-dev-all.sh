#!/usr/bin/env bash
# 开发联调：system + corridor + station
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash "${SCRIPT_DIR}/system/import.sh"
bash "${SCRIPT_DIR}/smart-corridor/import.sh"
bash "${SCRIPT_DIR}/smart-station/import.sh"
echo "done: dev all"
