-- ============================================================================
-- 系统 · dynamic_entity_type_config
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_entity_type_config: 16 row(s), upsert by entity_type_code

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'billing', '收费管理',
  'DEDICATED', 'ent_billing',
  NULL, TRUE,
  '各项收费', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'customer', '客户管理',
  'DEDICATED', 'ent_customer',
  NULL, TRUE,
  '客户管理、关系维护', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'emergency_resource', '应急资源',
  'DEDICATED', 'ent_emergency_resource',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'emergency_team', '应急队伍',
  'DEDICATED', 'ent_emergency_team',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'equipment', '设备管理',
  'DEDICATED', 'ent_equipment',
  NULL, TRUE,
  'equipment业务类型存储配置', 1,
  '{"code": {"type": "VARCHAR", "column": "code", "length": 100}, "guid": {"type": "VARCHAR", "column": "guid"}, "zone_id": {"type": "BIGINT", "column": "zone_id"}, "device_code": {"type": "VARCHAR", "column": "device_code"}, "device_type": {"type": "BIGINT", "column": "device_type"}, "facility_id": {"type": "BIGINT", "column": "facility_id"}, "coordinate_3d": {"type": "VARCHAR", "column": "coordinate_3d"}, "coordinate_gis": {"type": "VARCHAR", "column": "coordinate_gis"}}', 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'facility', '设施管理',
  'DEDICATED', 'ent_facility',
  NULL, TRUE,
  '设施点 ent_facility；站场/厂区多 Model', 1,
  '{"address": {"type": "VARCHAR", "column": "address", "length": 500}, "latitude": {"type": "DECIMAL", "scale": 8, "column": "latitude", "precision": 12}, "longitude": {"type": "DECIMAL", "scale": 8, "column": "longitude", "precision": 12}, "region_id": {"type": "BIGINT", "column": "region_id"}, "facility_type": {"type": "VARCHAR", "column": "facility_type", "length": 100}}', 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'fault', '故障管理',
  'DEDICATED', 'ent_fault',
  NULL, TRUE,
  'fault业务类型存储配置', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'inspection_item', '检查内容',
  'DEDICATED', 'ent_inspection_item',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'maintenance', '维修管理',
  'DEDICATED', 'ent_maintenance',
  NULL, TRUE,
  '维修管理 - 动态业务', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'task', '任务',
  'DEDICATED', 'ent_task',
  NULL, TRUE,
  '任务定义实体；巡检/维修通过 Scope 域入口区分', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  description = EXCLUDED.description,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'pipeline', '管线管理',
  'DEDICATED', 'ent_pipeline',
  NULL, TRUE,
  'pipeline业务类型存储配置', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'region', '运营区域',
  'DEDICATED', 'ent_region',
  NULL, TRUE,
  'ent_region；树= dynamic_category；实例由分类绑实体产生', 1,
  '{"bbox": {"type": "JSONB", "column": "bbox"}, "region_code": {"type": "VARCHAR", "column": "region_code", "length": 255}, "region_name": {"type": "VARCHAR", "column": "region_name", "length": 255}, "region_type": {"type": "VARCHAR", "column": "region_type", "length": 100}, "boundary_crs": {"type": "VARCHAR", "column": "boundary_crs", "length": 32}, "centroid_lat": {"type": "DECIMAL", "scale": 8, "column": "centroid_lat", "precision": 12}, "centroid_lng": {"type": "DECIMAL", "scale": 8, "column": "centroid_lng", "precision": 12}, "max_height_m": {"type": "DECIMAL", "scale": 3, "column": "max_height_m", "precision": 10}, "min_height_m": {"type": "DECIMAL", "scale": 3, "column": "min_height_m", "precision": 10}, "boundary_status": {"type": "VARCHAR", "column": "boundary_status", "length": 32}, "boundary_geojson": {"type": "JSONB", "column": "boundary_geojson"}}', 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'spare_parts', '备件管理',
  'DEDICATED', 'ent_spare_part',
  NULL, TRUE,
  'spare_parts业务类型存储配置', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'zone', '空间分区',
  'DEDICATED', 'ent_zone',
  NULL, TRUE,
  '站内分区 ent_zone', 1,
  '{"zone_type": {"type": "VARCHAR", "column": "zone_type", "length": 100}, "description": {"type": "VARCHAR", "column": "description", "length": 500}, "facility_id": {"type": "BIGINT", "column": "facility_id"}, "boundary_crs": {"type": "VARCHAR", "column": "boundary_crs", "length": 32}, "centroid_lat": {"type": "DECIMAL", "scale": 8, "column": "centroid_lat", "precision": 12}, "centroid_lng": {"type": "DECIMAL", "scale": 8, "column": "centroid_lng", "precision": 12}, "max_height_m": {"type": "DECIMAL", "scale": 3, "column": "max_height_m", "precision": 10}, "min_height_m": {"type": "DECIMAL", "scale": 3, "column": "min_height_m", "precision": 10}, "boundary_status": {"type": "VARCHAR", "column": "boundary_status", "length": 32}, "boundary_geojson": {"type": "JSONB", "column": "boundary_geojson"}}', 1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
