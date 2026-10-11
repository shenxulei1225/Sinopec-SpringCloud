-- 实时画面上的负载控制：框选、变焦拨轮、点对焦、对焦值、画面拖动、双击瞄准、对焦模式。
-- 负责：这些操作产出的数标成界面操作；补上还没有的动作，并把指令上的方法参数拆成官网字段。
-- 不负责：把变焦倍数或对焦点写进航线文件；下发时回读设备或猜画面中心。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES
  ('frame_x', '框选左上角横坐标', 'NUMBER', '在实时画面上拉框，框的左上角水平位置。镜头左上角为 0，右边为 1。', 'LIBRARY', 1, 1, 'seed'),
  ('frame_y', '框选左上角纵坐标', 'NUMBER', '在实时画面上拉框，框的左上角竖直位置。镜头左上角为 0，下边为 1。', 'LIBRARY', 1, 1, 'seed'),
  ('frame_width', '框选宽度', 'NUMBER', '在实时画面上拉框的宽度，相对整幅画面，范围 0 到 1。', 'LIBRARY', 1, 1, 'seed'),
  ('frame_height', '框选高度', 'NUMBER', '在实时画面上拉框的高度，相对整幅画面，范围 0 到 1。', 'LIBRARY', 1, 1, 'seed'),
  ('pitch_speed', '画面拖动俯仰速度', 'NUMBER', '在实时画面上向上或向下拖动时的云台俯仰速度，单位弧度每秒。', 'LIBRARY', 1, 1, 'seed'),
  ('yaw_speed', '画面拖动偏航速度', 'NUMBER', '在实时画面上向左或向右拖动时的云台偏航速度，单位弧度每秒。', 'LIBRARY', 1, 1, 'seed'),
  ('aim_x', '双击瞄准横坐标', 'NUMBER', '在实时画面上双击的水平位置。镜头左上角为 0，右边为 1。', 'LIBRARY', 1, 1, 'seed'),
  ('aim_y', '双击瞄准纵坐标', 'NUMBER', '在实时画面上双击的竖直位置。镜头左上角为 0，下边为 1。', 'LIBRARY', 1, 1, 'seed'),
  ('focus_value', '对焦值', 'NUMBER', '手动对焦滑条的位置。范围用这架飞机上报的最小、最大对焦值，不在这里写死。', 'LIBRARY', 1, 1, 'seed'),
  ('focus_mode', '对焦模式', 'NUMBER', '0 手动对焦，1 单次自动对焦，2 连续自动对焦。', 'LIBRARY', 1, 1, 'seed'),
  ('locked', '机头锁定', 'STRING', '锁定时云台和机身一起转；不锁定时只转云台。', 'LIBRARY', 1, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET name = '点对焦横坐标',
    description = '在实时画面上点一下的水平位置。镜头左上角为 0，右边为 1。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'point_focus_x';

UPDATE dynamic_field
SET name = '点对焦纵坐标',
    description = '在实时画面上点一下的竖直位置。镜头左上角为 0，下边为 1。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'point_focus_y';

UPDATE dynamic_field
SET description = '变焦拨轮产出的倍数。可见光是 2 到 200，红外是 2 到 20。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'zoom_factor';

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            (
              SELECT jsonb_agg(
                CASE
                  WHEN f->>'fieldCode' IN ('zoom_factor', 'point_focus_x', 'point_focus_y')
                    THEN jsonb_set(f, '{valueSource}', '"INTERACTION"'::jsonb)
                  ELSE f
                END
              )
              FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN ('zoom', 'focus');

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  creator, deleted
)
SELECT
  1, 'action', m.id, v.name, v.code, 1,
  jsonb_build_array('UAV'),
  jsonb_build_object('version', 2, 'methods', jsonb_build_array(v.method)),
  '[]'::jsonb,
  false,
  'seed',
  false
FROM (VALUES
  (
    'frameZoom',
    '框选变焦',
    '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"locked","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"frame_x","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"frame_y","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"frame_width","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"frame_height","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb
  ),
  (
    'screenDrag',
    '画面拖动',
    '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"locked","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"pitch_speed","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"yaw_speed","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb
  ),
  (
    'cameraAim',
    '双击瞄准',
    '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"locked","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"aim_x","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"aim_y","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb
  ),
  (
    'focusValue',
    '对焦值',
    '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"focus_value","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb
  ),
  (
    'focusMode',
    '对焦模式',
    '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"focus_mode","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb
  )
) AS v(code, name, method)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'action'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a
  WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, r.category_id, 'action', a.id, r.domain, r.sort, 'seed', false
FROM ent_action_t1 a
JOIN ent_action_t1 zoom
  ON zoom.deleted = false AND zoom.tenant_id = 1 AND zoom.code = 'zoom'
JOIN dynamic_entity_category_relation_t1 r
  ON r.deleted = false
 AND r.tenant_id = 1
 AND r.entity_type_code = 'action'
 AND r.entity_id = zoom.id
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN ('frameZoom', 'screenDrag', 'cameraAim', 'focusValue', 'focusMode')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 x
    WHERE x.deleted = false
      AND x.tenant_id = 1
      AND x.entity_id = a.id
      AND x.category_id = r.category_id
  );

UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(
      m.custom_fields,
      '{param_slot_mapping_json}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN item->>'slot' IN ('zoom_factor', 'point_focus_x', 'point_focus_y')
              THEN item || '{"sourceKind":"INTERACTION"}'::jsonb
            WHEN item->>'slot' IS NOT NULL AND item->>'slot' <> '' AND item->>'sourceKind' IS NULL
              THEN item || '{"sourceKind":"ACTION_PARAM"}'::jsonb
            ELSE item
          END
        )
        FROM jsonb_array_elements(m.custom_fields->'param_slot_mapping_json') item
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN (
    'map-zoom-dji-dock', 'map-zoom-dji-pilot',
    'map-focus-dji-dock', 'map-focus-dji-pilot'
  );

UPDATE ent_data_protocol_t1 p
SET custom_fields = jsonb_set(
      jsonb_set(
        p.custom_fields,
        '{command_json,outbound,data}',
        COALESCE(p.custom_fields->'command_json'->'outbound'->'data', '{}'::jsonb) || v.data,
        true
      ),
      '{field_description_json,outbound}',
      COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb) || (
        SELECT COALESCE(jsonb_agg(add.field), '[]'::jsonb)
        FROM jsonb_array_elements(v.fields) add(field)
        WHERE NOT EXISTS (
          SELECT 1
          FROM jsonb_array_elements(COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb)) old
          WHERE old->>'path' = add.field->>'path'
        )
      ),
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  (
    'proto-dji-dock-camera-frame-zoom',
    '{"payload_index":"","camera_type":"","locked":false,"x":0,"y":0,"width":0,"height":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"红外、广角或变焦。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"锁定时云台和机身一起转。"},{"code":"x","path":"data.x","type":"number","label":"框选左上角横坐标","required":true,"description":"镜头左上角为原点。"},{"code":"y","path":"data.y","type":"number","label":"框选左上角纵坐标","required":true,"description":"镜头左上角为原点。"},{"code":"width","path":"data.width","type":"number","label":"框选宽度","required":true,"description":"相对整幅画面。"},{"code":"height","path":"data.height","type":"number","label":"框选高度","required":true,"description":"相对整幅画面。"}]'::jsonb
  ),
  (
    'proto-dji-dock-camera-screen-drag',
    '{"payload_index":"","locked":false,"pitch_speed":0,"yaw_speed":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"不锁定时偏航速度才生效。"},{"code":"pitch_speed","path":"data.pitch_speed","type":"number","label":"画面拖动俯仰速度","required":true,"description":"弧度每秒。"},{"code":"yaw_speed","path":"data.yaw_speed","type":"number","label":"画面拖动偏航速度","required":true,"description":"弧度每秒。"}]'::jsonb
  ),
  (
    'proto-dji-pilot-camera-screen-drag',
    '{"payload_index":"","locked":false,"pitch_speed":0,"yaw_speed":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"不锁定时偏航速度才生效。"},{"code":"pitch_speed","path":"data.pitch_speed","type":"number","label":"画面拖动俯仰速度","required":true,"description":"弧度每秒。"},{"code":"yaw_speed","path":"data.yaw_speed","type":"number","label":"画面拖动偏航速度","required":true,"description":"弧度每秒。"}]'::jsonb
  ),
  (
    'proto-dji-dock-camera-aim',
    '{"payload_index":"","camera_type":"","locked":false,"x":0,"y":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"红外、广角或变焦。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"锁定时云台和机身一起转。"},{"code":"x","path":"data.x","type":"number","label":"双击瞄准横坐标","required":true,"description":"镜头左上角为原点。"},{"code":"y","path":"data.y","type":"number","label":"双击瞄准纵坐标","required":true,"description":"镜头左上角为原点。"}]'::jsonb
  ),
  (
    'proto-dji-pilot-camera-aim',
    '{"payload_index":"","camera_type":"","locked":false,"x":0,"y":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"红外、广角或变焦。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"锁定时云台和机身一起转。"},{"code":"x","path":"data.x","type":"number","label":"双击瞄准横坐标","required":true,"description":"镜头左上角为原点。"},{"code":"y","path":"data.y","type":"number","label":"双击瞄准纵坐标","required":true,"description":"镜头左上角为原点。"}]'::jsonb
  ),
  (
    'proto-dji-dock-camera-focus-value-set',
    '{"payload_index":"","camera_type":"","focus_value":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"focus_value","path":"data.focus_value","type":"integer","label":"对焦值","required":true,"description":"范围用飞机上报的最小、最大对焦值。"}]'::jsonb
  ),
  (
    'proto-dji-pilot-camera-focus-value-set',
    '{"payload_index":"","camera_type":"","focus_value":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"focus_value","path":"data.focus_value","type":"integer","label":"对焦值","required":true,"description":"范围用飞机上报的最小、最大对焦值。"}]'::jsonb
  ),
  (
    'proto-dji-dock-camera-focus-mode-set',
    '{"payload_index":"","camera_type":"","focus_mode":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"focus_mode","path":"data.focus_mode","type":"integer","label":"对焦模式","required":true,"description":"0 手动，1 单次自动，2 连续自动。"}]'::jsonb
  ),
  (
    'proto-dji-pilot-camera-focus-mode-set',
    '{"payload_index":"","camera_type":"","focus_mode":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"focus_mode","path":"data.focus_mode","type":"integer","label":"对焦模式","required":true,"description":"0 手动，1 单次自动，2 连续自动。"}]'::jsonb
  ),
  (
    'proto-dji-pilot-camera-frame-zoom',
    '{"payload_index":"","camera_type":"","locked":false,"x":0,"y":0,"width":0,"height":0}'::jsonb,
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"红外、广角或变焦。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"锁定时云台和机身一起转。"},{"code":"x","path":"data.x","type":"number","label":"框选左上角横坐标","required":true,"description":"镜头左上角为原点。"},{"code":"y","path":"data.y","type":"number","label":"框选左上角纵坐标","required":true,"description":"镜头左上角为原点。"},{"code":"width","path":"data.width","type":"number","label":"框选宽度","required":true,"description":"相对整幅画面。"},{"code":"height","path":"data.height","type":"number","label":"框选高度","required":true,"description":"相对整幅画面。"}]'::jsonb
  )
) AS v(code, data, fields)
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = v.code;

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', model.id,
  v.action_name || ' · ' || v.version_name,
  v.map_code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.version_code,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  'seed', false
FROM (VALUES
  ('map-frame-zoom-dji-dock', 'frameZoom', '框选变焦', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-frame-zoom', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"frame_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"frame_y","sourceKind":"INTERACTION","path":"data.y"},{"slot":"frame_width","sourceKind":"INTERACTION","path":"data.width"},{"slot":"frame_height","sourceKind":"INTERACTION","path":"data.height"}]'),
  ('map-frame-zoom-dji-pilot', 'frameZoom', '框选变焦', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-frame-zoom', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"frame_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"frame_y","sourceKind":"INTERACTION","path":"data.y"},{"slot":"frame_width","sourceKind":"INTERACTION","path":"data.width"},{"slot":"frame_height","sourceKind":"INTERACTION","path":"data.height"}]'),
  ('map-screen-drag-dji-dock', 'screenDrag', '画面拖动', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-screen-drag', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"pitch_speed","sourceKind":"INTERACTION","path":"data.pitch_speed"},{"slot":"yaw_speed","sourceKind":"INTERACTION","path":"data.yaw_speed"}]'),
  ('map-screen-drag-dji-pilot', 'screenDrag', '画面拖动', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-screen-drag', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"pitch_speed","sourceKind":"INTERACTION","path":"data.pitch_speed"},{"slot":"yaw_speed","sourceKind":"INTERACTION","path":"data.yaw_speed"}]'),
  ('map-camera-aim-dji-dock', 'cameraAim', '双击瞄准', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-aim', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"aim_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"aim_y","sourceKind":"INTERACTION","path":"data.y"}]'),
  ('map-camera-aim-dji-pilot', 'cameraAim', '双击瞄准', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-aim', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"aim_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"aim_y","sourceKind":"INTERACTION","path":"data.y"}]'),
  ('map-focus-value-dji-dock', 'focusValue', '对焦值', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-focus-value-set', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focus_value","sourceKind":"INTERACTION","path":"data.focus_value"}]'),
  ('map-focus-value-dji-pilot', 'focusValue', '对焦值', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-focus-value-set', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focus_value","sourceKind":"INTERACTION","path":"data.focus_value"}]'),
  ('map-focus-mode-dji-dock', 'focusMode', '对焦模式', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-focus-mode-set', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focus_mode","sourceKind":"INTERACTION","path":"data.focus_mode"}]'),
  ('map-focus-mode-dji-pilot', 'focusMode', '对焦模式', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-focus-mode-set', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focus_mode","sourceKind":"INTERACTION","path":"data.focus_mode"}]')
) AS v(map_code, action_code, action_name, version_code, version_name, instruction_code, slots)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = 'protocol_mapping_standard'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.tenant_id = 1 AND e.code = v.map_code AND e.deleted = false
);

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'protocol_mapping', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'map-frame-zoom-dji-dock', 'map-frame-zoom-dji-pilot',
    'map-screen-drag-dji-dock', 'map-screen-drag-dji-pilot',
    'map-camera-aim-dji-dock', 'map-camera-aim-dji-pilot',
    'map-focus-value-dji-dock', 'map-focus-value-dji-pilot',
    'map-focus-mode-dji-dock', 'map-focus-mode-dji-pilot'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
      AND r.entity_id = e.id AND r.category_id = c.id AND r.deleted = false
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code LIKE 'map-%-dji-%'
  AND e.code IN (
    'map-frame-zoom-dji-dock', 'map-frame-zoom-dji-pilot',
    'map-screen-drag-dji-dock', 'map-screen-drag-dji-pilot',
    'map-camera-aim-dji-dock', 'map-camera-aim-dji-pilot',
    'map-focus-value-dji-dock', 'map-focus-value-dji-pilot',
    'map-focus-mode-dji-dock', 'map-focus-mode-dji-pilot'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'map-frame-zoom-dji-dock', 'map-frame-zoom-dji-pilot',
    'map-screen-drag-dji-dock', 'map-screen-drag-dji-pilot',
    'map-camera-aim-dji-dock', 'map-camera-aim-dji-pilot',
    'map-focus-value-dji-dock', 'map-focus-value-dji-pilot',
    'map-focus-mode-dji-dock', 'map-focus-mode-dji-pilot'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'map-frame-zoom-dji-dock', 'map-frame-zoom-dji-pilot',
    'map-screen-drag-dji-dock', 'map-screen-drag-dji-pilot',
    'map-camera-aim-dji-dock', 'map-camera-aim-dji-pilot',
    'map-focus-value-dji-dock', 'map-focus-value-dji-pilot',
    'map-focus-mode-dji-dock', 'map-focus-mode-dji-pilot'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
  );

COMMIT;
