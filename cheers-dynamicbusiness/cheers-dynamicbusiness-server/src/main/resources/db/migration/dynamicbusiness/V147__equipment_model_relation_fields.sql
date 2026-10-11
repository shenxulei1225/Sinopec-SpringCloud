-- 适用机型、可挂载设备类型是模型关联字段，不进设备类型基础字段。
-- 名单不写进每台设备的自定义字段，写在型号—型号关联表。
-- 适用机型挂在摄像机型号上，方向向外：这一型号指向能挂上的主机型号。
-- 可挂载设备类型挂在无人机型号上，方向向内：哪些设备型号指向这一架无人机。
-- 不负责：挂载点（仍是主机上的具体设备）、机场、机器人、遥控器。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  'applicable_host_models',
  '适用机型',
  'MODEL_RELATION',
  NULL,
  '这个设备型号可以挂到哪些主机型号。保存时按这一型号写入型号关联，不写进某一台设备。',
  'USER',
  1,
  NULL,
  'NONE',
  NULL,
  NULL,
  'MODEL_RELATION_OUT',
  m.tenant_id,
  'v147'
FROM dynamic_model m
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (m.code LIKE 'dji-pl-%' OR m.code = 'hik-uav-cam')
GROUP BY m.tenant_id
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'v147',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  'mountable_device_models',
  '可挂载设备类型',
  'MODEL_RELATION',
  NULL,
  '哪些设备型号可以挂到这个主机型号。和适用机型是同一份型号关联，查询方向相反。',
  'USER',
  1,
  NULL,
  'NONE',
  NULL,
  NULL,
  'MODEL_RELATION_IN',
  m.tenant_id,
  'v147'
FROM dynamic_model m
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (
    m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
  )
GROUP BY m.tenant_id
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'v147',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, false, false, 9,
  'CUSTOM', m.tenant_id, 'v147'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'applicable_host_models'
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (m.code LIKE 'dji-pl-%' OR m.code = 'hik-uav-cam')
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = f.code
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, false, false, 10,
  'CUSTOM', m.tenant_id, 'v147'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'mountable_device_models'
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (
    m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
  )
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = f.code
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'equipment'
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code = 'equipment'
   );
