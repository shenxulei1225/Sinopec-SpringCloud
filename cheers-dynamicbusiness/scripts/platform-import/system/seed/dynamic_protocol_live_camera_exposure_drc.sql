-- 实时画面上的光圈、快门、感光度。下发走指令飞行下行，不写进航线文件。
-- 航线里的数由取景结束时按官网元素另记。这里只对照实时下发。
-- 不负责：把曝光补偿写成环境亮度；没进指令飞行时替飞机填一个光圈。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES
  ('aperture_value', '光圈', 'NUMBER', '官网光圈档。大于 0 的数是实际光圈乘 100。0 是自动。', 'LIBRARY', 1, 1, 'seed'),
  ('shutter_value', '快门', 'NUMBER', '官网快门档。0 到 59 是时间，60 是自动。', 'LIBRARY', 1, 1, 'seed'),
  ('iso_value', '感光度档', 'NUMBER', '官网感光度档。0 自动，255 固定。', 'LIBRARY', 1, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description, status = 1, deleted = false,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite, creator, deleted
)
SELECT 1, 'action', m.id, v.name, v.code, 1, jsonb_build_array('UAV'),
  jsonb_build_object('version', 2, 'methods', jsonb_build_array(v.method)),
  '[]'::jsonb, false, 'seed', false
FROM (VALUES
  ('aperture', '光圈', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"aperture_value","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('shutter', '快门', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"shutter_value","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('iso', '感光度', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"iso_value","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb)
) AS v(code, name, method)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'action'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted
)
SELECT 1, 'data_protocol', model.id, spec.action_name,
  'proto-' || version.book_code || '-live-' || spec.action_code,
  1,
  jsonb_build_object(
    'opcode', 0,
    'command_direction', 'downlink',
    'protocol_version', jsonb_build_array(version.version_code),
    'communication_protocol', 'mqtt',
    'mqtt_topic', 'thing/product/{gateway_sn}/drc/down',
    'command_json', jsonb_build_object(
      'outbound', jsonb_build_object('method', spec.method_name, 'data', spec.data)
    ),
    'field_description_json', jsonb_build_object('outbound', spec.fields)
  ),
  'downlink', 'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', 'data_protocol_dji_dock'),
  ('dji-pilot', 'dji-pilot-1.16.1', 'data_protocol_dji_pilot')
) AS version(book_code, version_code, model_code)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = version.model_code
CROSS JOIN (VALUES
  ('aperture', '光圈', 'drc_camera_aperture_value_set', '{"payload_index":"","camera_type":"","aperture_value":280}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"aperture_value","path":"data.aperture_value","type":"integer","label":"光圈","required":true,"description":"官网光圈档。"}]'::jsonb),
  ('shutter', '快门', 'drc_camera_shutter_set', '{"payload_index":"","camera_type":"","shutter_value":12}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"shutter_value","path":"data.shutter_value","type":"integer","label":"快门","required":true,"description":"官网快门档。"}]'::jsonb),
  ('iso', '感光度', 'drc_camera_iso_set', '{"payload_index":"","camera_type":"","iso_value":5}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"iso_value","path":"data.iso_value","type":"integer","label":"感光度","required":true,"description":"官网感光度档。"}]'::jsonb)
) AS spec(action_code, action_name, method_name, data, fields)
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.tenant_id = 1 AND e.deleted = false
    AND e.code = 'proto-' || version.book_code || '-live-' || spec.action_code
);

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT 1, 'protocol_mapping', model.id, spec.action_name || ' · ' || version.version_name,
  'map-live-' || spec.action_code || '-' || version.book_code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', version.version_code,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', spec.slots
  ),
  'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('dji-pilot', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1')
) AS version(book_code, version_code, version_name)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = 'protocol_mapping_standard'
CROSS JOIN (VALUES
  ('aperture', '光圈', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"aperture_value","sourceKind":"INTERACTION","path":"data.aperture_value"}]'::jsonb),
  ('shutter', '快门', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"shutter_value","sourceKind":"INTERACTION","path":"data.shutter_value"}]'::jsonb),
  ('iso', '感光度', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"iso_value","sourceKind":"INTERACTION","path":"data.iso_value"}]'::jsonb)
) AS spec(action_code, action_name, slots)
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = spec.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1
  AND p.code = 'proto-' || version.book_code || '-live-' || spec.action_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.tenant_id = 1 AND e.deleted = false
    AND e.code = 'map-live-' || spec.action_code || '-' || version.book_code
);

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, r.category_id, 'action', a.id, r.domain, r.sort, 'seed', false
FROM ent_action_t1 a
JOIN ent_action_t1 zoom
  ON zoom.deleted = false AND zoom.tenant_id = 1 AND zoom.code = 'zoom'
JOIN dynamic_entity_category_relation_t1 r
  ON r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'action' AND r.entity_id = zoom.id
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('aperture', 'shutter', 'iso')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.entity_id = a.id AND x.category_id = r.category_id
  );

COMMIT;
