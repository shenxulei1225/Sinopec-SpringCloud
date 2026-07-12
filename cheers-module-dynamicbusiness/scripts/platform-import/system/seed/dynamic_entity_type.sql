-- ============================================================================
-- 系统 · dynamic_entity_type
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_entity_type: 17 row(s), upsert by code

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('billing', '收费管理', NULL, '各项收费', 'businessIcon:收费管理.png', '费用', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_billing', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('customer', '入廊客户', NULL, '客户管理、关系维护', 'businessIcon:客户管理.png', '客户', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_customer', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('emergency', '应急管理', NULL, NULL, 'businessIcon:区域管理.png', NULL, 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_emergency', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('emergency_resource', '应急资源', 42, NULL, 'ep:discount', '资源', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_emergency_resource', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('emergency_team', '应急队伍', 42, NULL, 'fa:user-circle', '队伍', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_emergency_team', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('equipment', '设备管理', NULL, '需要分类-纯分类1', 'businessIcon:设备管理.png', '设备', 3, 'active', 'USER', '{}', 'DEDICATED', 'ent_equipment', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('facility', '设施管理', NULL, '行业标准设施点：站场、厂区等；多 Model 区分类型；REF_REGION 挂行政区划', 'businessIcon:设施管理.png', '设施', 2, 'active', 'USER', '{}', 'DEDICATED', 'ent_facility', FALSE, NULL, 1, 'seed')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
SELECT 'patrol_schedule', '巡检排期', p.id, '排期模板与实例（cron / 单次）', 'fa:clock-o', '排期', 1, 'active', 'USER', '{}', 'DEDICATED', 'ent_patrol_schedule', FALSE, NULL, 1, 'seed'
FROM dynamic_entity_type p
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'patrol'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = (SELECT id FROM dynamic_entity_type WHERE code = 'patrol' AND tenant_id = 1 AND deleted = false LIMIT 1),
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
SELECT 'patrol_object', '巡检对象', p.id, '巡检对象模板：设施、设备、分区', 'fa:cube', '对象', 2, 'active', 'USER', '{}', 'DEDICATED', 'ent_patrol_object', FALSE, NULL, 1, 'seed'
FROM dynamic_entity_type p
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'patrol'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = (SELECT id FROM dynamic_entity_type WHERE code = 'patrol' AND tenant_id = 1 AND deleted = false LIMIT 1),
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
SELECT 'patrol_point', '巡检点', p.id, '停留点 nodeId 列表、拓扑版本、机动剖面', 'fa:map-marker', '巡检点', 3, 'active', 'USER', '{}', 'DEDICATED', 'ent_patrol_point', FALSE, NULL, 1, 'seed'
FROM dynamic_entity_type p
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'patrol'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = (SELECT id FROM dynamic_entity_type WHERE code = 'patrol' AND tenant_id = 1 AND deleted = false LIMIT 1),
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
SELECT 'fault', '故障管理', p.id, '故障记录、故障分析', 'businessIcon:故障管理.png', '故障', 4, 'active', 'USER', '{}', 'DEDICATED', 'ent_fault', FALSE, NULL, 1, '1'
FROM dynamic_entity_type p
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'patrol'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = (SELECT id FROM dynamic_entity_type WHERE code = 'patrol' AND tenant_id = 1 AND deleted = false LIMIT 1),
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('inspection_item', '检查内容', 2, NULL, 'fa:calendar-check-o', '检查项', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_inspection_item', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('maintenance', '维修管理', 2, '分类管理（极速分类）', 'businessIcon:维修管理.png', '维修', 5, 'active', 'USER', '{}', 'DEDICATED', 'ent_maintenance', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('patrol', '巡检域', NULL, '巡检辅助主数据（故障等）；任务定义见 entityTypeCode=task、modelCode=patrol_task；路径拓扑见 platform-topology', 'ep:baseball', '巡检', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_patrol', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('task', '任务', NULL, '任务定义（含巡检类 patrol_task）；通过模板 id 引用排期/对象/巡检点/检查项', 'ep:calendar', '任务', 0, 'active', 'USER', '{}', 'DEDICATED', 'ent_task', FALSE, NULL, 1, 'seed')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('pipeline', '管线', NULL, '管线台账、管线档案管理', 'businessIcon:管线管理.png', '管线', 2, 'active', 'USER', '{}', 'DEDICATED', 'ent_pipeline', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('region', '运营区域', NULL, '国家管网运营/管理区域；树由分类（category_type=region）维护；Pattern C 分类即实体，不在 Flyway 预置 region 行', 'businessIcon:区域管理.png', '区域', 1, 'active', 'USER', '{}', 'DEDICATED', 'ent_region', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('spare_parts', '备件管理', 2, '备件台账、备件库存', 'businessIcon:备件管理.png', '备件', 5, 'active', 'USER', '{}', 'DEDICATED', 'ent_spare_part', FALSE, NULL, 1, '1')
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

INSERT INTO dynamic_entity_type (code, name, parent_id, description, icon, alias, sort, status, type_level, association_fields, storage_type, dedicated_table_name, enable_rule_engine, physical_column_mapping, tenant_id, creator)
VALUES ('zone', '空间分区', NULL, '设施内部空间单元：罐组、库棚、功能分区等；REF_FACILITY 必填', 'businessIcon:区域管理.png', '分区', 2, 'active', 'USER', '{}', 'DEDICATED', 'ent_zone', FALSE, NULL, 1, 'seed')
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
