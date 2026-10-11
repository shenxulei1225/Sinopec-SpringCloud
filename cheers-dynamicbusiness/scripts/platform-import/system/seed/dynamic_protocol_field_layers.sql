-- 协议指令的基础字段与扩展字段收口。
-- 前置：Flyway V141（protocol_version、communication_protocol 两列）。
-- 须在指令种子之后跑：种子会把协议版本和通讯方式写回扩展袋，这里再搬进列。
-- 指令 JSON、字段说明、样例保持扩展字段。航线文件和 JSBridge 不分配这三列。
-- 这两列升成基础字段后，扩展索引里的同名字段删掉，列表改读专用表列。

SET search_path TO dynamicbusiness, public;

-- 基础字段挂在业务类型上，全部型号继承。扩展字段只挂在具体型号上，不进基础字段表。
-- 指令 JSON、字段说明、样例不是全部型号共有，从基础字段表拿掉，改到用 JSON 报文的型号上。
UPDATE dynamic_entity_type_base_field
SET
  status = 0,
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'data_protocol'
  AND tenant_id = 1
  AND deleted = false
  AND field_code IN ('command_json', 'field_description_json', 'sample_json');

UPDATE dynamic_entity_type
SET
  physical_column_mapping = jsonb_set(
    jsonb_set(
      jsonb_set(
        COALESCE(physical_column_mapping, '{}'::jsonb),
        '{protocol_version}',
        '{"column":"protocol_version","type":"JSONB"}'::jsonb,
        true
      ),
      '{communication_protocol}',
      '{"column":"communication_protocol","type":"VARCHAR","length":32}'::jsonb,
      true
    ),
    '{fieldLabels}',
    COALESCE(physical_column_mapping->'fieldLabels', '{}'::jsonb)
      || '{"protocol_version":"协议版本","communication_protocol":"通讯方式"}'::jsonb,
    true
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'data_protocol';

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'data_protocol',
  f.id,
  v.field_code,
  v.field_name,
  v.data_type,
  false,
  v.description,
  NULL,
  v.sort_order,
  1,
  true,
  true,
  v.is_sortable,
  1,
  'seed'
FROM (
  VALUES
    ('protocol_version', '协议版本', 'STRING', '对接协议。对象栏按此列筛选。值是数组，如 ["dji-dock-1.16.1"]。', 16, false),
    ('communication_protocol', '通讯方式', 'ENUM', '这条指令走 MQTT、HTTPS、WebSocket、JSBridge 或航线文件。对象栏按此列筛选。', 18, true)
) AS v(field_code, field_name, data_type, description, sort_order, is_sortable)
JOIN dynamic_field f
  ON f.code = v.field_code
 AND f.deleted = false
 AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = 1,
  deleted = false,
  is_searchable = true,
  is_filterable = true,
  is_sortable = EXCLUDED.is_sortable,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, v.field_code, false, true, true, v.is_sortable, v.sort_order, 'BASE', 1, 'seed'
FROM dynamic_model m
JOIN (
  VALUES
    ('protocol_version', 16, false),
    ('communication_protocol', 18, true)
) AS v(field_code, sort_order, is_sortable) ON 1 = 1
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'data_protocol%'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_source = 'BASE',
  is_searchable = true,
  is_filterable = true,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_data_protocol_t1
SET
  protocol_version = CASE
    WHEN jsonb_typeof(custom_fields->'protocol_version') = 'array'
      THEN custom_fields->'protocol_version'
    WHEN NULLIF(btrim(custom_fields->>'protocol_version'), '') IS NOT NULL
      THEN jsonb_build_array(btrim(custom_fields->>'protocol_version'))
    ELSE protocol_version
  END,
  communication_protocol = COALESCE(
    NULLIF(btrim(custom_fields->>'communication_protocol'), ''),
    communication_protocol
  ),
  custom_fields = custom_fields - 'protocol_version' - 'communication_protocol',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND custom_fields ?| ARRAY['protocol_version', 'communication_protocol'];

UPDATE dynamic_entity_field_index_t1 AS idx
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE idx.deleted = false
  AND idx.tenant_id = 1
  AND idx.field_code IN ('protocol_version', 'communication_protocol')
  AND EXISTS (
    SELECT 1
    FROM ent_data_protocol_t1 e
    WHERE e.id = idx.entity_id
      AND e.tenant_id = 1
  );

UPDATE dynamic_field
SET
  source = 'USER',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN ('command_json', 'field_description_json', 'sample_json')
  AND source IS DISTINCT FROM 'USER';

UPDATE dynamic_model_field_assignment
SET
  field_source = 'CUSTOM',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND field_code IN ('command_json', 'field_description_json', 'sample_json')
  AND model_code LIKE 'data_protocol%'
  AND model_code NOT IN (
    'data_protocol_dji_dock_wayline',
    'data_protocol_dji_pilot_wayline',
    'data_protocol_dji_pilot_jsbridge'
  )
  AND field_source IS DISTINCT FROM 'CUSTOM';

UPDATE dynamic_model_field_assignment
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND field_code IN ('command_json', 'field_description_json', 'sample_json')
  AND model_code IN (
    'data_protocol_dji_dock_wayline',
    'data_protocol_dji_pilot_wayline',
    'data_protocol_dji_pilot_jsbridge'
  );

-- 扩展袋里还没有、专用表同名列里有内容时，补进扩展袋。列上的空对象不覆盖已有正文。
UPDATE ent_data_protocol_t1 AS e
SET
  custom_fields = jsonb_set(COALESCE(e.custom_fields, '{}'::jsonb), '{command_json}', e.command_json, true),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.model_id = m.id
  AND m.code NOT IN (
    'data_protocol_dji_dock_wayline',
    'data_protocol_dji_pilot_wayline',
    'data_protocol_dji_pilot_jsbridge'
  )
  AND e.command_json IS NOT NULL
  AND e.command_json <> 'null'::jsonb
  AND e.command_json <> '{}'::jsonb
  AND (
    NOT (COALESCE(e.custom_fields, '{}'::jsonb) ? 'command_json')
    OR e.custom_fields->'command_json' IS NULL
    OR e.custom_fields->'command_json' = 'null'::jsonb
    OR e.custom_fields->'command_json' = '{}'::jsonb
  );

UPDATE ent_data_protocol_t1 AS e
SET
  custom_fields = jsonb_set(COALESCE(e.custom_fields, '{}'::jsonb), '{field_description_json}', e.field_description_json, true),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.model_id = m.id
  AND m.code NOT IN (
    'data_protocol_dji_dock_wayline',
    'data_protocol_dji_pilot_wayline',
    'data_protocol_dji_pilot_jsbridge'
  )
  AND e.field_description_json IS NOT NULL
  AND e.field_description_json <> 'null'::jsonb
  AND e.field_description_json <> '{}'::jsonb
  AND (
    NOT (COALESCE(e.custom_fields, '{}'::jsonb) ? 'field_description_json')
    OR e.custom_fields->'field_description_json' IS NULL
    OR e.custom_fields->'field_description_json' = 'null'::jsonb
    OR e.custom_fields->'field_description_json' = '{}'::jsonb
  );

UPDATE ent_data_protocol_t1 AS e
SET
  custom_fields = jsonb_set(COALESCE(e.custom_fields, '{}'::jsonb), '{sample_json}', e.sample_json, true),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.model_id = m.id
  AND m.code NOT IN (
    'data_protocol_dji_dock_wayline',
    'data_protocol_dji_pilot_wayline',
    'data_protocol_dji_pilot_jsbridge'
  )
  AND e.sample_json IS NOT NULL
  AND e.sample_json <> 'null'::jsonb
  AND e.sample_json <> '{}'::jsonb
  AND (
    NOT (COALESCE(e.custom_fields, '{}'::jsonb) ? 'sample_json')
    OR e.custom_fields->'sample_json' IS NULL
    OR e.custom_fields->'sample_json' = 'null'::jsonb
    OR e.custom_fields->'sample_json' = '{}'::jsonb
  );

UPDATE platformresource.pr_component_props p
SET
  props = (
    jsonb_set(
      jsonb_set(
        jsonb_set(
          COALESCE(NULLIF(btrim(p.props), '')::jsonb, '{}'::jsonb),
          '{displayContent}',
          '["name","command_direction","protocol_version","communication_protocol"]'::jsonb,
          true
        ),
        '{filter,externalFilterEnabled}',
        'true'::jsonb,
        true
      ),
      '{filter,filterSelectedOptions}',
      '["command_direction","protocol_version","communication_protocol"]'::jsonb,
      true
    )
  )::text,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dm_data_tab_layout d
JOIN dynamic_entity_type et
  ON et.deleted = false
 AND et.tenant_id = 1
 AND et.code = 'data_protocol'
 AND et.data_layout_id = d.layout_id
WHERE p.deleted = false
  AND d.deleted = false
  AND d.tenant_id = 1
  AND d.column_kind = 'ENTITY'
  AND d.props_id = p.id
  AND p.component_code = 'list';
