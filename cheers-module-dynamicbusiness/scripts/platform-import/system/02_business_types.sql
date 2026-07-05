-- ============================================================================
-- 系统共用 · 02 业务类型（15 个业务 + config + 业务间关联）
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：system/01_schema.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_business_type: 15 row(s), upsert by code

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('billing', '收费管理', NULL, '各项收费', 'businessIcon:收费管理.png', '费用', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_billing', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('customer', '入廊客户', NULL, '客户管理、关系维护', 'businessIcon:客户管理.png', '客户', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_customer', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('emergency', '应急管理', NULL, NULL, 'businessIcon:区域管理.png', NULL, 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_emergency', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('emergency_resource', '应急资源', 42, NULL, 'ep:discount', '资源', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_emergency_resource', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('emergency_team', '应急队伍', 42, NULL, 'fa:user-circle', '队伍', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_emergency_team', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('equipment', '设备管理', NULL, '需要分类-纯分类1', 'businessIcon:设备管理.png', '设备', 3, 'active', 'USER', '{}', 'DEDICATED', 'biz_equipment', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('fault', '故障管理', 41, '故障记录、故障分析', 'businessIcon:故障管理.png', '故障', 4, 'active', 'USER', '{}', 'DEDICATED', 'biz_fault', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('inspection_item', '检查内容', 2, NULL, 'fa:calendar-check-o', '检查项', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_inspection_item', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('inspection_point', '巡检点位', 41, NULL, 'ep:calendar', '点位', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_inspection_point', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('maintenance', '维修管理', 2, '分类管理（极速分类）', 'businessIcon:维修管理.png', '维修', 5, 'active', 'USER', '{}', 'DEDICATED', 'biz_maintenance', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('patrol', '巡检管理', NULL, NULL, 'ep:baseball', '巡检任务', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_patrol', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('pipeline', '管线', NULL, '管线台账、管线档案管理', 'businessIcon:管线管理.png', '管线', 2, 'active', 'USER', '{}', 'DEDICATED', 'biz_pipeline', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('region', '区域管理', NULL, '智慧站场：站场/库区边界与罐组分区；Pattern C 分类即实体', 'businessIcon:区域管理.png', '区域', 1, 'active', 'USER', '{}', 'DEDICATED', 'biz_region', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('route', '路线管理', 41, NULL, 'ep:burger', '路线', 0, 'active', 'USER', '{}', 'DEDICATED', 'biz_route', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('spare_parts', '备件管理', 2, '备件台账、备件库存', 'businessIcon:备件管理.png', '备件', 5, 'active', 'USER', '{}', 'DEDICATED', 'biz_spare_part', FALSE, NULL, 1, '1')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;


-- dynamic_business_type_config: 14 row(s), upsert by business_type_code

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'billing', '收费管理',
  'DEDICATED', 'biz_shou_fei',
  NULL, TRUE,
  '各项收费', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'customer', '客户管理',
  'DEDICATED', 'biz_ke_hu',
  NULL, TRUE,
  '客户管理、关系维护', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'emergency_resource', '应急资源',
  'DEDICATED', 'biz_ying_ji_zi_yuan',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'emergency_team', '应急队伍',
  'DEDICATED', 'biz_ying_ji_dui_wu',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'equipment', '设备管理',
  'DEDICATED', 'biz_equipment',
  NULL, TRUE,
  'equipment业务类型存储配置', 1,
  '{"code": {"type": "VARCHAR", "column": "code", "length": 100}, "guid": {"type": "VARCHAR", "column": "guid"}, "device_code": {"type": "VARCHAR", "column": "device_code"}, "device_type": {"type": "BIGINT", "column": "device_type"}, "coordinate_3d": {"type": "VARCHAR", "column": "coordinate_3d"}, "coordinate_gis": {"type": "VARCHAR", "column": "coordinate_gis"}}', 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'fault', '故障管理',
  'DEDICATED', 'biz_fault',
  NULL, TRUE,
  'fault业务类型存储配置', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'inspection_item', '检查内容',
  'DEDICATED', 'biz_jian_cha_nei_rong',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'inspection_point', '点位管理',
  'DEDICATED', 'biz_dian_wei',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'maintenance', '维修管理',
  'DEDICATED', 'biz_maintenance',
  NULL, TRUE,
  '维修管理 - 动态业务', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'patrol', '巡检管理',
  'DEDICATED', 'biz_xun_jian',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'pipeline', '管线管理',
  'DEDICATED', 'biz_pipeline',
  NULL, TRUE,
  'pipeline业务类型存储配置', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'region', '区域管理',
  'DEDICATED', 'biz_region',
  NULL, TRUE,
  '智慧站场 Region：身份/树/边界存 biz_region；路网归 platform-routing', 1,
  '{"bbox": {"type": "JSONB", "column": "bbox"}, "region_code": {"type": "VARCHAR", "column": "region_code", "length": 255}, "region_name": {"type": "VARCHAR", "column": "region_name", "length": 255}, "region_type": {"type": "VARCHAR", "column": "region_type", "length": 100}, "boundary_crs": {"type": "VARCHAR", "column": "boundary_crs", "length": 32}, "centroid_lat": {"type": "DECIMAL", "scale": 8, "column": "centroid_lat", "precision": 12}, "centroid_lng": {"type": "DECIMAL", "scale": 8, "column": "centroid_lng", "precision": 12}, "max_height_m": {"type": "DECIMAL", "scale": 3, "column": "max_height_m", "precision": 10}, "min_height_m": {"type": "DECIMAL", "scale": 3, "column": "min_height_m", "precision": 10}, "boundary_status": {"type": "VARCHAR", "column": "boundary_status", "length": 32}, "boundary_geojson": {"type": "JSONB", "column": "boundary_geojson"}}', 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'route', '路线管理',
  'DEDICATED', 'biz_lu_xian_guan_li',
  NULL, TRUE,
  NULL, 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'spare_parts', '备件管理',
  'DEDICATED', 'biz_spare_part',
  NULL, TRUE,
  'spare_part业务类型存储配置', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_business_type_relation: 5 row(s), upsert by (source, target, tenant_id)

INSERT INTO dynamic_business_type_relation (
  source_business_type_code, target_business_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'customer',
  '关联客户管理', TRUE,
  '关联客户管理', 1, 'seed'
)
ON CONFLICT (source_business_type_code, target_business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_relation (
  source_business_type_code, target_business_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'region',
  '关联区域管理', TRUE,
  '关联区域管理', 1, 'seed'
)
ON CONFLICT (source_business_type_code, target_business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_relation (
  source_business_type_code, target_business_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'spare_parts',
  '关联备件管理', TRUE,
  '关联备件管理', 1, 'seed'
)
ON CONFLICT (source_business_type_code, target_business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_relation (
  source_business_type_code, target_business_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'inspection_point', 'region',
  '所属区域', TRUE,
  '关联区域管理', 1, 'seed'
)
ON CONFLICT (source_business_type_code, target_business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_relation (
  source_business_type_code, target_business_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'region', 'equipment',
  '区域-设备关联', TRUE,
  '所属设备管理', 1, 'seed'
)
ON CONFLICT (source_business_type_code, target_business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
