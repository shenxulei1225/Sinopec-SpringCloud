#!/usr/bin/env bash
# 系统共用包 · 仅 seed（建表由 Flyway V1→V2→V3→V4 负责，勿在此重复执行 DDL）
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

run "${SCRIPT_DIR}/seed/dynamic_entity_type.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_type_config.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_type_relation.sql"
run "${SCRIPT_DIR}/seed/dynamic_seed_rename_pinyin_field_codes.sql"
run "${SCRIPT_DIR}/seed/dynamic_seed_base_field_schema.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_type_base_field.sql"
run "${SCRIPT_DIR}/seed/dynamic_base_field_library_fields.sql"
run "${SCRIPT_DIR}/seed/dynamic_field.sql"
run "${SCRIPT_DIR}/seed/dynamic_group.sql"
run "${SCRIPT_DIR}/seed/dynamic_group_relation.sql"
run "${SCRIPT_DIR}/seed/dynamic_model.sql"
run "${SCRIPT_DIR}/seed/dynamic_model_task.sql"
run "${SCRIPT_DIR}/seed/dynamic_model_facility.sql"
run "${SCRIPT_DIR}/seed/dynamic_model_region.sql"
run "${SCRIPT_DIR}/seed/dynamic_region_intro_fields.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_equipment.generated.sql"
run "${SCRIPT_DIR}/seed/dynamic_model_equipment.generated.sql"
run "${SCRIPT_DIR}/seed/dynamic_model_category_equipment.generated.sql"
run "${SCRIPT_DIR}/seed/dynamic_base_field_library_assignments.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_task.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_region.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_region_intro_content.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_region_intro_cover_geo.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_scene_asset.sql"
run "${SCRIPT_DIR}/seed/dynamic_category.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_type.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_tree_path_repair.sql"
run "${SCRIPT_DIR}/seed/dynamic_business.sql"
run "${SCRIPT_DIR}/seed/dynamic_business_entry.sql"
run "${SCRIPT_DIR}/seed/business_capability.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_equipment.generated.sql"
run "${SCRIPT_DIR}/seed/dynamic_equipment_category_links.generated.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_facility_dev_sample.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_facility_fujian_dev_sample.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_facility_fujian.sql"
run "${SCRIPT_DIR}/seed/dynamic_category_tree_path_repair.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_inspection_item_dev_sample.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_task_scope_regression.sql"
run "${SCRIPT_DIR}/seed/dynamic_purge_patrol_domain.sql"
run "${SCRIPT_DIR}/seed/dynamic_seed_retire_route_inspection_point.sql"
# 点位/路线 Scope 与两站样例须在 purge/retire 之后（重新注册 patrol_point / route）
run "${SCRIPT_DIR}/seed/dynamic_entity_point_route_scope.sql"
run "${SCRIPT_DIR}/seed/dynamic_entity_facility_jinqiao_luoyang.sql"
# 三维场景/摆放阶段 1（前置 Flyway V17）
run "${SCRIPT_DIR}/seed/dynamic_scene_3d_phase1.sql"

bash "${SCRIPT_DIR}/evict_category_tree_cache.sh"

echo "done: 系统共用 seed（前置：Flyway V1→…→V17 已执行）"
