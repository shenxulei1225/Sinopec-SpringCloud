-- 东古无人机的产品型号，以及名称曾经和型号写成同一个词的两台实体。
-- 负责：设备型号增加「东古小型无人机」「东古大型无人机」；把这两台实体改成带序号的名称，并改挂到对应产品型号。
-- 不负责：改大疆机场、Pilot 那两台设备的名称或型号；不删除按体型登记的「小型无人机」「大型无人机」。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
)
VALUES
  ('donggu-uav-small', '东古小型无人机', 'equipment', '东古小型无人机产品型号', 1, 155700, '[]', 1, 'seed'),
  ('donggu-uav-large', '东古大型无人机', 'equipment', '东古大型无人机产品型号', 1, 156800, '[]', 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = 1,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', m.code, c.code, 1024, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.code = 'EQCAT-L1-MISC' AND c.tenant_id = 1 AND c.deleted = false
WHERE m.code IN ('donggu-uav-small', 'donggu-uav-large')
  AND m.tenant_id = 1
  AND m.deleted = false
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, default_value, tenant_id, creator
)
SELECT
  m.id, m.code, a.field_id, a.field_code,
  a.required, a.is_searchable, a.is_filterable, a.is_sortable, a.sort,
  a.field_source, a.default_value, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_model src
  ON src.tenant_id = 1
 AND src.deleted = false
 AND src.code = CASE m.code
   WHEN 'donggu-uav-small' THEN 'MODEL-6a309e29fa6e4cf5b982dd055dd08333'
   ELSE 'MODEL-16d02ad599e645a7aab03cc7500e4f4a'
 END
JOIN dynamic_model_field_assignment a
  ON a.model_id = src.id AND a.tenant_id = 1 AND a.deleted = false
WHERE m.code IN ('donggu-uav-small', 'donggu-uav-large')
  AND m.tenant_id = 1
  AND m.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_equipment_t1 e
SET
  name = '1#东古小型无人机',
  model_id = m.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.code = '25408e8c453544b8b39e6d1dc17bb147'
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code = 'donggu-uav-small'
  AND m.tenant_id = 1
  AND m.deleted = false;

UPDATE ent_equipment_t1 e
SET
  name = '1#东古大型无人机',
  model_id = m.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.code = 'de6e497903c847d0b2a6803aeaa1136c'
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code = 'donggu-uav-large'
  AND m.tenant_id = 1
  AND m.deleted = false;

COMMIT;
