-- 设备参数里的最长飞行时间。
-- 负责：轮式机器人型号补上这一格；这一台设备空着时，把型号已经写下的默认值抄进去。
-- 小型轮式机器人写下 240 分钟，大型轮式机器人写下 360 分钟。大疆、东古仍用各自型号上已有的默认值。
-- 不负责：耗电曲线。已经填过的不覆盖。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, true, true, 64,
  spec.minutes, 1, 'seed'
FROM dynamic_model m
JOIN (
  SELECT '小型轮式机器人'::text AS model_name, '240' AS minutes
  UNION ALL
  SELECT '大型轮式机器人', '360'
) AS spec ON spec.model_name = m.name
JOIN dynamic_field f ON f.code = 'max_flight_time' AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.entity_type_code = 'equipment'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE dynamic_model_field_assignment.default_value IS NULL
   OR btrim(dynamic_model_field_assignment.default_value) = '';

UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
    || jsonb_build_object('max_flight_time', a.default_value::numeric),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model_field_assignment a
WHERE e.model_id = a.model_id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND a.deleted = false
  AND a.tenant_id = 1
  AND a.field_code = 'max_flight_time'
  AND a.default_value IS NOT NULL
  AND btrim(a.default_value) <> ''
  AND NULLIF(btrim(e.custom_fields ->> 'max_flight_time'), '') IS NULL;

DELETE FROM dynamic_entity_field_index i
USING ent_equipment_t1 e
WHERE i.entity_id = e.id
  AND i.tenant_id = 1
  AND e.tenant_id = 1
  AND e.deleted = false
  AND i.field_code = 'max_flight_time'
  AND NULLIF(btrim(e.custom_fields ->> 'max_flight_time'), '') IS NOT NULL;

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, value_number, tenant_id, creator
)
SELECT
  e.id,
  e.model_id,
  'max_flight_time',
  NULL,
  (e.custom_fields ->> 'max_flight_time')::numeric,
  1,
  'seed'
FROM ent_equipment_t1 e
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND NULLIF(btrim(e.custom_fields ->> 'max_flight_time'), '') IS NOT NULL;

COMMIT;
