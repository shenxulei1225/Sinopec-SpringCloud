-- ============================================================================
-- 退役与路径规划方案冲突的实体类型：inspection_point、route
-- 停靠点（Station）与路网拓扑归属 platform-topology，不在动态业务建台账。
-- 幂等：重复执行安全。
-- ============================================================================

SET search_path TO dynamicbusiness;

UPDATE dynamic_business_entry
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND entity_type_code IN ('inspection_point', 'route');

UPDATE dynamic_business
SET deleted = true, status = 'inactive', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN ('inspection_point', 'route');

UPDATE dynamic_entity_type_config
SET status = 0, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND entity_type_code IN ('inspection_point', 'route');

UPDATE dynamic_entity_type
SET deleted = true, status = 'inactive', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN ('inspection_point', 'route');

UPDATE dynamic_entity_type_relation
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND source_entity_type_code = 'inspection_point';

UPDATE dynamic_entity_type_base_field
SET deleted = true, status = 0, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND entity_type_code IN ('inspection_point', 'route');
