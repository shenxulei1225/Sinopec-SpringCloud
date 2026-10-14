-- 东古无人机的耗电曲线。
-- 负责：两台东古无人机还没有曲线时，写下常规锂聚合物电池的一条。
-- 100% 用完整段最长飞行时间。20% 及以下剩余可飞时间为 0。
-- 中间每 10 个百分点取一个点。点的高度是 (电量减去 20) 除以 80，再取 1.15 次方。
-- 电量越低，同样的百分比能支持的时间越短。已经写下曲线的不覆盖。
-- 不负责：大疆飞机。大疆直接用报上的剩余可飞秒数。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'battery_discharge_curve',
  '耗电曲线',
  'JSON',
  NULL,
  '电量百分比对应剩余时间占最长飞行时间的比例。常规锂聚合物电池：100% 为 1，20% 及以下为 0。',
  'SYSTEM',
  1,
  NULL,
  'NONE',
  NULL,
  NULL,
  NULL,
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  deleted = false,
  status = 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, false, false, 65,
  $curve$[{"percent":0,"ratio":0},{"percent":10,"ratio":0},{"percent":20,"ratio":0},{"percent":30,"ratio":0.0915},{"percent":40,"ratio":0.2031},{"percent":50,"ratio":0.3237},{"percent":60,"ratio":0.4506},{"percent":70,"ratio":0.5825},{"percent":80,"ratio":0.7183},{"percent":90,"ratio":0.8576},{"percent":100,"ratio":1}]$curve$,
  1,
  'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'battery_discharge_curve'
 AND f.tenant_id = 1
 AND f.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN ('donggu-uav-small', 'donggu-uav-large')
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE dynamic_model_field_assignment.default_value IS NULL
   OR btrim(dynamic_model_field_assignment.default_value) = '';

UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
    || jsonb_build_object('battery_discharge_curve', $curve$[{"percent":0,"ratio":0},{"percent":10,"ratio":0},{"percent":20,"ratio":0},{"percent":30,"ratio":0.0915},{"percent":40,"ratio":0.2031},{"percent":50,"ratio":0.3237},{"percent":60,"ratio":0.4506},{"percent":70,"ratio":0.5825},{"percent":80,"ratio":0.7183},{"percent":90,"ratio":0.8576},{"percent":100,"ratio":1}]$curve$::jsonb),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.model_id = m.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN ('donggu-uav-small', 'donggu-uav-large')
  AND (
    e.custom_fields->'battery_discharge_curve' IS NULL
    OR jsonb_typeof(e.custom_fields->'battery_discharge_curve') <> 'array'
    OR jsonb_array_length(e.custom_fields->'battery_discharge_curve') = 0
  );

COMMIT;
