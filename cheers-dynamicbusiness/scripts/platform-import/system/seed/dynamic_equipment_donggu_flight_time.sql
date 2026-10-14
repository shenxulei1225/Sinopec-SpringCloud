-- 东古无人机满电能支持的时间。
-- 负责：东古小型无人机写下 30 分钟，东古大型无人机写下 45 分钟。写在型号默认值，并写进这两台已经建好的设备。
-- 这一格是最长飞行时间。开跑时再乘这台设备上的耗电曲线，算出还能用的秒数。
-- 不负责：大疆飞机、机器人。耗电曲线见 dynamic_equipment_donggu_discharge_curve.sql。已经填过的不覆盖。
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
  SELECT 'donggu-uav-small' AS model_code, '30' AS minutes
  UNION ALL
  SELECT 'donggu-uav-large', '45'
) AS spec ON spec.model_code = m.code
JOIN dynamic_field f ON f.code = 'max_flight_time' AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
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
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || jsonb_build_object('max_flight_time', spec.minutes::numeric),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
JOIN (
  SELECT 'donggu-uav-small' AS model_code, 30 AS minutes
  UNION ALL
  SELECT 'donggu-uav-large', 45
) AS spec ON spec.model_code = m.code
WHERE e.model_id = m.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.deleted = false
  AND m.tenant_id = 1
  AND NULLIF(btrim(e.custom_fields ->> 'max_flight_time'), '') IS NULL;

DELETE FROM dynamic_entity_field_index i
USING ent_equipment_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false
WHERE i.entity_id = e.id
  AND i.tenant_id = 1
  AND e.tenant_id = 1
  AND e.deleted = false
  AND m.code IN ('donggu-uav-small', 'donggu-uav-large')
  AND i.field_code = 'max_flight_time';

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
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND m.code IN ('donggu-uav-small', 'donggu-uav-large')
  AND NULLIF(btrim(e.custom_fields ->> 'max_flight_time'), '') IS NOT NULL;

COMMIT;
