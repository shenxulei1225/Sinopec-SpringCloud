-- ============================================================================
-- 退役与「停靠站几何台账」冲突的旧实体类型：inspection_point
--
-- 说明（2026-07-14）：
--   · 路网停靠站几何仍归属 platform-topology，不在动态业务重复建「巡检几何点」。
--   · 动态业务 NATIVE `route` / SCOPED `patrol_route` 用于保存**路径规划标准计算结果**，
--     不再退役 route（见 dynamic_entity_point_route_scope.sql）。
-- 幂等：重复执行安全。
-- ============================================================================

SET search_path TO dynamicbusiness;

UPDATE dynamic_business_entry
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND entity_type_code IN ('inspection_point');

UPDATE dynamic_business
SET deleted = true, status = 'inactive', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN ('inspection_point');

UPDATE dynamic_entity_type_config
SET status = 0, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND entity_type_code IN ('inspection_point');

UPDATE dynamic_entity_type
SET deleted = true, status = 'inactive', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN ('inspection_point');

UPDATE dynamic_entity_type_relation
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND source_entity_type_code = 'inspection_point';

UPDATE dynamic_entity_type_base_field
SET deleted = true, status = 0, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND entity_type_code IN ('inspection_point');
