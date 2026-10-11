-- 通讯方式写在指令上，不写在型号上。
-- 只整理已经是 MQTT 服务下发的大疆指令，以及东古、机器人的 WebSocket 指令。
-- Pilot 的 HTTPS / WebSocket / JSBridge，以及机场 requests_reply，由 dynamic_protocol_dji_cloud_channels.sql 写入，这里不再改写。
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_data_protocol_t1 e
SET custom_fields = (COALESCE(e.custom_fields, '{}'::jsonb) - 'http_path')
  || jsonb_build_object(
    'communication_protocol', 'mqtt',
    'protocol_channel', 'services',
    'mqtt_topic', 'thing/product/{gateway_sn}/services'
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.model_id = m.id
  AND m.tenant_id = 1
  AND m.deleted = false
  AND m.code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
  AND COALESCE(e.custom_fields->>'communication_protocol', 'mqtt') = 'mqtt'
  AND COALESCE(e.custom_fields->>'protocol_channel', 'services') = 'services';

UPDATE ent_data_protocol_t1 e
SET custom_fields = (COALESCE(e.custom_fields, '{}'::jsonb) - 'http_path' - 'protocol_channel' - 'mqtt_topic')
  || jsonb_build_object('communication_protocol', 'websocket'),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.model_id = m.id
  AND m.tenant_id = 1
  AND m.deleted = false
  AND m.code IN ('data_protocol_ws', 'data_protocol_robot');

INSERT INTO dynamic_entity_field_index_t1 (
  entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted
)
SELECT e.id, e.model_id, v.field_code, v.value_string, NULL, 'seed', 1, false
FROM ent_data_protocol_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false AND m.tenant_id = 1
JOIN (
  VALUES
    ('data_protocol_dji_dock', 'communication_protocol', 'mqtt'),
    ('data_protocol_dji_dock', 'protocol_channel', 'services'),
    ('data_protocol_dji_dock', 'mqtt_topic', 'thing/product/{gateway_sn}/services'),
    ('data_protocol_dji_pilot', 'communication_protocol', 'mqtt'),
    ('data_protocol_dji_pilot', 'protocol_channel', 'services'),
    ('data_protocol_dji_pilot', 'mqtt_topic', 'thing/product/{gateway_sn}/services'),
    ('data_protocol_ws', 'communication_protocol', 'websocket'),
    ('data_protocol_robot', 'communication_protocol', 'websocket')
) AS v(model_code, field_code, value_string)
  ON m.code = v.model_code
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false
      AND idx.tenant_id = 1
      AND idx.entity_id = e.id
      AND idx.field_code = v.field_code
  );

UPDATE dynamic_entity_field_index_t1 idx
SET value_string = v.value_string,
    model_id = e.model_id,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.tenant_id = 1
JOIN (
  VALUES
    ('data_protocol_dji_dock', 'communication_protocol', 'mqtt'),
    ('data_protocol_dji_dock', 'protocol_channel', 'services'),
    ('data_protocol_dji_dock', 'mqtt_topic', 'thing/product/{gateway_sn}/services'),
    ('data_protocol_dji_pilot', 'communication_protocol', 'mqtt'),
    ('data_protocol_dji_pilot', 'protocol_channel', 'services'),
    ('data_protocol_dji_pilot', 'mqtt_topic', 'thing/product/{gateway_sn}/services'),
    ('data_protocol_ws', 'communication_protocol', 'websocket'),
    ('data_protocol_robot', 'communication_protocol', 'websocket')
) AS v(model_code, field_code, value_string)
  ON m.code = v.model_code
WHERE idx.deleted = false
  AND idx.tenant_id = 1
  AND idx.entity_id = e.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND idx.field_code = v.field_code;

UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
JOIN dynamic_model m ON m.id = e.model_id
WHERE idx.deleted = false
  AND idx.tenant_id = 1
  AND idx.entity_id = e.id
  AND e.tenant_id = 1
  AND (
    m.code IN ('data_protocol_ws', 'data_protocol_robot')
    OR (
      m.code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
      AND COALESCE(e.custom_fields->>'communication_protocol', 'mqtt') = 'mqtt'
    )
  )
  AND idx.field_code = 'http_path';

-- 指令从「无人机」父分类挪到东古或大疆。已有目标分类行就停用父分类那一行。
UPDATE dynamic_entity_category_relation_t1 kept
SET deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
JOIN dynamic_model m ON m.id = e.model_id
JOIN dynamic_category target
  ON target.deleted = false
 AND target.tenant_id = 1
 AND target.code = CASE m.code
   WHEN 'data_protocol_ws' THEN 'cat-protocol-donggu-uav'
   ELSE 'cat-protocol-dji-uav'
 END
WHERE kept.tenant_id = 1
  AND kept.entity_type_code = 'data_protocol'
  AND kept.entity_id = e.id
  AND kept.category_id = target.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code IN ('data_protocol_ws', 'data_protocol_dji_dock', 'data_protocol_dji_pilot');

UPDATE dynamic_entity_category_relation_t1 r
SET category_id = target.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
JOIN dynamic_model m ON m.id = e.model_id
JOIN dynamic_category target
  ON target.deleted = false
 AND target.tenant_id = 1
 AND target.code = CASE m.code
   WHEN 'data_protocol_ws' THEN 'cat-protocol-donggu-uav'
   ELSE 'cat-protocol-dji-uav'
 END
JOIN dynamic_category src
  ON src.tenant_id = 1
 AND src.code IN ('cat-protocol-uav', 'cat-protocol-gs', 'cat-protocol-iot')
WHERE r.deleted = false
  AND r.tenant_id = 1
  AND r.category_id = src.id
  AND r.entity_type_code = 'data_protocol'
  AND r.entity_id = e.id
  AND e.deleted = false
  AND e.tenant_id = 1
  AND m.code IN ('data_protocol_ws', 'data_protocol_dji_dock', 'data_protocol_dji_pilot')
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_category_relation_t1 other
    WHERE other.tenant_id = r.tenant_id
      AND other.entity_type_code = r.entity_type_code
      AND other.entity_id = r.entity_id
      AND other.category_id = target.id
      AND other.id <> r.id
  );

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
JOIN dynamic_model m ON m.id = e.model_id
JOIN dynamic_category src
  ON src.tenant_id = 1
 AND src.code IN ('cat-protocol-uav', 'cat-protocol-gs', 'cat-protocol-iot')
JOIN dynamic_category target
  ON target.deleted = false
 AND target.tenant_id = 1
 AND target.code = CASE m.code
   WHEN 'data_protocol_ws' THEN 'cat-protocol-donggu-uav'
   ELSE 'cat-protocol-dji-uav'
 END
WHERE r.deleted = false
  AND r.tenant_id = 1
  AND r.category_id = src.id
  AND r.entity_type_code = 'data_protocol'
  AND r.entity_id = e.id
  AND e.tenant_id = 1
  AND m.code IN ('data_protocol_ws', 'data_protocol_dji_dock', 'data_protocol_dji_pilot')
  AND EXISTS (
    SELECT 1
    FROM dynamic_entity_category_relation_t1 other
    WHERE other.tenant_id = r.tenant_id
      AND other.entity_type_code = r.entity_type_code
      AND other.entity_id = r.entity_id
      AND other.category_id = target.id
      AND other.id <> r.id
  );

-- 详情表单：去掉 HTTP 路径，补上通讯方式；大疆再补通道和 MQTT 主题。
UPDATE model_crud_form_definition form
SET crud_form_fields = jsonb_set(
      form.crud_form_fields,
      '{customFieldDefs}',
      (
        SELECT COALESCE(jsonb_agg(elem ORDER BY (elem->>'sort')::int), '[]'::jsonb)
        FROM (
          SELECT elem
          FROM jsonb_array_elements(COALESCE(form.crud_form_fields->'customFieldDefs', '[]'::jsonb)) elem
          WHERE elem->>'fieldCode' NOT IN (
            'http_method', 'http_path', 'communication_protocol', 'protocol_channel', 'mqtt_topic'
          )
          UNION ALL
          SELECT jsonb_strip_nulls(jsonb_build_object(
            'sort', CASE d.code
              WHEN 'communication_protocol' THEN 18
              WHEN 'protocol_channel' THEN 19
              WHEN 'mqtt_topic' THEN 21
              WHEN 'http_method' THEN 22
              WHEN 'http_path' THEN 23
            END,
            'label', d.name,
            'fieldId', d.id,
            'fieldKey', d.code,
            'renderAs', CASE WHEN d.type = 'ENUM' THEN 'select' ELSE 'input' END,
            'required', false,
            'baseField', false,
            'fieldCode', d.code,
            'fieldName', d.name,
            'fieldType', d.type,
            'fieldSource', 'CUSTOM',
            'semanticType', d.code,
            'optionsSource', CASE
              WHEN d.type = 'ENUM' THEN jsonb_build_object('kind', 'static', 'options', d.options::jsonb)
              ELSE NULL
            END
          ))
          FROM dynamic_field d
          JOIN dynamic_model m ON m.id = form.model_id
          WHERE d.deleted = false
            AND d.tenant_id = 1
            AND (
              (d.code = 'communication_protocol' AND m.code IN (
                'data_protocol_ws', 'data_protocol_robot', 'data_protocol_dji_dock', 'data_protocol_dji_pilot'
              ))
              OR (d.code IN ('protocol_channel', 'mqtt_topic', 'http_method', 'http_path') AND m.code IN (
                'data_protocol_dji_dock', 'data_protocol_dji_pilot'
              ))
            )
        ) defs(elem)
      ),
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE form.deleted = false
  AND form.tenant_id = 1
  AND form.model_id = m.id
  AND m.code IN ('data_protocol_ws', 'data_protocol_robot', 'data_protocol_dji_dock', 'data_protocol_dji_pilot');

UPDATE model_crud_form_definition form
SET crud_form_fields = jsonb_set(
      form.crud_form_fields,
      '{formLayout,panels}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN panel ? 'fieldKeys' AND panel->'fieldKeys' ? 'http_path' THEN
              jsonb_set(
                panel,
                '{fieldKeys}',
                (
                  SELECT COALESCE(jsonb_agg(key ORDER BY ord), '[]'::jsonb)
                  FROM (
                    SELECT key, ord
                    FROM jsonb_array_elements_text(panel->'fieldKeys') WITH ORDINALITY AS listed(key, ord)
                    WHERE key <> 'http_path'
                    UNION ALL
                    SELECT 'communication_protocol', 1000
                    WHERE NOT EXISTS (
                      SELECT 1
                      FROM jsonb_array_elements_text(panel->'fieldKeys') existing(key)
                      WHERE existing.key = 'communication_protocol'
                    )
                  ) keys
                )
              )
            ELSE panel
          END
        ), '[]'::jsonb)
        FROM jsonb_array_elements(form.crud_form_fields->'formLayout'->'panels') panel
      ),
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE form.deleted = false
  AND form.tenant_id = 1
  AND form.model_id = m.id
  AND m.code IN ('data_protocol_ws', 'data_protocol_robot', 'data_protocol_dji_dock', 'data_protocol_dji_pilot')
  AND form.crud_form_fields ? 'formLayout';

COMMIT;
