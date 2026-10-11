-- 摄像机的直播地址。
-- 负责：在云台摄像机上增加广角、红外两格，在 FPV 上增加一格，并给已经挂接的样本摄像机写上本机模拟流的名字。
-- 不负责：把地址写进实时画面窗口，或写进设备检查配置。
-- 窗口只读这里。这一格空着就没有画面。已经填过的不覆盖。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, tenant_id, creator
) VALUES
  (
    'wide_live_address',
    '广角直播地址',
    'STRING',
    '这台云台摄像机广角这一路在流媒体网关上的流名字，或这一路的推流地址。实时画面按这一格播放。空着就没有广角画面。',
    'USER',
    1,
    'NONE',
    1,
    'seed'
  ),
  (
    'ir_live_address',
    '红外直播地址',
    'STRING',
    '这台云台摄像机红外这一路在流媒体网关上的流名字，或这一路的推流地址。实时画面按这一格播放。空着就没有红外画面。',
    'USER',
    1,
    'NONE',
    1,
    'seed'
  ),
  (
    'fpv_live_address',
    'FPV直播地址',
    'STRING',
    '这台飞行器 FPV 在流媒体网关上的流名字，或这一路的推流地址。实时画面按这一格播放。空着就没有 FPV 画面。',
    'USER',
    1,
    'NONE',
    1,
    'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  tenant_id, creator
)
SELECT m.id, m.code, field.id, field.code, false, false, false, false, spec.sort_no, 1, 'seed'
FROM dynamic_model m
JOIN (
  VALUES
    ('wide_live_address', 45),
    ('ir_live_address', 46)
) AS spec(code, sort_no) ON true
JOIN dynamic_field field
  ON field.code = spec.code AND field.tenant_id = 1 AND field.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'dji-pl-%'
  AND m.code NOT IN ('dji-pl-fpv', 'dji-pl-assist', 'dji-pl-dock-cam')
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  tenant_id, creator
)
SELECT m.id, m.code, f.id, f.code, false, false, false, false, 47, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.code = 'fpv_live_address' AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'dji-pl-fpv'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 已经挂接的样本摄像机：没填过才写上本机模拟流，方便打开实时画面就能看到。
UPDATE ent_equipment_t1 e
SET
  custom_fields = jsonb_set(e.custom_fields, '{wide_live_address}', '"sim-wide"', true),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.model_id = m.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code LIKE 'dji-pl-%'
  AND m.code NOT IN ('dji-pl-fpv', 'dji-pl-assist', 'dji-pl-dock-cam')
  AND e.parent_id IS NOT NULL
  AND NOT (e.custom_fields ? 'wide_live_address');

UPDATE ent_equipment_t1 e
SET
  custom_fields = jsonb_set(e.custom_fields, '{ir_live_address}', '"sim-ir"', true),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.model_id = m.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code IN (
    'dji-pl-h20t', 'dji-pl-h20n', 'dji-pl-h30t', 'dji-pl-xt2',
    'dji-pl-m30t-cam', 'dji-pl-m3t-cam', 'dji-pl-m3ta-cam', 'dji-pl-m3td-cam',
    'dji-pl-m4t-cam', 'dji-pl-m4td-cam'
  )
  AND e.parent_id IS NOT NULL
  AND NOT (e.custom_fields ? 'ir_live_address');

UPDATE ent_equipment_t1 e
SET
  custom_fields = jsonb_set(e.custom_fields, '{fpv_live_address}', '"sim-fpv"', true),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.model_id = m.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code = 'dji-pl-fpv'
  AND e.parent_id IS NOT NULL
  AND NOT (e.custom_fields ? 'fpv_live_address');

COMMIT;
