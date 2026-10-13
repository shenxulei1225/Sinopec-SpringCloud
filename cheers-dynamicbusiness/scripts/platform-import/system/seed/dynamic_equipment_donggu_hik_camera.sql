-- 东古无人机吊装海康专用的无人机吊装相机。
-- 负责：建「海康无人机吊装相机」这个型号，并在 1#东古小型、1#东古大型下面各吊一台。品牌和生产厂家写海康威视。
-- 不负责：把管廊里已有的海康网络摄像机、半球摄像机挂到飞机上。不编规格型号、镜头和云台角度。

SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
)
VALUES (
  'hik-uav-cam',
  '海康无人机吊装相机',
  'equipment',
  '东古无人机吊装的海康专用吊装相机',
  1,
  157100,
  '[]',
  1,
  'seed'
)
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
WHERE m.code = 'hik-uav-cam'
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
  cam.id, cam.code, a.field_id, a.field_code,
  a.required, a.is_searchable, a.is_filterable, a.is_sortable, a.sort,
  a.field_source,
  CASE a.field_code
    WHEN 'brand' THEN '海康威视'
    WHEN 'manufacturer' THEN '海康威视'
    ELSE a.default_value
  END,
  1,
  'seed'
FROM dynamic_model cam
JOIN dynamic_model src
  ON src.id = 1742 AND src.tenant_id = 1 AND src.deleted = false
JOIN dynamic_model_field_assignment a
  ON a.model_id = src.id AND a.tenant_id = 1 AND a.deleted = false
WHERE cam.code = 'hik-uav-cam'
  AND cam.tenant_id = 1
  AND cam.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_equipment_t1 (
  entity_type_code, model_id, parent_id, name, code, guid, custom_fields,
  tenant_id, facility_id, status, creator
)
SELECT
  'equipment',
  cam.id,
  host.id,
  host.name || ' · 海康吊装相机',
  mount.child_code,
  'guid-' || lower(mount.child_code),
  jsonb_build_object('brand', '海康威视', 'manufacturer', '海康威视'),
  1,
  host.facility_id,
  1,
  'seed'
FROM (
  VALUES
    ('25408e8c453544b8b39e6d1dc17bb147', 'EQ-DONGGU-SMALL-HIK-CAM'),
    ('de6e497903c847d0b2a6803aeaa1136c', 'EQ-DONGGU-LARGE-HIK-CAM')
) AS mount(host_code, child_code)
JOIN ent_equipment_t1 host
  ON host.code = mount.host_code AND host.tenant_id = 1 AND host.deleted = false
JOIN dynamic_model cam
  ON cam.code = 'hik-uav-cam' AND cam.tenant_id = 1 AND cam.deleted = false
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  custom_fields = COALESCE(ent_equipment_t1.custom_fields, '{}'::jsonb) || EXCLUDED.custom_fields,
  facility_id = EXCLUDED.facility_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_equipment_t1 child
SET tree_path = '/' || child.parent_id || '/' || child.id || '/',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE child.tenant_id = 1
  AND child.deleted = false
  AND child.code IN ('EQ-DONGGU-SMALL-HIK-CAM', 'EQ-DONGGU-LARGE-HIK-CAM')
  AND child.parent_id IS NOT NULL
  AND child.parent_id <> 0;

INSERT INTO dynamic_entity_category_relation_t1 (
  entity_id, category_id, entity_type_code, tenant_id, creator
)
SELECT child.id, 409, 'equipment', 1, 'seed'
FROM ent_equipment_t1 child
WHERE child.tenant_id = 1
  AND child.deleted = false
  AND child.code IN ('EQ-DONGGU-SMALL-HIK-CAM', 'EQ-DONGGU-LARGE-HIK-CAM')
ON CONFLICT (tenant_id, entity_type_code, entity_id, category_id)
DO UPDATE SET
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

COMMIT;
